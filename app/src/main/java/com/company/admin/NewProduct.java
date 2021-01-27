package com.company.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.company.admin.model.Product;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewProduct extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add a product");

        editTextTitle = findViewById(R.id.edit_text_product_title);
        editTextDescription = findViewById(R.id.edit_text_product_description);
        editTextPrice = findViewById(R.id.edit_text_product_price);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_product_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_product:
                saveProduct();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveProduct(){
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        String price = editTextPrice.getText().toString();

        if (title.trim().isEmpty() ||description.trim().isEmpty() || price.trim().isEmpty()){
            Toast.makeText(this, "Please dont leave any field empty", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference productRef = FirebaseFirestore.getInstance().collection("products");
        productRef.add(new Product(title, price, description, true));
        Toast.makeText(this, "Product added", Toast.LENGTH_SHORT).show();
        finish();
    }
}