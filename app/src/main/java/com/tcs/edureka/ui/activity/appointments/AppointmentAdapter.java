package com.tcs.edureka.ui.activity.appointments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tcs.edureka.R;
import com.tcs.edureka.model.AppointmentDataModel;

import java.util.ArrayList;

/**
 * @author Suraj
 */
public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.MyViewHolder> {

    private ArrayList<AppointmentDataModel> dataModel;
    private Context context;

    public AppointmentAdapter(ArrayList<AppointmentDataModel> dataModel, Context context) {
        this.dataModel = dataModel;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminders_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvTitle.setText(dataModel.get(position).getTitle());
        holder.tvDescription.setText(dataModel.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvDescription;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }

}
