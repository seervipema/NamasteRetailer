package com.pemaseervi.myretailer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.pemaseervi.myretailer.Adapter.CategoryWiseProductAdapter;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.Model.CategoryWiseProductModel;
import com.pemaseervi.myretailer.utility.CommonApiConstant;
import com.pemaseervi.myretailer.utility.Utility;
import com.pemaseervi.myretailer.utility.VolleyMultipartRequest;
import com.pemaseervi.myretailer.utility.VolleySingleton;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class CategoryWiseAllProductsActivity extends AppCompatActivity {


    private GridView categoryGridView;
    public static LinearLayout viewCartLinearLayout;
    public static long totalCartAmount=0;
    public static long totalCartItems=0;
    public static Dialog signInDialog;
    public static LinearLayout viewCart;
    public static Dialog loadingDialog;
    private Button viewCartBtn;
    private CategoryWiseProductAdapter categoryWiseProductAdapter;
    public static TextView total_added_products_in_cart;
    public static TextView total_added_products_amount_in_cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_wise_all_products);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final String  title= getIntent().getStringExtra("categoryName");
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        totalCartAmount=0;
        totalCartItems=0;
         viewCart = findViewById(R.id.view_cart);
        viewCartBtn=findViewById(R.id.view_cart_btn);

        signInDialog= new Dialog(CategoryWiseAllProductsActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);
        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Button signInDialogBtn=signInDialog.findViewById(R.id.sign_in_btn);
        Button signUpDialogBtn=signInDialog.findViewById(R.id.sign_up_btn);
        loadingDialog= new Dialog(CategoryWiseAllProductsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);


        final Intent registerIntent = new Intent(CategoryWiseAllProductsActivity.this,RegisterActivity.class);

        signInDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInFragment.disableCloseBtn=true;
                SignUpFragment.disableCloseBtn=true;

                signInDialog.dismiss();
                RegisterActivity.setSignUpFragement=false;
                registerIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(registerIntent);
            }
        });

        signUpDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInFragment.disableCloseBtn=true;
                SignUpFragment.disableCloseBtn=true;
                signInDialog.dismiss();
                RegisterActivity.setSignUpFragement=true;
                registerIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(registerIntent);
            }
        });

        viewCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
////                    if(currentUser ==null){
////                        signInDialog.show();
////                    }else{
                Intent cartIntent= new Intent(view.getContext(), CartActivity.class);
                cartIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                        cartIntent.putExtra("totalCartItems",totalCartItems);
                cartIntent.putExtra("totalCartAmount",totalCartAmount);
//                        HomeActivity.showCart=true;
                view.getContext().startActivity(cartIntent);
                finish();
