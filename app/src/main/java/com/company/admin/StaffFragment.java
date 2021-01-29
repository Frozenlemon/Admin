package com.company.admin;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.company.admin.adapter.StaffAdapter;
import com.company.admin.model.Staff;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class StaffFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference staffRef = db.collection("staffs");
    private StaffAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.activity_staff_recycler_view, container, false);
        setUpRecyclerView(view);
        FloatingActionButton buttonAddProduct = view.findViewById(R.id.button_add_staff);
        buttonAddProduct.setOnClickListener(v -> startActivity(new Intent(view.getContext(), NewStaff.class)));
        return view;
    }

    private void setUpRecyclerView(View view){
        Query query = staffRef.orderBy("name", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Staff> options = new FirestoreRecyclerOptions.Builder<Staff>()
                .setQuery(query, Staff.class)
                .build();
        adapter = new StaffAdapter(options);

        RecyclerView recyclerView = view.findViewById(R.id.staff_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((documentSnapshot, position) -> {
            Intent intent = new Intent(view.getContext(), StaffDetail.class);
            intent.putExtra("uid", documentSnapshot.getId());
            startActivity(intent);
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