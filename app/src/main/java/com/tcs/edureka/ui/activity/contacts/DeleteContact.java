package com.tcs.edureka.ui.activity.contacts;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.tcs.edureka.R;

public class DeleteContact extends AppCompatActivity {
    EditText contactNumber, contactName;
    Button deleteContact, backToMain;
    String name, no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_contact);

        contactName = (EditText) findViewById(R.id.deleteName);
        contactNumber = (EditText) findViewById(R.id.deleteNumber);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("Name");
            no = bundle.getString("Number");
        }

        contactName.setText(name);
        contactNumber.setText(no);

        deleteContact = (Button) findViewById(R.id.deleteContact);

        deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = contactName.getText().toString();
                Log.d("tag", "Deleted = " + name);
                String number = contactNumber.getText().toString();
                deleteContact(name);
            }
        });

        backToMain = (Button) findViewById(R.id.backToMain);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReadContacts.class);
                startActivity(intent);
            }
        });
    }

    public long getRawContactIdByName(String givenName) {
        ContentResolver contentResolver = getContentResolver();

        String queryColumnArr[] = {ContactsContract.RawContacts._ID};

        // Create where condition clause.
        String displayName = givenName;
        String whereClause = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + " = '" + displayName + "'";

        // Query raw contact id through RawContacts uri.
        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;

        // Return the query cursor.
        Cursor cursor = contentResolver.query(rawContactUri, queryColumnArr, whereClause, null, null);

        long rawContactId = -1;

        if (cursor != null) {
            int queryResultCount = cursor.getCount();
            if (queryResultCount > 0) {
                cursor.moveToFirst();
                rawContactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
            }
        }

        return rawContactId;
    }

    public void deleteContact(String givenName) {
        // First select raw contact id by given name and family name.
        long rawContactId = getRawContactIdByName(givenName);

        ContentResolver contentResolver = getContentResolver();

        // Data table content process uri.
        Uri dataContentUri = ContactsContract.Data.CONTENT_URI;

        // Create data table where clause.
        StringBuffer dataWhereClauseBuf = new StringBuffer();
        dataWhereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID);
        dataWhereClauseBuf.append(" = ");
        dataWhereClauseBuf.append(rawContactId);
        contentResolver.delete(dataContentUri, dataWhereClauseBuf.toString(), null);

        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;

        // Create raw_contacts table where clause.
        StringBuffer rawContactWhereClause = new StringBuffer();
        rawContactWhereClause.append(ContactsContract.RawContacts._ID);
        rawContactWhereClause.append(" = ");
        rawContactWhereClause.append(rawContactId);

        contentResolver.delete(rawContactUri, rawContactWhereClause.toString(), null);
        Uri contactUri = ContactsContract.Contacts.CONTENT_URI;

        StringBuffer contactWhereClause = new StringBuffer();
        contactWhereClause.append(ContactsContract.Contacts._ID);
        contactWhereClause.append(" = ");
        contactWhereClause.append(rawContactId);

        contentResolver.delete(contactUri, contactWhereClause.toString(), null);
//       Log.d("tag", String.valueOf(contentResolver.delete(contactUri, contactWhereClause.toString(), null)));

    }
}