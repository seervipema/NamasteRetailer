package com.pemaseervi.myretailer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;

import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pemaseervi.myretailer.utility.AppPref;
import com.pemaseervi.myretailer.utility.CommonApiConstant;
import com.pemaseervi.myretailer.utility.Utility;
import com.pemaseervi.myretailer.utility.VolleyMultipartRequest;
import com.pemaseervi.myretailer.utility.VolleySingleton;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class VerificationCodeActivity extends AppCompatActivity {

    private Activity mActivity = VerificationCodeActivity.this;
    private AppCompatEditText etDigit1;
    private AppCompatEditText etDigit2;
    private AppCompatEditText etDigit3;
    private AppCompatEditText etDigit4;
    private AppCompatEditText etDigit5;
    private AppCompatEditText etDigit6;
    private AppCompatButton btnContinue;
    private AppCompatButton btnResendCode;
    private AppCompatTextView tvToolbarBack;
    private AppCompatTextView tvToolbarTitle;
    private AppCompatTextView tvCountDownTimer;
    private LinearLayout llContinue;
    private RelativeLayout rlResend;
    private ProgressBar pbVerify;
    private String strPhoneCode;
    private String strPhoneNumber;
    private static String otp_number;
//    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
//    private FirebaseAuth mAuth;
    private String mVerificationId;
    private CountDownTimer countDownTimer;
//    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);
//        firebaseFirestore=FirebaseFirestore.getInstance();
        setUpUI();
//        setUpToolBar();


        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(AppConstant.PhoneCode)) {
                strPhoneCode = getIntent().getStringExtra(AppConstant.PhoneCode);
            }
            if (getIntent().hasExtra(AppConstant.PhoneNumber)) {
                strPhoneNumber = getIntent().getStringExtra(AppConstant.PhoneNumber);
            }
//            tvToolbarBack.setText("< Edit Number");
            tvToolbarTitle.setText(AppConstant.PLUS + strPhoneCode + " " + strPhoneNumber + "");
        }

//        mAuth = FirebaseAuth.getInstance();

