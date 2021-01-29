package com.company.admin;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.company.admin.adapter.JobAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class OrderFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ref = db.collection("orders");
    private JobAdapter adapter;
    private ArrayList<DocumentSnapshot> orderRef = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_order_recycler_view, container, false);
        setUpRecyclerView(view);
        return view;
    }

    private void setUpRecyclerView(View view){
        ref.get().addOnCompleteListener(task ->{
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult())
                    orderRef.add(document);
                adapter = new JobAdapter(orderRef);
                RecyclerView recyclerView = view.findViewById(R.id.order_recycler_view);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener((documentSnapshot, position) -> {
                    Intent intent = new Intent(view.getContext(), OrderDetail.class);
                    intent.putExtra("uid", documentSnapshot.getId());
                    startActivity(intent);
                });
            }
        });


    }
}