package com.company.admin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.admin.R;
import com.company.admin.model.Product;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ProductAdapter extends FirestoreRecyclerAdapter<Product, ProductAdapter.ProductHolder> {

    public ProductAdapter(@NonNull FirestoreRecyclerOptions<Product> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProductHolder holder, int position, @NonNull Product model) {
        if (model.isStatus()) {
            holder.textViewTitle.setText(model.getName());
            holder.textViewDescription.setText(model.getDescription());
            holder.textViewPrice.setText(model.getPrice());
        }
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent,false);
        return new ProductHolder(v);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().update("status",false);
    }

    class ProductHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        TextView textViewDescription;
        TextView textViewPrice;

        public ProductHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.product_title);
            textViewDescription = itemView.findViewById(R.id.product_description);
            textViewPrice = itemView.findViewById(R.id.product_price);
        }

    }
}
