package com.pemaseervi.myretailer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.pemaseervi.myretailer.Adapter.CategoryWiseProductAdapter;
import com.pemaseervi.myretailer.Adapter.ProductDetailsAdapter;
import com.pemaseervi.myretailer.Adapter.ProductImagesAdapter;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.Model.CartItemModel;
import com.pemaseervi.myretailer.Model.ProductSpecificationModel;
import com.pemaseervi.myretailer.Model.WishlistModel;
import com.pemaseervi.myretailer.utility.AppPref;
import com.pemaseervi.myretailer.utility.CommonApiConstant;
import com.pemaseervi.myretailer.utility.Utility;
import com.pemaseervi.myretailer.utility.VolleyMultipartRequest;
import com.pemaseervi.myretailer.utility.VolleySingleton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailsActivity extends AppCompatActivity {

//    private static final int CART_FRAGMENT=1;
//    private static int currentFragment=-1;
//    private FrameLayout frameLayout;
    ////////product desc
    private ConstraintLayout productDetailsOnlyContainer;
    private ConstraintLayout productDetailsTabsContainer;
    private ViewPager productDetailsViewPager;
    private TabLayout productDetailsTabLayout;
    private TextView productOnlyDescriptionBody;
    public static TextView badgeCount;
    public static String productDescription;
    public static String productOtherDetails;
    public static int tabPosition=-1;
    public static Activity productDetailsActivity;
    public static List<ProductSpecificationModel> productSpecificationModelList=new ArrayList<>();
    ////////product desc
    /////////rating layput
    private LinearLayout rateNowContainer;
    ////////rating
    private ViewPager productImagesViewPager;
    private TabLayout viewPagerIndicator;
    public static FloatingActionButton addToWishlist;
    public static boolean ALREADY_ADDED_TO_WISHLIST=false;
//    public static boolean ALREADY_ADDED_TO_CART=false;

    private TextView productTitle,averageRatingMiniView,totalRatingMiniView,productPrice,cuttedPrice,discount;
    private Button buyNowBtn,addToWishListBtn;
    public static  LinearLayout addToCart;

    ///signInDialog
    private Dialog signInDialog;
    ////signInDialog
    private Dialog loadingDialog;
    public static String productId,productHindiName;
//    private DocumentSnapshot documentSnapshot;
    private JSONObject details;
    public static MenuItem cartItem;
    private boolean inStock=false;

    public static boolean fromSearch=false;

    private ImageView incrementProductQuantity,decrementProductQuantity;
    private TextView productCartQuantity;
    private LinearLayout incrementDecLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        productId=getIntent().getStringExtra("product_id");
        productHindiName=getIntent().getStringExtra("product_hindi_name");

        if(DBQueries.language.equals("hi")){
            getSupportActionBar().setTitle(productHindiName);
        }else{
            getSupportActionBar().setTitle(productId);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        frameLayout=findViewById(R.id.main_frameLayout);

        incrementDecLayout= findViewById(R.id.inc_dec_layout);
        incrementProductQuantity=findViewById(R.id.increment_product_quantity);
        decrementProductQuantity=findViewById(R.id.decrement_product_quantity);
        productImagesViewPager=findViewById(R.id.product_images_view_pager);
        viewPagerIndicator=findViewById(R.id.view_pager_indicator);
        productDetailsViewPager=findViewById(R.id.product_details_view_pager);
        productDetailsTabLayout= findViewById(R.id.product_details_tab_layout);
        buyNowBtn=findViewById(R.id.view_cart_btn);
        addToWishlist=findViewById(R.id.add_to_wishlist_btn);
        productTitle=findViewById(R.id.product_title);
        productCartQuantity = findViewById(R.id.ordered_quantity);

        totalRatingMiniView=findViewById(R.id.total_ratings_miniview);
        productPrice=findViewById(R.id.product_price);
        cuttedPrice=findViewById(R.id.cutted_price);
        discount= findViewById(R.id.discount);
        productDetailsTabsContainer=findViewById(R.id.product_details_tab_container);
        productDetailsOnlyContainer=findViewById(R.id.product_detials_container);
        productOnlyDescriptionBody =findViewById(R.id.product_details_body);
//        getIntent().getStringExtra("PRODUCT_ID")
//        productId="Sonia Plus Tub";
         addToWishlist.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
//                 if(currentUser == null){
//                     signInDialog
//                 }
             }
         });
         ////loading dialog
        loadingDialog= new Dialog(ProductDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();


        incrementProductQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int x= Integer.parseInt(productCartQuantity.getText().toString()) ;
                x++;
                productCartQuantity.setText(String.valueOf(x));
                int sz=DBQueries.cartItemModelList.size();
                for(int count=0;count<sz;count++){
                    if(DBQueries.cartItemModelList.get(count).getProductTitle().equals(productId)){
                        DBQueries.cartItemModelList.get(count).setProductQuantityForOrder(x);
                        break;
                    }
                }
            }
        });
         decrementProductQuantity.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 int x= Integer.parseInt(productCartQuantity.getText().toString()) ;

                 x--;
                 if(x>0){
                     productCartQuantity.setText(String.valueOf(x));
                     int sz=DBQueries.cartItemModelList.size();
                     for(int count=0;count<sz;count++){
                         if(DBQueries.cartItemModelList.get(count).getProductTitle().equals(productId)){
                             DBQueries.cartItemModelList.get(count).setProductQuantityForOrder(x);
                             break;
                         }
                     }
                     if(CategoryWiseProductAdapter.categoryWiseProductModelList!=null){
                         int size=CategoryWiseProductAdapter.categoryWiseProductModelList.size();
                         for(int i=0;i<size;i++){
                             if(CategoryWiseProductAdapter.categoryWiseProductModelList.get(i).getProductTitle().trim().equals(productId)){
                                 CategoryWiseProductAdapter.categoryWiseProductModelList.get(i).setProductQuantityForCart(x);
                             }
                         }
                     }
                 }

             }
         });
        //////loadingdialog
         addToCart=findViewById(R.id.add_to_cart_btn);
        addToWishlist=findViewById(R.id.add_to_wishlist_btn);

