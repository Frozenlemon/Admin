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
import android.widget.TextView;
import android.widget.Toast;

import com.company.admin.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class StaffDetail extends AppCompatActivity {

    private TextView name, role, email, phone;
    private EditText nameEdit, emailEdit, phoneEdit;
    private ImageView staffImage;
    private String id;
    private Button cancel, save;
    private CollectionReference staffRef;
    private MenuItem editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_detail);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Staff detail");

        name = findViewById(R.id.staff_name_content);
        role = findViewById(R.id.staff_role_content);
        email = findViewById(R.id.staff_email_content);
        phone = findViewById(R.id.staff_phone_content);
        nameEdit = findViewById(R.id.staff_name_content_edit);
        emailEdit = findViewById(R.id.staff_email_content_edit);
        phoneEdit = findViewById(R.id.staff_phone_content_edit);
        staffImage = findViewById(R.id.staff_photo);
        save = findViewById(R.id.save_edit_staff);
        cancel = findViewById(R.id.cancel_edit_staff);

        Intent intent = getIntent();
        id = intent.getStringExtra("uid");
        editButton = findViewById(R.id.edit_detail);

        staffRef = FirebaseFirestore.getInstance().collection("staffs");
        staffRef.document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot snapshot = task.getResult();
                name.setText(snapshot.get("name").toString());
                role.setText(snapshot.get("role").toString());
                email.setText(snapshot.get("email").toString());
                phone.setText(snapshot.get("phone").toString());
                new DownloadImageTask(staffImage).execute(snapshot.get("photo").toString());
            }
        });
        cancel.setOnClickListener(v -> toggleStaticDisplay(0));
        save.setOnClickListener(v -> {
            setChange();
            toggleStaticDisplay(0);
        });
        role.setOnClickListener(v -> {
            if (editButton != null){
                if (role.getText().toString().equals("admin"))
                    role.setText("driver");
                else
                    role.setText("admin");
            }
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
        data.put("email", emailEdit.getText().toString());
        data.put("role", role.getText().toString());
        data.put("phone", phoneEdit.getText().toString());


        staffRef.document(id).update(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                name.setText(nameEdit.getText());
                email.setText(emailEdit.getText());
                phone.setText(phoneEdit.getText());
                Toast.makeText(this, "Staff updated", Toast.LENGTH_SHORT).show();
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
                email.setVisibility(View.VISIBLE);
                phone.setVisibility(View.VISIBLE);
                nameEdit.setVisibility(View.GONE);
                emailEdit.setVisibility(View.GONE);
                phoneEdit.setVisibility(View.GONE);
                save.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                if (editButton != null)
                    editButton.setVisible(true);
                editButton = null;
                break;
            case 1:
                name.setVisibility(View.GONE);
                email.setVisibility(View.GONE);
                phone.setVisibility(View.GONE);
                nameEdit.setVisibility(View.VISIBLE);
                emailEdit.setVisibility(View.VISIBLE);
                phoneEdit.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                parseValueToEdit();
                break;
        }
    }

    private void parseValueToEdit(){
        nameEdit.setText(name.getText().toString());
        emailEdit.setText(email.getText().toString());
        phoneEdit.setText(phone.getText().toString());
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