//
//                    }
//                    Navigation.findNavController(Activity, R.id.nav_host_fragment)
//                    Navigation.findNavController(view).navigate(R.id.nav_cart);
            }
        });

        viewCartLinearLayout= findViewById(R.id.view_cart);
        viewCartLinearLayout.setVisibility(View.GONE);
        categoryGridView= findViewById(R.id.categorywise_grid_view);
        DBQueries.categoryWiseProducts.clear();
        int sz=(int)DBQueries.cartItemModelList.size();
        total_added_products_in_cart=  viewCartLinearLayout.findViewById(R.id.total_added_products_in_cart);
        total_added_products_amount_in_cart=viewCartLinearLayout.findViewById(R.id.total_added_products_amount_in_cart);

        if(DBQueries.cartList.size()>0){
            viewCart.setVisibility(View.VISIBLE);
            total_added_products_in_cart.setText(String.valueOf(totalCartItems)+" "+getResources().getString(R.string.item_count));
            total_added_products_amount_in_cart.setText(Utility.changeToIndianCurrency(totalCartAmount)+" "+getResources().getString(R.string.plus_taxes));
        }
        if(sz>0){
            for(int x=0;x<sz;x++){
               totalCartItems=totalCartItems + DBQueries.cartItemModelList.get(x).getProductQuantityForOrder();
               totalCartAmount= totalCartAmount + (DBQueries.cartItemModelList.get(x).getProductQuantityForOrder() * Integer.parseInt(DBQueries.cartItemModelList.get(x).getSellingPrice().toString() ));
            }
        }
        loadingDialog.show();
        DBQueries.categoryWiseProducts.clear();
        String URL= CommonApiConstant.backEndUrl+"/product/product-category-wise-products/"+getIntent().getStringExtra("categoryName");

        VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.GET, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                loadingDialog.dismiss();
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    int no_of_categories= result.getJSONArray("result").length();
                    DBQueries.categoryWiseProducts.clear();
                    for(int count=0;count<(no_of_categories);count++){
                        int productQuantity=0;
                        int sz=DBQueries.cartItemModelList.size();
                        for(int i=0;i<(sz);i++){
                            if(DBQueries.cartItemModelList.get(i).getProductTitle().trim().equals( result.getJSONArray("result").getJSONObject(count).get("product_name").toString())){
                                productQuantity=DBQueries.cartItemModelList.get(i).getProductQuantityForOrder();
                                break;
                            }
                        }
                        DBQueries.categoryWiseProducts.add(new CategoryWiseProductModel(
                                result.getJSONArray("result").getJSONObject(count).get("product_name").toString(),
                                URLDecoder.decode(   result.getJSONArray("result").getJSONObject(count).get("product_hindi_name").toString(), "utf-8"),
                                Integer.parseInt(result.getJSONArray("result").getJSONObject(count).get("price").toString()),
                                productQuantity,
                                result.getJSONArray("result").getJSONObject(count).get("product_image").toString(),
                                result.getJSONArray("result").getJSONObject(count).get("selling_price").toString(),
                                result.getJSONArray("result").getJSONObject(count).get("catalog_name").toString(),
                                result.getJSONArray("result").getJSONObject(count).get("product_category").toString()
                        ));
                    }
                    if(DBQueries.categoryWiseProducts.size()>0) {
                        categoryWiseProductAdapter = new CategoryWiseProductAdapter(DBQueries.categoryWiseProducts);
                        categoryGridView.setAdapter(categoryWiseProductAdapter);
                        categoryWiseProductAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
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
                Toast.makeText(CategoryWiseAllProductsActivity.this,errorMessage,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
                return params;
            }
        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest1);
//        FirebaseFirestore.getInstance().collection("PRODUCTS")
//                .whereEqualTo("product_category",getIntent().getStringExtra("categoryName"))
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            DBQueries.categoryWiseProducts.clear();
//                            int no_of_products=  (int)task.getResult().getDocuments().size();
//                            for(int x=0;x<(no_of_products);x++){
//                                DBQueries.categoryWiseProducts.add(new CategoryWiseProductModel(
//                                        task.getResult().getDocuments().get(x).get("product_name").toString(),
//                                        task.getResult().getDocuments().get(x).get("product_hindi_name").toString(),
//                                        Integer.parseInt(task.getResult().getDocuments().get(x).get("price").toString()),
//                                        1,
//                                        task.getResult().getDocuments().get(x).get("product_image").toString()
//                                ));
//                            }
//                            if(DBQueries.categoryWiseProducts.size()>0) {
//                                CategoryWiseProductAdapter categoryWiseProductAdapter = new CategoryWiseProductAdapter(DBQueries.categoryWiseProducts);
//                                categoryGridView.setAdapter(categoryWiseProductAdapter);
//                                categoryWiseProductAdapter.notifyDataSetChanged();
//                            }
//                        }else{
//                            String error= task.getException().getMessage();
//                            Toast.makeText(CategoryWiseAllProductsActivity.this,error,Toast.LENGTH_SHORT).show();
//                        }
//                        loadingDialog.dismiss();
//                    }
//                });
//



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_icon, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.category_search_icon){
            Intent searchIntent= new Intent(CategoryWiseAllProductsActivity.this, SearchActivity.class);
            searchIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(searchIntent);

            return true;
        }else if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int sz=(int)DBQueries.cartItemModelList.size();
        totalCartItems=0;
        totalCartAmount=0;
        if(sz>0){
            for(int x=0;x<sz;x++){
                totalCartItems=totalCartItems + DBQueries.cartItemModelList.get(x).getProductQuantityForOrder();
                totalCartAmount= totalCartAmount + (DBQueries.cartItemModelList.get(x).getProductQuantityForOrder() * Integer.parseInt(DBQueries.cartItemModelList.get(x).getSellingPrice().toString() ));
            }
        }
