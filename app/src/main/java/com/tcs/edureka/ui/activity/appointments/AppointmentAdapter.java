package com.tcs.edureka.ui.activity.appointments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tcs.edureka.R;
import com.tcs.edureka.model.AppointmentDataModel;
import com.tcs.edureka.utility.Utility;

import java.util.Calendar;
import java.util.List;

/**
 * @author Suraj
 */
public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.MyViewHolder> {

    private List<AppointmentDataModel> dataModel;

    public void setDataModel(List<AppointmentDataModel> dataModel) {
        this.dataModel = dataModel;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminders_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int date = dataModel.get(position).getDate();
        int month = dataModel.get(position).getMonth();
        int year = dataModel.get(position).getYear();
        int hour = dataModel.get(position).getHour();
        int minute = dataModel.get(position).getMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, hour, minute);
        String todayTommorowOrDate = Utility.getTodayTommorowOrDate(calendar.getTime());
        holder.tvWhen.setText(todayTommorowOrDate);

        holder.tvTitle.setText(dataModel.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        return dataModel == null ? 0 : dataModel.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvWhen;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvWhen = itemView.findViewById(R.id.tvWhen);
        }
    }

}
