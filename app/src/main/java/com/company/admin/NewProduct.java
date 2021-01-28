package com.company.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class NewProduct extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription, editTextPrice, editTextCategory, editTextSubCategory;
    private ImageView photo, photo1, photo2, photo3, photo4;
    private File photoFile, photo1File, photo2File, photo3File, photo4File;
    private int selected;

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
        photo1 = findViewById(R.id.add_prodduct_photo1);
        photo2 = findViewById(R.id.add_product_photo2);
        photo3 = findViewById(R.id.add_product_photo3);
        photo4 = findViewById(R.id.add_product_photo4);

        photo.setOnClickListener(v -> {
            selected = 0;
            selectImage();
        });
        photo1.setOnClickListener(v -> {
            selected = 1;
            selectImage();
        });
        photo2.setOnClickListener(v -> {
            selected = 2;
            selectImage();
        });
        photo3.setOnClickListener(v -> {
            selected = 3;
            selectImage();
        });
        photo4.setOnClickListener(v -> {
            selected = 4;
            selectImage();
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

    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(NewProduct.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo"))
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(intent, 1);
            }
            else if (options[item].equals("Choose from Gallery"))
            {
                Intent intent = new   Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
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
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);
                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile;
                    File file = new File(path, System.currentTimeMillis() + ".jpg");
                    switch (selected){
                        case 0:
                            photo.setImageBitmap(bitmap);
                            photoFile = file;
                            break;
                        case 1:
                            photo1.setImageBitmap(bitmap);
                            photo1File = file;
                            break;
                        case 2:
                            photo2.setImageBitmap(bitmap);
                            photo2File = file;
                            break;
                        case 3:
                            photo3.setImageBitmap(bitmap);
                            photo3File = file;
                            break;
                        case 4:
                            photo4.setImageBitmap(bitmap);
                            photo4File = file;
                            break;
                    }

                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 2) {
                String TAG = "path of image from gallery......";
                Uri selectedImage = data.getData();

                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.w(TAG, picturePath+"");
                switch (selected){
                    case 0:
                        photo.setImageBitmap(thumbnail);
                        photoFile = new File(picturePath);
                        break;
                    case 1:
                        photo1.setImageBitmap(thumbnail);
                        photo1File = new File(picturePath);
                        break;
                    case 2:
                        photo2.setImageBitmap(thumbnail);
                        photo2File = new File(picturePath);
                        break;
                    case 3:
                        photo3.setImageBitmap(thumbnail);
                        photo3File = new File(picturePath);
                        break;
                    case 4:
                        photo4.setImageBitmap(thumbnail);
                        photo4File = new File(picturePath);
                        break;
                }
            }
        }
    }

    private void uploadPicture(String title, String price, String description, String category, String subCategory){
        String key = UUID.randomUUID().toString();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/"+key);

        imageRef.putFile(Uri.fromFile(photoFile)).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String key1 = UUID.randomUUID().toString();
            StorageReference imageRef1 = storageRef.child("images/"+key1);
            imageRef1.putFile(Uri.fromFile(photo1File)).addOnSuccessListener(taskSnapshot1 -> imageRef1.getDownloadUrl().addOnSuccessListener(uri1 -> {
                String key2 = UUID.randomUUID().toString();
                StorageReference imageRef2 = storageRef.child("images/"+key2);
                imageRef2.putFile(Uri.fromFile(photo2File)).addOnSuccessListener(taskSnapshot2 -> imageRef2.getDownloadUrl().addOnSuccessListener(uri2 -> {
                    String key3 = UUID.randomUUID().toString();
                    StorageReference imageRef3 = storageRef.child("images/"+key3);
                    imageRef3.putFile(Uri.fromFile(photo3File)).addOnSuccessListener(taskSnapshot3 -> imageRef3.getDownloadUrl().addOnSuccessListener(uri3 -> {
                        String key4 = UUID.randomUUID().toString();
                        StorageReference imageRef4 = storageRef.child("images/"+key4);
                        imageRef4.putFile(Uri.fromFile(photo4File)).addOnSuccessListener(taskSnapshot4 -> imageRef4.getDownloadUrl().addOnSuccessListener(uri4 -> {
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
}