//        DBQueries.categoryWiseProducts.clear();
//        String URL= CommonApiConstant.backEndUrl+"/product/product-category-wise-products/"+getIntent().getStringExtra("categoryName");
//
//        VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.GET, URL, new Response.Listener<NetworkResponse>() {
//            @Override
//            public void onResponse(NetworkResponse response) {
//                loadingDialog.dismiss();
//                String resultResponse = new String(response.data);
//                try {
//                    JSONObject result = new JSONObject(resultResponse);
//                    int no_of_categories= result.getJSONArray("result").length();
//                    DBQueries.categoryWiseProducts.clear();
//                    for(int count=0;count<(no_of_categories);count++){
//                        DBQueries.categoryWiseProducts.add(new CategoryWiseProductModel(
//                                result.getJSONArray("result").getJSONObject(count).get("product_name").toString(),
//                                URLDecoder.decode(   result.getJSONArray("result").getJSONObject(count).get("product_hindi_name").toString(), "utf-8"),
//                                Integer.parseInt(result.getJSONArray("result").getJSONObject(count).get("price").toString()),
//                                1,
//                                result.getJSONArray("result").getJSONObject(count).get("product_image").toString(),
//                                result.getJSONArray("result").getJSONObject(count).get("selling_price").toString(),
//                                result.getJSONArray("result").getJSONObject(count).get("catalog_name").toString(),
//                                result.getJSONArray("result").getJSONObject(count).get("product_category").toString()
//                        ));
//                    }
//                    if(DBQueries.categoryWiseProducts.size()>0) {
//                        categoryWiseProductAdapter = new CategoryWiseProductAdapter(DBQueries.categoryWiseProducts);
//                        categoryGridView.setAdapter(categoryWiseProductAdapter);
//                        categoryWiseProductAdapter.notifyDataSetChanged();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                loadingDialog.dismiss();
//                NetworkResponse networkResponse = error.networkResponse;
//                String errorMessage = "Unknown error";
//                if (networkResponse == null) {
//                    if (error.getClass().equals(TimeoutError.class)) {
//                        errorMessage = "Request timeout";
//                    } else if (error.getClass().equals(NoConnectionError.class)) {
//                        errorMessage = "Failed to connect server";
//                    }
//                } else {
//                    String result = new String(networkResponse.data);
//                    try {
//                        JSONObject response = new JSONObject(result);
//                        String message = response.getString("message");
//
//                        Log.e("Error Message", message);
//
//                        if (networkResponse.statusCode == 404) {
//                            errorMessage = "Resource not found";
//                        } else if (networkResponse.statusCode == 401) {
//                            errorMessage = message+" Please login again";
//                        } else if (networkResponse.statusCode == 400) {
//                            errorMessage = message+ " Check your inputs";
//                        } else if (networkResponse.statusCode == 500) {
//                            errorMessage = message+" Something is getting wrong";
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                Log.i("Error", errorMessage);
//                error.printStackTrace();
//                Toast.makeText(CategoryWiseAllProductsActivity.this,errorMessage,Toast.LENGTH_LONG).show();
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
//                return params;
//            }
//        };
//        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest1);

    }
}
