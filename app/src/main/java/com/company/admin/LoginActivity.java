package com.company.admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText username, password;
    private Button login;
    private FirebaseFirestore db;
    private CollectionReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);

        login.setOnClickListener(v -> authenticate());
        db = FirebaseFirestore.getInstance();
        userRef = db.collection("staffs");
        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},1);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
        }
    }

    private void authenticate (){
        if (!validateField())
            return;
        auth.signInWithEmailAndPassword(username.getText().toString().trim(), password.getText().toString().trim()).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                userRef.document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.get("role").toString().equals("admin")){
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(this, "You do not have permission to access this app",Toast.LENGTH_SHORT);
                        auth.signOut();
                    }
                });
            }
            else
                Toast.makeText(this, "Wrong email or password",Toast.LENGTH_SHORT);
        });
    }

    private boolean validateField() {
        String emailString = username.getText().toString().trim();
        String passwordString = password.getText().toString().trim();
        boolean isValid = true;
        if (emailString.isEmpty() || passwordString.isEmpty()) {
            isValid = false;
            Toast.makeText(this, "username or password cannot be empty",Toast.LENGTH_SHORT);
        }
        if (!emailString.matches("^(.+)@company.com.vn$")){
            isValid = false;
            Toast.makeText(this, "You dont have permission to use this app",Toast.LENGTH_SHORT);
        }
        if (password.length() < 8) {
            isValid = false;
            Toast.makeText(this, "password too short",Toast.LENGTH_SHORT);
        }
        return isValid;
    }
}