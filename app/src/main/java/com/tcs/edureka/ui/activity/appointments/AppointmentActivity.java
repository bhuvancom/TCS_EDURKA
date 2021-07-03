package com.tcs.edureka.ui.activity.appointments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import com.tcs.edureka.R;

/**
 * @author Suraj
 */
public class AppointmentActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView dateView;
    private Toolbar toolbar;
    private AppCompatEditText etTitle;
    private AppCompatEditText etDescription;
    private Button btnAdd;
    private String selectedDate;
    private String title;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        calendarView = findViewById(R.id.calendar);
        dateView = findViewById(R.id.date_view);
        toolbar = findViewById(R.id.toolBar);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnAdd = findViewById(R.id.btnAdd);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = dayOfMonth + "-" + (month + 1) + "-" + year;
                dateView.setText(selectedDate);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etTitle.getText().toString().equals("") || etDescription.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter all the details", Toast.LENGTH_SHORT).show();
                } else {
                    title = etTitle.getText().toString();
                    description = etDescription.getText().toString();
                    Intent intent = new Intent(getApplicationContext(), RemindersActivity.class);
                    intent.putExtra("TITLE", title);
                    intent.putExtra("DESCRIPTION", description);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Reminder Added", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}