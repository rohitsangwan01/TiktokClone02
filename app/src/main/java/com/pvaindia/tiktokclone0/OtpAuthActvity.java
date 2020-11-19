package com.pvaindia.tiktokclone0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OtpTextView;

public class OtpAuthActvity extends AppCompatActivity {

    ImageView verify;
    OtpTextView otpTextView;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    TextView notice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_auth);
        getSupportActionBar().hide();

        verify=findViewById(R.id.verify);
        otpTextView=findViewById(R.id.otp_view);
        notice=findViewById(R.id.notice);

        mAuth= FirebaseAuth.getInstance();

        notice.setText(getResources().getString(R.string.otp_notice)+" "+getIntent().getStringExtra("phone"));
        verify();
        verify.setOnClickListener(v -> {
            if (otpTextView.getOTP().isEmpty()){

                return;
            }
            verifyVerificationCode(otpTextView.getOTP());
        });
    }
    public void verify(){
        Intent intent=getIntent();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+intent.getStringExtra("phone"),
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }
    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            Log.d("code",code+""+phoneAuthCredential);
            if (code != null) {
                otpTextView.setOTP(code);
                verifyVerificationCode(code);
            }else {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.d("message",e.getMessage()+"");
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId=s;
        }
    };

    private void verifyVerificationCode(String code) {
        try {
            InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(OtpAuthActvity.this.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

            signInWithPhoneAuthCredential(credential);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        ProgressDialog progressDialog=new ProgressDialog(OtpAuthActvity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Verifying...");
        progressDialog.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OtpAuthActvity.this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                final Intent intent=getIntent();


                                FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("PVA")
                                        .child("Users")
                                        .orderByChild("auth")
                                        .equalTo(intent.getStringExtra("phone"))
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                                        SharedPreferences sp=getApplicationContext().getSharedPreferences("pref",0);
                                                        SharedPreferences.Editor editor=sp.edit();
                                                        progressDialog.dismiss();
                                                        editor.putString("login","true");
                                                        editor.putString("uname",dataSnapshot.child("uname").getValue(String.class));
                                                        editor.putString("id",dataSnapshot.child("id").getValue(String.class));
                                                        editor.apply();
                                                        editor.commit();
                                                        startActivity(new Intent(
                                                                        getApplicationContext(),
                                                                        MainActivity.class)
                                                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                                                Intent.FLAG_ACTIVITY_CLEAR_TASK|
                                                                                Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                        );
                                                    }
                                                }else {
                                                    progressDialog.dismiss();
                                                    startActivity(new Intent(getApplicationContext(),PersonalInfoActvity.class)
                                                            .putExtra("auth",intent.getStringExtra("phone")));
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                            }
                        });


                    } else {
                        progressDialog.dismiss();
                        Log.d("errorhere",task.getException().toString()+" "+task.toString());
                        Toast.makeText(getApplicationContext(),"Verification failed",Toast.LENGTH_LONG).show();
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                            otpTextView.resetState();
                        }


                    }
                });
    }
}