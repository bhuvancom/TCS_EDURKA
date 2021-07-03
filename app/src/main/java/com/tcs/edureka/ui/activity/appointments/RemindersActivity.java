package com.tcs.edureka.ui.activity.appointments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tcs.edureka.R;
import com.tcs.edureka.model.AppointmentDataModel;

import java.util.ArrayList;

/**
 * @author Suraj
 */
public class RemindersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String title;
    private String description;
    private ArrayList<AppointmentDataModel> dataModel;
    private AppointmentAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        recyclerView = findViewById(R.id.recyclerView);

        title = getIntent().getStringExtra("TITLE");
        description = getIntent().getStringExtra("DESCRIPTION");

        if (dataModel == null) {
            dataModel = new ArrayList<>();
            dataModel.add(new AppointmentDataModel(title, description));

            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            myAdapter = new AppointmentAdapter(dataModel, getApplicationContext());
            recyclerView.setAdapter(myAdapter);
        } else {
            dataModel.add(new AppointmentDataModel(title, description));
            myAdapter.notifyDataSetChanged();
        }
    }
}