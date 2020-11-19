package com.pvaindia.tiktokclone0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class PhoneActvity extends AppCompatActivity {

    EditText phone;
    TextView next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_actvity);
        getSupportActionBar().hide();
        phone=findViewById(R.id.phone);
        next=findViewById(R.id.next);

        next.setOnClickListener(v->{
            if (phone.getText().toString().isEmpty()||phone.getText().toString().length()<10){
                phone.setError("Invalid Phone");
                return;
            }

            startActivity(new Intent(getApplicationContext(),OtpAuthActvity.class)
            .putExtra("phone",phone.getText().toString()));
        });

    }
}