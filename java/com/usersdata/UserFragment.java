package com.usersdata;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class UserFragment extends Fragment {
    List<String>  Users= new ArrayList<>();
    List<String> Place = new ArrayList<>();
    List<String> Age =new ArrayList<>();
    List<String> ProfilePic =new ArrayList<>();
    List<String> Key =new ArrayList<>();
    RecyclerViewAdapter recyclerViewAdapter;
    RecyclerView recyclerView;
    DatabaseReference dref;
    int year = Calendar.getInstance().get(Calendar.YEAR);
    ProgressBar progressBar;
    //Constructor
    public UserFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users,container,false);
        //Initiatiating Fragment
        dref = FirebaseDatabase.getInstance().getReference();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(Users,Place,Age,ProfilePic,Key);
        recyclerView.setAdapter(recyclerViewAdapter);
        progressBar = view.findViewById(R.id.progressBar);
        GettingData();
        return view;
    }

    //Method to Retreive Data
    public void GettingData(){
        progressBar.setVisibility(View.VISIBLE);
        dref.child("Users").orderByChild("Time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Reset();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Getter getter  =  child.getValue(Getter.class);
                        Users.add(getter.getName());
                        Place.add(getter.getState());
                        Age.add(String.valueOf(year - (Integer.parseInt(getter.getAge()) % 10000)));
                        ProfilePic.add(getter.getUrl());
                        Key.add(getter.getKey());
                    }
                    recyclerViewAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), ""+databaseError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Resetting Data
    public void Reset(){
        Users.clear();
        Place.clear();
        Age.clear();
        ProfilePic.clear();
        Key.clear();
    }
}
