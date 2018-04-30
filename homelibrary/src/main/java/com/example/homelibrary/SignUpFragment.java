package com.example.homelibrary;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;



public class SignUpFragment extends Fragment {

        EditText email_edit,rollno_edit, password_edit , retypePassword_edit;
        Button sign_up_button;
        TextView text_input_layout1,text_input_layout2,text_input_layout3,text_input_layout4;
        TextView signin;
        String email, password , roll_no;
        private FirebaseAuth Auth;
        private FirebaseAuth.AuthStateListener AuthListener;
        private afterSignUpListener activity_listener;
        DatabaseReference data1;
        View view;
        ProgressDialog progressDialog;
        FragmentChangeListener fcl;
        private int checker=0;

        public SignUpFragment() {
                // Required empty public constructor
        }

        public interface afterSignUpListener{
                void afterSignUp(String RollNumber);
        }

        @Override
        public void onAttach(Activity activity) {
                super.onAttach(activity);
                activity_listener=(SignUpFragment.afterSignUpListener)activity;
                fcl=(FragmentChangeListener) activity;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
                view=inflater.inflate(R.layout.fragment_sign_up, container, false);



                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Please Wait...");

                email_edit = view.findViewById(R.id.email);
                rollno_edit = view.findViewById(R.id.roll_no);
                password_edit =  view.findViewById(R.id.input_password);
                retypePassword_edit = view.findViewById(R.id.re_password);
                sign_up_button =  view.findViewById(R.id.signup);
                rollno_edit.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                signin=view.findViewById(R.id.create_account);
                text_input_layout1=view.findViewById(R.id.usertext);
                text_input_layout2=view.findViewById(R.id.emailtext);
                text_input_layout3=view.findViewById(R.id.passtext1);
                text_input_layout4=view.findViewById(R.id.passtext2);

                signin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fcl.changeFragment(new LoginFragment());
                    }
                });

                Auth = FirebaseAuth.getInstance();
                AuthListener = new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                                final FirebaseUser user = firebaseAuth.getCurrentUser();


                                sign_up_button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view1) {

                                                email = email_edit.getText().toString().trim();
                                                roll_no = rollno_edit.getText().toString().trim();
                                                password = password_edit.getText().toString().trim();

                                                if (checkValidEntries()) {

                                                        progressDialog.show();

                                                        Auth.createUserWithEmailAndPassword(email, password)
                                                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                                                if (!task.isSuccessful()) {
                                                                                        progressDialog.hide();
                                                                                    Snackbar.make(view,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                                                                                } else {

                                                                                        if(firebaseAuth.getCurrentUser()!=null){

                                                                                                FirebaseUser user = firebaseAuth.getCurrentUser();

                                                                                                // Giving the user welcome reward

                                                                                                DatabaseReference database;

                                                                                                //Inserting user email and Roll number in Users

                                                                                                database = FirebaseDatabase.getInstance().getReference().child("Users");
                                                                                                database.child(roll_no).child("Roll Number").setValue(roll_no);
                                                                                                database.child(roll_no).child("Email").setValue(email);
                                                                                                database.child(roll_no).child("Uid").setValue(user.getUid());

                                                                                                progressDialog.hide();

                                                                                        }
                                                                                        activity_listener.afterSignUp(roll_no);
                                                                                    Snackbar.make(view,"Signed Up Successfully",Snackbar.LENGTH_LONG).show();
                                                                                }
                                                                        }
                                                                });
                                                }
                                        }
                                });
                        }
                };

                return view;
        }

        boolean checkValidEntries() {

            final Boolean value;
            text_input_layout1.setText("");
            text_input_layout2.setText("");
            text_input_layout3.setText("");
            text_input_layout4.setText("");


                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    checker++;
                }
                else
                    text_input_layout2.setText("Email Not Valid");
                rollno_edit.setText(rollno_edit.getText().toString().trim());

                if(rollno_edit.getText().toString().trim().length()>=5){

                    if(rollno_edit.getText().toString().trim().length()<=15) {
                        final DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("Users");
                        data.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(roll_no)) {
                                    text_input_layout1.setText("User already exist!");
                                    data.removeEventListener(this);
                                } else {
                                    checker++;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }else{

                        text_input_layout1.setText("Username must have at max 15 characters");
                    }

                }else{
                    text_input_layout1.setText("Username must have atleast 5 characters digits");
                    }
                if (password_edit.getText().toString().length() >= 6) {

                    if(password_edit.getText().toString().equals(retypePassword_edit.getText().toString())){

                        if(checker==2) {
                            return true;
                        }
                    }else{

                        text_input_layout4.setText("Passwords Do not Match");
                    }


                }
                else {
                    text_input_layout3.setText("Password is too short");
                    text_input_layout4.setText("Passwords Do not Match");
                }
           checker=0;


                return false;
        }

        @Override
        public void onStart() {
                super.onStart();
                Auth.addAuthStateListener(AuthListener);
        }

        @Override
        public void onStop() {
                super.onStop();
                if (AuthListener != null) {
                        Auth.removeAuthStateListener(AuthListener);
                }
        }

        public Long getTimeStamp(){

                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                return -tsLong;
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
