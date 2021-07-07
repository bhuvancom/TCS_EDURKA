package com.tcs.edureka.ui.activity.contacts;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tcs.edureka.R;

import java.util.ArrayList;

public class EditContact extends AppCompatActivity {
    EditText personName, personNo;
    Button updateContact, deleteContact, backToMain;
    String name, contactNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        personName = (EditText) findViewById(R.id.editName);
        personNo = (EditText) findViewById(R.id.editContact);
        updateContact = (Button) findViewById(R.id.updateButton);
        deleteContact = (Button) findViewById(R.id.deleteButton);
        backToMain = (Button) findViewById(R.id.backButton);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("Name");
            contactNo = bundle.getString("Number");
        }

        personName.setText(name);
        personNo.setText(contactNo);

        updateContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long rowContactId = getRawContactId();
                String updatedName = personName.getText().toString();
                String updatedNo = personNo.getText().toString();
//                Log.d("tag", "Names == " + updatedName + " Number == " + updatedNo);
                updateContact(updatedName, updatedNo, rowContactId);
            }
        });

        deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name1 = personName.getText().toString();
                String no1 = personNo.getText().toString();
                Intent intent = new Intent(getApplicationContext(), DeleteContact.class);
                intent.putExtra("Name", name1);
                intent.putExtra("Number", no1);
                startActivity(intent);
            }
        });

        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReadContacts.class);
                startActivity(intent);
            }
        });
    }

    private long getRawContactId() {
        ContentValues contentValues = new ContentValues();
        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, contentValues);
        long ret = ContentUris.parseId(rawContactUri);
        return ret;
    }

    public void updateContact(String name, String number, long contactId) {
        ContentResolver contentResolver = getContentResolver();
//        ContentValues values =new ContentValues();

        String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";

        String[] nameParams = new String[]{String.valueOf(contactId), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
        String[] numberParams = new String[]{String.valueOf(contactId), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        if (!name.equals("") && !number.equals("")) {
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, nameParams)
                    .withValue(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, name)
                    .build());
            Log.d("tag", "Name == " + ops.toString());
//        values.put("Name",name);

            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, numberParams)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                    .build());
//        values.put("Number",number);
            Log.d("tag", "Number == " + ops.toString());

            try {
                getApplicationContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                Toast.makeText(getApplicationContext(), "Contact is successfully edited", Toast.LENGTH_SHORT).show();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
        }
    }
}
