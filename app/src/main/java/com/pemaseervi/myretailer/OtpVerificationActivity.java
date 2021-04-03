package com.pemaseervi.myretailer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.pemaseervi.myretailer.Adapter.CartAdapter;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.utility.AppPref;
import com.pemaseervi.myretailer.utility.CommonApiConstant;
import com.pemaseervi.myretailer.utility.VolleyMultipartRequest;
import com.pemaseervi.myretailer.utility.VolleySingleton;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OtpVerificationActivity extends AppCompatActivity {

    private TextView phoneNumber;
    private EditText otp;
    private Button verifiyBtn;
    private String userNumber;
    public static CartAdapter cartAdapter;
    private Socket iSocket;
    private static final String URL_SOCKET = CommonApiConstant.backEndUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        phoneNumber=findViewById(R.id.phone_number);
        otp= findViewById(R.id.otp);
        verifiyBtn = findViewById(R.id.verify);
        userNumber= getIntent().getStringExtra("mobileNumber");
        try {
            IO.Options opts = new IO.Options();
//            opts.query = "auth_token=" + authToken;
            iSocket = IO.socket(URL_SOCKET);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        iSocket.connect();
        phoneNumber.setText(getResources().getString(R.string.verification_msg) +" "+userNumber);

        Random random = new Random();

        final int otp_number= random.nextInt(999999 - 111111) + 111111;

        String SMS_API ="https://www.fast2sms.com/dev/bulk";

        StringRequest stringRequest = new StringRequest(Request.Method.POST,SMS_API , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                verifiyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (otp.getText().toString().equals(String.valueOf(otp_number))){
                            Map<String,String> updateStatus = new HashMap<>();
                            updateStatus.put("payment_status","Paid");
                            updateStatus.put("order_status","Ordered");
                            updateStatus.put("user_id",CommonApiConstant.currentUser);

                            final String order_id=getIntent().getStringExtra("OrderID");
                            updateStatus.put("order_id",order_id);
                            String URL= CommonApiConstant.backEndUrl+"/order/details-after-payment";

                            VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.PUT, URL, new Response.Listener<NetworkResponse>() {
                                @Override
                                public void onResponse(NetworkResponse response) {

                                    String resultResponse = new String(response.data);
                                    try {
                                        JSONObject result = new JSONObject(resultResponse);
                                        String URL= CommonApiConstant.backEndUrl+"/order/save-user-orders";
                                        Map<String,String> updateUserOrder = new HashMap<>();
                                        updateUserOrder.put("order_id",order_id);
                                        updateUserOrder.put("user_id",CommonApiConstant.currentUser);

                                        VolleyMultipartRequest multipartRequest2 = new VolleyMultipartRequest(Request.Method.PUT, URL, new Response.Listener<NetworkResponse>() {
                                            @Override
                                            public void onResponse(NetworkResponse response) {

                                                String resultResponse = new String(response.data);

                                                try {
//                                                    iSocket.emit("new-order",order_id);
                                                    String URL= CommonApiConstant.backEndUrl+"/fcm/send-push-notification";
                                                    Map<String,String> notificationDetails = new HashMap<>();
                                                    notificationDetails.put("title","Order id# "+order_id);
                                                    notificationDetails.put("message","Congrats, you got new order.");
                                                    notificationDetails.put("image",DeliveryActivity.orderImage);
                                                    notificationDetails.put("user_id","+918442084369");

                                                    VolleyMultipartRequest multipartRequest3 = new VolleyMultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
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
                                                                        DBQueries.signOut(getBaseContext());
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
                                                            Toast.makeText(OtpVerificationActivity.this,errorMessage,Toast.LENGTH_LONG).show();
                                                        }
                                                    }){
                                                        @Override
                                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                                            Map<String, String> headers = new HashMap<>();
                                                            headers.put("Authorization","Bearer "+ AppPref.getUserToken(getBaseContext()));
                                                            return headers;
                                                        }
                                                        @Override
                                                        protected Map<String, String> getParams() {
                                                            return notificationDetails;
                                                        }
                                                    };
                                                    VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest3);

                                                    JSONObject result = new JSONObject(resultResponse);
                                                    DeliveryActivity.codOrderConfirm =true;
                                                    finish();

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
                                                            DBQueries.signOut(getBaseContext());
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
                                                Toast.makeText(OtpVerificationActivity.this,errorMessage,Toast.LENGTH_LONG).show();
                                            }
                                        }){
                                            @Override
                                            public Map<String, String> getHeaders() throws AuthFailureError {
                                                Map<String, String> headers = new HashMap<>();
                                                headers.put("Authorization","Bearer "+ AppPref.getUserToken(getBaseContext()));
                                                return headers;
                                            }
                                            @Override
                                            protected Map<String, String> getParams() {
                                                return updateUserOrder;
                                            }
                                        };
                                        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest2);

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
                                                DBQueries.signOut(getBaseContext());
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
                                    Toast.makeText(OtpVerificationActivity.this,errorMessage,Toast.LENGTH_LONG).show();
                                }
                            }){
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> headers = new HashMap<>();
                                    headers.put("Authorization","Bearer "+ AppPref.getUserToken(getBaseContext()));
                                    return headers;
                                }
                                @Override
                                protected Map<String, String> getParams() {
                                    return updateStatus;
                                }
                            };
                            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest1);




//                            Map<String,Object> updateStatus = new HashMap<>();
//                            updateStatus.put("Order Status","Ordered");
//                            final String order_id=getIntent().getStringExtra("OrderID");
//                            FirebaseFirestore.getInstance().collection("ORDERS")
//                                    .document(order_id)
//                                    .update(updateStatus)
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if(task.isSuccessful()){
//                                                Map<String,Object> userOrder = new HashMap<>();
//                                                userOrder.put("order_id",order_id);
//                                                FirebaseFirestore.getInstance().collection("USERS")
//                                                        .document(FirebaseAuth.getInstance().getUid())
//                                                        .collection("USER_ORDERS")
//                                                        .document(order_id)
//                                                        .set(userOrder)
//                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                if(task.isSuccessful()){
//                                                                    DeliveryActivity.codOrderConfirm =true;
//                                                                    finish();
//                                                                }else{
//                                                                    Toast.makeText(OtpVerificationActivity.this, R.string.order_list_saving_failure_msg, Toast.LENGTH_LONG).show();
//
//                                                                }
//                                                            }
//                                                        });
//                                            }else {
//                                                Toast.makeText(OtpVerificationActivity.this, R.string.order_cancelled_msg, Toast.LENGTH_LONG).show();
//
//                                            }
//                                        }
//                                    });


                        }else{
                            Toast.makeText(OtpVerificationActivity.this,R.string.incorrect_otp_msg,Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              finish();
                Toast.makeText(OtpVerificationActivity.this,R.string.otp_sent_failure_msg,Toast.LENGTH_SHORT).show();
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
                body.put("numbers",userNumber);
                body.put("variables","{#BB#}");
                body.put("variables_values", String.valueOf(otp_number));
                 return body;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        RequestQueue requestQueue = Volley.newRequestQueue(OtpVerificationActivity.this);
        requestQueue.add(stringRequest);


    }
}
