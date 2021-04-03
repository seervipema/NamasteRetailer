package com.pemaseervi.myretailer;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.utility.CommonApiConstant;
import com.pemaseervi.myretailer.utility.VolleyMultipartRequest;
import com.pemaseervi.myretailer.utility.VolleySingleton;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.pemaseervi.myretailer.ProductDetailsActivity.addToCart;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {


    public SignUpFragment() {
        // Required empty public constructor
    }
    private TextView alreadyHaveAnAccount;
    private FrameLayout parentFrameLayout;
    private EditText email;
    private EditText fullName;
    private EditText password;
    private EditText confirmPassword;
    private ImageButton closeBtn;
    private Button signUpBtn,usePhoneNumberBtn;
    private ProgressBar progressBar;
    private Button btnHaveAnEmailAccount;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    public static boolean disableCloseBtn=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_sign_up, container, false);
        email=view.findViewById(R.id.sign_up_email);
        fullName=view.findViewById(R.id.sign_up_fullName);
        password=view.findViewById(R.id.sign_up_password);
        confirmPassword =view.findViewById(R.id.sign_up_confirm_password);
        closeBtn=view.findViewById(R.id.sign_up_close);
        signUpBtn=view.findViewById(R.id.sign_up_btn);
        progressBar=view.findViewById(R.id.sign_up_progressbar);
        btnHaveAnEmailAccount=view.findViewById(R.id.tv_already_have_account);
        alreadyHaveAnAccount= view.findViewById(R.id.sign_up_btn);
        parentFrameLayout= getActivity().findViewById(R.id.register_framelayout);
        usePhoneNumberBtn = view.findViewById(R.id.tv_have_phone_number);
        usePhoneNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneNumberIntent = new Intent(getView().getContext(),PhoneNumberActivity.class);
                phoneNumberIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(phoneNumberIntent);
            }
        });
        if(disableCloseBtn){
            closeBtn.setVisibility(View.GONE);
        }else{
            closeBtn.setVisibility(View.VISIBLE);
        }
        return view;
    }
    private void saveTokenForPushNotification(){
        SharedPreferences sharedPreferences= getActivity().getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
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
                            DBQueries.signOut(getActivity().getBaseContext());
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
                Toast.makeText(getActivity(),errorMessage, Toast.LENGTH_LONG).show();
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
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(multipartRequest2);

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new SignInFragment());
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                     checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        fullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                  checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                  checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo to send data to firebase
                checkEmailAndPassword();
            }
        });
        btnHaveAnEmailAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new SignInFragment());
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainIntent();
            }
        });


    }

    private void checkEmailAndPassword() {
        Drawable customErrorIcon = getResources().getDrawable(R.mipmap.custom_error_icon);
        customErrorIcon.setBounds(0,0,customErrorIcon.getIntrinsicWidth(),customErrorIcon.getIntrinsicHeight());
        if(email.getText().toString().matches(emailPattern)){
            if(password.getText().toString().equals(confirmPassword.getText().toString())){
                    //to show progress bar use below code
                     progressBar.setVisibility(View.VISIBLE);
                     signUpBtn.setEnabled(false);
                     signUpBtn.setTextColor(Color.argb(50,255,255,255));
                Map<String,String>userdata = new HashMap<>();
                userdata.put("fullName",fullName.getText().toString());
                userdata.put("email",email.getText().toString());
                userdata.put("password",password.getText().toString());
                userdata.put("profile_pic","");
                String URL= CommonApiConstant.backEndUrl+"/user/signup";

                VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        //code to enable sign up button
                        signUpBtn.setEnabled(true);
                        signUpBtn.setTextColor(Color.rgb(255,255,255));
                        if(addToCart !=null){
                            addToCart.setEnabled(true);
                        }
                        String resultResponse = new String(response.data);
                        try {
                            JSONObject result = new JSONObject(resultResponse);
                            String message= result.get("message").toString();
                            Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();

                            CommonApiConstant.currentUser=email.getText().toString();
                            saveTokenForPushNotification();
                            setFragment(new SignInFragment());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressBar.setVisibility(View.INVISIBLE);
                        //code to enable sign up button
                        signUpBtn.setEnabled(true);
                        signUpBtn.setTextColor(Color.rgb(255,255,255));


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
                                    DBQueries.signOut(getActivity().getBaseContext());
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
                        Toast.makeText(getActivity(),errorMessage,Toast.LENGTH_LONG).show();
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
                VolleySingleton.getInstance(getActivity().getBaseContext()).addToRequestQueue(multipartRequest1);

//                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
//                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                  if(task.isSuccessful()){
//
//
//
////                                      Map<String,Object>userdata = new HashMap<>();
////                                      userdata.put("fullname",fullName.getText().toString());
////                                      userdata.put("email",email.getText().toString());
////                                      userdata.put("profile","");
////                                      firebaseFirestore.collection("USERS").document(firebaseAuth.getUid())
////                                              .set(userdata)
////                                             .addOnCompleteListener(new OnCompleteListener<Void>() {
////                                                 @Override
////                                                 public void onComplete(@NonNull Task<Void> task) {
////                                                     if(task.isSuccessful()){
////                                                         Map<String,Object>listSize = new HashMap<>();
////                                                         listSize.put("list_size",(long)0);
////                                                         firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA")
////                                                                 .document("MY_WISHLIST")
////                                                                 .set(listSize)
////                                                                 .addOnCompleteListener(new OnCompleteListener<Void>() {
////                                                                     @Override
////                                                                     public void onComplete(@NonNull Task<Void> task) {
////                                                                         if(task.isSuccessful()){
////                                                                             Map<String,Object>cartlistSize = new HashMap<>();
////                                                                             cartlistSize.put("list_size",(long)0);
////                                                                             firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA")
////                                                                                     .document("MY_CART")
////                                                                                     .set(cartlistSize)
////                                                                                     .addOnCompleteListener(new OnCompleteListener<Void>() {
////                                                                                         @Override
////                                                                                         public void onComplete(@NonNull Task<Void> task) {
////                                                                                             if(task.isSuccessful()){
////                                                                                                 Map<String,Object>addresslistSize = new HashMap<>();
////                                                                                                 addresslistSize.put("list_size",(long)0);
////                                                                                                 firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA")
////                                                                                                         .document("MY_ADDRESSES")
////                                                                                                         .set(addresslistSize)
////                                                                                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
////                                                                                                             @Override
////                                                                                                             public void onComplete(@NonNull Task<Void> task) {
////                                                                                                                 if(task.isSuccessful()){
////                                                                                                                     Map<String,Object>nofifyMap = new HashMap<>();
////                                                                                                                     nofifyMap.put("list_size",(long)0);
////                                                                                                                     firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA")
////                                                                                                                             .document("MY_NOTIFICATIONS")
////                                                                                                                             .set(nofifyMap)
////                                                                                                                             .addOnCompleteListener(new OnCompleteListener<Void>() {
////                                                                                                                                 @Override
////                                                                                                                                 public void onComplete(@NonNull Task<Void> task) {
////                                                                                                                                     if(task.isSuccessful()){
////                                                                                                                                         DBQueries.currentUser = FirebaseAuth.getInstance().getCurrentUser();
////                                                                                                                                         mainIntent();
////                                                                                                                                     }else{
////                                                                                                                                         progressBar.setVisibility(View.INVISIBLE);
////                                                                                                                                         //code to enable sign up button
////                                                                                                                                         signUpBtn.setEnabled(true);
////                                                                                                                                         DBQueries.currentUser=FirebaseAuth.getInstance().getCurrentUser();
////                                                                                                                                         signUpBtn.setTextColor(Color.rgb(255,255,255));
////                                                                                                                                         //finish of enable sign up button code
////                                                                                                                                         String error = task.getException().getMessage();
////                                                                                                                                         Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
////                                                                                                                                     }
////                                                                                                                                 }
////                                                                                                                             });
////                                                                                                                 }else{
////                                                                                                                     progressBar.setVisibility(View.INVISIBLE);
////                                                                                                                     //code to enable sign up button
////                                                                                                                     signUpBtn.setEnabled(true);
////                                                                                                                     DBQueries.currentUser=FirebaseAuth.getInstance().getCurrentUser();
////                                                                                                                     signUpBtn.setTextColor(Color.rgb(255,255,255));
////                                                                                                                     //finish of enable sign up button code
////                                                                                                                     String error = task.getException().getMessage();
////                                                                                                                     Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
////                                                                                                                 }
////                                                                                                             }
////                                                                                                         });
////                                                                                             }else{
////                                                                                                 progressBar.setVisibility(View.INVISIBLE);
////                                                                                                 //code to enable sign up button
////                                                                                                 signUpBtn.setEnabled(true);
////                                                                                                 DBQueries.currentUser=FirebaseAuth.getInstance().getCurrentUser();
////                                                                                                 signUpBtn.setTextColor(Color.rgb(255,255,255));
////                                                                                                 //finish of enable sign up button code
////                                                                                                 String error = task.getException().getMessage();
////                                                                                                 Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
////                                                                                             }
////                                                                                         }
////                                                                                     });
////                                                                         }else{
////                                                                             progressBar.setVisibility(View.INVISIBLE);
////                                                                             //code to enable sign up button
////                                                                             signUpBtn.setEnabled(true);
////                                                                             DBQueries.currentUser=FirebaseAuth.getInstance().getCurrentUser();
////                                                                             signUpBtn.setTextColor(Color.rgb(255,255,255));
////                                                                             //finish of enable sign up button code
////                                                                             String error = task.getException().getMessage();
////                                                                             Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
////                                                                         }
////                                                                     }
////                                                                 });
////
////                                                     }else{
////                                                         String error = task.getException().getMessage();
////                                                         Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
////                                                     }
////                                                 }
////                                             });
//                                  }else{
//                                      progressBar.setVisibility(View.INVISIBLE);
//                                      //code to enable sign up button
//                                      signUpBtn.setEnabled(true);
//                                      signUpBtn.setTextColor(Color.rgb(255,255,255));
//                                      //finish of enable sign up button code
//                                     String error = task.getException().getMessage();
//                                      Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
//                                  }
//                                }
//                            });
            }else{
                   confirmPassword.setError("Password doesn't matched.",customErrorIcon);
            }
        }else{
                   email.setError("Invalid Email.",customErrorIcon);
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left,R.anim.slideout_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
    private void checkInputs(){
        if(!TextUtils.isEmpty(email.getText())){
            if(!TextUtils.isEmpty(fullName.getText())){
                if(!TextUtils.isEmpty(password.getText()) && password.length()>=8){
                    if(!TextUtils.isEmpty(confirmPassword.getText())){
                        signUpBtn.setEnabled(true);
                        signUpBtn.setTextColor(Color.rgb(255,255,255));
                    }else{
                        signUpBtn.setEnabled(false);
                        signUpBtn.setTextColor(Color.argb(50,255,255,255));
                    }
                }else{
                    signUpBtn.setEnabled(false);
                    signUpBtn.setTextColor(Color.argb(50,255,255,255));
                }
            }else{
                signUpBtn.setEnabled(false);
                signUpBtn.setTextColor(Color.argb(50,255,255,255));
            }
        }else{
                     signUpBtn.setEnabled(false);
                     signUpBtn.setTextColor(Color.argb(50,255,255,255));
        }
    }
    private void mainIntent(){
        if(disableCloseBtn){
            disableCloseBtn=false;
        }else{
            Intent mainIntent= new Intent(getActivity(),HomeActivity.class);
            startActivity(mainIntent);
        }
        getActivity().finish();
    }
}
