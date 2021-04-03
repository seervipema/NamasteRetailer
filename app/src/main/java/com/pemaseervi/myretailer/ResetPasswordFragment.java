package com.pemaseervi.myretailer;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class ResetPasswordFragment extends Fragment {


    public ResetPasswordFragment() {
        // Required empty public constructor
    }
     private EditText registeredEmail;
     private Button resetPasswordBtn;
     private TextView goBack;
     FrameLayout parentFrameLayout;

     private ViewGroup emailIconContainer; // viewGroup to help in animation
     private ImageView emailIcon;
     private TextView emailIconText;
     private ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_reset_password, container, false);
        registeredEmail=view.findViewById(R.id.forgot_password_email);
        resetPasswordBtn=view.findViewById(R.id.forgot_password_btn);
        goBack=view.findViewById(R.id.forgot_password_goback);
        parentFrameLayout = getActivity().findViewById(R.id.register_framelayout);
        emailIconContainer = view.findViewById(R.id.forgot_password_email_icon_container);
        emailIcon=view.findViewById(R.id.forgot_password_email_icon);
        emailIconText=view.findViewById(R.id.forgot_password_email_icon_text);
        progressBar=view.findViewById(R.id.forgot_passord_progressbar);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        registeredEmail.addTextChangedListener(new TextWatcher() {
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
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new SignInFragment());
            }
        });

        resetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TransitionManager.beginDelayedTransition(emailIconContainer);
                emailIconText.setVisibility(View.GONE);
                TransitionManager.beginDelayedTransition(emailIconContainer); // this is for all the icons and text which will appear and go with effect
                emailIcon.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                resetPasswordBtn.setEnabled(false);
                resetPasswordBtn.setTextColor(Color.argb(50,255,255,255));
                String URL= CommonApiConstant.backEndUrl+"/user/forgot";
                VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String resultResponse = new String(response.data);

                        try {
                            progressBar.setVisibility(View.GONE);
                            JSONObject result = new JSONObject(resultResponse);
                            ScaleAnimation scaleAnimation = new ScaleAnimation(1,0,1,0);
                            scaleAnimation.setDuration(100);
                            scaleAnimation.setInterpolator(new AccelerateInterpolator());
                            scaleAnimation.setRepeatMode(Animation.REVERSE);
                            scaleAnimation.setRepeatCount(1);
                            scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
//                                            emailIconText.setTextColor(getResources().getColor(R.color.successGreen));
//                                            emailIcon.setImageResource(R.mipmap.ic_mail_outline_24px);
//                                            emailIconText.setText("Recovery Email sent successfully. Check your email inbox.");
//                                            TransitionManager.beginDelayedTransition(emailIconContainer);
//                                            emailIconText.setVisibility(View.VISIBLE);
//                                            emailIcon.setVisibility(View.VISIBLE);


                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }

                            });
                            emailIconText.setTextColor(getResources().getColor(R.color.successGreen));
                            emailIcon.setImageResource(R.mipmap.ic_mail_outline_24px);
                            emailIconText.setText(R.string.recovery_email_sent_msg);
                            TransitionManager.beginDelayedTransition(emailIconContainer);
                            emailIconText.setVisibility(View.VISIBLE);
                            emailIcon.setVisibility(View.VISIBLE);




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        progressBar.setVisibility(View.GONE);
                        emailIcon.setImageResource(R.mipmap.red_email);
                        emailIconText.setTextColor(getResources().getColor(R.color.btnRed));
                        TransitionManager.beginDelayedTransition(emailIconContainer);
                        emailIconText.setVisibility(View.VISIBLE);
                        resetPasswordBtn.setEnabled(true);
                        resetPasswordBtn.setTextColor(Color.rgb(255,255,255));

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
                        emailIconText.setText(errorMessage);
                        Toast.makeText(getActivity().getBaseContext(),errorMessage,Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() {

                        Map<String,String>params= new HashMap<>();
                        params.put("email",registeredEmail.getText().toString());
                        return params;

                    }
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization","Bearer "+ AppPref.getUserToken(getActivity().getBaseContext()));
                        return headers;
                    }
                };
                VolleySingleton.getInstance(getActivity().getBaseContext()).addToRequestQueue(multipartRequest1);

            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right,R.anim.slideout_from_left);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }

    private void checkInputs() {
        if(TextUtils.isEmpty(registeredEmail.getText())){
              resetPasswordBtn.setEnabled(false);
              resetPasswordBtn.setTextColor(Color.argb(50,255,255,255));
        }else{
             resetPasswordBtn.setEnabled(true);
             resetPasswordBtn.setTextColor(Color.rgb(255,255,255));
        }
    }
}