//        List<Integer> productImages=  new ArrayList<>();
//        productImages.add(R.drawable.forgot_password_image);
//        productImages.add(R.mipmap.home_icon);
//        productImages.add(R.mipmap.red_email);
//        final List<String> productImages = new ArrayList<>();
        productDetailsOnlyContainer.setVisibility(View.GONE);
        productDetailsTabsContainer.setVisibility(View.GONE);
        productOnlyDescriptionBody.setVisibility(View.GONE);
        addToCart.setVisibility(View.GONE);
        addToWishlist.setVisibility(View.GONE);
        buyNowBtn.setVisibility(View.GONE);
        productImagesViewPager.setVisibility(View.GONE);
        viewPagerIndicator.setVisibility(View.GONE);
        incrementDecLayout.setVisibility(View.GONE);
        productTitle.setVisibility(View.GONE);
        productPrice.setVisibility(View.GONE);
        loadingDialog.show();
        String URL1= CommonApiConstant.backEndUrl+"/product/products-by-product-id/"+productId;
        VolleyMultipartRequest multipartRequest2 = new VolleyMultipartRequest(Request.Method.GET, URL1, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                loadingDialog.dismiss();
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                   JSONObject productDetails= result.getJSONArray("result").getJSONObject(0);
                   details=productDetails;
                   inStock=(Boolean)productDetails.get("in_stock");
                    JSONArray images= productDetails.getJSONArray("product_images");
//                            productImages.add(documentSnapshot.get("product_image").toString());
                    images= productDetails.getJSONArray("product_images");
                    List<String> imagesInList=new ArrayList<>();
                    for(int count =0;count<(images.length());count++){
                        imagesInList.add(images.getString(count));
                    }
                    ProductImagesAdapter productImagesAdapter= new ProductImagesAdapter(imagesInList);
                    productImagesViewPager.setAdapter(productImagesAdapter);
                    if(DBQueries.language.equals("hi")){
                        productTitle.setText( URLDecoder.decode(  productDetails.get("product_hindi_name").toString(), "utf-8")
                                );
                    }else{
                        productTitle.setText( productDetails.get("product_name").toString());
                    }

//                            averageRatingMiniView.setText(documentSnapshot.get("average_rating").toString());
//                             totalRatingMiniView.setText("("+(long)documentSnapshot.get("total_rating")+")Ratings");
                    productPrice.setText(Utility.changeToIndianCurrency(Integer.parseInt(productDetails.get("selling_price").toString())));
                    cuttedPrice.setText(Utility.changeToIndianCurrency(Integer.parseInt(productDetails.get("price").toString())));
                    int price=Integer.parseInt(productDetails.getString("price"));
                    int selling_price=Integer.parseInt(productDetails.getString("selling_price"));
                    discount.setText(String.valueOf((int)(((double)(price-selling_price)/(double)price)*100.0))+getResources().getString(R.string.discount));
                    productOnlyDescriptionBody.setVisibility(View.VISIBLE);
                    addToCart.setVisibility(View.VISIBLE);
                    addToCart.setVisibility(View.VISIBLE);
                    addToWishlist.setVisibility(View.VISIBLE);
                    buyNowBtn.setVisibility(View.VISIBLE);
                    productImagesViewPager.setVisibility(View.VISIBLE);
                    viewPagerIndicator.setVisibility(View.VISIBLE);
                    incrementDecLayout.setVisibility(View.VISIBLE);
                    productTitle.setVisibility(View.VISIBLE);
                    productPrice.setVisibility(View.VISIBLE);
                    if(DBQueries.cartList.contains(productId)){
                        int sz=DBQueries.cartItemModelList.size();
                        for(int i=0;i<sz;i++){
                            if(DBQueries.cartItemModelList.get(i).getProductTitle().trim().equals(productId.trim())){
                                productCartQuantity.setText(String.valueOf(DBQueries.cartItemModelList.get(i).getProductQuantityForOrder()));
                            }
                        }
                    }
                    if(!productDetails.get("use_tab_layout").toString().equals("null") &&  (boolean) productDetails.get("use_tab_layout")){
                        productDetailsOnlyContainer.setVisibility(View.GONE);
                        productDetailsTabsContainer.setVisibility(View.VISIBLE);
                        productDescription= productDetails.get("description").toString();

                        productOtherDetails =  productDetails.get("product_other_details").toString();
                        for(long x=1;x<((long) productDetails.get("total_spec_titles")+1);x++){
                            productSpecificationModelList.add(new ProductSpecificationModel(0,productDetails.get("spec_title_"+x).toString()));
                            for(long y=1;y<((long) productDetails.get("spec_title_"+String.valueOf(x)+"_total_fields"));y++){
                                productSpecificationModelList.add(new ProductSpecificationModel(1,productDetails.get("spec_title_"+String.valueOf(x)+"_field_"+String.valueOf(y)+"_name").toString(),productDetails.get("spec_title_"+x+"_field_"+y+"_value").toString()));


                            }
                        }

                    }else{
                        productDetailsOnlyContainer.setVisibility(View.VISIBLE);
                        productDetailsTabsContainer.setVisibility(View.GONE);
                        productOnlyDescriptionBody.setText(  URLDecoder.decode( productDetails.get("description").toString(), "utf-8"));

                    }
                    if(CommonApiConstant.currentUser!=null){
                        if(DBQueries.wishList.size() == 0){
                            DBQueries.loadWishList(ProductDetailsActivity.this,loadingDialog, false);
                        }
//                                if(DBQueries.cartList.size() == 0){
//                                    DBQueries.loadCartList(ProductDetailsActivity.this,loadingDialog, false,badgeCount,new TextView(ProductDetailsActivity.this));
//                                }else{
//                                    loadingDialog.dismiss();
//                                }
                    }else{
                        loadingDialog.dismiss();
                    }
//                            if(DBQueries.cartList.contains(productId)){
//                                ALREADY_ADDED_TO_CART=true;
//                            }else{
//                                ALREADY_ADDED_TO_CART=false;
//                            }
                    if(DBQueries.wishList.contains(productId)){
                        ALREADY_ADDED_TO_WISHLIST=true;
                        addToWishlist.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimary));
                    }else{
                        ProductDetailsActivity.addToWishlist.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                        ALREADY_ADDED_TO_WISHLIST=false;
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
                Toast.makeText(ProductDetailsActivity.this,errorMessage,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
                return params;
            }
        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest2);

