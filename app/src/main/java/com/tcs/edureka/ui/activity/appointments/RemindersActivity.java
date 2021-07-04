package com.tcs.edureka.ui.activity.appointments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tcs.edureka.R;
import com.tcs.edureka.model.AppointmentDataModel;
import com.tcs.edureka.utility.Utility;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * @author Suraj
 */
@AndroidEntryPoint
public class RemindersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<AppointmentDataModel> appointmentDataModelList = new ArrayList<>();
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

        Utility.makeToast("Swipe to delete", this);

        appointmentViewModel.getAllAppointments().observe(this, data -> {
            appointmentDataModelList = data;
            myAdapter.setDataModel(data);
            myAdapter.notifyDataSetChanged();
            tvNoAppointment.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(data.isEmpty() ? View.GONE : View.VISIBLE);
        });


        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                AppointmentDataModel model = appointmentDataModelList.get(pos);
                appointmentViewModel.deleteAppointment(model);
                myAdapter.notifyDataSetChanged();
            }
        };

        ItemTouchHelper helper = new ItemTouchHelper(simpleCallback);
        helper.attachToRecyclerView(recyclerView);
    }
}