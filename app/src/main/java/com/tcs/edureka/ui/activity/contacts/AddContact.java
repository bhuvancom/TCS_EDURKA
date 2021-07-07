package com.tcs.edureka.ui.activity.contacts;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tcs.edureka.R;

public class AddContact extends AppCompatActivity {
    EditText displayNameEditor;
    EditText phoneNumberEditor;

    public static void start(Context context) {
        Intent intent = new Intent(context, AddContact.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        displayNameEditor = (EditText) findViewById(R.id.add_phone_contact_display_name);
        phoneNumberEditor = (EditText) findViewById(R.id.add_phone_contact_number);

        Spinner phoneTypeSpinner = (Spinner) findViewById(R.id.add_phone_contact_type);
        String phoneTypeArr[] = {"Mobile", "Home", "Work"};

        ArrayAdapter<String> phoneTypeSpinnerAdaptor = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, phoneTypeArr);
        phoneTypeSpinner.setAdapter(phoneTypeSpinnerAdaptor);

        Button savePhoneContactButton = (Button) findViewById(R.id.add_phone_contact_save_button);
        savePhoneContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri addContactsUri = ContactsContract.Data.CONTENT_URI;
                long rowContactId = getRawContactId();
                String displayName = displayNameEditor.getText().toString();
                insertContactDisplayName(addContactsUri, rowContactId, displayName);
                String phoneNumber = phoneNumberEditor.getText().toString();
                String phoneTypeStr = (String) phoneTypeSpinner.getSelectedItem();
                insertContactPhoneNumber(addContactsUri, rowContactId, phoneNumber, phoneTypeStr);
                Toast.makeText(getApplicationContext(), "New contact has been added, go back to previous page to see it in contacts list.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private long getRawContactId() {
        ContentValues contentValues = new ContentValues();
        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, contentValues);
        long ret = ContentUris.parseId(rawContactUri);
        return ret;
    }

    private void insertContactDisplayName(Uri addContactsUri, long rawContactId, String displayName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, displayName);
        getContentResolver().insert(addContactsUri, contentValues);
    }

    private void insertContactPhoneNumber(Uri addContactsUri, long rawContactId, String phoneNumber, String phoneTypeStr) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);
        int phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
        if ("home".equalsIgnoreCase(phoneTypeStr)) {
            phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
        } else if ("mobile".equalsIgnoreCase(phoneTypeStr)) {
            phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
        } else if ("work".equalsIgnoreCase(phoneTypeStr)) {
            phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_WORK;
        }

        contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, phoneContactType);
        getContentResolver().insert(addContactsUri, contentValues);
    }
}