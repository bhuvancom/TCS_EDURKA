package com.tcs.edureka.ui.activity.appointments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tcs.edureka.R;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * @author Suraj
 */
@AndroidEntryPoint
public class RemindersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    private AppointmentAdapter myAdapter;
    private AppointmentViewModel appointmentViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        appointmentViewModel = new ViewModelProvider(this)
                .get(AppointmentViewModel.class);

        recyclerView = findViewById(R.id.recyclerView);
        TextView tvNoAppointment = findViewById(R.id.tvNoAppointment);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        myAdapter = new AppointmentAdapter();
        recyclerView.setAdapter(myAdapter);

        appointmentViewModel.getAllAppointments().observe(this, data -> {
            myAdapter.setDataModel(data);
            myAdapter.notifyDataSetChanged();
            tvNoAppointment.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(data.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }
}