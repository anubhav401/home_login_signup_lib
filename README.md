# home_login_signup_lib

Add home,Login and signup page as a fragment with just 1 line of code.

this library allows to generate home,login and signup page with less customization. with all access to the firebase on its own.

SETUP
Not available on jcenter,Maven and jitpack : 
1. So include .aar package in your LIBS folder under /app path.
if 'libs' folder is not present then make one of your own and then add this aar file to it.
2. Declare following line in your app level gradle file.
      "compile files('libs/homelibrary-debug.aar')"
      
3. add all the following dependencies in app level gradle file:
    {
    implementation 'com.android.support:design:26.1.0'
    implementation 'com.android.support:support-compat:26.1.0'
    implementation 'com.android.support:appcompat-v7:26.1.0'
    compile 'com.google.firebase:firebase-auth:15.0.0'
    compile 'com.google.firebase:firebase-database:15.0.0'
    compile ('com.github.bumptech.glide:glide:4.4.0@aar'){
        transitive =true
      }
  }
  
4. To create Fragment of home, login signup page add this to top of your activity
      import android.support.v4.app.Fragment
      
   and implement listeners as shown: 
   
        <activity name> implements FragmentChangeListener,SignUpFragment.afterSignUpListener , LoginFragment.afterLoginListener{....}
        
        for home fragment to show some thing on the home page, confirm to add 'home' node as a parent and 
        'signed_in_home','default_home','images' as the child node of 'home' node and add txt in signed in and default nodes and images in images node.
        
        now to have login and signup page working and get the user name of logged in and signed up user override afterSignUp(String username) and afterLogin(String username) methods.
        In these methods save the username of user(may be in shared preferrences) and change the activity response as required.
      
5. Declare fragment variable
        Fragment fragment;
5. Its done now just create the object of HomeFragment,LoginFragment and SignUpFragment.
        fragment = new HomeFragment();
        fragment = new LoginFragment();
        fragment = new SignUpFragment();
        
6. override changefragment() method to get the supportfragmentmanager from activity and add/replace the fragment as needed. call this method when needed in the activity. ovverriding this method is important for working of login and signup page.
