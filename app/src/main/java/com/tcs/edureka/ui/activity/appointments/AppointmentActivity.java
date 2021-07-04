package com.tcs.edureka.ui.activity.appointments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.tcs.edureka.R;
import com.tcs.edureka.model.AppointmentDataModel;
import com.tcs.edureka.receivers.AlarmReceiver;
import com.tcs.edureka.utility.Constants;
import com.tcs.edureka.utility.Utility;

import java.util.Calendar;
import java.util.Date;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * @author Suraj
 * <p>
 * This activity is expecting two extras
 * EXTRA_DATA_TITLE as Title value in String form
 * EXTRA_DATE_AND_TIME as Date in  Date form
 * if these extra is present there it will save to DB and will create alarm also
 */

@AndroidEntryPoint
public class AppointmentActivity extends AppCompatActivity {
    private static final String TAG = "AppointmentActivity";

    private AppointmentViewModel appointmentViewModel;
    private Toolbar toolbar;
    private AppCompatEditText etTitle;
    private Button btnAdd;
    private String title;
    private String datetime;
    private Calendar date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        appointmentViewModel = new ViewModelProvider(this).get(AppointmentViewModel.class);

        toolbar = findViewById(R.id.toolBar);
        etTitle = findViewById(R.id.etTitle);
        btnAdd = findViewById(R.id.btnAdd);

        title = getIntent().getStringExtra(Constants.EXTRA_DATA_TITLE);
        Object date2 = getIntent().getSerializableExtra(Constants.EXTRA_DATE_AND_TIME);

        if (title != null && !title.trim().isEmpty() && date2 instanceof Date) {
            date = Calendar.getInstance();
            date.setTime((Date) date2);
            AppointmentDataModel model = new AppointmentDataModel(
                    title,
                    date.get(Calendar.MONTH),
                    date.get(Calendar.DATE),
                    date.get(Calendar.YEAR),
                    date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE)
            );
            saveInDb(model, date);
        }

        btnAdd.setOnClickListener(v -> {
            if (etTitle.getText().toString().trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please Enter all the details", Toast.LENGTH_SHORT).show();
            } else {
                pickDate();
            }
        });
    }

    private void pickDate() {
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            view.setMinDate(System.currentTimeMillis() - 1000);
            date.set(year, month, dayOfMonth);
            new TimePickerDialog(AppointmentActivity.this, (view1, hourOfDay, minute) -> {
                view1.setIs24HourView(true);
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(date.getTimeInMillis());

                datetime = Utility.getTodayTommorowOrDate(calendar.getTime());
                title = etTitle.getText().toString();

                saveInDb(new AppointmentDataModel(title,
                        date.get(Calendar.MONTH),
                        date.get(Calendar.DATE),
                        date.get(Calendar.YEAR),
                        date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE)
                ), date);
                etTitle.setText("");

            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }


    void saveInDb(AppointmentDataModel dataModel, Calendar date) {
        appointmentViewModel
                .addAppointment(dataModel);
        String todayTommorowOrDate = Utility.getTodayTommorowOrDate(date.getTime());
        long timeInMillis = date.getTimeInMillis();
        setAppointments(timeInMillis, todayTommorowOrDate);
        Utility.makeToast("Appointment is set for "
                + todayTommorowOrDate, this);
        showDialog(dataModel.getTitle(), todayTommorowOrDate);
    }

    void showDialog(String title, String date) {
        new AlertDialog.Builder(this)
                .setTitle("Appointment set")
                .setCancelable(true)
                .setMessage(" " + title + "\nDate time - " + date)
                .setPositiveButton("View All Appointments", (d, w) -> openAllAppointments())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appointment_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_show_all_appointment) {
            openAllAppointments();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAppointments(long dateTimeInMillis, String todayTom) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(Constants.EXTRA_DATA_TITLE, title);
        intent.putExtra(Constants.EXTRA_DATE_AND_TIME, todayTom);
        intent.setAction(Constants.APPOINTMENT_ACTION);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            Log.d(TAG, "setAppointments: setting alrm");

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    dateTimeInMillis, // trigger 2 minute before
                    pendingIntent);
        }
    }


    void openAllAppointments() {
        Intent intent = new Intent(AppointmentActivity.this, RemindersActivity.class);
        startActivity(intent);
    }
}