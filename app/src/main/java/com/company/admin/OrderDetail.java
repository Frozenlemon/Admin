package com.company.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderDetail extends AppCompatActivity {

    private DocumentReference order;
    private CollectionReference ref = FirebaseFirestore.getInstance().collection("orders");
    private String id;

    private TextView orderId, createdDate, status, driver, totalShipping;
    private Button assign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        orderId = findViewById(R.id.order_id_detail);
        createdDate = findViewById(R.id.createdDate);
        status = findViewById(R.id.Status);
        driver = findViewById(R.id.driver);
        totalShipping = findViewById(R.id.total_shipping);
        assign = findViewById(R.id.Assign);

        Intent intent = getIntent();
        id = intent.getStringExtra("uid");
        if (id != null && id != "")
            order = ref.document(id);
        if (order != null)
            order.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                     DocumentSnapshot snapshot = task.getResult();
                        orderId.setText(snapshot.getId());
                     if (snapshot.contains("createdDate")) {
                         Timestamp time = (Timestamp) snapshot.get("createdDate");
                         SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                         String dateString = sfd.format(new Date(time.getSeconds()*1000));
                         createdDate.setText(dateString);
                     }
                     if (snapshot.contains("orderStatus"))
                        status.setText(snapshot.get("orderStatus").toString());
                     if (snapshot.contains("driverID"))
                        driver.setText(snapshot.get("driverID").toString());
                     if (snapshot.contains("totalIncShipping"))
                        totalShipping.setText(snapshot.get("totalIncShipping").toString());
                    if (driver == null && driver.getText().toString().trim().equals(""))
                        assign.setVisibility(View.GONE);
                    else
                        assign.setVisibility(View.VISIBLE);

                    assign.setOnClickListener(v-> {
                        Intent newIntent = new Intent(OrderDetail.this, CustomStaffRecyclerView.class);
                        newIntent.putExtra("orderId", orderId.getText().toString());
                        startActivity(newIntent);
                    });
                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();

        order.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot snapshot = task.getResult();
                orderId.setText(snapshot.getId());
                if (snapshot.contains("createdDate")) {
                    Timestamp time = (Timestamp) snapshot.get("createdDate");
                    SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String dateString = sfd.format(new Date(time.getSeconds()*1000));
                    createdDate.setText(dateString);
                }
                if (snapshot.contains("orderStatus"))
                    status.setText(snapshot.get("orderStatus").toString());
                if (snapshot.contains("driverID"))
                    driver.setText(snapshot.get("driverID").toString());
                if (snapshot.contains("totalIncShipping"))
                    totalShipping.setText(snapshot.get("totalIncShipping").toString());
                if (driver == null && driver.getText().toString().trim().equals(""))
                    assign.setVisibility(View.GONE);
                else
                    assign.setVisibility(View.VISIBLE);

                assign.setOnClickListener(v-> {
                    Intent newIntent = new Intent(OrderDetail.this, CustomStaffRecyclerView.class);
                    newIntent.putExtra("orderId", orderId.getText().toString());
                    startActivity(newIntent);
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.immutable_product, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}