package com.tcs.edureka.ui.activity.contacts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tcs.edureka.R;

import java.util.ArrayList;
import java.util.List;

public class ReadContacts extends AppCompatActivity {
    ListView list;
    List<String> contactList = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    Button addContact, refreshContact;
    EditText nameSearch;

    int PERMISSION_REQUEST_CODE_READ_CONTACTS = 1;
    int PERMISSION_REQUEST_CODE_WRITE_CONTACTS = 2;

//    String personName,personContact,id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_contacts);


        list = (ListView) findViewById(R.id.listNumbers);
        nameSearch = (EditText) findViewById(R.id.searchName);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactList);
        list.setAdapter(adapter);

        nameSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

//        if(!hasPhoneContactsPermission(Manifest.permission.WRITE_CONTACTS))
//        {
//            requestPermission(Manifest.permission.WRITE_CONTACTS, PERMISSION_REQUEST_CODE_WRITE_CONTACTS);
//        }
//        else
//        {
////            readContacts();
////            AddContact.start(getApplicationContext());
//        }
        if (!hasPhoneContactsPermission(Manifest.permission.READ_CONTACTS)) {
            requestPermission(Manifest.permission.READ_CONTACTS, PERMISSION_REQUEST_CODE_READ_CONTACTS);
        } else {
            readContacts();
        }
    }

    private boolean hasPhoneContactsPermission(String permission) {
        boolean ret = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasPermission = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                ret = true;
            }
        } else
            ret = true;

        return ret;
    }

    private void requestPermission(String permission, int requestCode) {
        String requestPermissionArray[] = {permission};
        ActivityCompat.requestPermissions(this, requestPermissionArray, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int length = grantResults.length;
        if (length > 0) {
            int grantResult = grantResults[0];
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                if (requestCode == PERMISSION_REQUEST_CODE_READ_CONTACTS) {
                    readContacts();
                } else if (requestCode == PERMISSION_REQUEST_CODE_WRITE_CONTACTS) {
                    AddContact.start(getApplicationContext());
                }
            } else {
                Toast.makeText(getApplicationContext(), "You denied permission.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void readContacts() {
        Uri readContactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = getContentResolver().query(readContactUri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            do {
                int diaplayNameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String userDisplayName = cursor.getString(diaplayNameIndex);

                int phoneNumberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String phoneNumber = cursor.getString(phoneNumberIndex);

                String phoneTypeStr = "Mobile";
                int phoneTypeColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                int phoneTypeInt = cursor.getInt(phoneTypeColumnIndex);
                if (phoneTypeInt == ContactsContract.CommonDataKinds.Phone.TYPE_HOME) {
                    phoneTypeStr = "Home";
                } else if (phoneTypeInt == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                    phoneTypeStr = "Mobile";
                } else if (phoneTypeInt == ContactsContract.CommonDataKinds.Phone.TYPE_WORK) {
                    phoneTypeStr = "Work";
                }
                StringBuffer contactStringBuf = new StringBuffer();
                contactStringBuf.append(userDisplayName);
                contactStringBuf.append("\r\n");
                contactStringBuf.append(phoneNumber);
                contactStringBuf.append("\r\n");
                contactStringBuf.append(phoneTypeStr);
                contactList.add(contactStringBuf.toString());
            } while (cursor.moveToNext());

            adapter.notifyDataSetChanged();
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object cursor1 = list.getItemAtPosition(i);

                String str = cursor1.toString();
                String[] str1 = str.split("\n");
                String name = str1[0];
                String contactNo = str1[1];

                Log.d("namesss", name);
                Log.d("namesss", contactNo);
                updateContact(name, contactNo);
            }
        });

        refreshContact = (Button) findViewById(R.id.refreshContacts);
        refreshContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readContacts();
            }
        });

        addContact = (Button) findViewById(R.id.addContactButton);
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasPhoneContactsPermission(Manifest.permission.WRITE_CONTACTS)) {
                    requestPermission(Manifest.permission.WRITE_CONTACTS, PERMISSION_REQUEST_CODE_WRITE_CONTACTS);
                } else {
                    Intent intent = new Intent(getApplicationContext(), AddContact.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void updateContact(String name, String contactNo) {
        Intent intent = new Intent(getApplicationContext(), EditContact.class);
        intent.putExtra("Name", name);
        intent.putExtra("Number", contactNo);
        startActivity(intent);
    }
}
