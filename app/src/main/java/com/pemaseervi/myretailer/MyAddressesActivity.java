package com.pemaseervi.myretailer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.pemaseervi.myretailer.Adapter.AddressAdapter;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.utility.AppPref;
import com.pemaseervi.myretailer.utility.CommonApiConstant;
import com.pemaseervi.myretailer.utility.VolleyMultipartRequest;
import com.pemaseervi.myretailer.utility.VolleySingleton;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.pemaseervi.myretailer.DeliveryActivity.SELECT_ADDRESS;
import static com.pemaseervi.myretailer.ProductDetailsActivity.addToCart;

public class MyAddressesActivity extends AppCompatActivity {

    private int previousAddress;
    private LinearLayout addNewAddressBtn;
    private RecyclerView myAddressesRecyclerView;
    public static TextView addressesSaved;
    public static AddressAdapter addressAdapter;
    public static Button deliverHereBtn;
    private Dialog loadingDialog;
    private int mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.my_addresses_heading);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myAddressesRecyclerView= findViewById(R.id.addresses_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        myAddressesRecyclerView.setLayoutManager(linearLayoutManager);
        deliverHereBtn=findViewById(R.id.deliver_here_btn);
        loadingDialog= new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        deliverHereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DBQueries.selectedAddress !=previousAddress){
                    final int previousAddressIndex = previousAddress;
                    loadingDialog.show();
                    Map<String,String> updateSelection= new HashMap<>();
                    updateSelection.put("previous_address_id",DBQueries.addressModelList.get(previousAddressIndex).getAddress_id());
                    updateSelection.put("current_address_id",DBQueries.addressModelList.get(DBQueries.selectedAddress).getAddress_id());
                    updateSelection.put("user_id", CommonApiConstant.currentUser);
                    previousAddress= DBQueries.selectedAddress;
                    String URL= CommonApiConstant.backEndUrl+"/user/select-address";

                    VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.PUT, URL, new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            if(loadingDialog !=null){
                                loadingDialog.dismiss();
                            }
                            if(addToCart !=null){
                                addToCart.setEnabled(true);
                            }
                            String resultResponse = new String(response.data);
                            try {
                                JSONObject result = new JSONObject(resultResponse);
                                finish();
                               } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            previousAddress=previousAddressIndex;
                            if(loadingDialog !=null){
                                loadingDialog.dismiss();
                            }
                            if(addToCart !=null){
                                addToCart.setEnabled(true);
                            }
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
                            Toast.makeText(MyAddressesActivity.this,errorMessage,Toast.LENGTH_LONG).show();
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
                            return updateSelection;
                        }
                    };
                    VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest1);
//
//                    FirebaseFirestore.getInstance().collection("USERS")
//                            .document(FirebaseAuth.getInstance().getUid())
//                            .collection("USER_DATA")
//                            .document("MY_ADDRESSES")
//                            .update(updateSelection)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if(task.isSuccessful()){
//                                        finish();
//                                    }else{
//                                        previousAddress=previousAddressIndex;
//                                        String error =  task.getException().getMessage();
//                                        Toast.makeText(MyAddressesActivity.this,error,Toast.LENGTH_SHORT).show();
//                                    }
//                                    loadingDialog.dismiss();
//                                }
//                            });
                }else{
                    finish();
                }
            }
        });
        addNewAddressBtn= findViewById(R.id.add_new_address_btn);
        addressesSaved = findViewById(R.id.address_saved);
        previousAddress= DBQueries.selectedAddress;
       mode = getIntent().getIntExtra("MODE",-1);
       if(mode == SELECT_ADDRESS){
           deliverHereBtn.setVisibility(View.VISIBLE);
       }else{
           deliverHereBtn.setVisibility(View.GONE);
       }
        addressAdapter = new AddressAdapter(DBQueries.addressModelList,mode);
        myAddressesRecyclerView.setAdapter(addressAdapter);
        ((SimpleItemAnimator)myAddressesRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        addressAdapter.notifyDataSetChanged();
        addNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addAddressIntent= new Intent(MyAddressesActivity.this,AddAddressActivity.class);
                addAddressIntent.putExtra("INTENT","null");
                addAddressIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(addAddressIntent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        addressesSaved.setText(String.valueOf(DBQueries.addressModelList.size())+" "+getResources().getString(R.string.saved_addresses_msg));
    }

    public static void refreshItem(int deselect, int select){
        addressAdapter.notifyItemChanged(deselect);
        addressAdapter.notifyItemChanged(select);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();
        if(id== android.R.id.home){
            if(mode == SELECT_ADDRESS) {
                if (DBQueries.selectedAddress != previousAddress) {
                    DBQueries.addressModelList.get(DBQueries.selectedAddress).setSelected(false);
                    DBQueries.addressModelList.get(previousAddress).setSelected(true);
                    DBQueries.selectedAddress = previousAddress;
                }
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mode == SELECT_ADDRESS){
            if(DBQueries.selectedAddress != previousAddress){
                DBQueries.addressModelList.get(DBQueries.selectedAddress).setSelected(false);
                DBQueries.addressModelList.get(previousAddress).setSelected(true);
                DBQueries.selectedAddress= previousAddress;
            }
        }
        super.onBackPressed();
    }
}
