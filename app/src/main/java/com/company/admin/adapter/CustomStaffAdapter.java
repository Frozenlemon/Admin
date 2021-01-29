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
import com.company.admin.model.Staff;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.InputStream;

public class CustomStaffAdapter extends FirestoreRecyclerAdapter<Staff, CustomStaffAdapter.CustomStaffHolder> {

    private OnItemClickListener listener;

    public CustomStaffAdapter(@NonNull FirestoreRecyclerOptions<Staff> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CustomStaffHolder holder, int position, @NonNull Staff model) {
        holder.textViewName.setText(model.getName());
        holder.textViewEmail.setText(model.getEmail());
        holder.textViewRole.setText(model.getRole());
        new DownloadImageTask(holder.staffImage).execute(model.getPhoto());
    }

    @NonNull
    @Override
    public CustomStaffHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_staff_item, parent,false);
        return new CustomStaffHolder(v);
    }

    public class CustomStaffHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewRole;
        TextView textViewEmail;
        ImageView staffImage;

        public CustomStaffHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.custom_staff_name);
            textViewRole = itemView.findViewById(R.id.custom_staff_role);
            textViewEmail = itemView.findViewById(R.id.custom_staff_email);
            staffImage = itemView.findViewById(R.id.custom_staff_image);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null){
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(CustomStaffAdapter.OnItemClickListener listener){
        this.listener = listener;
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
