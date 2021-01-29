package com.company.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.company.admin.model.Product;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class NewProduct extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription, editTextPrice, editTextCategory, editTextSubCategory;
    private ImageView photo, photo1, photo2, photo3, photo4;
    private Uri photoFile, photo1File, photo2File, photo3File, photo4File;
    private int selected;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_GALLERY_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add a product");

        editTextTitle = findViewById(R.id.edit_text_product_title);
        editTextDescription = findViewById(R.id.edit_text_product_description);
        editTextPrice = findViewById(R.id.edit_text_product_price);
        editTextCategory= findViewById(R.id.edit_text_product_category);
        editTextSubCategory = findViewById(R.id.edit_text_product_sub_category);

        photo = findViewById(R.id.add_product_photo);
        photo1 = findViewById(R.id.add_product_photo1);
        photo2 = findViewById(R.id.add_product_photo2);
        photo3 = findViewById(R.id.add_product_photo3);
        photo4 = findViewById(R.id.add_product_photo4);

        photo.setOnClickListener(v -> {
            selected = 0;
            selectImage(0);
        });
        photo1.setOnClickListener(v -> {
            selected = 1;
            selectImage(1);
        });
        photo2.setOnClickListener(v -> {
            selected = 2;
            selectImage(2);
        });
        photo3.setOnClickListener(v -> {
            selected = 3;
            selectImage(3);
        });
        photo4.setOnClickListener(v -> {
            selected = 4;
            selectImage(4);
        });
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
        String category = editTextCategory.getText().toString();
        String subCategory = editTextSubCategory.getText().toString();

        if (title.trim().isEmpty() ||description.trim().isEmpty() || price.trim().isEmpty()){
            Toast.makeText(this, "Please dont leave any field empty", Toast.LENGTH_SHORT).show();
            return;
        }
        uploadPicture(title, price, description, category, subCategory);
    }

    private void selectImage(int i) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(NewProduct.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo"))
            {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File file = null;
                    try {
                        file = createImageFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    // Continue only if the File was successfully created
                    if (file != null) {
                        switch (i){
                            case 0:
                                photoFile = FileProvider.getUriForFile(this,
                                        "com.example.android.fileprovider",
                                        file);
                                break;
                            case 1:
                                photo1File = FileProvider.getUriForFile(this,
                                        "com.example.android.fileprovider",
                                        file);
                                break;
                            case 2:
                                photo2File = FileProvider.getUriForFile(this,
                                        "com.example.android.fileprovider",
                                        file);
                                break;
                            case 3:
                                photo3File = FileProvider.getUriForFile(this,
                                        "com.example.android.fileprovider",
                                        file);
                                break;
                            case 4:
                                photo4File = FileProvider.getUriForFile(this,
                                        "com.example.android.fileprovider",
                                        file);
                                break;
                        }

                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
            else if (options[item].equals("Choose from Gallery"))
            {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select picture"), REQUEST_GALLERY_IMAGE);
            }
            else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                switch (selected){
                    case 0:
                        photo.setImageBitmap(bitmap);
                        break;
                    case 1:
                        photo1.setImageBitmap(bitmap);
                        break;
                    case 2:
                        photo2.setImageBitmap(bitmap);
                        break;
                    case 3:
                        photo3.setImageBitmap(bitmap);
                        break;
                    case 4:
                        photo4.setImageBitmap(bitmap);
                        break;
                }
            } else if (requestCode == REQUEST_GALLERY_IMAGE) {
                try {
                    switch (selected){

                        case 0:
                            photoFile = data.getData();
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoFile);
                            photo.setImageBitmap(bitmap);
                            break;
                        case 1:
                            photo1File = data.getData();
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photo1File);
                            photo1.setImageBitmap(bitmap);
                            break;
                        case 2:
                            photo2File = data.getData();
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photo2File);
                            photo2.setImageBitmap(bitmap);
                            break;
                        case 3:
                            photo3File = data.getData();
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photo3File);
                            photo3.setImageBitmap(bitmap);
                            break;
                        case 4:
                            photo4File = data.getData();
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photo4File);
                            photo4.setImageBitmap(bitmap);
                            break;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void uploadPicture(String title, String price, String description, String category, String subCategory){
        String key = UUID.randomUUID().toString();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/"+key);

        imageRef.putFile(photoFile).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String key1 = UUID.randomUUID().toString();
            StorageReference imageRef1 = storageRef.child("images/"+key1);
            imageRef1.putFile(photo1File).addOnSuccessListener(taskSnapshot1 -> imageRef1.getDownloadUrl().addOnSuccessListener(uri1 -> {
                String key2 = UUID.randomUUID().toString();
                StorageReference imageRef2 = storageRef.child("images/"+key2);
                imageRef2.putFile(photo2File).addOnSuccessListener(taskSnapshot2 -> imageRef2.getDownloadUrl().addOnSuccessListener(uri2 -> {
                    String key3 = UUID.randomUUID().toString();
                    StorageReference imageRef3 = storageRef.child("images/"+key3);
                    imageRef3.putFile(photo3File).addOnSuccessListener(taskSnapshot3 -> imageRef3.getDownloadUrl().addOnSuccessListener(uri3 -> {
                        String key4 = UUID.randomUUID().toString();
                        StorageReference imageRef4 = storageRef.child("images/"+key4);
                        imageRef4.putFile(photo4File).addOnSuccessListener(taskSnapshot4 -> imageRef4.getDownloadUrl().addOnSuccessListener(uri4 -> {
                            CollectionReference productRef = FirebaseFirestore.getInstance().collection("products");
                            try {
                                productRef.add(new Product(title, Long.parseLong(price), description,
                                        0, category, subCategory, 0,
                                        new URL(uri.toString()).toString(), new URL(uri1.toString()).toString(),
                                        new URL(uri2.toString()).toString(), new URL(uri3.toString()).toString(),
                                        new URL(uri4.toString()).toString()));
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(this, "Product added", Toast.LENGTH_SHORT).show();
                            finish();
                        }));
                    }));
                }));
            }));
        })).addOnFailureListener(e ->
                Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show());
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }
}