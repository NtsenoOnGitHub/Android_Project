package com.example.wil_project.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.wil_project.R;
import com.example.wil_project.app_utilities.ApplicationClass;
import com.example.wil_project.data_models.Address;
import com.example.wil_project.data_models.Contact;
import com.example.wil_project.data_models.Hitchhiker;
import com.example.wil_project.data_models.TripRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class AccountActivity extends AppCompatActivity {

    TextView tvAccBalance, tvName, tvId, tvCellphone, tvEmail, tvCountry, tvAddress;
    BottomNavigationView bottomNavigationView;
    int HitchhikerId = ApplicationClass.HitchhikerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.Account);
        tvAccBalance = findViewById(R.id.tvAccBalance);
        tvName = findViewById(R.id.tvName);
        tvId = findViewById(R.id.tvId);
        tvCellphone = findViewById(R.id.tvCellphone);
        tvEmail = findViewById(R.id.tvEmail);
        tvCountry = findViewById(R.id.tvCountry);
        tvAddress = findViewById(R.id.tvAddress);

        String whereClause = "HitchhikerId =" + HitchhikerId;
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of(Hitchhiker.class).find(queryBuilder, new AsyncCallback<List<Hitchhiker>>() {
            @Override
            public void handleResponse(List<Hitchhiker> user) {
                tvAccBalance.setText("Account Balance: R" + user.get(0).getFunds());
                tvName.setText(user.get(0).getName() + " " + user.get(0).getSurname());
                tvId.setText(user.get(0).getIdentityNumber());
                tvEmail.setText(user.get(0).getUsername());
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(AccountActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Backendless.Data.of(Address.class).find(queryBuilder, new AsyncCallback<List<Address>>() {
            @Override
            public void handleResponse(List<Address> user) {
                tvCountry.setText(user.get(0).getCountry());
                tvAddress.setText(user.get(0).getHouseNumber() + " " + user.get(0).getStreetName() + " " + user.get(0).getCity());
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(AccountActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Backendless.Data.of(Contact.class).find(queryBuilder, new AsyncCallback<List<Contact>>() {
            @Override
            public void handleResponse(List<Contact> user) {
                tvCellphone.setText(user.get(0).getCellphoneNumber());
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(AccountActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.Dashboard:
                        startActivity(new Intent(getApplicationContext(), Dashboardctivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.Home:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.Account:
                        return true;
                }
                return false;
            }
        });
    }

    public void Topup(View view) {
    }
}