package com.usersdata;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<String> User = new ArrayList<>();
    private List<String> Place = new ArrayList<>();
    private List<String> Age =new ArrayList<>();
    private List<String> ProfilePic =new ArrayList<>();
    private List<String> Key =new ArrayList<>();
    DatabaseReference dref = FirebaseDatabase.getInstance().getReference();

    RecyclerViewAdapter(List<String> User, List<String> Place, List<String> Age, List<String> ProfilePic, List<String> Key){
        this.User = User;
        this.Place = Place;
        this.Age = Age;
        this.ProfilePic = ProfilePic;
        this.Key = Key;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row, parent, false));
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Building AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
        builder.setMessage("Do you want to delete profile?").setCancelable(true).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dref.child("Users").child(Key.get(position)).removeValue();
                dialog.dismiss();
                notifyDataSetChanged();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        holder.Name.setText(this.User.get(position));
        holder.Place.setText(this.Place.get(position));
        holder.Age.setText(Age.get(position));
        Glide.with(holder.itemView.getContext()).load(this.ProfilePic.get(position)).into(holder.Profile);
        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.User.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView Name, Place, Age;
        ImageView Profile;
        ImageButton Delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.Name = itemView.findViewById(R.id.Name);
            this.Place = itemView.findViewById(R.id.place);
            this.Age = itemView.findViewById(R.id.age);
            this.Profile = itemView.findViewById(R.id.profileImage);
            this.Delete = itemView.findViewById(R.id.delete);
        }
    }
}