//        firebaseFirestore.collection("PRODUCTS").document(productId)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if(task.isSuccessful()){
//                               documentSnapshot= task.getResult();
//
////                               for(long x=1;x<((long)documentSnapshot.get("no_of_product_images")+1);x++){
////                                  productImages.add(documentSnapshot.get("product_image_"+x).toString());
////                               }
//                            inStock=(Boolean) documentSnapshot.get("in_stock");
//                            List<String> images= new ArrayList<>();
//                            images= (List<String>) documentSnapshot.get("product_images");
////                            productImages.add(documentSnapshot.get("product_image").toString());
//                            images= (List<String>) documentSnapshot.get("product_images");
//                            ProductImagesAdapter productImagesAdapter= new ProductImagesAdapter(images);
//                            productImagesViewPager.setAdapter(productImagesAdapter);
//                            if(DBQueries.language.equals("hi")){
//                                productTitle.setText(documentSnapshot.get("product_hindi_name").toString());
//                            }else{
//                                productTitle.setText(documentSnapshot.get("product_name").toString());
//                            }
//
////                            averageRatingMiniView.setText(documentSnapshot.get("average_rating").toString());
////                             totalRatingMiniView.setText("("+(long)documentSnapshot.get("total_rating")+")Ratings");
//                            productPrice.setText("Rs."+documentSnapshot.get("price")+"/-");
////                            cuttedPrice.setText("Rs."+documentSnapshot.get("cutted_price")+"/-");
//                            productOnlyDescriptionBody.setVisibility(View.VISIBLE);
//                            addToCart.setVisibility(View.VISIBLE);
//                            addToCart.setVisibility(View.VISIBLE);
//                            addToWishlist.setVisibility(View.VISIBLE);
//                            buyNowBtn.setVisibility(View.VISIBLE);
//                            productImagesViewPager.setVisibility(View.VISIBLE);
//                            viewPagerIndicator.setVisibility(View.VISIBLE);
//                            incrementDecLayout.setVisibility(View.VISIBLE);
//                            productTitle.setVisibility(View.VISIBLE);
//                            productPrice.setVisibility(View.VISIBLE);
//                            if((boolean)documentSnapshot.get("use_tab_layout")){
//                                   productDetailsOnlyContainer.setVisibility(View.GONE);
//                                   productDetailsTabsContainer.setVisibility(View.VISIBLE);
//                                  productDescription=documentSnapshot.get("description").toString();
//
//                                   productOtherDetails = documentSnapshot.get("product_other_details").toString();
//                                   for(long x=1;x<((long)documentSnapshot.get("total_spec_titles")+1);x++){
//                                       productSpecificationModelList.add(new ProductSpecificationModel(0,documentSnapshot.get("spec_title_"+x).toString()));
//                                       for(long y=1;y<((long)documentSnapshot.get("spec_title_"+String.valueOf(x)+"_total_fields"));y++){
//                                           productSpecificationModelList.add(new ProductSpecificationModel(1,documentSnapshot.get("spec_title_"+String.valueOf(x)+"_field_"+String.valueOf(y)+"_name").toString(),documentSnapshot.get("spec_title_"+x+"_field_"+y+"_value").toString()));
//
//
//                                       }
//                                   }
//
//                            }else{
//                                productDetailsOnlyContainer.setVisibility(View.VISIBLE);
//                                productDetailsTabsContainer.setVisibility(View.GONE);
//                                productOnlyDescriptionBody.setText(documentSnapshot.get("description").toString());
//
//                            }
//                            if(currentUser!=null){
//                                if(DBQueries.wishList.size() == 0){
//                                    DBQueries.loadWishList(ProductDetailsActivity.this,loadingDialog, false);
//                                }
////                                if(DBQueries.cartList.size() == 0){
////                                    DBQueries.loadCartList(ProductDetailsActivity.this,loadingDialog, false,badgeCount,new TextView(ProductDetailsActivity.this));
////                                }else{
////                                    loadingDialog.dismiss();
////                                }
//                            }else{
//                                loadingDialog.dismiss();
//                            }
////                            if(DBQueries.cartList.contains(productId)){
////                                ALREADY_ADDED_TO_CART=true;
////                            }else{
////                                ALREADY_ADDED_TO_CART=false;
////                            }
//                            if(DBQueries.wishList.contains(productId)){
//                                ALREADY_ADDED_TO_WISHLIST=true;
//                                addToWishlist.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimary));
//                            }else{
//                                ProductDetailsActivity.addToWishlist.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
//                                ALREADY_ADDED_TO_WISHLIST=false;
//                            }
//
//                        }else{
//                            loadingDialog.dismiss();
//                            Toast.makeText(ProductDetailsActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                        }
//                        loadingDialog.dismiss();
//                    }
//                });


        viewPagerIndicator.setupWithViewPager(productImagesViewPager,true);



        addToWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CommonApiConstant.currentUser == null ){
                    signInDialog.show();
                }else{
                    addToWishlist.setEnabled(false);
                    if(ALREADY_ADDED_TO_WISHLIST){
                        int index =DBQueries.wishList.indexOf(productId);
                        DBQueries.removeFromWishList(index,ProductDetailsActivity.this);
                        addToWishlist.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                    }else{


                        Map<String,String> addProductID= new HashMap<>();
//                        addProductID.put("email","pemaseervi@gmail.com");
//                        addProductID.put("product_id_"+String.valueOf(DBQueries.wishList.size()),productId);
//                        addProductID.put("list_size",String.valueOf((long)(DBQueries.wishList.size()+1)));
                        addProductID.put("product_id",productId);
                        addProductID.put("user_id",CommonApiConstant.currentUser);
                        String URL= CommonApiConstant.backEndUrl+"/user/add-to-wishlist";

                        VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.PUT, URL, new Response.Listener<NetworkResponse>() {
                            @Override
                            public void onResponse(NetworkResponse response) {
                                addToWishlist.setEnabled(true);
                                if(loadingDialog !=null){
                                    loadingDialog.dismiss();
                                }
                                if(addToCart !=null){
                                    addToCart.setEnabled(true);
                                }
                                String resultResponse = new String(response.data);
                                try {
                                    JSONObject result = new JSONObject(resultResponse);
                                    if(DBQueries.wishlistModelList.size() !=0){
                                        DBQueries.wishlistModelList.add(new WishlistModel(details.get("product_image").toString(),
                                                details.get("product_name").toString(),
                                                URLDecoder.decode(  details.get("product_hindi_name").toString(), "utf-8"),
                                                details.get("price").toString(),
                                                details.get("selling_price").toString(),
                                                (boolean) details.get("in_stock"),
                                                details.get("catalog_name").toString(),
                                                details.get("product_category").toString()
                                        ));
                                    }
                                    ALREADY_ADDED_TO_WISHLIST=true;
                                    addToWishlist.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimary));
                                    DBQueries.wishList.add(productId);
                                    Toast.makeText(ProductDetailsActivity.this,"Product added successfully",Toast.LENGTH_SHORT).show();
                                    invalidateOptionsMenu();
                                    } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                addToWishlist.setEnabled(true);
                                addToWishlist.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));

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
                                Toast.makeText(ProductDetailsActivity.this,errorMessage,Toast.LENGTH_LONG).show();
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
                                return addProductID;
                            }
                        };
                        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest1);
