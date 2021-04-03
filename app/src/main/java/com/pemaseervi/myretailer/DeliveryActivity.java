package com.pemaseervi.myretailer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.pemaseervi.myretailer.Model.CartItemModel;
import com.pemaseervi.myretailer.utility.AppPref;
import com.pemaseervi.myretailer.utility.CommonApiConstant;
import com.pemaseervi.myretailer.utility.Utility;
import com.pemaseervi.myretailer.utility.VolleyMultipartRequest;
import com.pemaseervi.myretailer.utility.VolleySingleton;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DeliveryActivity extends AppCompatActivity {

    public static List<CartItemModel> cartItemModelList;
    private RecyclerView deliveryRecyclerView;
    private Button changeOrAddAddressBtn;
    public static final int SELECT_ADDRESS=0;
    public static TextView totalAmount;

    private TextView fullName;
    private String name,mobileNumber,flatNumber,locality,landmark,city,state;
    private TextView fullAddress;
    private TextView pincode;
    private Button continueBtn;
    private String paymentMethod="PAYTM";
    private Dialog loadingDialog,paymentMethodDialog;
    private ImageView paytm,cod;
    private TextView continueShoppingBtn;
    private ConstraintLayout orderConfirmationLayout;
    private TextView orderId;
    private boolean successResponse=false;
    public static boolean fromCart =false;
    private  String order_id;
    public static boolean codOrderConfirm=false;
    public static int totalCartItems=0;
    public static int totalCartAmount=0;
    public static CartAdapter cartAdapter;
    private Socket iSocket;
    public static String orderImage;
    private static final String URL_SOCKET = CommonApiConstant.backEndUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.delivery_heading);
        loadingDialog= new Dialog(DeliveryActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        paymentMethodDialog= new Dialog(DeliveryActivity.this);
        paymentMethodDialog.setContentView(R.layout.payment_method);
        paymentMethodDialog.setCancelable(true);
        paymentMethodDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        cod=paymentMethodDialog.findViewById(R.id.cod_btn);
        paytm=paymentMethodDialog.findViewById(R.id.paytm_logo_btn);

        orderConfirmationLayout= findViewById(R.id.order_confirmation_layout);
        orderId= findViewById(R.id.order_id);
        continueShoppingBtn= findViewById(R.id.continue_shopping_btn);
        totalAmount= findViewById(R.id.total_delivery_amount);
        deliveryRecyclerView = findViewById(R.id.delivery_recycler_viiew);
        continueBtn = findViewById(R.id.delivery_continue_btn);


        changeOrAddAddressBtn=findViewById(R.id.chnage_or_add_address_btn);
        fullName= findViewById(R.id.fullName);
        fullAddress= findViewById(R.id.address);
        pincode=findViewById(R.id.pincode);

        try {
            IO.Options opts = new IO.Options();
//            opts.query = "auth_token=" + authToken;
            iSocket = IO.socket(URL_SOCKET);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        iSocket.connect();



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deliveryRecyclerView.setLayoutManager(linearLayoutManager);
        cartAdapter = new CartAdapter(cartItemModelList,totalAmount,false,true);
        deliveryRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();
        changeOrAddAddressBtn.setVisibility(View.VISIBLE);
        LinearLayout parent =(LinearLayout) totalAmount.getParent().getParent();
        parent.setVisibility(View.VISIBLE);
        totalCartItems = 0;
        totalCartAmount = 0;
        int sz=(int)DBQueries.cartItemModelList.size();
        if(sz>0){
            for(int x=0;x<sz;x++){
                totalCartItems +=DBQueries.cartItemModelList.get(x).getProductQuantityForOrder();
                totalCartAmount+= (DBQueries.cartItemModelList.get(x).getProductQuantityForOrder() * Integer.parseInt(DBQueries.cartItemModelList.get(x).getSellingPrice().toString() ));
            }
        }else if(cartItemModelList.size()!=0){
            totalCartItems +=cartItemModelList.get(0).getProductQuantityForOrder();
            totalCartAmount+= (cartItemModelList.get(0).getProductQuantityForOrder() * Integer.parseInt(cartItemModelList.get(0).getSellingPrice().toString() ));

        }
        totalAmount.setText(Utility.changeToIndianCurrency(totalCartAmount));
        changeOrAddAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myAddressesActivity = new Intent(DeliveryActivity.this,MyAddressesActivity.class);
                myAddressesActivity.putExtra("MODE",SELECT_ADDRESS);
                myAddressesActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(myAddressesActivity);
            }
        });
        if(DBQueries.addressModelList.size()>0){
            name=DBQueries.addressModelList.get(DBQueries.selectedAddress).getName();
            mobileNumber=DBQueries.addressModelList.get(DBQueries.selectedAddress).getMobileNumber();
            if(DBQueries.addressModelList.get(DBQueries.selectedAddress).getAlternateMobileNumber().equals("")){
                fullName.setText(name+" - "+mobileNumber);
            }else{
                fullName.setText(name+" - "+mobileNumber + " or "+DBQueries.addressModelList.get(DBQueries.selectedAddress).getAlternateMobileNumber());
            }
            flatNumber= DBQueries.addressModelList.get(DBQueries.selectedAddress).getFlatNumber();
            locality= DBQueries.addressModelList.get(DBQueries.selectedAddress).getLocality();
            landmark= DBQueries.addressModelList.get(DBQueries.selectedAddress).getLandmark();
            city= DBQueries.addressModelList.get(DBQueries.selectedAddress).getCity();
            state= DBQueries.addressModelList.get(DBQueries.selectedAddress).getState();
            if(landmark.equals("")){
                fullAddress.setText(flatNumber+", "+locality+", "+city+", "+state);
            }else{
                fullAddress.setText(flatNumber+", "+locality+", "+landmark+", "+city+", "+state);
            }
            pincode.setText(DBQueries.addressModelList.get(DBQueries.selectedAddress).getPincode());
        }
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(DBQueries.cartItemModelList.size()==0){
//                    finish();
//                    Toast.makeText(DeliveryActivity.this,getResources().getString(R.string.no_cart_items_found).toString(),Toast.LENGTH_SHORT).show();
//                }else{
                    paymentMethodDialog.show();
//                }
            }
        });
        continueBtn.setVisibility(View.VISIBLE);
        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              paymentMethod="COD";
                try {
                    placeOrderDetails();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentMethod="PAYTM";
                try {
                    placeOrderDetails();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(DBQueries.addressModelList.size()>0){
            name=DBQueries.addressModelList.get(DBQueries.selectedAddress).getName();
            mobileNumber=DBQueries.addressModelList.get(DBQueries.selectedAddress).getMobileNumber();
            if(DBQueries.addressModelList.get(DBQueries.selectedAddress).getAlternateMobileNumber().equals("")){
                fullName.setText(name+" - "+mobileNumber);
            }else{
                fullName.setText(name+" - "+mobileNumber + " or "+DBQueries.addressModelList.get(DBQueries.selectedAddress).getAlternateMobileNumber());
            }
            flatNumber= DBQueries.addressModelList.get(DBQueries.selectedAddress).getFlatNumber();
            locality= DBQueries.addressModelList.get(DBQueries.selectedAddress).getLocality();
            landmark= DBQueries.addressModelList.get(DBQueries.selectedAddress).getLandmark();
            city= DBQueries.addressModelList.get(DBQueries.selectedAddress).getCity();
            state= DBQueries.addressModelList.get(DBQueries.selectedAddress).getState();
            if(landmark.equals("")){
                fullAddress.setText(flatNumber+", "+locality+", "+city+", "+state);
            }else{
                fullAddress.setText(flatNumber+", "+locality+", "+landmark+", "+city+", "+state);
            }
            pincode.setText(DBQueries.addressModelList.get(DBQueries.selectedAddress).getPincode());
        }
        if(codOrderConfirm){
            showConfirmationLayout();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        if(successResponse){
            finish();
            return;
        }
        super.onBackPressed();

    }
    private void showConfirmationLayout(){
        codOrderConfirm=false;
        successResponse=true;
        iSocket.emit("new-order",order_id);
//        String SMS_API ="https://www.fast2sms.com/dev/bulk";
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, SMS_API, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> headers= new HashMap<>();
//                headers.put("authorization","UXd0BpiT8FK74QthMaZJkzsbnleCr2RHjxSyu53PqAYwf1mvgIVbS4KNwE8WpJCIh61tFuAL5Zz0ykGn");
//                return headers;
//            }
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> body= new HashMap<>();
//                body.put("sender_id","FSTSMS");
//                body.put("language","english");
//                body.put("route","qt");
//                body.put("numbers",mobileNumber);
//                body.put("message","6706");
//                body.put("variables","{#FF#}");
//                body.put("variables_values", order_id);
//                return body;
//            }
//        };
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                5000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//        ));
//        RequestQueue requestQueue = Volley.newRequestQueue(DeliveryActivity.this);
//        requestQueue.add(stringRequest);



        if(HomeActivity.mainActivity!=null){
            HomeActivity.mainActivity.finish();
            HomeActivity.mainActivity = null;
            HomeActivity.showCart=false;
        }
        if(ProductDetailsActivity.productDetailsActivity!=null){
            ProductDetailsActivity.productDetailsActivity.finish();
            ProductDetailsActivity.productDetailsActivity = null;
        }
        if(fromCart){
//            loadingDialog.show();
            Map<String,Object> updateCartList = new HashMap<>();
            long cartListSize=0;
            final List<Integer> indexList = new ArrayList<>();
            JSONArray cartItems=new JSONArray(DBQueries.cartItemModelList);
            Map<String,String> removeItemFromCart= new HashMap<>();
            removeItemFromCart.put("no_of_cart_items",String.valueOf(DBQueries.cartItemModelList.size()));
            for(int count=0;count<(DBQueries.cartItemModelList.size());count++){
                removeItemFromCart.put("product_id_"+count,DBQueries.cartItemModelList.get(count).getProductTitle());

            }
            removeItemFromCart.put("user_id",CommonApiConstant.currentUser);
            String URL= CommonApiConstant.backEndUrl+"/user/remove-all-items-from-cart";

            VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.PUT, URL, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {
                    loadingDialog.dismiss();
                    String resultResponse = new String(response.data);
                    CartActivity.cartAdapter.notifyDataSetChanged();
                    DeliveryActivity.cartAdapter.notifyDataSetChanged();

                    try {
                        JSONObject result = new JSONObject(resultResponse);
                        DBQueries.cartList.clear();
                        DBQueries.cartItemModelList.clear();
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
                    Toast.makeText(DeliveryActivity.this,errorMessage,Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization","Bearer "+ AppPref.getUserToken(DeliveryActivity.this));
                    return headers;
                }
                @Override
                protected Map<String, String> getParams() {
                    return removeItemFromCart;
                }
            };
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest1);


//            FirebaseFirestore.getInstance().collection("USERS")
//                    .document(FirebaseAuth.getInstance().getUid())
//                    .collection("USER_DATA")
//                    .document("MY_CART")
//                    .set(updateCartList)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(task.isSuccessful()){
//                                for(int x=0;x<indexList.size();x++){
//                                    DBQueries.cartList.remove(indexList.get(x).intValue());
//                                    DBQueries.cartItemModelList.remove(indexList.get(x).intValue());
//                                    DBQueries.cartItemModelList.remove(DBQueries.cartItemModelList.size()-1);
//                                }
//                                CartActivity.cartAdapter.notifyDataSetChanged();
//                                DeliveryActivity.cartAdapter.notifyDataSetChanged();
//                            }else{
//                                String error = task.getException().getMessage();
//                                Toast.makeText(DeliveryActivity.this,error,Toast.LENGTH_SHORT).show();
//                            }
//                            loadingDialog.dismiss();
//                        }
//                    });
        }
        continueBtn.setEnabled(false);
        changeOrAddAddressBtn.setEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        orderId.setText(getResources().getString( R.string.order_id) +order_id);
        orderConfirmationLayout.setVisibility(View.VISIBLE);
        continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent mainIntent= new Intent(DeliveryActivity.this,HomeActivity.class);
                startActivity(mainIntent);

            }
        });

    }
    private void placeOrderDetails() throws UnsupportedEncodingException {
        Random rand = new Random();
//        UUID.randomUUID().toString().substring(0,28)
        order_id = String.valueOf(rand.nextInt(1000000));;
        String userId=CommonApiConstant.currentUser;
        loadingDialog.show();
//        for (CartItemModel cartItemModel:cartItemModelList){
            int sz=cartItemModelList.size();
             Map<String,String> orderDetails =  new HashMap<>();
             orderDetails.put("order_id",order_id);
             orderDetails.put("user_id",userId);
             orderDetails.put("no_of_items",String.valueOf(sz-1));
            for(int count=0;count<sz;count++){
                CartItemModel cartItemModel=cartItemModelList.get(count);
            if(cartItemModel.getType() ==   CartItemModel.CART_ITEM){


                orderDetails.put("product_id_"+count,cartItemModel.getProductTitle());

                orderDetails.put("product_quantity_"+count,String.valueOf(cartItemModel.getProductQuantityForOrder()));
//                orderDetails.put("Cutted Price",order_id);
                orderDetails.put("product_price_"+count,cartItemModel.getSellingPrice());
//                orderDetails.put("Coupen Id",order_id);
//                orderDetails.put("Discounted  Price",order_id);
                orderDetails.put("product_catalog_"+count,cartItemModel.getProduct_catalog());
                orderDetails.put("product_category_"+count,cartItemModel.getProduct_category());
                orderDetails.put("product_image_"+count,cartItemModel.getProduct_image());
                orderDetails.put("product_title_"+count,cartItemModel.getProductTitle());
                orderDetails.put("product_hindi_title_"+count, URLEncoder.encode(  cartItemModel.getProductHindiTitle(), "utf-8"));

                if(count==sz-2){
                    String URL= CommonApiConstant.backEndUrl+"/order/place-order-items";

                    VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            if(loadingDialog !=null){
                                loadingDialog.dismiss();
                            }
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
                            if(loadingDialog !=null){
                                loadingDialog.dismiss();
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
                            Toast.makeText(DeliveryActivity.this,errorMessage,Toast.LENGTH_LONG).show();
                        }
                    }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Authorization","Bearer "+ AppPref.getUserToken(DeliveryActivity.this));
                            return headers;
                        }
                        @Override
                        protected Map<String, String> getParams() {
                            return orderDetails;
                        }
                    };
                    VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest1);
                }

            }else{
                Map<String,String> orderDetail = new HashMap<>();

                orderDetail.put("total_items",String.valueOf(totalCartItems));
                orderDetail.put("delivery_price",String.valueOf(cartItemModel.getDeliveryPrice()));
                orderDetail.put("total_amount",String.valueOf(totalCartAmount));
                orderDetail.put("saved_amount",String.valueOf(cartItemModel.getSavedAmount()));
                orderDetail.put("payment_status","not paid");
                orderDetail.put("order_id",order_id);
//                orderDetails.put("Order Status","Ordered");
                orderDetail.put("user_id",userId);
                orderDetail.put("order_status","Pending");
                orderDetail.put("order_image",cartItemModelList.get(0).getProduct_image());
                orderImage=cartItemModelList.get(0).getProduct_image();
                orderDetail.put("payment_method",paymentMethod);
                orderDetail.put("flat_number",URLEncoder.encode(DBQueries.addressModelList.get(DBQueries.selectedAddress).getFlatNumber(),"utf-8"));
                orderDetail.put("locality",URLEncoder.encode(DBQueries.addressModelList.get(DBQueries.selectedAddress).getLocality(),"utf-8"));
                orderDetail.put("landmark",URLEncoder.encode(DBQueries.addressModelList.get(DBQueries.selectedAddress).getLandmark(),"utf-8"));
                orderDetail.put("city",URLEncoder.encode(DBQueries.addressModelList.get(DBQueries.selectedAddress).getCity(),"utf-8"));
                orderDetail.put("state",DBQueries.addressModelList.get(DBQueries.selectedAddress).getState());
                orderDetail.put("full_name",URLEncoder.encode(DBQueries.addressModelList.get(DBQueries.selectedAddress).getName(),"utf-8"));
                orderDetail.put("mobile_number",DBQueries.addressModelList.get(DBQueries.selectedAddress).getMobileNumber());
                orderDetail.put("alternate_mobile",DBQueries.addressModelList.get(DBQueries.selectedAddress).getAlternateMobileNumber());
                orderDetail.put("pincode",pincode.getText().toString());
                String URL= CommonApiConstant.backEndUrl+"/order/place-order";

                VolleyMultipartRequest multipartRequest2 = new VolleyMultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        if(loadingDialog !=null){
                            loadingDialog.dismiss();
                        }
                        String resultResponse = new String(response.data);
                        try {
                            JSONObject result = new JSONObject(resultResponse);
                            if(paymentMethod.equals("PAYTM")){
                                        paytm();
                        }else{
                            cod();
                        }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(loadingDialog !=null){
                            loadingDialog.dismiss();
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
                        Toast.makeText(DeliveryActivity.this,errorMessage,Toast.LENGTH_LONG).show();
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization","Bearer "+ AppPref.getUserToken(DeliveryActivity.this));
                        return headers;
                    }
                    @Override
                    protected Map<String, String> getParams() {
                        return orderDetail;
                    }
                };
                VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest2);