//        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//
//            @Override
//            public void onVerificationCompleted(PhoneAuthCredential credential) {
//                Utility.log("onVerificationCompleted: " + credential);
//                signInWithPhoneAuthCredential(credential);
//                pbVerify.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onVerificationFailed(FirebaseException e) {
//                Utility.log("onVerificationFailed" + e);
//                if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                } else if (e instanceof FirebaseTooManyRequestsException) {
//                }
//                pbVerify.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
//                Utility.log("onCodeSent: " + verificationId);
//                Utility.log("token: " + token);
//                pbVerify.setVisibility(View.GONE);
//                mVerificationId = verificationId;
//                mResendToken = token;
//
//            }
//        };
        startPhoneNumberVerification(AppConstant.PLUS + strPhoneCode + strPhoneNumber + "");


    }
    private void signInWithPhoneAuthCredential() {
        CommonApiConstant.currentUser=AppConstant.PLUS + strPhoneCode + strPhoneNumber + "";
        saveTokenForPushNotification();
        Intent mainIntent= new Intent(VerificationCodeActivity.this,HomeActivity.class);
        startActivity(mainIntent);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Utility.log("signInWithCredential:success");
//                            pbVerify.setVisibility(View.GONE);
//                            final FirebaseUser user = task.getResult().getUser();
//                            Utility.showToast(VerificationCodeActivity.this, user.getPhoneNumber() + " verified successfully");
//
//                            Map<String,Object> userdata = new HashMap<>();
//                            userdata.put("fullname","");
//                            userdata.put("email",user.getPhoneNumber());
//                            userdata.put("profile","");
//                            firebaseFirestore.collection("USERS").document(mAuth.getUid())
//                                    .set(userdata)
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if(task.isSuccessful()){
//                                                Map<String,Object>listSize = new HashMap<>();
//                                                listSize.put("list_size",(long)0);
//                                                firebaseFirestore.collection("USERS").document(mAuth.getUid()).collection("USER_DATA")
//                                                        .document("MY_WISHLIST")
//                                                        .set(listSize)
//                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                if(task.isSuccessful()){
//                                                                    Map<String,Object>cartlistSize = new HashMap<>();
//                                                                    cartlistSize.put("list_size",(long)0);
//                                                                    firebaseFirestore.collection("USERS").document(mAuth.getUid()).collection("USER_DATA")
//                                                                            .document("MY_CART")
//                                                                            .set(cartlistSize)
//                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                @Override
//                                                                                public void onComplete(@NonNull Task<Void> task) {
//                                                                                    if(task.isSuccessful()){
//                                                                                        Map<String,Object>addresslistSize = new HashMap<>();
//                                                                                        addresslistSize.put("list_size",(long)0);
//                                                                                        firebaseFirestore.collection("USERS").document(mAuth.getUid()).collection("USER_DATA")
//                                                                                                .document("MY_ADDRESSES")
//                                                                                                .set(addresslistSize)
//                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                                    @Override
//                                                                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                                                                        if(task.isSuccessful()){
//                                                                                                            Map<String,Object>nofifyMap = new HashMap<>();
//                                                                                                            nofifyMap.put("list_size",(long)0);
//                                                                                                            firebaseFirestore.collection("USERS").document(mAuth.getUid()).collection("USER_DATA")
//                                                                                                                    .document("MY_NOTIFICATIONS")
//                                                                                                                    .set(nofifyMap)
//                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                                                        @Override
//                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                                                                                            if(task.isSuccessful()){
//                                                                                                                                DBQueries.currentUser = FirebaseAuth.getInstance().getCurrentUser();
//                                                                                                                                DBQueries.email="";
//                                                                                                                                DBQueries.fullname="";
//                                                                                                                                DBQueries.profile="";
//                                                                                                                                new Handler().postDelayed(new Runnable() {
//                                                                                                                                    @Override
//                                                                                                                                    public void run() {
//                                                                                                                                        Intent intent = new Intent();
//                                                                                                                                        intent.putExtra("PHONE_NUMBER", user.getPhoneNumber());
//                                                                                                                                        setResult(1080, intent);
//                                                                                                                                        finish();
//                                                                                                                                    }
//                                                                                                                                }, 500);
//                                                                                                                                // ...
//                                                                                                                            }else{
//
//                                                                                                                                DBQueries.currentUser=FirebaseAuth.getInstance().getCurrentUser();
//                                                                                                                                //finish of enable sign up button code
//                                                                                                                                String error = task.getException().getMessage();
//                                                                                                                                Toast.makeText(VerificationCodeActivity.this,error,Toast.LENGTH_SHORT).show();
//                                                                                                                            }
//                                                                                                                        }
//                                                                                                                    });
//                                                                                                        }else{
//
//                                                                                                            DBQueries.currentUser=FirebaseAuth.getInstance().getCurrentUser();
//
//                                                                                                            //finish of enable sign up button code
//                                                                                                            String error = task.getException().getMessage();
//                                                                                                            Toast.makeText(VerificationCodeActivity.this,error,Toast.LENGTH_SHORT).show();
//                                                                                                        }
//                                                                                                    }
//                                                                                                });
//                                                                                    }else{
//
//                                                                                        DBQueries.currentUser=FirebaseAuth.getInstance().getCurrentUser();
//
//                                                                                        //finish of enable sign up button code
//                                                                                        String error = task.getException().getMessage();
//                                                                                        Toast.makeText(VerificationCodeActivity.this,error,Toast.LENGTH_SHORT).show();
//                                                                                    }
//                                                                                }
//                                                                            });
//                                                                }else{
//
//                                                                    DBQueries.currentUser=FirebaseAuth.getInstance().getCurrentUser();
//                                                                    String error = task.getException().getMessage();
//                                                                    Toast.makeText(VerificationCodeActivity.this,error,Toast.LENGTH_SHORT).show();
//                                                                }
//                                                            }
//                                                        });
//
//                                            }else{
//                                                String error = task.getException().getMessage();
//                                                Toast.makeText(VerificationCodeActivity.this,error,Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(VerificationCodeActivity.this,e.getMessage(),Toast.LENGTH_LONG);
//                                }
//                            });
//
//                        } else {
//                            // Sign in failed, display a message and update the UI
//                            pbVerify.setVisibility(View.GONE);
//                            Utility.log("signInWithCredential:failure " + task.getException());
//                            Utility.showToast(VerificationCodeActivity.this, " Verification failed");
//                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                // The verification code entered was invalid
//                            }
//                            Intent intent = new Intent();
//                            intent.putExtra("PHONE_NUMBER", "");
//                            setResult(1080, intent);
//                            finish();
//                        }
//                    }
//                });
    }

    private void setUpUI() {
        rlResend = findViewById(R.id.rlResend);
        llContinue = findViewById(R.id.llContinue);
        llContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnContinue.isClickable())
                    btnContinue.performClick();
            }
        });
        pbVerify = findViewById(R.id.pbVerify);

        btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.hideKeyBoardFromView(mActivity);
                if (validate()) {
//                    if (!TextUtils.isEmpty(mVerificationId)) {
                        verifyPhoneNumberWithCode(
                                etDigit1.getText().toString().trim() +
                                        etDigit2.getText().toString().trim() +
                                        etDigit3.getText().toString().trim() +
                                        etDigit4.getText().toString().trim() +
                                        etDigit5.getText().toString().trim() +
                                        etDigit6.getText().toString().trim());
//                    } else {
//                        Utility.showToast(VerificationCodeActivity.this, "Verification id not received");
//                    }
                }
            }
        });

        btnResendCode = findViewById(R.id.btnResendCode);
        btnResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.hideKeyBoardFromView(mActivity);
