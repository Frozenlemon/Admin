package com.company.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.company.admin.model.Staff;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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

public class NewStaff extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextRole, editTextPhone;
    private ImageView staffImage;
    private File imageFile;

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
        uploadPicture(Uri.fromFile(imageFile), email, name, role, phone);
    }

    private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(NewStaff.this);
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
                    staffImage.setImageBitmap(bitmap);
                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile;
                    File file = new File(path, System.currentTimeMillis() + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                        imageFile = file;
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
                staffImage.setImageBitmap(thumbnail);
                imageFile = new File(picturePath);
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
}