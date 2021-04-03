package com.pemaseervi.myretailer;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.utility.AppPref;
import com.pemaseervi.myretailer.utility.CommonApiConstant;
import com.pemaseervi.myretailer.utility.VolleyMultipartRequest;
import com.pemaseervi.myretailer.utility.VolleySingleton;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.pemaseervi.myretailer.RegisterActivity.onResetPasswordFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    public SignInFragment() {
        // Required empty public constructor
    }
    private Button dontHaveAnAccount;
    private FrameLayout parentFrameLayout;
    private EditText email,password;
    private ImageButton closeBtn;
    private Button signInBtn,usePhoneNumberBtn;

    private ProgressBar progressBar;
    private TextView forgotPassword;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    public static boolean disableCloseBtn=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_sign_in, container, false);
        dontHaveAnAccount=view.findViewById(R.id.tv_dont_have_account);
        parentFrameLayout=getActivity().findViewById(R.id.register_framelayout);
        email=view.findViewById(R.id.sign_in_email);
        password=view.findViewById(R.id.sign_in_password);
        closeBtn=view.findViewById(R.id.sign_in_close);
        signInBtn=view.findViewById(R.id.sign_in_btn);

        progressBar=view.findViewById(R.id.sign_in_progressbar);
        forgotPassword= view.findViewById(R.id.sign_in_forgot_password);
        usePhoneNumberBtn= view.findViewById(R.id.tv_have_phone_number);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dontHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new SignUpFragment());
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
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkEmailAndPassword();
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainIntent();
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResetPasswordFragment=true;
                setFragment(new ResetPasswordFragment());
            }
        });
    }

    private void checkEmailAndPassword() {
       if(email.getText().toString().matches(emailPattern)){
           if(password.length() >=8){
                  progressBar.setVisibility(View.VISIBLE);
                  signInBtn.setEnabled(false);
                  signInBtn.setTextColor(Color.argb(50,255,255,255));

               Map<String,String>userdata = new HashMap<>();
               userdata.put("email",email.getText().toString());
               userdata.put("password",password.getText().toString());
               String URL= CommonApiConstant.backEndUrl+"/user/login";

               VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
                   @Override
                   public void onResponse(NetworkResponse response) {
                       progressBar.setVisibility(View.INVISIBLE);
                       signInBtn.setEnabled(false);
                       signInBtn.setTextColor(Color.argb(50,255,255,255));


                       String resultResponse = new String(response.data);
                       try {
                           JSONObject result = new JSONObject(resultResponse);
                           String message= result.get("message").toString();
                           String token=result.get("token").toString();
                           String username=result.getJSONObject("userData").getString("email");
                           Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
                           CommonApiConstant.currentUser=email.getText().toString();
                           AppPref.saveUserToken(getActivity(),token);
                           AppPref.saveUserName(getActivity(),username);
                           saveTokenForPushNotification();
                           mainIntent();
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                   }
               }, new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {


                       progressBar.setVisibility(View.INVISIBLE);
                       signInBtn.setEnabled(false);
                       signInBtn.setTextColor(Color.argb(50,255,255,255));


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
                                   errorMessage = message;
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
               VolleySingleton.getInstance(getActivity().getBaseContext()).addToRequestQueue(multipartRequest1);
           }else{
               Toast.makeText(getActivity(),R.string.incorrect_email_or_password,Toast.LENGTH_SHORT).show();
           }
       }else{
           Toast.makeText(getActivity(),R.string.incorrect_email_or_password,Toast.LENGTH_SHORT).show();
       }
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
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization","Bearer "+ AppPref.getUserToken(getActivity().getBaseContext()));
                return headers;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(multipartRequest2);

    }
    private void checkInputs() {
        if(!TextUtils.isEmpty(email.getText().toString())){
            if(!TextUtils.isEmpty(password.getText().toString())){
                   signInBtn.setEnabled(true);
                   signInBtn.setTextColor(Color.rgb(255,255,255));
            }else{
                  signInBtn.setEnabled(false);
                  signInBtn.setTextColor(Color.argb(50,255,255,255));
            }
        }else{
            signInBtn.setEnabled(false);
            signInBtn.setTextColor(Color.argb(50,255,255,255));
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right,R.anim.slideout_from_left);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();
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
