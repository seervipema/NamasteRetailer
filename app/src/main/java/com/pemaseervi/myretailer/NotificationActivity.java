package com.pemaseervi.myretailer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.pemaseervi.myretailer.Adapter.NotificationAdapter;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.utility.AppPref;
import com.pemaseervi.myretailer.utility.CommonApiConstant;
import com.pemaseervi.myretailer.utility.VolleyMultipartRequest;
import com.pemaseervi.myretailer.utility.VolleySingleton;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    public static NotificationAdapter adapter;
    private boolean runQuery= false;
    private LottieAnimationView no_notification_found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.notifications_heading);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView=findViewById(R.id.notification_recycle_view);
        no_notification_found=findViewById(R.id.no_notification_found);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        Map<String,Object> readMap= new HashMap<>();
        int no_of_notifications=DBQueries.notificationModelList.size();
        if(no_of_notifications==0){
            if(no_notification_found!=null){
                no_notification_found.setVisibility(View.VISIBLE);
            }
        }else{
            no_notification_found.setVisibility(View.GONE);
        }
        for(int x=0;x<(DBQueries.notificationModelList.size());x++){
            if(!DBQueries.notificationModelList.get(x).isReaded()){
                runQuery=true;
            }
            readMap.put("Readed_"+x,true);

        }
        if(runQuery){

            String URL= CommonApiConstant.backEndUrl+"/user/notification";
            VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.PUT, URL, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    String resultResponse = new String(response.data);
                    try {
                        JSONObject result = new JSONObject(resultResponse);
                        Toast.makeText(NotificationActivity.this,result.getJSONObject("message").toString(),Toast.LENGTH_LONG).show();

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
                    Toast.makeText(NotificationActivity.this,errorMessage,Toast.LENGTH_LONG).show();
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
                    Map<String, String> params = new HashMap<>();
                    params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
                    params.put("user_id",CommonApiConstant.currentUser);
                    return params;
                }
            };
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest1);

//            FirebaseFirestore.getInstance().collection("USERS")
//                    .document(FirebaseAuth.getInstance().getUid())
//                    .collection("USER_DATA")
//                    .document("MY_NOTIFICATIONS")
//                    .update(readMap)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(task.isSuccessful()){
//
//
//                            }else{
//                                Toast.makeText(NotificationActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
        }
        adapter =  new NotificationAdapter(DBQueries.notificationModelList);
        recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for(int x=0;x<(DBQueries.notificationModelList.size());x++){
            DBQueries.notificationModelList.get(x).setReaded(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id= item.getItemId();
        if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
