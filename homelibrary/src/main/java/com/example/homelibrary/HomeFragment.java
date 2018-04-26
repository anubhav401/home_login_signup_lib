package com.example.homelibrary;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.CONNECTIVITY_SERVICE;


public class HomeFragment extends Fragment {


        private RelativeLayout root_layout;
        private LinearLayout first_linear_layout;
        private DatabaseReference root_ref= FirebaseDatabase.getInstance().getReference();
        private ArrayList<String> pictures_images = new ArrayList<>();
        FirebaseUser user;
        public String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        private boolean shouldGetReward = true;
        ProgressDialog prog;
        SlideShowAdapter adapter;
        Timer timer;
        Handler handler;
        Runnable runnable;


        static Boolean isLoggedIn = false;
        String RollNumber;

        public HomeFragment() {

        }


        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


                View view = inflater.inflate(R.layout.fragment_home, container, false);


               // root_layout=(RelativeLayout)view.findViewById(R.id.home_root_layout_id);
                first_linear_layout = (LinearLayout)view.findViewById(R.id.first_linear_layout);

                user= FirebaseAuth.getInstance().getCurrentUser();
                if (user!=null)
                        isLoggedIn = true;
                else
                        isLoggedIn = false;

                if(isNetworkAvailable()){
                      //  prog= ProgressDialog.show(getActivity(),"Loading","Please wait...");
                        refreshView(inflater);

                        Log.e("hello this is tab2let","this is working");
                        showImages();

                }
                else
                {
                        updateView(inflater);
                        Toast.makeText(getActivity(),"No internet connection", Toast.LENGTH_SHORT).show();
                }
                adapter = new SlideShowAdapter(getActivity(),pictures_images);
                final ViewPager viewpager = (ViewPager) view.findViewById(R.id.viewPager);
                viewpager.setAdapter(adapter);

                TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);

                tabLayout.setupWithViewPager(viewpager, true);


                handler = new Handler();
                runnable = new Runnable() {
                        @Override
                        public void run() {
                                int i = viewpager.getCurrentItem();
                                viewpager.setCurrentItem(i,true);
                                if(i==adapter.picturesArray.size()-1){
                                        i=0;
                                        viewpager.setCurrentItem(i,true);
                                }
                                else{
                                        i++;
                                        viewpager.setCurrentItem(i,true);
                                }
                        }
                };
                timer = new Timer();
                timer.schedule(new TimerTask() {
                        @Override
                        public void run() {

                                handler.post(runnable);
                        }
                },3000,3000);









                return view;
        }

        public void updateView(LayoutInflater inflater){
                if(isLoggedIn) {
                        SharedPreferences share =getActivity().getApplicationContext().getSharedPreferences("Signedin", Context.MODE_PRIVATE);
                        int i = 0;
                        while (i < share.getInt("count", 0)) {
                                View current_notif = inflater.inflate(R.layout.notification_row, null, false);
                                TextView notif_textview = (TextView) current_notif.findViewById(R.id.notification_textview_id);
                                notif_textview.setText(share.getString("StringNum" + i, "cannot acces internet"));
                                first_linear_layout.addView(current_notif);
                                i++;
                        }
                }else {
                        int j=0;
                        final SharedPreferences shared = getActivity().getApplicationContext().getSharedPreferences("unsignedData", Context.MODE_PRIVATE);
                        while (j < shared.getInt("count", 0)) {
                                View current_notif = inflater.inflate(R.layout.notification_row, null, false);
                                TextView notif_textview = (TextView) current_notif.findViewById(R.id.notification_textview_id);
                                notif_textview.setText(shared.getString("StringNumber" + j, ""));
                                first_linear_layout.addView(current_notif);
                                j++;
                        }
                }
        }


        private boolean isNetworkAvailable()
        {
                ConnectivityManager manager=(ConnectivityManager)getActivity().getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo info=manager.getActiveNetworkInfo();
                if(info!=null && info.isConnectedOrConnecting())
                        return true;

                return false;
        }


        private void refreshView(final LayoutInflater inflater) {

                if (isLoggedIn) {
                       // showImages();

                        DatabaseReference databaseReference2 = root_ref.child("id_map_roll");
                        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                        for(DataSnapshot single : dataSnapshot.getChildren()){

                                                String db_email = single.child("id").getValue().toString();
                                                String db_roll = single.getKey();

                                                String user_email = user.getEmail();

                                                if(user_email.equals(db_email)){

                                                        RollNumber = db_roll;

                                                        final DatabaseReference dRef = root_ref.child("new_rewards").child(RollNumber);
                                                        dRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {


                                                                        for (DataSnapshot item_snapshot : dataSnapshot.getChildren()) {



                                                                                String reward_date = item_snapshot.child("reward_date").getValue().toString();
                                                                                String reward_detail = item_snapshot.child("reward_detail").getValue().toString();


                                                                                if(reward_date.equals(date)  &&  reward_detail.equals("Daily Login Reward")){

                                                                                        shouldGetReward = false;



                                                                                }
                                                                        }


                                                                        if(shouldGetReward){

                                                                                AlertDialog.Builder builder;
                                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                                                        builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert );
                                                                                } else {
                                                                                        builder = new AlertDialog.Builder(getActivity());
                                                                                }
                                                                                builder.setTitle("Congratulations")
                                                                                        .setMessage("You have won 250 points as your daily login reward. Continue visiting our app daily to earn more!")
                                                                                        .setPositiveButton("OK", null)
                                                                                        .show();


                                                                                Map<String, Object> map = new HashMap<String, Object>();
                                                                                map.put("reward_date", date);
                                                                                map.put("reward_detail", "Daily Login Reward");
                                                                                map.put("reward_points", "250");
                                                                                map.put("time_stamp",getTimeStamp());


                                                                                dRef.push().setValue(map);

                                                                        }

                                                                }


                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {

                                                                }
                                                        });

                                                }

                                        }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                        });



                        final SharedPreferences shared=getActivity().getSharedPreferences("Signedin", Context.MODE_PRIVATE);

                        prog= ProgressDialog.show(getActivity(),"","Loading..");
                        root_ref.child("home").child("signed_in_home").addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                        SharedPreferences.Editor edi= shared.edit().clear();

                                        int count = 0;
                                        for (DataSnapshot current_snapshot : dataSnapshot.getChildren()) {


                                                edi.putString("StringNum"+count,current_snapshot.getValue(String.class));
                                                edi.apply();
//                                                first_linear_layout.addView(current_notif);

                                                count++;




                                        }
                                        Log.e("helloo this  countno 1",String.valueOf(count));
                                        edi.putInt("count",count);
                                        edi.apply();
                                        try {
                                                prog.dismiss();
                                                updateView(inflater);
                                        }catch(Exception e){}
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {


                                        Toast.makeText(getActivity(), "Could not fetch data", Toast.LENGTH_SHORT).show();
                                }
                        });



                }
                else {

                       // showImages();

                        final SharedPreferences share=getActivity().getApplicationContext().getSharedPreferences("unsignedData", Context.MODE_PRIVATE);
                        prog= ProgressDialog.show(getActivity(),"","Loading..");
                        root_ref.child("home").child("default_home").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {


                                        SharedPreferences.Editor edit = share.edit().clear();
                                        int count = 0;
                                        for (DataSnapshot current_snapshot : dataSnapshot.getChildren()) {


                                                edit.putString("StringNumber" + count, current_snapshot.getValue(String.class));

                                                edit.apply();
                                            /*    if (count % 2 == 0){
                                                        current_notif.setBackgroundColor(Color.GRAY);
                                                        notif_textview.setTextColor(Color.WHITE);
                                                }
*/
                                                count++;
                                        }
                                        Log.e("hello this is no 2", "this is working" + count);
                                        edit.putInt("count", count);
                                        edit.apply();
                                        try {
                                                prog.dismiss();
                                                updateView(inflater);
                                        }catch(Exception e){}
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {


                                        Toast.makeText(getActivity(), "Could not fetch data", Toast.LENGTH_SHORT).show();
                                }
                        });
                }




        }



        public void showImages(){


                DatabaseReference databaseReference = root_ref.child("home").child("images");
                databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                                pictures_images.clear();

                                for(DataSnapshot singleDatasnapshot : dataSnapshot.getChildren()){

                                        String photo_url = singleDatasnapshot.getValue().toString();
                                        pictures_images.add(photo_url);

                                }

                                adapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                });

        }

        public Long getTimeStamp(){

                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                return -tsLong;
        }

}
