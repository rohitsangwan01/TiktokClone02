package com.pvaindia.tiktokclone0;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    LinearLayout phone,google_signin;
    private static final int RC_SIGN_IN = 1;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        phone=findViewById(R.id.phone);
        google_signin=findViewById(R.id.google_signin);
        mAuth=FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);

        phone.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(),PhoneActvity.class)));

        google_signin.setOnClickListener(v->{
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

       
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("Google signin", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("PVA")
                        .child("Users")
                        .orderByChild("auth")
                        .equalTo(account.getEmail())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){

                                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                        SharedPreferences sp=getApplicationContext().getSharedPreferences("pref",0);
                                        SharedPreferences.Editor editor=sp.edit();

                                        editor.putString("login","true");
                                        editor.putString("uname",dataSnapshot.child("uname").getValue(String.class));
                                        editor.putString("id",dataSnapshot.child("id").getValue(String.class));
                                        editor.apply();
                                        editor.commit();
                                        startActivity(new Intent(
                                                        getApplicationContext(), MainActivity.class)
                                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                                Intent.FLAG_ACTIVITY_CLEAR_TASK|
                                                                Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    }

                                }else {
                                    startActivity(new Intent(getApplicationContext(),PersonalInfoActvity.class)
                                            .putExtra("auth",account.getEmail()));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            } catch (ApiException e) {
                Log.w("Google signin", "Google sign in failed", e);
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Login Success",Toast.LENGTH_LONG).show();

                            Log.d("Google sign", "signInWithCredential:success");
                        } else {
                            Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_LONG).show();
                            Log.w("Google sign", "signInWithCredential:failure", task.getException());
                        }

                    }
                });
    }

    public void onClickTest(View view) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickPrivacyPolicy(View view) {
        Intent webIntent = new Intent(LoginActivity.this, WebViewActivity.class);
        webIntent.putExtra("url", "http://www.pvaindia.com");
        startActivity(webIntent);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}