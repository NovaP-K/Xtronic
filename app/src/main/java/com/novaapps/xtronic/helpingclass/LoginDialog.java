package com.novaapps.xtronic.helpingclass;



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.novaapps.xtronic.HomeScreen;
import com.novaapps.xtronic.R;

import java.util.Objects;



public class LoginDialog extends DialogFragment {

    private static final String TAG = "LoginDialog";
    private static final int RC_SIGN_IN = 9001;
    private static final String QuizCreation = "QuizCreated";
    private static final String QuizJoin = "QuizJoined";

    //XmlWidget
    private SignInButton GoogleSignInButton;

    //Objects
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference RefToUser;
    private View progressView ;
    CustomProgress customProgress ;


    //var
    Context context;

    public LoginDialog(Context ct , View view) {
        context = ct ;
        progressView = view ;
        customProgress = new CustomProgress(context , view);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_dialog , container , false);
        XmlHookUp(view);
        setCancelable(true);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context , gso );
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInWithGoogle();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void XmlHookUp(View inflatedView){
        GoogleSignInButton = inflatedView.findViewById(R.id.google_sign_in_button);
    }

    private void SignInWithGoogle(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //ProgressBar StartIt
        customProgress.startProgressBar();
        dismiss();
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(context, "Failed to SignIn", Toast.LENGTH_SHORT).show();
                // [START_EXCLUDE]
                updateUI(null);
                //ProgressBar StopIt
                customProgress.stopProgressBar();
                // [END_EXCLUDE]
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(context, "Unable to connect to Server", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        // [START_EXCLUDE]
                        //ProgressBar StopIt
                        customProgress.stopProgressBar();
                        // [END_EXCLUDE]
                    }
                });
     }

     private void updateUI(FirebaseUser user){
        if(user != null) {

            final UI_Data ui_data = new UI_Data(context);
            ui_data.updateUI(true, user.getDisplayName(), user.getEmail(), Objects.requireNonNull(user.getPhotoUrl()).toString() , user.getUid());

            Intent intent = new Intent("com.novaapps.xtronic");
            intent.putExtra("Status","UserLoggedIn");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            mDatabase = FirebaseDatabase.getInstance();
            RefToUser = mDatabase.getReference("User").child(user.getUid());

            RefToUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()){
                        NewUserUpdateDatabase(ui_data);
                    }
                    else
                        RefToUser.child("_UserProfile").setValue(ui_data.get_UserProfile());  // Update Profile Url
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(context.toString() , error.toException().toString());
                }
            });



        }
    }

    private void NewUserUpdateDatabase(UI_Data data){
        RefToUser.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //ProgressBar StopIt
                customProgress.stopProgressBar();
            }
        });
        AddCountOnCreation(RefToUser);
        AddCountOnJoin(RefToUser);
    }

    private void AddCountOnCreation(DatabaseReference ref){
        ref.child(QuizCreation).child("Count").setValue(0);
    }

    private void AddCountOnJoin(DatabaseReference ref){
        ref.child(QuizJoin).child("Count").setValue(0);
    }

}
