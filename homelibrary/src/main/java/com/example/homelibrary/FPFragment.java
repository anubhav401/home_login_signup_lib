package com.example.homelibrary;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by ANUBHAV on 27/04/2018.
 */

public class FPFragment extends Fragment {

    EditText UserName;
    TextView error;
    Button Continuebutton;
    private FirebaseAuth Auth;
    private ProgressDialog progressDialog;
    private String email;

    public FPFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view=inflater.inflate(R.layout.forgotpasspage,container,false);
        UserName = view.findViewById(R.id.UserName);
        Continuebutton = view.findViewById(R.id.Send_Link);
        error=view.findViewById(R.id.usernametext);
        Continuebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final String roll_num=UserName.getText().toString().trim();
                if(checkforgetentry()){
                    progressDialog= ProgressDialog.show(getActivity(),"","Loading...");
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            try{

                                if(dataSnapshot.child("Users").hasChild(roll_num)!=false) {
                                    email = dataSnapshot.child("Users").child(roll_num).child("Email").getValue().toString();
                                }

                                Auth.sendPasswordResetEmail(email)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                   @Override
                                                                   public void onComplete(@NonNull Task<Void> task) {

                                                                       if(task.isSuccessful()){
                                                                           Snackbar.make(view,"Link send to email. Check your email for further help!",Snackbar.LENGTH_INDEFINITE).show();
                                                                           progressDialog.hide();

                                                                       }else{
                                                                           progressDialog.hide();
                                                                           Snackbar.make(view,task.getException().getMessage(),Snackbar.LENGTH_INDEFINITE).show();
                                                                       }

                                                                   }

                                                                   // ...
                                                               }
                                        );

                            }catch (Exception e){
                                progressDialog.hide();
                                Snackbar.make(view,"User don't Exist",Snackbar.LENGTH_LONG).show();
                                UserName.setText("");

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            progressDialog.hide();
                        }
                    });

                }

            }
        });


        return view;
    }

    boolean checkforgetentry() {
        error.setText("");
        if (UserName.getText().toString().trim().length()>5) {
            if(UserName.getText().toString().trim().length() < 15) {
                return true;
            }else
                error.setText("Username must have at Max 15 characters");
        }else
            error.setText("Username must have at Least 5 characters");

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(getFragmentManager().getBackStackEntryCount()>0){
                    if(i==KeyEvent.KEYCODE_BACK && keyEvent.getAction()==KeyEvent.ACTION_UP)
                    {
                        getFragmentManager().popBackStackImmediate();
                        return true;
                    }
                    return true;
                }
                return false;
            }
        });

    }

}
