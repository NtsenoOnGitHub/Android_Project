package com.example.wil_project.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.rt.data.EventHandler;
import com.example.wil_project.R;
import com.example.wil_project.app_utilities.ApplicationClass;
import com.example.wil_project.data_models.Hitchhiker;
import com.example.wil_project.data_models.Route;
import com.example.wil_project.data_models.TripRequest;

import java.util.List;

public class TripActivity extends AppCompatActivity {
    TextView tvWay, tvDurDeparture, tvDurDestination;
    String source, destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        tvWay =findViewById(R.id.tvWay);
        tvDurDeparture = findViewById(R.id.tvDurDeparture);
        tvDurDestination = findViewById(R.id.tvDurDestination);

        //Notifications Channel code
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("MY_NOTIFICATION", "MY_NOTIFICATION", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        String whereClause = "RouteId =" + getIntent().getIntExtra("RouteId", -1);
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of(Route.class).find(queryBuilder, new AsyncCallback<List<Route>>(){
            @Override
            public void handleResponse(List<Route> foundRoute )
            {
                tvWay.setText(String.format("You are on you way to %s", foundRoute.get(0).getDestinationPoint()));
                tvDurDeparture.setText(String.format("%s", foundRoute.get(0).getDeparturePoint()));
                tvDurDestination.setText(String.format("%s", foundRoute.get(0).getDestinationPoint()));
                source = foundRoute.get(0).getDeparturePoint();
                destination = foundRoute.get(0).getDestinationPoint();
            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                Toast.makeText(TripActivity.this, "" + fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Listening to End trip
        EventHandler<TripRequest> TripEventHandler = Backendless.Data.of(TripRequest.class ).rt();
        TripEventHandler.addUpdateListener( "StartTrip = false AND HitchhikerId =" + ApplicationClass.HitchhikerId, new AsyncCallback<TripRequest>() {
            @Override
            public void handleResponse( TripRequest updatedTripRequest )
            {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(TripActivity.this, "MY_NOTIFICATION");
                builder.setSmallIcon(R.drawable.logo);
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.location));
                builder.setContentTitle("Trip has Ended !");
                builder.setContentText("Rate the Trip. Tap Here");
                builder.setAutoCancel(true);
                builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

                Intent intent = new Intent(TripActivity.this , FeedbackActivity.class);
                intent.putExtra("RouteId", updatedTripRequest.getRouteId());
                intent.putExtra("DriverId", updatedTripRequest.getDriverId());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(TripActivity.this, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, builder.build());

            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                Toast.makeText(TripActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } );

    }

    public void Direct(View view) {
        track(source, destination);
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
}