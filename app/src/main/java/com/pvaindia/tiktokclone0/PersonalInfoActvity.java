package com.pvaindia.tiktokclone0;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aigestudio.wheelpicker.widgets.WheelDatePicker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
//import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PersonalInfoActvity extends AppCompatActivity {

//    SingleDateAndTimePicker singleDateAndTimePicker;
    WheelDatePicker datePicker;
    ImageView male,female,img;
    String gender="male",dob,image,id;
    EditText uname;
    TextView reg;
    Uri uri=null;
    SharedPreferences sp;
    private StorageReference mStorageRef;
    ProgressDialog progressDialog;
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        getSupportActionBar().hide();
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://tiktokclone-0.appspot.com/ProfilePicture/");

        sp = getSharedPreferences("pref", 0);

//        singleDateAndTimePicker=findViewById(R.id.date);
        datePicker = findViewById(R.id.date_picker);
        male=findViewById(R.id.male_option);
        female=findViewById(R.id.female_option);
        uname=findViewById(R.id.name);
        reg=findViewById(R.id.reg);
        img=findViewById(R.id.img);
        progressDialog=new ProgressDialog(PersonalInfoActvity.this);

        FirebaseApp.initializeApp(getApplicationContext());

//        singleDateAndTimePicker.setDayFormatter(new SimpleDateFormat("yyyy-MM-dd"));
//        singleDateAndTimePicker.setDisplayHours(false);
//        singleDateAndTimePicker.setDisplayMinutes(false);
//        singleDateAndTimePicker.setDisplayDays(true);
//        singleDateAndTimePicker.addOnDateChangedListener((displayed, date) -> {
//            setDOB(date);
//        });


        datePicker = findViewById(R.id.date_picker);
        datePicker.setCyclic(true);
        datePicker.setCurved(true);
        datePicker.setVisibleItemCount(4);
        datePicker.setSelectedYear(2020);
        datePicker.setIndicator(true);
        datePicker.setIndicatorColor(R.color.colorAccent);
        datePicker.setSelectedItemTextColor(R.color.colorPrimaryDark);
        datePicker.setAtmospheric(true);
        datePicker.setItemTextSize(60);
        datePicker.setYearFrame(1900, Calendar.getInstance().get(Calendar.YEAR) - 1);

        datePicker.setOnDateSelectedListener(new WheelDatePicker.OnDateSelectedListener() {
            @Override
            public void onDateSelected(WheelDatePicker picker, Date date) {
                Log.i("test", date.toString());
                Log.i("test", String.valueOf(picker.getCurrentYear()));
                Log.i("test", String.valueOf(picker.getCurrentMonth()));
                Log.i("test", String.valueOf(picker.getCurrentDay()));


                setDOB(date);
            }
        });


        male.setOnClickListener(v->{
            gender="male";
            female.setAlpha(0.3f);
            male.setAlpha(1f);
        });
        female.setOnClickListener(v->{
            gender="female";
            male.setAlpha(0.3f);
            female.setAlpha(1f);
        });

        reg.setOnClickListener(v->{
            regUser();
        });

        img.setOnClickListener(v->{
            Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent,"Pick Image"),111);
        });
    }



    private void setDOB(Date date) {
        String[] d =date.toString().split(" ");
        dob=d[5]+"/"+d[1]+"/"+d[2];
    }


    private void regUser() {
        if (uname.getText().toString().isEmpty())
            return;
        if (dob==null)
            return;


        progressDialog.setMessage("Registering");
        progressDialog.setCancelable(false);
        progressDialog.show();

        id= UUID.randomUUID().toString().replaceAll("-","");

        if (uri==null){
            image="null";
            RegToDb();
        }else {
            image="https://firebasestorage.googleapis.com/v0/b/tiktokclone-0.appspot.com/o/ProfilePicture%2F"+id+"?alt=media";
            StorageReference storage=mStorageRef.child(id);

            storage.putFile(uri).addOnCompleteListener(task -> RegToDb());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==111&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            uri=data.getData();
            Glide.with(getApplicationContext())
                    .load(uri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(img);
        }
    }

    public void RegToDb(){
        progressDialog.setMessage("Setting Up...");
        Map<String, String> map=new HashMap<>();
        map.put("uname",uname.getText().toString());
        map.put("gender",gender);
        map.put("dob",dob);
        map.put("id",id);
        map.put("image",image);
        map.put("followersCount","0");
        map.put("followingsCount","0");
        map.put("likes","0");
        map.put("followers"," ");
        map.put("followings"," ");
        map.put("auth",getIntent().getStringExtra("auth"));
        map.put("categories", sp.getString("categories", ""));
        map.put("inbox", "");

        FirebaseDatabase.getInstance()
                .getReference()
                .child("PVA")
                .child("Users")
                .push()
                .setValue(map).addOnCompleteListener(task -> {
            SharedPreferences sp=getApplicationContext().getSharedPreferences("pref",0);
            SharedPreferences.Editor editor=sp.edit();
            editor.putString("login","true");
            editor.putString("uname",uname.getText().toString());
            editor.putString("id",id);
            editor.apply();
            editor.commit();
            progressDialog.dismiss();
            startActivity(new Intent(getApplicationContext(), MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK));
        });
    }
}