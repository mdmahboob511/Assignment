package com.usersdata;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.internal.ContextUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.zip.Inflater;

import static android.app.Activity.RESULT_OK;

public class EnrollFragment extends Fragment {
    DatabaseReference dref;
    StorageReference sref;
    TextInputEditText FirstName,LastName,DOB,Gender,Country,State,City,Phone,Telephone;
    String firstName,lastName,dob,gender,country,state,city,phone,telephone;
    MaterialButton AddImage,AddUser;
    ImageView imageView,GIF;
    Uri resultUri, downloadUri;
    ProgressDialog progressDialog;
    DatePickerDialog datePickerDialog;
    public EnrollFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_enroll, container, false);
        //Initiating Values
        dref = FirebaseDatabase.getInstance().getReference();
        sref = FirebaseStorage.getInstance().getReference();
        AddImage = view.findViewById(R.id.addImage);
        GIF = view.findViewById(R.id.imageView2);
        imageView = view.findViewById(R.id.imageView);
        AddUser = view.findViewById(R.id.addUser);
        progressDialog = new ProgressDialog(getContext());
        datePickerDialog = new DatePickerDialog(getContext());
        Glide.with(view.getContext()).load(R.raw.avatar).into(GIF);
        String[] options = getResources().getStringArray(R.array.items);
        DOB = view.findViewById(R.id.dateBirth);
        Gender = view.findViewById(R.id.gender);

        //Creating Dialog for Gender Selection
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(getContext());
        materialAlertDialogBuilder.setTitle("Select Gender").setItems(options, new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Gender.setText("Male");
                        Gender.clearFocus();
                        break;
                    case 1:
                        Gender.setText("Female");
                        Gender.clearFocus();
                        break;
                    case 2:
                        Gender.setText("Other");
                        Gender.clearFocus();
                        break;
                    default:
                        Gender.clearFocus();
                }
            }
        });
        Gender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    materialAlertDialogBuilder.show();
                }
            }
        });

        //Attching Calender Dialog to DateofBirth
        DOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    datePickerDialog.show();
                    datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            if (year > 2001){
                                Toast.makeText(getContext(),"Age Must be above 18", Toast.LENGTH_SHORT).show();
                            }
                            month = month + 1;
                            if (month<=9 || dayOfMonth<=9){
                                if (month<=9 && dayOfMonth<=9){
                                    String Month = "0" + String.valueOf(month);
                                    String Day = "0" + String.valueOf(dayOfMonth);
                                    dob = Day+Month+String.valueOf(year);
                                }
                                else if (month<=9){
                                    String Month = "0" + String.valueOf(month);
                                    dob = dayOfMonth+Month+String.valueOf(year);
                                }
                                else if (dayOfMonth<=9){
                                    String Day = "0" + String.valueOf(dayOfMonth);
                                    dob = Day+month+String.valueOf(year);
                                }
                            }
                            DOB.setText(String.valueOf(dayOfMonth)+"|"+String.valueOf(month)+"|"+String.valueOf(year));
                            DOB.clearFocus();
                        }
                    });
                    datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            DOB.clearFocus();
                        }
                    });
                }
            }
        });

        //Adding onClickListener to Image
        AddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setAspectRatio(1, 1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setFixAspectRatio(true)
                        .start(getContext(),EnrollFragment.this);
            }
        });

        //Adding onClickListener to Create profile
        AddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirstName = view.findViewById(R.id.firstName);
                LastName = view.findViewById(R.id.lastName);
                Gender = view.findViewById(R.id.gender);
                Country = view.findViewById(R.id.country);
                State = view.findViewById(R.id.state);
                City = view.findViewById(R.id.city);
                Phone = view.findViewById(R.id.phone);
                Telephone = view.findViewById(R.id.telephone);
                progressDialog.setMessage("Uploading profile picture...");
                progressDialog.show();
                initialize();
                storage();
            }
        });
        return view;
    }

    //Method for Uploading Image & Creating Details
    public void storage() {
        if (firstName.isEmpty() || lastName.isEmpty() || country.isEmpty() || state.isEmpty() || city.isEmpty() || phone.isEmpty() || telephone.isEmpty() || gender.isEmpty() || dob.isEmpty() || resultUri == null) {
            Toast.makeText(getContext(), "Please fill all details and Image", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
        else{
            StorageReference storageReference = sref;
            dref = FirebaseDatabase.getInstance().getReference();
            String key = dref.child("Users").push().getKey();
            storageReference.child("Pictures/" + key).putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (task.isSuccessful()) {
                        StorageReference storageReference = sref;
                        return storageReference.child("Pictures/" + key).getDownloadUrl();
                    }
                    throw task.getException();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                public void onComplete(Task<Uri> task) {
                    if (task.isSuccessful()) {
                        downloadUri = task.getResult();
                        UploadDetails();
                        progressDialog.setMessage("Creating User Profile...");
                        return;
                    }
                    Context applicationContext = getContext();
                    Toast.makeText(applicationContext, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

                private void UploadDetails() {
                    HashMap hashMap = new HashMap();
                    hashMap.put("Name", firstName +" "+ lastName);
                    hashMap.put("State", state);
                    hashMap.put("Age", dob);
                    hashMap.put("Url", String.valueOf(downloadUri));
                    hashMap.put("Country", country);
                    hashMap.put("City", city);
                    hashMap.put("Phone", phone);
                    hashMap.put("Telephone", telephone);
                    hashMap.put("Gender", gender);
                    hashMap.put("Key", key);
                    hashMap.put("Time", ServerValue.TIMESTAMP);
                    dref.child("Users").child(key).setValue(hashMap);
                    clearText();
                }
            });
        }

    }
        //Assigning Edittext values to Strings
        public void initialize(){
            firstName = FirstName.getText().toString().trim();
            lastName= LastName.getText().toString().trim();
            gender = Gender.getText().toString();
            country = Country.getText().toString().trim();
            city = City.getText().toString().trim();
            phone = Phone.getText().toString().trim();
            telephone = Telephone.getText().toString().trim();
            state = State.getText().toString().trim();
        }

        //Clearing Edittext on Upload Complete
        public void clearText(){
            FirstName.setText("");
            LastName.setText("");
            DOB.setText("");
            Gender.setText("");
            Country.setText("");
            City.setText("");
            Phone.setText("");
            Telephone.setText("");
            State.setText("");
            resultUri = null;
            GIF.setVisibility(View.VISIBLE);
            progressDialog.dismiss();
        }

        //ActivityResult for Selecting Image
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Uri uri = result.getUri();
                resultUri = uri;
                imageView.setImageURI(uri);
                GIF.setVisibility(View.GONE);
            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
                Toast.makeText(getContext(), ""+error, Toast.LENGTH_SHORT).show();
            }
        }
    }
    }