//                if (mResendToken != null)
                    resendVerificationCode(AppConstant.PLUS + strPhoneCode + strPhoneNumber);
//                else {
//                    Utility.showToast(VerificationCodeActivity.this, "Resend token null");
//                    onBackPressed();
//                }
            }
        });


        tvToolbarBack = findViewById(R.id.tvToolbarBack);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        tvCountDownTimer = findViewById(R.id.tvCountDownTimer);

        etDigit1 = findViewById(R.id.etDigit1);
        etDigit2 = findViewById(R.id.etDigit2);
        etDigit3 = findViewById(R.id.etDigit3);
        etDigit4 = findViewById(R.id.etDigit4);
        etDigit5 = findViewById(R.id.etDigit5);
        etDigit6 = findViewById(R.id.etDigit6);

        setButtonContinueClickbleOrNot();
        tvToolbarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        etDigit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setButtonContinueClickbleOrNot();
                if (editable.toString().length() == 1) {
                    etDigit2.requestFocus();
                }
            }
        });
        etDigit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setButtonContinueClickbleOrNot();
                if (editable.toString().length() == 1) {
                    etDigit3.requestFocus();
                } else {
                    etDigit1.requestFocus();
                }
            }
        });
        etDigit3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setButtonContinueClickbleOrNot();
                if (editable.toString().length() == 1) {
                    etDigit4.requestFocus();
                } else {
                    etDigit2.requestFocus();
                }
            }
        });
        etDigit4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setButtonContinueClickbleOrNot();
                if (editable.toString().length() == 1) {
                    etDigit5.requestFocus();
                } else {
                    etDigit3.requestFocus();
                }
            }
        });
        etDigit5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setButtonContinueClickbleOrNot();
                if (editable.toString().length() == 1) {
                    etDigit6.requestFocus();
                } else {
                    etDigit4.requestFocus();
                }
            }
        });
        etDigit6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setButtonContinueClickbleOrNot();
                if (editable.toString().length() == 1) {
                } else {
                    etDigit5.requestFocus();
                }
            }
        });

        etDigit1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                } else {
                    if (etDigit1.getText().toString().trim().length() == 1) {
                        etDigit2.requestFocus();
                    }
                }
                return false;
            }
        });
        etDigit2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (etDigit2.getText().toString().trim().length() == 0)
                        etDigit1.requestFocus();
                } else {
                    if (etDigit2.getText().toString().trim().length() == 1) {
                        etDigit3.requestFocus();
                    }
                }
                return false;
            }
        });
        etDigit3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (etDigit3.getText().toString().trim().length() == 0)
                        etDigit2.requestFocus();
                } else {
                    if (etDigit3.getText().toString().trim().length() == 1) {
                        etDigit4.requestFocus();
                    }
                }
                return false;
            }
        });
        etDigit4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (etDigit4.getText().toString().trim().length() == 0)
                        etDigit3.requestFocus();
                } else {
                    if (etDigit4.getText().toString().trim().length() == 1) {
                        etDigit5.requestFocus();
                    }
                }
                return false;
            }
        });
        etDigit5.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (etDigit5.getText().toString().trim().length() == 0)
                        etDigit4.requestFocus();
                } else {
                    if (etDigit5.getText().toString().trim().length() == 1) {
                        etDigit6.requestFocus();
                    }
                }
                return false;
            }
        });
        etDigit6.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (etDigit6.getText().toString().trim().length() == 0)
                        etDigit5.requestFocus();
                }
                return false;
            }
        });

    }

    private boolean validate() {
        if (TextUtils.isEmpty(etDigit1.getText().toString().trim())) {
            return false;
        } else if (TextUtils.isEmpty(etDigit2.getText().toString().trim())) {
            return false;
        } else if (TextUtils.isEmpty(etDigit3.getText().toString().trim())) {
            return false;
        } else if (TextUtils.isEmpty(etDigit4.getText().toString().trim())) {
            return false;
        } else if (TextUtils.isEmpty(etDigit5.getText().toString().trim())) {
            return false;
        } else if (TextUtils.isEmpty(etDigit6.getText().toString().trim())) {
            return false;
        }
        return true;
    }

    private void setButtonContinueClickbleOrNot() {
        if (!validate()) {
            llContinue.setAlpha(.5f);
            btnContinue.setClickable(false);
        } else {
            llContinue.setAlpha(1.0f);
            btnContinue.setClickable(true);
        }
    }

