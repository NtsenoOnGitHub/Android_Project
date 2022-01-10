package com.example.wil_project.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.bumptech.glide.Glide;
import com.example.wil_project.R;
import com.example.wil_project.app_utilities.ApplicationClass;
import com.example.wil_project.data_models.Contact;
import com.example.wil_project.data_models.Driver;
import com.example.wil_project.data_models.Hitchhiker;
import com.example.wil_project.data_models.Payment;
import com.example.wil_project.data_models.Route;

import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    TextView tvPayDeparture, tvPayDestination, tvPayDriverName, tvPayDriverCell, tvPayFare, tvPayAccBalance;
    double accountBalance, tripAmount, driverFunds;
    ImageView ivPayDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        tvPayDeparture = findViewById(R.id.tvDurDeparture);
        tvPayDestination = findViewById(R.id.tvDurDestination);
        tvPayDriverName = findViewById(R.id.tvPayDriverName);
        tvPayDriverCell = findViewById(R.id.tvPayDriverCell);
        tvPayFare = findViewById(R.id.tvPayFare);
        tvPayAccBalance = findViewById(R.id.tvPayAccBalance);
        ivPayDriver = findViewById(R.id.ivPayDriver);

        String whereClause = "HitchhikerId =" + ApplicationClass.HitchhikerId;
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of(Hitchhiker.class).find(queryBuilder, new AsyncCallback<List<Hitchhiker>>(){
            @Override
            public void handleResponse(List<Hitchhiker> foundHitchhiker )
            {
                accountBalance = foundHitchhiker.get(0).getFunds();
                tvPayAccBalance.setText(String.format(" Account Balance: R%s", accountBalance));
            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                Toast.makeText(PaymentActivity.this, "" + fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Toast.makeText(this, "" + getIntent().getIntExtra("RouteId", -1), Toast.LENGTH_SHORT).show();

        whereClause = "RouteId =" + getIntent().getIntExtra("RouteId", -1);
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of(Route.class).find(queryBuilder, new AsyncCallback<List<Route>>(){
            @Override
            public void handleResponse(List<Route> foundRoute )
            {
                tripAmount = foundRoute.get(0).getPrice();
                tvPayDeparture.setText(foundRoute.get(0).getDeparturePoint());
                tvPayDestination.setText(foundRoute.get(0).getDestinationPoint());
                tvPayFare.setText(String.format("Hike Fare: R %s",foundRoute.get(0).getPrice()));
            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                Toast.makeText(PaymentActivity.this, "" + fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        whereClause = "DriverId =" + getIntent().getIntExtra("DriverId", -1);
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of(Contact.class).find(queryBuilder, new AsyncCallback<List<Contact>>(){
            @Override
            public void handleResponse(List<Contact> foundContact )
            {
                tvPayDriverCell.setText("Cell Number: " +foundContact.get(0).getCellphoneNumber());

            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                Toast.makeText(PaymentActivity.this, "" + fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        whereClause = "DriverId =" + getIntent().getIntExtra("DriverId", -1);
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of(Driver.class).find(queryBuilder, new AsyncCallback<List<Driver>>(){
            @Override
            public void handleResponse(List<Driver> foundDriver )
            {
                tvPayDriverName.setText("Driver Name: " + foundDriver.get(0).getName());
                Glide.with(PaymentActivity.this).load(foundDriver.get(0).getPhotoUrl()).into(ivPayDriver);  //Check this
            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                Toast.makeText(PaymentActivity.this, "" + fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void Pay(View view) {
        if(accountBalance < tripAmount){
            Toast.makeText(this, "Insufficient Funds to Pay for trip. Please Fund you Account balance", Toast.LENGTH_SHORT).show();
        }
        else{
            Payment payment = new Payment();

            String whereClause = "DriverId =" + getIntent().getIntExtra("DriverId", -1);
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            Backendless.Data.of(Driver.class).find(queryBuilder, new AsyncCallback<List<Driver>>(){
                @Override
                public void handleResponse(List<Driver> foundDriver )
                {
                    driverFunds = foundDriver.get(0).getFunds();
                    foundDriver.get(0).setFunds(driverFunds + tripAmount);

                    String whereClause = "HitchhikerId =" + ApplicationClass.HitchhikerId;
                    DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                    queryBuilder.setWhereClause(whereClause);
                    Backendless.Data.of( Hitchhiker.class ).find(queryBuilder, new AsyncCallback<List<Hitchhiker>>() {
                        @Override
                        public void handleResponse( List<Hitchhiker> response )
                        {
                            response.get(0).setFunds(accountBalance - tripAmount);
                            Backendless.Data.of(Hitchhiker.class ).save(response.get(0), new AsyncCallback<Hitchhiker>() {
                                @Override
                                public void handleResponse( Hitchhiker response )
                                {
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(PaymentActivity.this, "MY_NOTIFICATION");
                                    builder.setSmallIcon(R.drawable.logo);
                                    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.location));
                                    builder.setContentTitle("Trip Payment !");
                                    builder.setContentText("A payment of R" + tripAmount + " was made. Tap Here");
                                    builder.setAutoCancel(true);
                                    builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                                    Intent intent = new Intent(PaymentActivity.this , AccountActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(PaymentActivity.this, 0, intent,
                                            PendingIntent.FLAG_UPDATE_CURRENT);
                                    builder.setContentIntent(pendingIntent);

                                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.notify(0, builder.build());
                                }
                                @Override
                                public void handleFault( BackendlessFault fault )
                                {
                                    Toast.makeText(PaymentActivity.this, "", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        @Override
                        public void handleFault( BackendlessFault fault )
                        {
                            Toast.makeText(PaymentActivity.this, "", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Backendless.Data.of( Driver.class ).save(foundDriver.get(0), new AsyncCallback<Driver>() {
                        @Override
                        public void handleResponse( Driver response )
                        {
                            Toast.makeText(PaymentActivity.this, "Payment was successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
                            startActivity(intent);
                            PaymentActivity.this.finish();

                        }
                        @Override
                        public void handleFault( BackendlessFault fault )
                        {
                            Toast.makeText(PaymentActivity.this, "", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void handleFault( BackendlessFault fault )
                {
                    Toast.makeText(PaymentActivity.this, "" + fault.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}