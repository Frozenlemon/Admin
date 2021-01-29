package com.company.admin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.admin.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobHolder> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ref = db.collection("orders");
    private ArrayList<DocumentSnapshot> orderRef = new ArrayList<>();
    private JobAdapter.OnItemClickListener listener;

    public JobAdapter(ArrayList<DocumentSnapshot> orderRef){
       this.orderRef = orderRef;
    }


    @NonNull
    @Override
    public JobHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent,false);
        return new JobHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull JobHolder holder, int position) {
        DocumentSnapshot document = orderRef.get(position);
        Timestamp time = (Timestamp) document.get("createdDate");
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dateString = sfd.format(new Date(time.getSeconds()*1000));
        holder.orderId.setText(document.getId());
        holder.createdDate.setText(dateString);
        holder.orderStatus.setText(document.get("orderStatus").toString());
        if (!document.contains("driverID") || document.get("driverID").toString().equals(""))
            holder.driver.setText("NOT ASSIGNED");
        else
            holder.driver.setText("ASSIGNED");

    }

    @Override
    public int getItemCount() {
        return orderRef.size();
    }

    public class JobHolder extends RecyclerView.ViewHolder {
        TextView orderId;
        TextView createdDate;
        TextView driver;
        TextView orderStatus;

        public JobHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
            createdDate = itemView.findViewById(R.id.order_created_date);
            driver = itemView.findViewById(R.id.order_assigned);
            orderStatus = itemView.findViewById(R.id.order_status);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null){
                    listener.onItemClick(orderRef.get(position), position);
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(JobAdapter.OnItemClickListener listener){
        this.listener = listener;
    }
}
