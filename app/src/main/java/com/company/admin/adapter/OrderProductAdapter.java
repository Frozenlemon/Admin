package com.company.admin.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.admin.R;
import com.company.admin.model.StaticProduct;

import java.io.InputStream;
import java.util.ArrayList;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.OProductHolder>{

    private ArrayList<StaticProduct> products;
    private JobAdapter.OnItemClickListener listener;

    public OrderProductAdapter(ArrayList<StaticProduct> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public OProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_order_product_detail, parent,false);
        return new OProductHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OProductHolder holder, int position) {
        holder.name.setText(products.get(position).getName());
        holder.quantity.setText(products.get(position).getQuantity());
        holder.price.setText(products.get(position).getPrice());
        new DownloadImageTask(holder.productImage).execute(products.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class OProductHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView name;
        TextView quantity;
        TextView price;

        public OProductHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.order_product_image);
            name = itemView.findViewById(R.id.order_product_detail_name);
            quantity = itemView.findViewById(R.id.order_product_detail_quantity);
            price = itemView.findViewById(R.id.order_product_quantity_price);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