//    private void setUpToolBar() {
//        Toolbar mToolBar = findViewById(R.id.toolbar);
//        setSupportActionBar(mToolBar);
//    }


    private void signOut() {
//        mAuth.signOut();
    }

    @Override
    public void onBackPressed() {
        signOut();
        Intent intent = new Intent(mActivity, PhoneNumberActivity.class);
        intent.putExtra("TITLE", getResources().getString(R.string.app_name));
        intent.putExtra("PHONE_NUMBER", "");
        startActivity(intent);
        finish();
        super.onBackPressed();
    }


    private void startPhoneNumberVerification(String phoneNumber) {

        String URL= CommonApiConstant.backEndUrl+"/user/otp-for-verification/"+phoneNumber;
        VolleyMultipartRequest multipartRequest2 = new VolleyMultipartRequest(Request.Method.GET, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                String resultResponse = new String(response.data);

                try {
                    JSONObject result = new JSONObject(resultResponse);

                   final String otp_number=result.getString("otp");

                    String SMS_API ="https://www.fast2sms.com/dev/bulk";

                    StringRequest stringRequest = new StringRequest(Request.Method.POST,SMS_API , new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(VerificationCodeActivity.this,"Otp sent successfully",Toast.LENGTH_SHORT).show();

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            finish();
                            Toast.makeText(VerificationCodeActivity.this,R.string.otp_sent_failure_msg,Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String,String> headers= new HashMap<>();
                            headers.put("authorization","UXd0BpiT8FK74QthMaZJkzsbnleCr2RHjxSyu53PqAYwf1mvgIVbS4KNwE8WpJCIh61tFuAL5Zz0ykGn");
                            return headers;
                        }

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> body= new HashMap<>();
                            body.put("sender_id","FSTSMS");
                            body.put("language","english");
                            body.put("route","qt");
                            body.put("message","30697");
                            body.put("numbers",strPhoneNumber);
                            body.put("variables","{#BB#}");
                            body.put("variables_values", otp_number);
                            return body;
                        }
                    };
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            5000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    ));
                    RequestQueue requestQueue = Volley.newRequestQueue(VerificationCodeActivity.this);
                    requestQueue.add(stringRequest);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String message = response.getString("message");

                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                Toast.makeText(VerificationCodeActivity.this,errorMessage,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                return params;
            }
        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest2);




        // [START start_phone_auth]
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNumber,        // Phone number to verify
//                30,                 // Timeout duration
//                TimeUnit.SECONDS,   // Unit of timeout
//                this,               // Activity (for callback binding)
//                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]
        startCounter();
    }

    private void saveTokenForPushNotification(){
        SharedPreferences sharedPreferences= getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        String token=sharedPreferences.getString(getString(R.string.FCM_TOKEN),"");
        String SERVER_URL=CommonApiConstant.backEndUrl+"/fcm/token";
        VolleyMultipartRequest multipartRequest2 = new VolleyMultipartRequest(Request.Method.POST, SERVER_URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                String resultResponse = new String(response.data);

                try {

                    JSONObject result = new JSONObject(resultResponse);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String message = response.getString("message");

                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                Toast.makeText(getBaseContext(),errorMessage, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {

                Map<String,String>params= new HashMap<>();
                params.put("fcm_token",token);
                params.put("user_id",CommonApiConstant.currentUser.toString());

                return params;

            }
        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest2);

    }
    private void startCounter() {
        if (countDownTimer != null)
            countDownTimer.cancel();

        countDownTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvCountDownTimer.setText("" + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                tvCountDownTimer.setText("");
                btnResendCode.setEnabled(true);
                setResendButtonEnableDisable();
            }

        };
        countDownTimer.start();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber) {
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNumber,        // Phone number to verify
//                30,                 // Timeout duration
//                TimeUnit.SECONDS,   // Unit of timeout
//                this,               // Activity (for callback binding)
//                mCallbacks,         // OnVerificationStateChangedCallbacks
//                token);             // ForceResendingToken from callbacks
        startPhoneNumberVerification(AppConstant.PLUS + strPhoneCode + strPhoneNumber + "");
        startCounter();
        btnResendCode.setEnabled(false);
        setResendButtonEnableDisable();
    }

    private void verifyPhoneNumberWithCode(String code) {




            pbVerify.setVisibility(View.VISIBLE);

        Map<String,String>userdata = new HashMap<>();
        userdata.put("user_name",AppConstant.PLUS + strPhoneCode + "" + strPhoneNumber + "");
        userdata.put("otp",code);
        String URL= CommonApiConstant.backEndUrl+"/user/verify_otp_typed_by_user";

        VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    pbVerify.setVisibility(View.GONE);
                    Boolean is_verified=result.getBoolean("is_verified");
                    String token=result.getString("token");
                    if(is_verified){
                        AppPref.saveUserToken(VerificationCodeActivity.this,token);
                        AppPref.saveUserName(VerificationCodeActivity.this,AppConstant.PLUS + strPhoneCode + "" + strPhoneNumber + "");
                        signInWithPhoneAuthCredential();
                        finish();
                    }else{
                        finish();
                        Toast.makeText(VerificationCodeActivity.this,getResources().getString(R.string.incorrect_otp_msg),Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                pbVerify.setVisibility(View.GONE);
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String message = response.getString("message");

                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong.";
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                Toast.makeText(VerificationCodeActivity.this,errorMessage,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                return userdata;
            }
        };
//                multipartRequest1.setRetryPolicy(new DefaultRetryPolicy(
//                        5000,
//                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest1);




//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);


    }


    private void setResendButtonEnableDisable() {
        if (btnResendCode.isEnabled()) {
            rlResend.setBackgroundResource(R.drawable.border_red_dark);
            btnResendCode.setTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
        } else {
            rlResend.setBackgroundResource(R.drawable.border_red_light);
            btnResendCode.setTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
        }
    }
}
