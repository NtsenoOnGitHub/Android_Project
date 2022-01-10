package com.example.wil_project.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.rt.data.EventHandler;
import com.example.wil_project.R;
import com.example.wil_project.app_utilities.ApplicationClass;
import com.example.wil_project.data_models.Hitchhiker;
import com.example.wil_project.data_models.Route;
import com.example.wil_project.data_models.RouteAdapter;
import com.example.wil_project.data_models.TripRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements RouteAdapter.ItemClicked {

    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerView;
    RecyclerView.Adapter myAdapter;
    RecyclerView.LayoutManager layoutManager;
    private static  final int ACCESS_LOCATION_REQUEST_CODE = 1;
    String CurrentLocation;
    TextInputEditText edtPickup,edtDestination;
    FusedLocationProviderClient fusedLocationProviderClient;
    List<Route> routes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        edtPickup = findViewById(R.id.edtPickUp);
        edtDestination = findViewById(R.id.edtDestination);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.Home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.Dashboard:
                        startActivity(new Intent(getApplicationContext(), Dashboardctivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.Home:
                        return true;
                    case R.id.Account:
                        startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });



        //ApprovalStatus Code
        String whereClause = "HitchhikerId =" + ApplicationClass.HitchhikerId;
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of(Hitchhiker.class).find(queryBuilder, new AsyncCallback<List<Hitchhiker>>(){
            @Override
            public void handleResponse(List<Hitchhiker> foundHitchhiker )
            {
                if(foundHitchhiker.size() > 0){
                    if(!foundHitchhiker.get(0).isApprovalStatus()){
                        startActivity(new Intent(HomeActivity.this, VerificationActivity.class));
                        HomeActivity.this.finish();
                    }
                }
            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                Toast.makeText(HomeActivity.this, "" + fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Notifications Channel code
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("MY_NOTIFICATION", "MY_NOTIFICATION", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        //Listening to RideRequest StartTrip
        EventHandler<TripRequest> TripEventHandler = Backendless.Data.of(TripRequest.class ).rt();
        TripEventHandler.addUpdateListener( "StartTrip = true AND HitchhikerId =" + ApplicationClass.HitchhikerId, new AsyncCallback<TripRequest>() {
            @Override
            public void handleResponse( TripRequest updatedTripRequest )
            {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(HomeActivity.this, "MY_NOTIFICATION");
                builder.setSmallIcon(R.drawable.logo);
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.location));
                builder.setContentTitle("Trip has Started !");
                builder.setContentText("Driver is on the way to pick you up. Tap Here");
                builder.setAutoCancel(true);
                builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                Intent intent = new Intent(HomeActivity.this , TripActivity.class);
                intent.putExtra("RouteId", updatedTripRequest.getRouteId());
                intent.putExtra("DriverId", updatedTripRequest.getDriverId());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(HomeActivity.this, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, builder.build());
            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                Toast.makeText(HomeActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } );

        //Listening to RideRequest Approval code
        EventHandler<TripRequest> ApprovedEventHandler = Backendless.Data.of(TripRequest.class ).rt();
        ApprovedEventHandler.addUpdateListener( "Status = true AND HitchhikerId =" + ApplicationClass.HitchhikerId, new AsyncCallback<TripRequest>() {
            @Override
            public void handleResponse( TripRequest updatedTripRequest )
            {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(HomeActivity.this, "MY_NOTIFICATION");
                builder.setSmallIcon(R.drawable.logo);
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.location));
                builder.setContentTitle("Ride Request Approval");
                builder.setContentText("Your Ride request has been ACCEPTED. Tap Here");
                builder.setAutoCancel(true);
                builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                Intent intent = new Intent(HomeActivity.this , PaymentActivity.class);
                intent.putExtra("RouteId", updatedTripRequest.getRouteId());
                intent.putExtra("DriverId", updatedTripRequest.getDriverId());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(HomeActivity.this, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, builder.build());

            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                Toast.makeText(HomeActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } );

        //Listening to RideRequest Deny code
        EventHandler<TripRequest> DeclinedEventHandler = Backendless.Data.of(TripRequest.class ).rt();
        DeclinedEventHandler.addUpdateListener( "Status = false AND HitchhikerId =" + ApplicationClass.HitchhikerId, new AsyncCallback<TripRequest>() {
            @Override
            public void handleResponse( TripRequest updatedTripRequest )
            {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(HomeActivity.this, "MY_NOTIFICATION");
                builder.setSmallIcon(R.drawable.logo);
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.location));
                builder.setContentTitle("Ride Request Denied");
                builder.setContentText("Your Ride request has been DECLINED. Tap Here");
                builder.setAutoCancel(true);
                builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                Intent intent = new Intent(HomeActivity.this , HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(HomeActivity.this, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, builder.build());

            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                Toast.makeText(HomeActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } );


        //check for Location Permission
        if(ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(HomeActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_LOCATION_REQUEST_CODE);
        }
        else{
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if(location != null){
                        try {
                            Geocoder geocoder =  new Geocoder(HomeActivity.this, Locale.getDefault());
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
                            CurrentLocation = addresses.get(0).getLocality();

                            String whereClause = String.format("DeparturePoint = '%s'",  CurrentLocation);
                            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                            queryBuilder.setWhereClause(whereClause);
                            Backendless.Data.of(Route.class).find(queryBuilder, new AsyncCallback<List<Route>>(){
                                @Override
                                public void handleResponse(List<Route> fillteredRoutes )
                                {
                                    if(fillteredRoutes.size() > 0){
                                        routes = fillteredRoutes;
                                        myAdapter = new RouteAdapter(HomeActivity.this, routes);
                                        recyclerView.setAdapter(myAdapter);
                                    }
                                }
                                @Override
                                public void handleFault( BackendlessFault fault )
                                {
                                    Toast.makeText(HomeActivity.this, "" + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public void Search(View view) {

        if(edtDestination.getText().toString().isEmpty() || edtPickup.getText().toString().isEmpty()){
            Toast.makeText(this, "Please enter a all pick-up location and destination", Toast.LENGTH_SHORT).show();
        }
        else{
            String whereClause = String.format("DeparturePoint = '%s' AND DestinationPoint = '%s'", edtPickup.getText().toString(), edtDestination.getText().toString());
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);

            Backendless.Data.of(Route.class).find(queryBuilder, new AsyncCallback<List<Route>>(){
                @Override
                public void handleResponse( List<Route> foundRoutes )
                {
                    if(foundRoutes.size() > 0){
                        routes = foundRoutes;
                        myAdapter = new RouteAdapter(HomeActivity.this, routes);
                        recyclerView.setAdapter(myAdapter);
                    }
                    else{
                        Toast.makeText(HomeActivity.this, "Array is empty", Toast.LENGTH_SHORT).show();
                    }

                }
                @Override
                public void handleFault( BackendlessFault fault )
                {
                    Toast.makeText(HomeActivity.this, "" + fault.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == ACCESS_LOCATION_REQUEST_CODE){

            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Routes filtered according to location
                if(ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED){
                    fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();
                            if(location != null){
                                try {
                                    Geocoder geocoder =  new Geocoder(HomeActivity.this, Locale.getDefault());
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
                                    CurrentLocation = addresses.get(0).getLocality();
                                    //Toast.makeText(HomeActivity.this, "" + addresses.get(0).getLocality() , Toast.LENGTH_SHORT).show();

                                    String whereClause = String.format("DeparturePoint = '%s'",  CurrentLocation);
                                    DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                                    queryBuilder.setWhereClause(whereClause);
                                    Backendless.Data.of(Route.class).find(queryBuilder, new AsyncCallback<List<Route>>(){
                                        @Override
                                        public void handleResponse(List<Route> fillteredRoutes )
                                        {
                                            if(fillteredRoutes.size() > 0){
                                                routes = fillteredRoutes;
                                                myAdapter = new RouteAdapter(HomeActivity.this, routes);
                                                recyclerView.setAdapter(myAdapter);
                                            }
                                            else {
                                                Toast.makeText(HomeActivity.this, "Array is empty", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        @Override
                                        public void handleFault( BackendlessFault fault )
                                        {
                                            Toast.makeText(HomeActivity.this, "" + fault.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
            else if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                //All Routes
                Backendless.Data.of(Route.class).find( new AsyncCallback<List<Route>>(){
                    @Override
                    public void handleResponse( List<Route> foundRoutes )
                    {
                        if(foundRoutes.size() > 0){
                            routes = foundRoutes;
                            myAdapter = new RouteAdapter(HomeActivity.this, routes);
                            recyclerView.setAdapter(myAdapter);
                        }
                        else{
                            Toast.makeText(HomeActivity.this, "Array is empty", Toast.LENGTH_SHORT).show();
                        }

                    }
                    @Override
                    public void handleFault( BackendlessFault fault )
                    {
                        Toast.makeText(HomeActivity.this, "" + fault.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }

    @Override
    public void onItemClicked(int index) {
        Intent intent = new Intent(HomeActivity.this, TripDetailsActivity.class);
        intent.putExtra("RouteId", routes.get(index).getRouteId());
        intent.putExtra("DriverId", routes.get(index).getDriverId());
        startActivity(intent);
    }

}