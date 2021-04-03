package com.pemaseervi.myretailer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import com.pemaseervi.myretailer.Model.AddressModel;
import com.pemaseervi.myretailer.utility.AppPref;
import com.pemaseervi.myretailer.utility.CommonApiConstant;
import com.pemaseervi.myretailer.utility.VolleyMultipartRequest;
import com.pemaseervi.myretailer.utility.VolleySingleton;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class AddAddressActivity extends AppCompatActivity {

    private Button saveBtn;
    private TextView locality;
    private TextView flatNumber;
    private TextView pincode;
    private TextView landmark;
    private TextView name;
    private TextView mobileNumber;
    private TextView alterMobileNumber;
    private TextView city;
    private Spinner stateSpinner;
    private Dialog loadingDialog;
    private String [] stateList;
    private String selectedState;
    private boolean updateAddress = false;
    private AddressModel addressModel;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.add_new_address);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadingDialog= new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        city=findViewById(R.id.city);
        locality=findViewById(R.id.locality);
        flatNumber=findViewById(R.id.flat_number);
        pincode=findViewById(R.id.pincode);
        landmark=findViewById(R.id.landmark);
        name=findViewById(R.id.name);
        saveBtn= findViewById(R.id.continue_in_english_btn);
        mobileNumber=findViewById(R.id.mobile_number);
        alterMobileNumber=findViewById(R.id.alternate_mobille_number);
        stateSpinner= findViewById(R.id.state);
        stateList= getResources().getStringArray(R.array.india_states);
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,stateList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        stateSpinner.setAdapter(spinnerAdapter);
        int spinnerPosition=spinnerAdapter.getPosition("Rajasthan");
        stateSpinner.setSelection(spinnerPosition);

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               selectedState=stateList[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if(getIntent().getStringExtra("INTENT").equals("update_address")){
            updateAddress=true;
            position= getIntent().getIntExtra("position",-1);
            addressModel =DBQueries.addressModelList.get(position);

            city.setText(addressModel.getCity());
            locality.setText(addressModel.getLocality());
            flatNumber.setText(addressModel.getFlatNumber());
            landmark.setText(addressModel.getLandmark());
            name.setText(addressModel.getName());
            mobileNumber.setText(addressModel.getMobileNumber());
            alterMobileNumber.setText(addressModel.getAlternateMobileNumber());
            pincode.setText(addressModel.getPincode());

            for(int i=0;i<stateList.length;i++){
                if(stateList[i].equals(addressModel.getState())){
                    stateSpinner.setSelection(i);
                }
            }
            saveBtn.setText(getResources().getString(R.string.update_btn));
        }else{
           position= (int)DBQueries.addressModelList.size();
        }


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable customErrorIcon = getResources().getDrawable(R.mipmap.custom_error_icon);
                customErrorIcon.setBounds(0,0,customErrorIcon.getIntrinsicWidth(),customErrorIcon.getIntrinsicHeight());

                if(!TextUtils.isEmpty(city.getText())){
                    if(!TextUtils.isEmpty(locality.getText())){
                        if(!TextUtils.isEmpty(flatNumber.getText())){
                            if(!TextUtils.isEmpty(pincode.getText()) && pincode.getText().length() ==6){
                                if(!TextUtils.isEmpty(name.getText())){
                                    if(!TextUtils.isEmpty(mobileNumber.getText()) && mobileNumber.getText().length() ==10 ){
                                        loadingDialog.show();
                                        String address_id= UUID.randomUUID().toString().substring(0,28);
                                        Map<String,String> addAddress = new HashMap();
                                         if(DBQueries.language.equals("hi")){
                                             try {
                                                 addAddress.put("city", URLEncoder.encode(city.getText().toString(),"utf-8"));
                                                 addAddress.put("locality",URLEncoder.encode(locality.getText().toString(),"utf-8"));
                                                 addAddress.put("flat_number",URLEncoder.encode(flatNumber.getText().toString(),"utf-8"));
                                                 addAddress.put("landmark",URLEncoder.encode(landmark.getText().toString(),"utf-8"));
                                                 addAddress.put("name",URLEncoder.encode(name.getText().toString(),"utf-8"));
                                             } catch (UnsupportedEncodingException e) {
                                                 e.printStackTrace();
                                             }
                                         }else{
                                             addAddress.put("city",city.getText().toString());
                                             addAddress.put("locality",locality.getText().toString());
                                             addAddress.put("flat_number",flatNumber.getText().toString());
                                             addAddress.put("landmark",landmark.getText().toString());
                                             addAddress.put("name",name.getText().toString());
                                         }
                                        addAddress.put("state",selectedState);
                                         addAddress.put("pincode",pincode.getText().toString());
                                        addAddress.put("mobile_number",mobileNumber.getText().toString());
                                        addAddress.put("alternate_mobile_number",alterMobileNumber.getText().toString());
                                        addAddress.put("user_id",CommonApiConstant.currentUser);
                                        if(updateAddress){
                                            addAddress.put("address_id",DBQueries.addressModelList.get(position).getAddress_id());
                                        }else{
                                            addAddress.put("address_id",address_id);
                                        }

                                        if(!updateAddress){
                                            addAddress.put("is_selected","true");
                                            if( DBQueries.addressModelList.size() >0){
                                                addAddress.put("is_selected","false");
                                            }
                                        }

                                        String URL= CommonApiConstant.backEndUrl+"/user/address/";
                                        int request_method;
                                        if(updateAddress){
                                            request_method=Request.Method.PUT;
                                            addAddress.put("is_selected",DBQueries.addressModelList.get(position).getSelected().toString());
                                        }else{
                                            request_method=Request.Method.POST;
                                        }

                                        VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(request_method, URL, new Response.Listener<NetworkResponse>() {
                                            @Override
                                            public void onResponse(NetworkResponse response) {
                                                loadingDialog.dismiss();
                                                String resultResponse = new String(response.data);
                                                try {
                                                    JSONObject result = new JSONObject(resultResponse);
                                                     Toast.makeText(AddAddressActivity.this,"Address uploaded successfully.",Toast.LENGTH_LONG).show();
                                                    if(!updateAddress){
                                                        if( DBQueries.addressModelList.size() >0){
                                                            DBQueries.addressModelList.get(DBQueries.selectedAddress).setSelected(false);
                                                        }
                                                        DBQueries.addressModelList.add(new AddressModel(city.getText().toString(),locality.getText().toString(),flatNumber.getText().toString(),pincode.getText().toString(),landmark.getText().toString(),name.getText().toString(),mobileNumber.getText().toString(),alterMobileNumber.getText().toString(),selectedState.toString(),false,address_id));
                                                        DBQueries.selectedAddress=DBQueries.addressModelList.size() -1;
                                                    }else{
                                                        DBQueries.addressModelList.set(position,new AddressModel(city.getText().toString(),locality.getText().toString(),flatNumber.getText().toString(),pincode.getText().toString(),landmark.getText().toString(),name.getText().toString(),mobileNumber.getText().toString(),alterMobileNumber.getText().toString(),selectedState.toString(),false,address_id));

                                                    }

//                                                            if(DBQueries.addressModelList.size() >0){

//                                                            }
                                                    if(getIntent().getStringExtra("INTENT").equals("deliveryIntent")){
                                                        Intent deliveryIntent = new Intent(AddAddressActivity.this,DeliveryActivity.class);
                                                        deliveryIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                                        startActivity(deliveryIntent);
                                                    }else{
                                                        MyAddressesActivity.refreshItem(DBQueries.selectedAddress,DBQueries.addressModelList.size() -1);

                                                    }
                                                    finish();

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                loadingDialog.dismiss();
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
                                                Toast.makeText(AddAddressActivity.this,errorMessage,Toast.LENGTH_LONG).show();
                                            }
                                        }){
                                            @Override
                                            protected Map<String, String> getParams() {
                                                return addAddress;
                                            }
                                            @Override
                                            public Map<String, String> getHeaders() throws AuthFailureError {
                                                Map<String, String> headers = new HashMap<>();
                                                headers.put("Authorization","Bearer "+ AppPref.getUserToken(AddAddressActivity.this));
                                                return headers;
                                            }
                                        };
                                        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest1);
//                                        FirebaseFirestore.getInstance().collection("USERS")
//                                                .document(FirebaseAuth.getInstance().getUid())
//                                                .collection("USER_DATA")
//                                                .document("MY_ADDRESSES")
//                                                .update(addAddress)
//                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                        if(task.isSuccessful()){
//                                                            if(!updateAddress){
//                                                                if( DBQueries.addressModelList.size() >0){
//                                                                    DBQueries.addressModelList.get(DBQueries.selectedAddress).setSelected(false);
//                                                                }
//                                                                DBQueries.addressModelList.add(new AddressModel(city.getText().toString(),locality.getText().toString(),flatNumber.getText().toString(),pincode.getText().toString(),landmark.getText().toString(),name.getText().toString(),mobileNumber.getText().toString(),alterMobileNumber.getText().toString(),selectedState.toString(),true));
//                                                                DBQueries.selectedAddress=DBQueries.addressModelList.size() -1;
//                                                            }else{
//                                                                DBQueries.addressModelList.set(position,new AddressModel(city.getText().toString(),locality.getText().toString(),flatNumber.getText().toString(),pincode.getText().toString(),landmark.getText().toString(),name.getText().toString(),mobileNumber.getText().toString(),alterMobileNumber.getText().toString(),selectedState.toString(),true));
//
//                                                            }
//
////                                                            if(DBQueries.addressModelList.size() >0){
//
////                                                            }
//                                                            if(getIntent().getStringExtra("INTENT").equals("deliveryIntent")){
//                                                                Intent deliveryIntent = new Intent(AddAddressActivity.this,DeliveryActivity.class);
//                                                                deliveryIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                                                                startActivity(deliveryIntent);
//                                                            }else{
//                                                                MyAddressesActivity.refreshItem(DBQueries.selectedAddress,DBQueries.addressModelList.size() -1);
//
//                                                            }
//                                                                finish();
//
//                                                        }else{
//                                                            String error= task.getException().getMessage();
//                                                            Toast.makeText(AddAddressActivity.this,error,Toast.LENGTH_SHORT).show();
//                                                        }
//                                                        loadingDialog.dismiss();
//                                                    }
//                                                });

                                    }else{
                                        mobileNumber.setSelected(true);
                                        mobileNumber.requestFocus();
                                       // Toast.makeText(AddAddressActivity.this,R.string.provide_valid_phone_number,Toast.LENGTH_SHORT).show();
                                        mobileNumber.setError(getResources().getString(R.string.provide_valid_phone_number),customErrorIcon);

                                    }

                                }else{
                                    name.requestFocus();
                                    name.setError(getResources().getString(R.string.name)+" "+getResources().getString(R.string.required_msg),customErrorIcon);

                                }
                            }else{
                                pincode.requestFocus();
                                pincode.setError(getResources().getString(R.string.provide_valid_pincode),customErrorIcon);

//                                Toast.makeText(AddAddressActivity.this,R.string.provide_valid_pincode,Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            flatNumber.requestFocus();
                            flatNumber.setError(getResources().getString(R.string.building)+" "+getResources().getString(R.string.required_msg),customErrorIcon);

                        }
                    }else{
                        locality.requestFocus();
                        locality.setError(getResources().getString(R.string.locality)+" "+getResources().getString(R.string.required_msg),customErrorIcon);

                    }
                }else{
                    city.requestFocus();
                    city.setError(getResources().getString(R.string.city)+" "+getResources().getString(R.string.required_msg),customErrorIcon);
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();
        if(id== android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
