package com.tcs.edureka.ui.activity.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.tcs.edureka.R;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TanuShree
 */
public class AddContactActivity extends AppCompatActivity {
    EditText addName, addEmail, addPersonalNo, addOfcNo;
    Button addContactButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        addName = (EditText) findViewById(R.id.addName);
        addEmail = (EditText) findViewById(R.id.addEMail);
        addPersonalNo = (EditText) findViewById(R.id.addPersonalNo);
        addOfcNo = (EditText) findViewById(R.id.addOfcNo);

        backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ContactsActivity.class));
                finish();
            }
        });

        addContactButton = (Button) findViewById(R.id.addContactButton);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertContact();
            }
        });
    }

    private void insertContact() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", addName.getText().toString());
        map.put("emailId", addEmail.getText().toString());
        map.put("personalNo", addPersonalNo.getText().toString());
        map.put("ofcNo", addOfcNo.getText().toString());

        FirebaseDatabase.getInstance().getReference().child("contacts").push()
                .setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        addName.setText("");
                        addEmail.setText("");
                        addPersonalNo.setText("");
                        addOfcNo.setText("");
                        Toast.makeText(getApplicationContext(), "Inserted Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("tag", String.valueOf(e));
                        Toast.makeText(getApplicationContext(), "Could not insert.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