//
//                        Map<String,Object> addProductID= new HashMap<>();
//                        addProductID.put("product_id_"+String.valueOf(DBQueries.wishList.size()),productId);
//                        addProductID.put("list_size",(long)(DBQueries.wishList.size()+1));
//                        firebaseFirestore.collection("USERS").document(DBQueries.currentUser.getUid())
//                                .collection("USER_DATA")
//                                .document("MY_WISHLIST")
//                                .update(addProductID)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if(task.isSuccessful()){
//                                           try {
//                                               if(DBQueries.wishlistModelList.size() !=0){
//                                                   DBQueries.wishlistModelList.add(new WishlistModel(details.get("product_image").toString(),
//                                                           details.get("product_name").toString(),
//                                                           details.get("product_hindi_name").toString(),
//                                                           details.get("price").toString(),
//                                                           (boolean) details.get("in_stock")
//                                                   ));
//                                               }
//                                           } catch (JSONException e) {
//                                               e.printStackTrace();
//                                           }
//
//
//                                            ALREADY_ADDED_TO_WISHLIST=true;
//                                            addToWishlist.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimary));
//                                            DBQueries.wishList.add(productId);
//                                            Toast.makeText(ProductDetailsActivity.this,"Product added successfully",Toast.LENGTH_SHORT).show();
//                                            invalidateOptionsMenu();
//                                        }else{
//                                            addToWishlist.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
//                                            String error = task.getException().getMessage();
//                                            Toast.makeText(ProductDetailsActivity.this,error,Toast.LENGTH_SHORT).show();
//                                        }
//                                        addToWishlist.setEnabled(true);
//                                    }
//                                });

                    }
                }
            }
        });

        productDetailsViewPager.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(),productDetailsTabLayout.getTabCount()));

        productDetailsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTabLayout));
        productDetailsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabPosition = tab.getPosition();
                productDetailsViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

            addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        if(!details.getBoolean("in_stock")){
                            if(CommonApiConstant.currentUser==null){
                                signInDialog.show();
                            }else{
                                addToCart.setEnabled(false);
                                if(DBQueries.cartList.contains(details.get("product_name").toString())){
                                    Toast.makeText(ProductDetailsActivity.this,getResources().getString(R.string.already_added_to_cart_msg),Toast.LENGTH_SHORT).show();
                                    addToCart.setEnabled(true);
                                }else{
                                    DBQueries.addToCart(ProductDetailsActivity.this,productId,details.get("product_name").toString(),
                                            URLDecoder.decode(  details.get("product_hindi_name").toString(), "utf-8"),
                                            details.get("price").toString(),
                                            details.get("selling_price").toString(),
                                            details.get("product_image").toString(),
                                            Integer.parseInt(productCartQuantity.getText().toString()),
                                            loadingDialog,
                                            details.get("catalog_name").toString(),
                                            details.get("product_category").toString()
                                    );
                                }
                            }
                        }else{
                            buyNowBtn.setVisibility(View.GONE);
                            TextView outOfStock = (TextView)addToCart.getChildAt(0);
                            outOfStock.setText(R.string.out_of_stock_msg);
                            outOfStock.setTextColor(getResources().getColor(R.color.colorPrimary));
                            outOfStock.setCompoundDrawables(null,null,null,null);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            });
        /////////ratings layour
        rateNowContainer=findViewById(R.id.rate_now_container);
        for(int x=0;x<rateNowContainer.getChildCount();x++){
            final int starPosition=x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setRating(starPosition);
                }
            });

        }
        ///////////ratings layout
        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CommonApiConstant.currentUser==null){
                    signInDialog.show();
                }else{
                    DeliveryActivity.fromCart=false;
                    loadingDialog.show();
                    productDetailsActivity=ProductDetailsActivity.this;
                    DeliveryActivity.cartItemModelList= new ArrayList<>();
                    try{
                        DeliveryActivity.cartItemModelList.add(new CartItemModel(0,details.get("product_name").toString(),
                                URLDecoder.decode(  details.get("product_hindi_name").toString(), "utf-8"),
                                details.get("price").toString(),
                                details.get("selling_price").toString(),
                                details.get("product_image").toString(),
                                Integer.parseInt(productCartQuantity.getText().toString()),
                                details.get("catalog_name").toString(),
                                details.get("product_category").toString()
                        ));
                    }catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));if(DBQueries.addressModelList.size()==0){
                        DBQueries.loadAddresses(ProductDetailsActivity.this,loadingDialog,true);
                    }else{
                        loadingDialog.dismiss();
                        Intent deliveryIntent = new Intent(ProductDetailsActivity.this,DeliveryActivity.class);
                        deliveryIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(deliveryIntent);
                    }
                 }
            }
        });

        ///signInDialog
        signInDialog= new Dialog(ProductDetailsActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);
        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Button signInDialogBtn=signInDialog.findViewById(R.id.sign_in_btn);
        final Button signUpDialogBtn=signInDialog.findViewById(R.id.sign_up_btn);
        final Intent registerIntent = new Intent(ProductDetailsActivity.this,RegisterActivity.class);

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
        ///end of signInDialog
    }

    private void setRating(int starPosition) {
        for(int x=0;x<rateNowContainer.getChildCount();x++){
            ImageView starBtn =(ImageView)rateNowContainer.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
            if(x<=starPosition){
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);
        cartItem = menu.findItem(R.id.main_cart_icon);

            cartItem.setActionView(R.layout.badge_layout);
            ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
            badgeIcon.setImageResource(R.mipmap.cart_white);
            badgeCount =cartItem.getActionView().findViewById(R.id.badge_count);
            if(CommonApiConstant.currentUser!=null){
                if(DBQueries.cartList.size() == 0){
                    DBQueries.loadCartList(ProductDetailsActivity.this,loadingDialog, badgeCount,new TextView(ProductDetailsActivity.this));
                }else{
                    badgeCount.setVisibility(View.VISIBLE);
                    if(DBQueries.cartList.size() < 99){
                        badgeCount.setText(String.valueOf(DBQueries.cartList.size()));
                    }else{
                        badgeCount.setText("99");
                    }
                }
            }
            cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   if(CommonApiConstant.currentUser ==null){
                       signInDialog.show();
                   }else{
                       Intent cartIntent= new Intent(ProductDetailsActivity.this,CartActivity.class);
                       cartIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                       startActivity(cartIntent);

                   }
                }
            });

        return true;
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.main_search_icon){
            if(fromSearch){
                finish();
            }else{
                Intent searchIntent= new Intent(this, SearchActivity.class);
                searchIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                startActivity(searchIntent);
            }

            return true;
        }else if(id== android.R.id.home){
            productDetailsActivity=null;
            finish();
            return true;
        }else if(id== R.id.main_cart_icon){
            if(CommonApiConstant.currentUser ==null ){
                signInDialog.show();
            }else{
                HomeActivity.showCart=true;
               Intent cartIntent = new Intent(ProductDetailsActivity.this,MainActivity.class);
                cartIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                startActivity(cartIntent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        productDetailsActivity=null;
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        addToCart.setEnabled(true);
        if(CommonApiConstant.currentUser !=null){

            if(DBQueries.wishList.size() == 0){
                DBQueries.loadWishList(ProductDetailsActivity.this,loadingDialog, false);
            }else{
                loadingDialog.dismiss();
            }

        }else{
            loadingDialog.dismiss();
        }
//        if(DBQueries.cartList.contains(productId)){
//            ALREADY_ADDED_TO_CART=true;
//        }else{
//            ALREADY_ADDED_TO_CART=false;
//        }
        if(DBQueries.wishList.contains(productId)){
            ALREADY_ADDED_TO_WISHLIST=true;
            addToWishlist.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimary));
        }else{
            ProductDetailsActivity.addToWishlist.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
            ALREADY_ADDED_TO_WISHLIST=false;
        }
        invalidateOptionsMenu();
    }

    protected void onDestroy(){
        super.onDestroy();
        fromSearch=false;

    }
}
