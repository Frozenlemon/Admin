package com.company.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class ProductDetail extends AppCompatActivity {

    private TextView name, category, subCategory, price, description, ratingCount;
    private EditText nameEdit, categoryEdit, subCategoryEdit, priceEdit, descriptionEdit;
    private ImageView photo, photo1, photo2, photo3, photo4;
    private RatingBar rating;
    private String id;
    private Button cancel, save;
    private CollectionReference productRef;
    private MenuItem editButton;
    private static DecimalFormat df2 = new DecimalFormat("#.##");
    private ScrollView container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Product detail");

        container = findViewById(R.id.description_content_edit_container);
        name = findViewById(R.id.title_content);
        category = findViewById(R.id.category_content);
        subCategory = findViewById(R.id.sub_category_content);
        price = findViewById(R.id.price_content);
        description = findViewById(R.id.description_content);
        nameEdit = findViewById(R.id.product_title_content_edit);
        categoryEdit = findViewById(R.id.category_content_edit);
        subCategoryEdit = findViewById(R.id.sub_category_content_edit);
        priceEdit = findViewById(R.id.price_content_edit);
        descriptionEdit = findViewById(R.id.description_content_edit);
        photo = findViewById(R.id.photo);
        photo1 = findViewById(R.id.photo1);
        photo2 = findViewById(R.id.photo2);
        photo3 = findViewById(R.id.photo3);
        photo4 = findViewById(R.id.photo4);
        rating = findViewById(R.id.rating_content);
        rating.setNumStars(5);
        ratingCount = findViewById(R.id.rating_count);
        save = findViewById(R.id.save_edit_product);
        cancel = findViewById(R.id.cancel_edit_product);

        Intent intent = getIntent();
        id = intent.getStringExtra("uid");
        editButton = findViewById(R.id.edit_detail);

        productRef = FirebaseFirestore.getInstance().collection("products");
        productRef.document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot snapshot = task.getResult();
                name.setText(snapshot.get("name").toString());
                category.setText(snapshot.get("category").toString());
                subCategory.setText(snapshot.get("subcategory").toString());
                description.setText(snapshot.get("description").toString());
                price.setText(snapshot.get("price").toString());
                rating.setRating(Float.parseFloat(snapshot.get("avgRating").toString()));
                ratingCount.setText(snapshot.get("numRatings").toString());
                new DownloadImageTask(photo).execute(snapshot.get("photo").toString());
                new DownloadImageTask(photo1).execute(snapshot.get("photo1").toString());
                new DownloadImageTask(photo2).execute(snapshot.get("photo2").toString());
                new DownloadImageTask(photo3).execute(snapshot.get("photo3").toString());
                new DownloadImageTask(photo4).execute(snapshot.get("photo4").toString());
            }
        });
        cancel.setOnClickListener(v -> toggleStaticDisplay(0));
        save.setOnClickListener(v -> {
            setChange();
            toggleStaticDisplay(0);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.item_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.edit_detail:
                editButton = item;
                toggleStaticDisplay(1);
                item.setVisible(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setChange(){
        Map<String, Object> data = new HashMap<>();
        data.put("name", nameEdit.getText().toString());
        data.put("category", categoryEdit.getText().toString());
        data.put("subcategory", subCategoryEdit.getText().toString());
        data.put("description", descriptionEdit.getText().toString());
        data.put("price", Long.parseLong(priceEdit.getText().toString().trim()));


        productRef.document(id).update(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                name.setText(nameEdit.getText());
                category.setText(categoryEdit.getText());
                subCategory.setText(subCategoryEdit.getText());
                description.setText(descriptionEdit.getText());
                price.setText(priceEdit.getText());
                Toast.makeText(this, "Product updated", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(this, "could not update data", Toast.LENGTH_SHORT).show();
        });
        toggleStaticDisplay(0);
    }

    private void toggleStaticDisplay(int i){
        switch (i){
            case 0:
                name.setVisibility(View.VISIBLE);
                category.setVisibility(View.VISIBLE);
                description.setVisibility(View.VISIBLE);
                subCategory.setVisibility(View.VISIBLE);
                price.setVisibility(View.VISIBLE);
                nameEdit.setVisibility(View.GONE);
                categoryEdit.setVisibility(View.GONE);
                subCategoryEdit.setVisibility(View.GONE);
                container.setVisibility(View.GONE);
                priceEdit.setVisibility(View.GONE);
                save.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                if (editButton != null)
                    editButton.setVisible(true);
                break;
            case 1:
                name.setVisibility(View.GONE);
                category.setVisibility(View.GONE);
                description.setVisibility(View.GONE);
                subCategory.setVisibility(View.GONE);
                price.setVisibility(View.GONE);
                nameEdit.setVisibility(View.VISIBLE);
                categoryEdit.setVisibility(View.VISIBLE);
                subCategoryEdit.setVisibility(View.VISIBLE);
                container.setVisibility(View.VISIBLE);
                priceEdit.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                parseValueToEdit();
                break;
        }
    }

    private void parseValueToEdit(){
        nameEdit.setText(name.getText().toString());
        categoryEdit.setText(category.getText().toString());
        subCategoryEdit.setText(subCategory.getText().toString());
        descriptionEdit.setText(description.getText().toString());
        priceEdit.setText(price.getText().toString());
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