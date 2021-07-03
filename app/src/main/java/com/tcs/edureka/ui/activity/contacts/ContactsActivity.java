package com.tcs.edureka.ui.activity.contacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;
import com.tcs.edureka.R;
import com.tcs.edureka.model.ContactModel;

public class ContactsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_activty);
        setTitle("Search Here...");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<ContactModel> options = new FirebaseRecyclerOptions.Builder<ContactModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("contacts").orderByChild("name"), ContactModel.class)
                .build();

        recyclerViewAdapter = new RecyclerViewAdapter(options);
        recyclerView.setAdapter(recyclerViewAdapter);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.buttonAdd);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddContactActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recyclerViewAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        recyclerViewAdapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchmenu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchProcess(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchProcess(s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void searchProcess(String name) {
        FirebaseRecyclerOptions<ContactModel> options = new FirebaseRecyclerOptions.Builder<ContactModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("contacts")
                        .startAt(name).endAt(name + "\uf8ff"), ContactModel.class)
                .build();

        recyclerViewAdapter = new RecyclerViewAdapter(options);
        recyclerViewAdapter.startListening();
        recyclerView.setAdapter(recyclerViewAdapter);

    }
}