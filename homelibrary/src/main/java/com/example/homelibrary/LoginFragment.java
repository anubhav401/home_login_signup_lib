package com.example.homelibrary;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginFragment extends Fragment {


        EditText rollno_edit, password_edit ;
        TextView login_text;
        Button login_button,forgot_button;
        String  password , roll_no ,email ;
        private FirebaseAuth Auth;
        private FirebaseAuth.AuthStateListener AuthListener;
        private LoginFragment.afterLoginListener activity_listener;
        View view;
        ProgressDialog progressDialog;
        public static View MyView;
        RelativeLayout l1,l2;
        TextView test;


        public interface afterLoginListener{
                void afterLogin(String RollNumber);
        }

        @Override
        public void onAttach(Activity activity) {
                super.onAttach(activity);
                activity_listener=(LoginFragment.afterLoginListener)activity;
        }


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
                view=inflater.inflate(R.layout.fragment_login, container, false);
             //   MyView=inflater.inflate(R.layout.fragment_login, container, false);


                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Please Wait...");


                rollno_edit = (EditText) view.findViewById(R.id.roll_no);
                login_text= view.findViewById(R.id.textView2);
                password_edit = (EditText) view.findViewById(R.id.input_password);
                login_button = (Button) view.findViewById(R.id.Login);
                rollno_edit.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                forgot_button=view.findViewById(R.id.forgot);
                test=view.findViewById(R.id.forgotText);


           test.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rollno_edit.setText("");
                    password_edit.setVisibility(View.INVISIBLE);
                    login_button.setVisibility(View.INVISIBLE);
                    login_text.setVisibility(View.INVISIBLE);
                    test.setVisibility(View.INVISIBLE);
                    forgot_button.setVisibility(View.VISIBLE);
                }
            });

                Auth = FirebaseAuth.getInstance();
                AuthListener = new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                              //  final FirebaseUser user = firebaseAuth.getCurrentUser();

                                login_button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                                roll_no = rollno_edit.getText().toString().trim();
                                                password = password_edit.getText().toString().trim();

                                                if (checkValidEntries()) {

                                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                                         try{

                                                                                 progressDialog.show();
                                                                                 if(dataSnapshot.child("Users").hasChild(roll_no)!=false) {
                                                                                     email = dataSnapshot.child("Users").child(roll_no).child("Email").getValue().toString();

                                                                                 }

                                                                                 Auth.signInWithEmailAndPassword(email, password)
                                                                                         .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                                                                                         @Override
                                                                                                         public void onComplete(@NonNull Task<AuthResult> task) {

                                                                                                                 if(task.isSuccessful()){

                                                                                                                         activity_listener.afterLogin(roll_no);
                                                                                                                         progressDialog.hide();

                                                                                                                 }else{
                                                                                                                         progressDialog.hide();
                                                                                                                         Toast.makeText(getActivity(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                                                                                 }

                                                                                                         }

                                                                                                         // ...
                                                                                                 }
                                                                                         );

                                                                         }catch (Exception e){
                                                                                 progressDialog.hide();
                                                                                 Toast.makeText(getActivity(),"User dont exists",Toast.LENGTH_SHORT).show();

                                                                                 password_edit.setText("");
                                                                                 rollno_edit.setText("");

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

                                forgot_button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                       final String roll_num=rollno_edit.getText().toString().trim();
                                     if(checkforgetentry()){
                                         DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                         databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                             @Override
                                             public void onDataChange(DataSnapshot dataSnapshot) {
                                                 try{

                                                     progressDialog.show();
                                                     if(dataSnapshot.child("Users").hasChild(roll_num)!=false) {
                                                         email = dataSnapshot.child("Users").child(roll_num).child("Email").getValue().toString();
                                                     }else{
                                                         email = dataSnapshot.child("Teachers").child(roll_num).child("Email").getValue().toString();
                                                     }
                                                     Log.e("hello email",email);
                                                     Auth.sendPasswordResetEmail(email)
                                                             .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                         @Override
                                                                         public void onComplete(@NonNull Task<Void> task) {

                                                                             if(task.isSuccessful()){
                                                                                 Toast.makeText(getActivity(),"link sent",Toast.LENGTH_SHORT).show();
                                                                                 progressDialog.hide();

                                                                             }else{
                                                                                 progressDialog.hide();
                                                                                 Toast.makeText(getActivity(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                                             }

                                                                         }

                                                                         // ...
                                                                     }
                                                             );

                                                 }catch (Exception e){
                                                     progressDialog.hide();
                                                     Toast.makeText(getActivity(),"User dont exists",Toast.LENGTH_SHORT).show();

                                                     rollno_edit.setText("");

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
                        }
                };

                return view;
        }

       boolean checkforgetentry() {
            if (rollno_edit.getText().toString().length() < 10) {
                rollno_edit.setError("Roll Number must be 10 digits");
                return false;

            }
            return true;
        }



        boolean checkValidEntries() {


                        if(rollno_edit.getText().toString().length()>=10){

                                if (password_edit.getText().toString().length() >= 6) {

                                                 return true;

                                }
                                else
                                        password_edit.setError("Password is too short");

                        }else{
                                rollno_edit.setError("Roll Number must be 10 digits");
                        }



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