//                FirebaseFirestore.getInstance().collection("ORDERS")
//                        .document(order_id)
//                        .set(orderDetails)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if(task.isSuccessful()){
//                                    if(paymentMethod.equals("PAYTM")){
//                                        paytm();
//                                    }else{
//                                        cod();
//                                    }
//                                }else{
//                                    String error = task.getException().getMessage();
//                                    Toast.makeText(DeliveryActivity.this,error,Toast.LENGTH_SHORT).show();
//
//                                }
//                            }
//                        });
            }
        }
    }

    private void paytm(){
        paymentMethodDialog.dismiss();
        loadingDialog.show();
        if(ContextCompat.checkSelfPermission(DeliveryActivity.this, Manifest.permission.READ_SMS )!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(DeliveryActivity.this,new String[]{Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS},101);
        }
        final String M_id="asllBy60809082502356";
        int size_of_current_user=CommonApiConstant.currentUser.length();
        final String customer_id= CommonApiConstant.currentUser.substring(1,size_of_current_user-1);
        String url="https://myretailer.000webhostapp.com/paytm/generateChecksum.php";
        final String callbackUrl="https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

        RequestQueue requestQueue = Volley.newRequestQueue(DeliveryActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Response.Listener(){

            @Override
            public void onResponse(Object response) {
                try {
                    JSONObject jsonObject = new JSONObject((String) response);
                    if(jsonObject.has("CHECKSUMHASH")){
                        String CHECKSUMHASH = jsonObject.getString("CHECKSUMHASH");

                        PaytmPGService paytmPGService = PaytmPGService.getStagingService();
                        HashMap<String, String> paramMap = new HashMap<String,String>();
                        paramMap.put( "MID" , M_id);
                        // Key in your staging and production MID available in your dashboard
                        paramMap.put( "ORDER_ID" , order_id);
                        paramMap.put( "CUST_ID" , customer_id);
                        paramMap.put( "CHANNEL_ID" , "WAP");
                        paramMap.put( "TXN_AMOUNT" , totalAmount.getText().toString().substring(3,totalAmount.getText().length()-2));
                        paramMap.put( "WEBSITE" , "WEBSTAGING");
                        // This is the staging value. Production value is available in your dashboard
                        paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
                        // This is the staging value. Production value is available in your dashboard
                        paramMap.put( "CALLBACK_URL", callbackUrl);
                        paramMap.put("CHECKSUMHASH",CHECKSUMHASH);

                        PaytmOrder paytmOrder = new PaytmOrder(paramMap);

                        paytmPGService.initialize(paytmOrder,null);
                        paytmPGService.startPaymentTransaction(DeliveryActivity.this, true, true, new PaytmPaymentTransactionCallback() {
                            @Override
                            public void onTransactionResponse(Bundle inResponse) {
//                                        Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                                if(inResponse.getString("STATUS").equals("TXN_SUCCESS")) {
                                    Map<String,String> updateStatus = new HashMap<>();
                                    updateStatus.put("payment_status","Paid");
                                    updateStatus.put("order_status","Ordered");
                                    updateStatus.put("user_id",CommonApiConstant.currentUser);
                                    updateStatus.put("order_id",order_id);
                                    String URL= CommonApiConstant.backEndUrl+"/order/details-after-payment";

                                    VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.PUT, URL, new Response.Listener<NetworkResponse>() {
                                        @Override
                                        public void onResponse(NetworkResponse response) {
                                            if(loadingDialog !=null){
                                                loadingDialog.dismiss();
                                            }
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
                                                        if(loadingDialog !=null){
                                                            loadingDialog.dismiss();
                                                        }
                                                        String resultResponse = new String(response.data);

                                                        try {
                                                            JSONObject result = new JSONObject(resultResponse);
                                                            String URL= CommonApiConstant.backEndUrl+"/fcm/send-push-notification";
                                                            Map<String,String> notificationDetails = new HashMap<>();
                                                            notificationDetails.put("title","Order id: "+order_id);
                                                            notificationDetails.put("message","Congrats, you got new order.");
                                                            notificationDetails.put("image",DeliveryActivity.orderImage);
                                                            notificationDetails.put("user_id","+918442084369");
//                                                            notificationDetails.put("order_id",order_id);
//                                                            notificationDetails.put("customer_user_id",CommonApiConstant.currentUser);

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
                                                                    Toast.makeText(DeliveryActivity.this,errorMessage,Toast.LENGTH_LONG).show();
                                                                }
                                                            }){
                                                                @Override
                                                                public Map<String, String> getHeaders() throws AuthFailureError {
                                                                    Map<String, String> headers = new HashMap<>();
                                                                    headers.put("Authorization","Bearer "+ AppPref.getUserToken(DeliveryActivity.this));
                                                                    return headers;
                                                                }
                                                                @Override
                                                                protected Map<String, String> getParams() {
                                                                    return notificationDetails;
                                                                }
                                                            };
                                                            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest3);







                                                            showConfirmationLayout();

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        if(loadingDialog !=null){
                                                            loadingDialog.dismiss();
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
                                                        Toast.makeText(DeliveryActivity.this,errorMessage,Toast.LENGTH_LONG).show();
                                                    }
                                                }){
                                                    @Override
                                                    protected Map<String, String> getParams() {
                                                        return updateUserOrder;
                                                    }
                                                    @Override
                                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                                        Map<String, String> headers = new HashMap<>();
                                                        headers.put("Authorization","Bearer "+ AppPref.getUserToken(DeliveryActivity.this));
                                                        return headers;
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
                                            if(loadingDialog !=null){
                                                loadingDialog.dismiss();
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
                                            Toast.makeText(DeliveryActivity.this,errorMessage,Toast.LENGTH_LONG).show();
                                        }
                                    }){
                                        @Override
                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                            Map<String, String> headers = new HashMap<>();
                                            headers.put("Authorization","Bearer "+ AppPref.getUserToken(DeliveryActivity.this));
                                            return headers;
                                        }
                                        @Override
                                        protected Map<String, String> getParams() {
                                            return updateStatus;
                                        }
                                    };
                                    VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest1);


//
//                                    FirebaseFirestore.getInstance().collection("ORDERS")
//                                            .document(order_id)
//                                            .update(updateStatus)
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if(task.isSuccessful()){
//                                                        Map<String,Object> userOrder = new HashMap<>();
//                                                        userOrder.put("order_id",order_id);
//                                                        FirebaseFirestore.getInstance().collection("USERS")
//                                                                .document(FirebaseAuth.getInstance().getUid())
//                                                                .collection("USER_ORDERS")
//                                                                .document(order_id)
//                                                                .set(userOrder)
//                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                    @Override
//                                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                                        if(task.isSuccessful()){
//                                                                            showConfirmationLayout();
//                                                                        }else{
//                                                                            Toast.makeText(getApplicationContext(), R.string.order_list_saving_failure_msg, Toast.LENGTH_LONG).show();
//
//                                                                        }
//                                                                    }
//                                                                });
//                                                    }else {
//                                                        Toast.makeText(getApplicationContext(), R.string.order_cancelled_msg, Toast.LENGTH_LONG).show();
//
//                                                    }
//                                                }
//                                            });
                                }
                            }

                            @Override
                            public void networkNotAvailable() {
                                Toast.makeText(getApplicationContext(), R.string.network_not_available_msg, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void clientAuthenticationFailed(String inErrorMessage) {
                                Toast.makeText(getApplicationContext(), R.string.client_authentication_failed_error + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void someUIErrorOccurred(String inErrorMessage) {
                                Toast.makeText(getApplicationContext(), R.string.ui_error_msg + inErrorMessage , Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                                Toast.makeText(getApplicationContext(), R.string.error_loading_web_page_msg + inErrorMessage.toString(), Toast.LENGTH_LONG).show();

                            }

                            @Override
                            public void onBackPressedCancelTransaction() {
                                Toast.makeText(getApplicationContext(), R.string.transaction_cancelled_msg , Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                                Toast.makeText(getApplicationContext(), R.string.transaction_cancelled_msg+inResponse.toString() , Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                Toast.makeText(DeliveryActivity.this,R.string.something_went_wrong_msg,Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> paramMap = new HashMap<String,String>();
                paramMap.put( "MID" , M_id);
                // Key in your staging and production MID available in your dashboard
                paramMap.put( "ORDER_ID" , order_id);
                paramMap.put( "CUST_ID" , customer_id);
                paramMap.put( "CHANNEL_ID" , "WAP");
                paramMap.put( "TXN_AMOUNT" , totalAmount.getText().toString().substring(3,totalAmount.getText().length()-2));
                paramMap.put( "WEBSITE" , "WEBSTAGING");
                // This is the staging value. Production value is available in your dashboard
                paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
                // This is the staging value. Production value is available in your dashboard
                paramMap.put( "CALLBACK_URL", callbackUrl);
                return paramMap;
            }
        };
        requestQueue.add(stringRequest);

    }
    private void cod(){
        paymentMethodDialog.dismiss();
        Intent otpIntent = new Intent(DeliveryActivity.this,OtpVerificationActivity.class);
        otpIntent.putExtra("mobileNumber",mobileNumber.substring(0,10));
        otpIntent.putExtra("OrderID",order_id);
        otpIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(otpIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(DBQueries.addressModelList.size()==0){
            finish();
            return;
        }
    }
}
