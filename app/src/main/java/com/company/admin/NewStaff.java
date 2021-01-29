package com.company.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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

import com.company.admin.model.Staff;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class NewStaff extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextRole, editTextPhone;
    private ImageView staffImage;
    private Uri imageFile;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_GALLERY_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_staff);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add a Staff");

        editTextName = findViewById(R.id.edit_text_staff_name);
        editTextEmail = findViewById(R.id.edit_text_staff_email);
        editTextRole = findViewById(R.id.edit_text_staff_role);
        editTextPhone = findViewById(R.id.edit_text_staff_phone);
        staffImage = findViewById(R.id.staff_add_photo);

        staffImage.setOnClickListener(v -> selectImage());
        staffImage.setImageDrawable(getDrawable(R.drawable.ic_account_circle));
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
                saveStaff();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveStaff(){
        String name = editTextName.getText().toString();
        String email = editTextEmail.getText().toString();
        String role = editTextRole.getText().toString();
        String phone = editTextPhone.getText().toString();

        if (name.trim().isEmpty() ||email.trim().isEmpty() || role.trim().isEmpty() || phone.trim().isEmpty()){
            Toast.makeText(this, "Please dont leave any field empty", Toast.LENGTH_SHORT).show();
            return;
        }
        uploadPicture(imageFile, email, name, role, phone);
    }

    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(NewStaff.this);
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
                        imageFile = FileProvider.getUriForFile(this,
                                "com.example.android.fileprovider",
                                file);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                staffImage.setImageBitmap(bitmap);
            } else if (requestCode == REQUEST_GALLERY_IMAGE) {
                imageFile = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFile);
                    staffImage.setImageBitmap(bitmap);
                }
                catch (Exception e){}
            }
        }
    }

    private void uploadPicture(Uri image, String email, String name, String role, String phone){
        String randomKey = UUID.randomUUID().toString();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/"+randomKey);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, "badpassword").addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                String uid = auth.getUid();
                imageRef.putFile(image).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    DocumentReference staffRef = FirebaseFirestore.getInstance().collection("staffs").document(uid);
                    try {
                        staffRef.set(new Staff(name, role, email, phone, 0, new URL(uri.toString()).toString()));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(this, "Staff added", Toast.LENGTH_SHORT).show();
                    finish();
                })).addOnFailureListener(e ->
                        Toast.makeText(getApplicationContext(), "Upload Image Failed", Toast.LENGTH_SHORT).show());
            }
            else {
                Toast.makeText(this, "Staff already exist", Toast.LENGTH_SHORT).show();
            }
        });
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