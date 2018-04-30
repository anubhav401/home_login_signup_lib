package com.example.homelibrary;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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


        EditText Username, password_edit ;
        TextView CreateAccount,forgotText,usertext,passphase;
        Button login_button;
        String  password , roll_no ,email ;
        private FirebaseAuth Auth;
        private FirebaseAuth.AuthStateListener AuthListener;
        private LoginFragment.afterLoginListener activity_listener;
        private FragmentChangeListener fr_change;
        ProgressDialog progressDialog;


        public interface afterLoginListener{
                void afterLogin(String RollNumber);

        }

        @Override
        public void onAttach(Activity activity) {
                super.onAttach(activity);
                activity_listener=(LoginFragment.afterLoginListener)activity;
                fr_change=(FragmentChangeListener)activity;
        }


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

              final View view=inflater.inflate(R.layout.fragment_login, container, false);

                usertext=view.findViewById(R.id.usertextname);
                passphase=view.findViewById(R.id.passphase);
                Username = (EditText) view.findViewById(R.id.username);
                password_edit = (EditText) view.findViewById(R.id.input_password);
                login_button = (Button) view.findViewById(R.id.Login);
                forgotText=view.findViewById(R.id.forgotText);
                CreateAccount = view.findViewById(R.id.create_account);

                Username.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

               forgotText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        fr_change.changeFragment(new FPFragment());

                    }
                });

               CreateAccount.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       fr_change.changeFragment(new SignUpFragment());
                   }
               });

                Auth = FirebaseAuth.getInstance();
                AuthListener = new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                              //  final FirebaseUser user = firebaseAuth.getCurrentUser();

                                login_button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view1) {

                                                roll_no = Username.getText().toString().trim();
                                                password = password_edit.getText().toString().trim();

                                                if (checkValidEntries()) {
                                                        progressDialog = ProgressDialog.show(getActivity(),"","Please Wait...");
                                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                                        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                         try{
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
                                                                                                                         Snackbar.make(view,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                                                                                                                 }
                                                                                                         }

                                                                                                         // ...
                                                                                                 }
                                                                                         );
                                                                         }catch (Exception e){
                                                                                 progressDialog.hide();
                                                                             Snackbar.make(view,"User don't Exist",Snackbar.LENGTH_LONG).show();

                                                                                 password_edit.setText("");
                                                                                 Username.setText("");
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

        boolean checkValidEntries() {
            int count=0;
            usertext.setText("");
            passphase.setText("");

            if(Username.getText().toString().trim().length()>5){
                if(Username.getText().toString().trim().length()<15) {
                    count++;
                }else
                    usertext.setText("Username must have at max 15 characters");
            }else{
                usertext.setText("Username must have at least 5 characters");
            }

            if (password_edit.getText().toString().length() >= 6) {
                if(count==1) {
                    return true;
                }
            }
            else
                passphase.setText("Password is too short");

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
