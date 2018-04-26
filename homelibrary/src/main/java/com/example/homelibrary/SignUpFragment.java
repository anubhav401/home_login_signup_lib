package com.example.homelibrary;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
        String email, password , roll_no  ;
        private FirebaseAuth Auth;
        private FirebaseAuth.AuthStateListener AuthListener;
        private afterSignUpListener activity_listener;
        DatabaseReference data1;
        View view;
        ProgressDialog progressDialog;
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
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
                view=inflater.inflate(R.layout.fragment_sign_up, container, false);



                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Please Wait...");

                email_edit = (EditText) view.findViewById(R.id.email);
                rollno_edit = view.findViewById(R.id.roll_no);
                password_edit = (EditText) view.findViewById(R.id.input_password);
                retypePassword_edit = view.findViewById(R.id.re_password);
                sign_up_button = (Button) view.findViewById(R.id.signup);
                rollno_edit.setFilters(new InputFilter[]{new InputFilter.AllCaps()});


                Auth = FirebaseAuth.getInstance();
                AuthListener = new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                                final FirebaseUser user = firebaseAuth.getCurrentUser();


                                sign_up_button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                                progressDialog.show();

                                                email = email_edit.getText().toString().trim();
                                                roll_no = rollno_edit.getText().toString().trim();
                                                password = password_edit.getText().toString().trim();

                                                if (checkValidEntries()) {

                                                        Auth.createUserWithEmailAndPassword(email, password)
                                                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                                                if (!task.isSuccessful()) {
                                                                                        progressDialog.hide();
                                                                                        Toast.makeText(getActivity(),  task.getException().getMessage(),
                                                                                                Toast.LENGTH_LONG).show();
                                                                                } else {

                                                                                        if(firebaseAuth.getCurrentUser()!=null){

                                                                                                FirebaseUser user = firebaseAuth.getCurrentUser();

                                                                                                // Giving the user welcome reward

                                                                                                DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("new_rewards");
                                                                                                String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());

                                                                                                Map<String, Object> map = new HashMap<String, Object>();
                                                                                                map.put("reward_date", date);
                                                                                                map.put("reward_detail", "Welcome Reward");
                                                                                                map.put("reward_points", "250");
                                                                                                map.put("time_stamp",getTimeStamp());

                                                                                                database.child(roll_no).push().setValue(map);


                                                                                                //Inserting user email in user_map_roll

                                                                                                database = FirebaseDatabase.getInstance().getReference().child("id_map_roll");
                                                                                                database.child(roll_no).child("id").setValue(email);

                                                                                                //Inserting user email and Roll number in Users

                                                                                                database = FirebaseDatabase.getInstance().getReference().child("Users");
                                                                                                database.child(roll_no).child("Roll Number").setValue(roll_no);
                                                                                                database.child(roll_no).child("Email").setValue(email);
                                                                                                database.child(roll_no).child("Referred").setValue(0);

                                                                                                progressDialog.hide();

                                                                                        }


                                                                                        activity_listener.afterSignUp(roll_no);
                                                                                        Toast.makeText(getActivity(), "Signed Up Successfully", Toast.LENGTH_SHORT).show();
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

                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    checker++;
                }
                else
                        email_edit.setError("Email Not Valid");

                if(rollno_edit.getText().toString().length()>=10){

                    final DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("Users");
                    data.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(roll_no)){
                                rollno_edit.setError("User already exist!");
                                data.removeEventListener(this);
                            }else{
                                checker++;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }else{
                              rollno_edit.setError("Roll Number must be 10 digits");
                    }
                if (password_edit.getText().toString().length() >= 6) {

                    if(password_edit.getText().toString().equals(retypePassword_edit.getText().toString())){

                        if(checker==2) {
                            return true;
                        }
                    }else{

                        retypePassword_edit.setError("Passwords Do not Match");
                    }


                }
                else
                    password_edit.setError("Password is too short");

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
