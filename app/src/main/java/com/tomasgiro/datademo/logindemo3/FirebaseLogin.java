package com.tomasgiro.datademo.logindemo3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FirebaseLogin extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_firebase_login);


        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build());

        startActivityForResult(
                AuthUI.getInstance()

                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore.getInstance().collection("userLogs").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).set(
                        new HashMap<String, Object>(){{put(String.valueOf(new Date(System.currentTimeMillis())),"picture: "+user.getPhotoUrl()+" name: "+user.getDisplayName()+" phone: "+user.getPhoneNumber()+" android version: "+android.os.Build.VERSION.SDK_INT);}}, SetOptions.merge()
                );
                Intent intent= new Intent(this,WhyUsage.class);
                startActivity(intent);
                // ...
            } else {
                // Sign in failed, check response for error code
                // ...
                finish();
            }
        }
    }
}
