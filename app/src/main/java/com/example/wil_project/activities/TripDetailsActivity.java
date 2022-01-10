package com.example.wil_project.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.bumptech.glide.Glide;
import com.example.wil_project.R;
import com.example.wil_project.app_utilities.ApplicationClass;
import com.example.wil_project.data_models.Car;
import com.example.wil_project.data_models.Contact;
import com.example.wil_project.data_models.Driver;
import com.example.wil_project.data_models.Hitchhiker;
import com.example.wil_project.data_models.Route;
import com.example.wil_project.data_models.TripRequest;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class TripDetailsActivity extends AppCompatActivity {

    ImageView ivDriver, ivCar;
    TextView tvTripDeparture, tvTripDestination, tvTripDate, tvTripTime, tvTripDescription;
    TextInputEditText edtPickUpSpot;
    LinearLayout lytRequest, lytPending;
    Button btnRequest;
    int HitchhikerId, DriverId, RouteId;
    String pickupLocation, destination, road;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("MY_NOTIFICATION", "MY_NOTIFICATION", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        lytRequest = findViewById(R.id.lytRequest);
        lytPending = findViewById(R.id.lytPending);
        ivDriver = findViewById(R.id.ivDriver);
        ivCar = findViewById(R.id.ivCar);
        tvTripDeparture = findViewById(R.id.tvDurDeparture);
        tvTripDestination = findViewById(R.id.tvDurDestination);
        tvTripDate = findViewById(R.id.tvTripDate);
        tvTripTime = findViewById(R.id.tvTripTime);
        tvTripDescription = findViewById(R.id.tvTripDescription);
        edtPickUpSpot = findViewById(R.id.edtPickUpSpot);
        btnRequest = findViewById(R.id.btnRequest);

        HitchhikerId = ApplicationClass.HitchhikerId;
        DriverId = getIntent().getIntExtra("DriverId", -1);
        RouteId = getIntent().getIntExtra("RouteId", -1);

        lytRequest.setVisibility(View.VISIBLE);
        lytPending.setVisibility(View.GONE);

        //If Request already made
        String whereClause = "RouteId =" + getIntent().getIntExtra("RouteId", -1);
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of(TripRequest.class).find(queryBuilder, new AsyncCallback<List<TripRequest>>() {
            @Override
            public void handleResponse(List<TripRequest> routes) {
                if(routes.size() > 0){
                    lytRequest.setVisibility(View.GONE);
                    lytPending.setVisibility(View.VISIBLE);

                }
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(TripDetailsActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        DisplayPhotos();

        whereClause = "RouteId =" + getIntent().getIntExtra("RouteId", -1);
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of(Route.class).find(queryBuilder, new AsyncCallback<List<Route>>() {
            @Override
            public void handleResponse(List<Route> routes) {
                tvTripDeparture.setText(routes.get(0).getDeparturePoint());
                tvTripDestination.setText(routes.get(0).getDestinationPoint());
                tvTripDate.setText(routes.get(0).getDepartureDate());
                tvTripTime.setText(routes.get(0).getDepartureTime());
                tvTripDescription.setText(routes.get(0).getTripDescription());
                road = routes.get(0).getRoad();
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(TripDetailsActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Request(View view) {
        if(edtPickUpSpot.getText().toString().isEmpty()){
            Toast.makeText(this, "Enter a spot/place where you wish to be picked up by the driver", Toast.LENGTH_SHORT).show();
        }
        else{
            TripRequest tripRequest = new TripRequest();
            tripRequest.setHitchhikerId(HitchhikerId);
            tripRequest.setDriverId(DriverId);
            tripRequest.setRouteId(RouteId);
            tripRequest.setDeparturePoint(tvTripDeparture.getText().toString());
            tripRequest.setDestinationPoint(tvTripDestination.getText().toString());
            tripRequest.setPickupSpot(edtPickUpSpot.getText().toString().trim());

            String whereClause = "HitchhikerId =" + ApplicationClass.HitchhikerId;
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            Backendless.Data.of(Hitchhiker.class).find(queryBuilder, new AsyncCallback<List<Hitchhiker>>() {
                @Override
                public void handleResponse(List<Hitchhiker> user) {
                    tripRequest.setName(user.get(0).getName());
                    tripRequest.setSurname(user.get(0).getSurname());
                    tripRequest.setPhotoUrl(user.get(0).getPhotoUrl());

                    String whereClause = "HitchhikerId =" + ApplicationClass.HitchhikerId;
                    DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                    queryBuilder.setWhereClause(whereClause);
                    Backendless.Data.of(Contact.class).find(queryBuilder, new AsyncCallback<List<Contact>>() {
                        @Override
                        public void handleResponse(List<Contact> contact) {
                            tripRequest.setCellphoneNumber(contact.get(0).getCellphoneNumber());
                        }
                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(TripDetailsActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    Backendless.Data.of(TripRequest.class ).save(tripRequest, new AsyncCallback<TripRequest>() {
                        public void handleResponse( TripRequest response ) {
                            Intent intent = new Intent(TripDetailsActivity.this, HomeActivity.class);
                            startActivity(intent);
                            Toast.makeText(TripDetailsActivity.this, "Trip Request Successfully sent !", Toast.LENGTH_SHORT).show();
                            TripDetailsActivity.this.finish();

                            //Display Confirmation notification of ride request sent
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(TripDetailsActivity.this, "MY_NOTIFICATION");
                            builder.setSmallIcon(R.drawable.logo);
                            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.location));
                            builder.setContentTitle("Ride Request Sent !");
                            builder.setContentText("Ride From " + tvTripDeparture.getText() + " to " + tvTripDestination.getText() + " via " + road + " Road");
                            builder.setAutoCancel(true);
                            builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(TripDetailsActivity.this);
                            managerCompat.notify(1, builder.build());
                        }
                        public void handleFault( BackendlessFault fault ) {
                            Toast.makeText(TripDetailsActivity.this, "Error occurred in saving trip request", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void handleFault(BackendlessFault fault) {
                    Toast.makeText(TripDetailsActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void Directions(View view) {
        String whereClause = "RouteId =" + getIntent().getIntExtra("RouteId", -1);
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of(Route.class).find(queryBuilder, new AsyncCallback<List<Route>>() {
            @Override
            public void handleResponse(List<Route> routes) {
                if(routes.size() > 0){
                    pickupLocation = routes.get(0).getDeparturePoint();
                    destination = routes.get(0).getDestinationPoint();
                    track(pickupLocation, destination);
                }
                else{
                    Toast.makeText(TripDetailsActivity.this, "Route Details not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(TripDetailsActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void track(String pickup, String destination){
        try{
            Uri uri = Uri.parse("https://www.google.co.za/maps/dir/" + pickup + "/" + destination);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        catch (ActivityNotFoundException e){

        }
    }

    public void DisplayPhotos() {
        String whereClause = "DriverId =" + getIntent().getIntExtra("DriverId", -1);
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);

        Backendless.Data.of(Driver.class).find(queryBuilder, new AsyncCallback<List<Driver>>() {
            @Override
            public void handleResponse(List<Driver> drivers) {
                Glide.with(TripDetailsActivity.this).load(drivers.get(0).getPhotoUrl()).into(ivDriver);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(TripDetailsActivity.this, "Driver was not found", Toast.LENGTH_SHORT).show();
            }
        });

        Backendless.Data.of(Car.class ).find(queryBuilder, new AsyncCallback<List<Car>>(){
            @Override
            public void handleResponse( List<Car> cars )
            {
                Glide.with(TripDetailsActivity.this).load(cars.get(0).getPhotoUrl()).into(ivCar); //.apply(RequestOptions.circleCropTransform())
            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                Toast.makeText(TripDetailsActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}