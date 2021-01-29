package com.company.admin;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.company.admin.adapter.CustomStaffAdapter;
import com.company.admin.model.Staff;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class CustomStaffRecyclerView extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference staffRef = db.collection("staffs");
    private CustomStaffAdapter adapter;
    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_staff_recycler_view);
        setUpRecyclerView();
    }

    private void setUpRecyclerView(){
        Query query = staffRef.whereLessThan("activeJob", 3).whereEqualTo("role", "driver");
        FirestoreRecyclerOptions<Staff> options = new FirestoreRecyclerOptions.Builder<Staff>()
                .setQuery(query, Staff.class)
                .build();
        adapter = new CustomStaffAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.custom_staff_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        intent = getIntent();
        String orderId = intent.getStringExtra("orderId");
        adapter.setOnItemClickListener((documentSnapshot, position) -> {
            if (documentSnapshot != null) {
                Map<String, Object> update = new HashMap<>();
                update.put("activeJob", Long.parseLong(documentSnapshot.get("activeJob").toString()) + 1);
                documentSnapshot.getReference().update(update);

                update = new HashMap<>();
                update.put("driverID", documentSnapshot.getId());
                db.collection("orders").document(orderId).update(update);
            }
            finish();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
