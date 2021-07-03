package com.tcs.edureka.ui.activity.contacts;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.tcs.edureka.R;
import com.tcs.edureka.model.ContactModel;

import java.util.HashMap;
import java.util.Map;

public class RecyclerViewAdapter extends FirebaseRecyclerAdapter<ContactModel, RecyclerViewAdapter.ViewHolder> {
    public RecyclerViewAdapter(@NonNull FirebaseRecyclerOptions<ContactModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ContactModel model) {
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getRandomColor();

        TextDrawable drawable = TextDrawable.builder().beginConfig().width(100).height(100).endConfig()
                .buildRound(model.getName().substring(0, 1), color);

        holder.contactImg.setImageDrawable(drawable);
        holder.name.setText(model.getName());
        holder.emailId.setText(model.getEmailId());
        holder.personalNo.setText(model.getPersonalNo());
        holder.ofcNo.setText(model.getOfcNo());

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.name.getContext())
                        .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.editcontact))
                        .setExpanded(true, 1500)
                        .create();

                View myview = dialogPlus.getHolderView();
                final EditText editName = myview.findViewById(R.id.editName);
                final EditText editEmail = myview.findViewById(R.id.editEmail);
                final EditText editPersonalNo = myview.findViewById(R.id.editPersonalNo);
                final EditText editOfcNo = myview.findViewById(R.id.editOfcNo);
                Button update = myview.findViewById(R.id.editButton);

                editName.setText(model.getName());
                editEmail.setText(model.getEmailId());
                editPersonalNo.setText(model.getPersonalNo());
                editOfcNo.setText(model.getOfcNo());

                dialogPlus.show();

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", editName.getText().toString());
                        map.put("emailId", editEmail.getText().toString());
                        map.put("personalNo", editPersonalNo.getText().toString());
                        map.put("ofcNo", editOfcNo.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("contacts").child(getRef(position).getKey()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialogPlus.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialogPlus.dismiss();
                                    }
                                });

                    }
                });
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.name.getContext());
                builder.setTitle("Delete Panel..");
                builder.setMessage("Delete... ?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("contacts")
                                .child(getRef(position).getKey()).removeValue();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contactrow, parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView contactImg;
        TextView name, emailId, personalNo, ofcNo;
        Button editButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactImg = (ImageView) itemView.findViewById(R.id.contactImg);

            name = (TextView) itemView.findViewById(R.id.name);
            emailId = (TextView) itemView.findViewById(R.id.emailId);
            personalNo = (TextView) itemView.findViewById(R.id.personalNo);
            ofcNo = (TextView) itemView.findViewById(R.id.ofcNo);

            editButton = (Button) itemView.findViewById(R.id.editButton);
            deleteButton = (Button) itemView.findViewById(R.id.deleteButton);
        }
    }
}
