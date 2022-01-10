package com.example.wil_project.data_models;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.wil_project.R;
import com.example.wil_project.activities.HomeActivity;
import com.example.wil_project.activities.VerificationActivity;
import com.example.wil_project.app_utilities.ApplicationClass;

import java.util.ArrayList;
import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ViewHolder> {
    private List<Route> routes;
    Context context;
    ItemClicked activity;

    public interface ItemClicked{
        void onItemClicked(int index);
    }

    public RouteAdapter(Context context, List<Route> list){
        routes = list;
        this.context = context;
        activity = (ItemClicked) context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView userImg;
        TextView tvPickup, tvDestination, tvPrice, tvDate, tvTime;
        CardView cardHolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardHolder = itemView.findViewById(R.id.cardHolder);
            userImg = itemView.findViewById(R.id.userImg);
            tvPickup = itemView.findViewById(R.id.tvPickup);
            tvDestination = itemView.findViewById(R.id.tvDestination);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.onItemClicked(routes.indexOf((Route) view.getTag()));
                }
            });


        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setTag(routes.get(position));

        holder.tvPickup.setText(routes.get(position).getDeparturePoint());
        holder.tvDestination.setText(routes.get(position).getDestinationPoint());
        holder.tvPrice.setText("R" + Double.toString(routes.get(position).getPrice()));
        holder.tvDate.setText(routes.get(position).getDepartureDate());
        holder.tvTime.setText(routes.get(position).getDepartureTime());

        String whereClause = "DriverId =" + routes.get(0).getDriverId();
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of(Driver.class).find(queryBuilder, new AsyncCallback<List<Driver>>(){
            @Override
            public void handleResponse(List<Driver> foundDriver )
            {
                Glide.with(context).load(foundDriver.get(0).getPhotoUrl()).into(holder.userImg);
            }
            @Override
            public void handleFault( BackendlessFault fault )
            {

            }
        });
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }


}
