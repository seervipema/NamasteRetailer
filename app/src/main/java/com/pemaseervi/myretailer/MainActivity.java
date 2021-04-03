package com.pemaseervi.myretailer;

import  androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;

import com.pemaseervi.myretailer.utility.AppPref;
import com.pemaseervi.myretailer.utility.CommonApiConstant;


public class MainActivity extends AppCompatActivity {


    private String mobileNumber = "";
    private static final int REQUEST_PHONE_VERIFICATION = 1080;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SystemClock.sleep(1000);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(AppPref.getUserName(MainActivity.this)==null){


            Intent registerIntent= new Intent(MainActivity.this,PhoneNumberActivity.class);
//            registerIntent.putExtra("TITLE", getResources().getString(R.string.app_name));
//            //Optionally you can pass phone number to populate automatically.
//            registerIntent.putExtra("PHONE_NUMBER", "");
//            startActivityForResult(registerIntent, REQUEST_PHONE_VERIFICATION);
            registerIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(registerIntent);
//            startActivityForResult(registerIntent,REQUEST_PHONE_VERIFICATION);
            finish();
        }else{
            CommonApiConstant.currentUser=AppPref.getUserName(MainActivity.this);
            Intent mainIntent= new Intent(MainActivity.this,HomeActivity.class);
            HomeActivity.currentFragment = 0;
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(mainIntent);
            finish();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PHONE_VERIFICATION:
// If mobile number is verified successfully then you get your phone number to perform further operations.
                if (data != null && data.hasExtra("PHONE_NUMBER") && data.getStringExtra("PHONE_NUMBER") != null) {
                    String phoneNumber = data.getStringExtra("PHONE_NUMBER");
                    mobileNumber = phoneNumber;
                    Toast.makeText(MainActivity.this,phoneNumber,Toast.LENGTH_SHORT);
                    Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } else {
                    // If mobile number is not verified successfully You can hendle according to your requirement.
                    Toast.makeText(MainActivity.this,getString(R.string.mobile_verification_fails),Toast.LENGTH_SHORT);
                }
                break;
        }
    }
}
