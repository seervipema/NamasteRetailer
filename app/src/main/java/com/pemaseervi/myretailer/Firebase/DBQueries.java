package com.pemaseervi.myretailer.Firebase;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.pemaseervi.myretailer.Adapter.CategoryAdapter;
import com.pemaseervi.myretailer.Adapter.CategoryHomePageAdapter;
import com.pemaseervi.myretailer.Adapter.CategoryPageAdapter;
import com.pemaseervi.myretailer.Adapter.HomePageAdapter;
import com.pemaseervi.myretailer.Adapter.OrderItemDetailsAdapter;
import com.pemaseervi.myretailer.Adapter.OrderStatusAdapter;
import com.pemaseervi.myretailer.Adapter.OrdersAdapter;
import com.pemaseervi.myretailer.AddAddressActivity;
import com.pemaseervi.myretailer.CartActivity;
import com.pemaseervi.myretailer.DeliveryActivity;
import com.pemaseervi.myretailer.HomeActivity;
import com.pemaseervi.myretailer.Model.AddressModel;
import com.pemaseervi.myretailer.Model.CartItemModel;
import com.pemaseervi.myretailer.Model.CategoryModel;
import com.pemaseervi.myretailer.Model.CategoryWiseProductModel;
import com.pemaseervi.myretailer.Model.HomePageModel;
import com.pemaseervi.myretailer.Model.HorizontalProductScrollModel;
import com.pemaseervi.myretailer.Model.MyOrderItemModel;
import com.pemaseervi.myretailer.Model.NotificationModel;
import com.pemaseervi.myretailer.Model.OrderStatusModel;
import com.pemaseervi.myretailer.Model.OrdersItemModel;
import com.pemaseervi.myretailer.Model.SliderModel;
import com.pemaseervi.myretailer.Model.WishlistModel;
import com.pemaseervi.myretailer.MyCartFragment;
import com.pemaseervi.myretailer.MyWishlistFragment;
import com.pemaseervi.myretailer.NotificationActivity;
import com.pemaseervi.myretailer.OrderFragment;
import com.pemaseervi.myretailer.PhoneNumberActivity;
import com.pemaseervi.myretailer.ProductDetailsActivity;
import com.pemaseervi.myretailer.R;
import com.pemaseervi.myretailer.ui.home.HomeFragment;
import com.pemaseervi.myretailer.utility.AppPref;
import com.pemaseervi.myretailer.utility.CommonApiConstant;
import com.pemaseervi.myretailer.utility.SharedPreferenceHelper;
import com.pemaseervi.myretailer.utility.VolleyMultipartRequest;
import com.pemaseervi.myretailer.utility.VolleySingleton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pemaseervi.myretailer.ProductDetailsActivity.addToCart;


public class DBQueries {
    public static String language;

   public static int selectedAddress=0;
   public static List<AddressModel> addressModelList = new ArrayList<>();
   public static List<CategoryWiseProductModel> categoryWiseProducts=new ArrayList<>();
   public static List<List<HorizontalProductScrollModel>> categoryWiseTop8ProductsArrayList=new ArrayList<List<HorizontalProductScrollModel>>();
    public static List<List<HorizontalProductScrollModel>> catalogWiseTop8ProductsArrayList=new ArrayList<List<HorizontalProductScrollModel>>();

    public static List<CategoryModel> categoryModelList= new ArrayList<CategoryModel>();
   public static List<CategoryModel> categoryModelListByCatalog= new ArrayList<>();
   public static List<CategoryModel> catalogModelList= new ArrayList<>();
    public static List<HomePageModel> homePageModelList = new ArrayList<>();
    public static List<HomePageModel> categoryPageModelList = new ArrayList<>();
    public static List<WishlistModel> wishlistModelList = new ArrayList<>();
    public static List<String> wishList= new ArrayList<>();
    ////////My cart
    public static List<CartItemModel> cartItemModelList = new ArrayList<>();
    public static List<String> cartList= new ArrayList<>();
    /////////My Cart
    public static List<NotificationModel> notificationModelList = new ArrayList<>();
    public static List<MyOrderItemModel> myOrderItemModelList = new ArrayList<>();
    public static List<OrdersItemModel> ordersItemModelList = new ArrayList<>();
    public static List<OrdersItemModel> nonFilteredOrdersItemModelList=new ArrayList<>();
    public static List<HorizontalProductScrollModel> horizontalProductScrollModelList=new ArrayList<>();
    public static String fullname;
    public static String email;
    public static String profile;
    public static boolean isNotificationClicked=false;
    public static List<OrderStatusModel> orderStatusModelList=new ArrayList<>();

    public static void loadCategories(final CategoryAdapter categoryAdapter, final Context context){
        String URL=CommonApiConstant.backEndUrl+"/category/allWithDetails";
        VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.GET, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    int no_of_categories= result.getJSONArray("result").length();
//                    categoryModelList.add(new CategoryModel(documentSnapshot.get("image").toString(),documentSnapshot.get("category_name").toString(),
//                            documentSnapshot.get("category_hindi_name").toString()
//                    ));

                categoryAdapter.notifyDataSetChanged();
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
                            DBQueries.signOut(context);
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
                Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
                return params;
            }

        };

        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest1);

//        firebaseFirestore.collection("CATEGORIES").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
//                                categoryModelList.add(new CategoryModel(documentSnapshot.get("image").toString(),documentSnapshot.get("category_name").toString(),
//                                        documentSnapshot.get("category_hindi_name").toString()
//                                ));
//                            }
//                            categoryAdapter.notifyDataSetChanged();
//                        }else{
//                            String error =  task.getException().getMessage();
//                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
    }
    public static void loadCatalogs(final HomePageAdapter homePageAdapter,final CategoryAdapter categoryAdapter,final Context context){
        String URL= CommonApiConstant.backEndUrl+"/catalog/allWithDetails";
        VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.GET, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    catalogModelList.clear();
                    JSONObject result = new JSONObject(resultResponse);
                    int no_of_categories= result.getJSONArray("result").length();
                    for(int count=0;count<(no_of_categories);count++){
                        catalogModelList.add(new CategoryModel(
                                result.getJSONArray("result").getJSONObject(count).get("catalog_image").toString(),
                                result.getJSONArray("result").getJSONObject(count).get("catalog_name").toString(),
                                URLDecoder.decode( result.getJSONArray("result").getJSONObject(count).get("catalog_hindi_name").toString(), "utf-8")

                        ));
                    }
                    for(int i=0;i<(int)DBQueries.catalogModelList.size();i++){
                                DBQueries.loadCatalogWiseTop8Data(homePageAdapter,context,3,DBQueries.catalogModelList.get(i).getCategoryName(),DBQueries.catalogModelList.get(i).getCategoryHindiName());
                            }
                    categoryAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
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
                            DBQueries.signOut(context);
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
                Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
                return params;
            }

        };

        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest1);


//        firebaseFirestore.collection("CATALOGS").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            for (QueryDocumentSnapshot document : task.getResult()) {
////                                list.add(document.getData().get("catalog_name").toString());
//                                  catalogModelList.add(
//                                          new CategoryModel(document.getData().get("catalog_image").toString(),
//                                                  document.getData().get("catalog_name").toString(),
//                                                  document.getData().get("catalog_hindi_name").toString())
//                                  );
//                            }
//                            for(int i=0;i<(int)DBQueries.catalogModelList.size();i++){
//                                DBQueries.loadCatalogWiseTop8Data(homePageAdapter,context,3,DBQueries.catalogModelList.get(i).getCategoryName());
//                            }
//                            categoryAdapter.notifyDataSetChanged();
//                        }else{
//                            Toast.makeText(context,task.getException().getMessage(),Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
    }
    public static void loadCategoriesByCatalog(final CategoryHomePageAdapter categoryHomePageAdapter, final CategoryPageAdapter categoryAdapter, final Context context, String catalogName){
        categoryModelListByCatalog.clear();
        String URL=CommonApiConstant.backEndUrl+"/category/allWithDetails/"+catalogName;
        VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.GET, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    int no_of_categories= result.getJSONArray("result").length();
                    for(int count=0;count<(no_of_categories);count++){
                        categoryModelListByCatalog.add(new CategoryModel(
                                result.getJSONArray("result").getJSONObject(count).get("category_image").toString(),
                                result.getJSONArray("result").getJSONObject(count).get("category_name").toString(),
                                URLDecoder.decode(   result.getJSONArray("result").getJSONObject(count).get("category_hindi_name").toString(), "utf-8")

                        ));
                    }
                            for(int i=0;i<(int)(DBQueries.categoryModelListByCatalog.size());i++){
                                DBQueries.loadCategoryWiseTop8Data(categoryHomePageAdapter,context,3,DBQueries.categoryModelListByCatalog.get(i).getCategoryName(),DBQueries.categoryModelListByCatalog.get(i).getCategoryHindiName());
                            }
                    categoryAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
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
                            DBQueries.signOut(context);
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
                Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
                return params;
            }

        };

        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest1);

//        firebaseFirestore.collection("CATEGORIES").whereEqualTo("catalog_name",catalogName)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            int no_of_categories= (int)task.getResult().getDocuments().size();
//                            for(int x=0;x<(no_of_categories);x++){
//                                categoryModelListByCatalog.add(
//                                        new CategoryModel(task.getResult().getDocuments().get(x).get("image").toString(),
//                                                task.getResult().getDocuments().get(x).get("category_name").toString(),
//                                                task.getResult().getDocuments().get(x).get("category_hindi_name").toString()
//                                        ));
//                            }
//                            categoryPageModelList.clear();
//                            categoryWiseTop8ProductsArrayList.clear();
//                            for(int i=0;i<(int)(DBQueries.categoryModelListByCatalog.size());i++){
//                                DBQueries.loadCategoryWiseTop8Data(categoryHomePageAdapter,context,2,DBQueries.categoryModelListByCatalog.get(i).getCategoryName());
//                            }
//                            categoryAdapter.notifyDataSetChanged();
//                        }else{
//                            String error =  task.getException().getMessage();
//                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

    }
    public static void loadFragmentData(final HomePageAdapter homePageAdapter, final Context context){
        String URL=CommonApiConstant.backEndUrl+"/banner/banners-by-catalog/"+"Home";
        VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.GET, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    homePageModelList.clear();
                    JSONObject result = new JSONObject(resultResponse);
                    int no_of_banners=result.getJSONArray("result").length();

                    List<SliderModel> sliderModelList=new ArrayList<>();
                    for(int count=0;count<(no_of_banners);count++){
                        JSONObject bannerDetails=result.getJSONArray("result").getJSONObject(count);
                        if((int)bannerDetails.get("view_type")==0){
                            sliderModelList.add(new SliderModel(bannerDetails.get("banner_image").toString(),bannerDetails.get("banner_background").toString()));
                        }else if((int)bannerDetails.get("view_type")==1){
                            homePageModelList.add(new HomePageModel(1,bannerDetails.get("banner_image").toString(),bannerDetails.get("banner_background").toString()));

                        }else if((int)bannerDetails.get("view_type")==2){

                        }else if((int)bannerDetails.get("view_type")==3){

                        }


                    }
                    if(sliderModelList.size()>0){
                        homePageModelList.add(new HomePageModel(0,sliderModelList));
                    }
                    homePageAdapter.notifyDataSetChanged();
                    if(HomeActivity.shimmerFrameLayout!=null){
                        HomeActivity.shimmerFrameLayout.stopShimmer();
                        HomeActivity.shimmerFrameLayout.setVisibility(View.GONE);
                    }
                    if(HomeFragment.swipeRefreshLayout !=null) {
                        HomeFragment.swipeRefreshLayout.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(HomeActivity.shimmerFrameLayout!=null){
                    HomeActivity.shimmerFrameLayout.stopShimmer();
                    HomeActivity.shimmerFrameLayout.setVisibility(View.GONE);
                }
                if(HomeFragment.swipeRefreshLayout !=null) {
                    HomeFragment.swipeRefreshLayout.setRefreshing(false);
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
                            DBQueries.signOut(context);
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
                Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
                return params;
            }

        };

        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest1);



//        firebaseFirestore.collection("CATEGORIES")
//                .document("HOME")
//                .collection("TOP_DEALS")
//                .orderBy("index").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
//                                if((long)documentSnapshot.get("view_type")==0){
//                                    List<SliderModel> sliderModelList = new ArrayList<>();
//                                    long no_of_banners= (long)documentSnapshot.get("no_of_banners");
//                                    for(long x=1;x<(no_of_banners+1);x++){
//                                        sliderModelList.add(new SliderModel(documentSnapshot.get("banner"+x).toString(),documentSnapshot.get("banner"+ x +"_background").toString()));
//
//                                    }
//                                    homePageModelList.add(new HomePageModel(0,sliderModelList));
//
//                                }else if((long)documentSnapshot.get("view_type")==1){
//                                    homePageModelList.add(new HomePageModel(1,documentSnapshot.get("strip_add_banner").toString(),documentSnapshot.get("strip_add_background").toString()));
//
//                                }else if((long)documentSnapshot.get("view_type")==2){
//
//                                    long no_of_products= (long)documentSnapshot.get("no_of_products");
//                                    for(long x=1;x<(no_of_products+1);x++){
////                                        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_id_"+x).toString(),
////                                                documentSnapshot.get("product_image_"+x).toString(),
////                                                documentSnapshot.get("product_title_"+x).toString(),
////                                                documentSnapshot.get("product_subtitle_"+x).toString(),
////                                                documentSnapshot.get("product_price_"+x).toString()
////                                        ));
//                                    }
//
////                                    homePageModelList.add(new HomePageModel(2,documentSnapshot.get("layout_title").toString(),documentSnapshot.get("layout_background").toString(),horizontalProductScrollModelList));
////                                    homePageModelList.add(new HomePageModel(3,documentSnapshot.get("layout_title").toString(),documentSnapshot.get("layout_background").toString(),horizontalProductScrollModelList));
////                                    homePageModelList.add(new HomePageModel(2,documentSnapshot.get("layout_title").toString(),documentSnapshot.get("layout_background").toString(),horizontalProductScrollModelList));
////                                    homePageModelList.add(new HomePageModel(3,documentSnapshot.get("layout_title").toString(),documentSnapshot.get("layout_background").toString(),horizontalProductScrollModelList));
//
//                                }else if((long)documentSnapshot.get("view_type")==3){
//
//                                }
//
//
//                            }
//                            homePageAdapter.notifyDataSetChanged();
//                            if(HomeActivity.shimmerFrameLayout!=null){
//                                HomeActivity.shimmerFrameLayout.stopShimmer();
//                                HomeActivity.shimmerFrameLayout.setVisibility(View.GONE);
//                            }
//                            if(HomeFragment.swipeRefreshLayout !=null) {
//                                HomeFragment.swipeRefreshLayout.setRefreshing(false);
//                            }
//                        }else{
//                            String error =  task.getException().getMessage();
//                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
    }
//    public static void loadCatalogFragmentData(final HomePageAdapter homePageAdapter, final Context context,final String catalogName){
//        firebaseFirestore.collection("CATALOGS")
//                .document(catalogName)
//                .collection("TOP_DEALS")
//                .orderBy("index").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
//                                if((long)documentSnapshot.get("view_type")==0){
//                                    List<SliderModel> sliderModelList = new ArrayList<>();
//                                    long no_of_banners= (long)documentSnapshot.get("no_of_banners");
//                                    for(long x=1;x<(no_of_banners+1);x++){
//                                        sliderModelList.add(new SliderModel(documentSnapshot.get("banner"+x).toString(),documentSnapshot.get("banner"+ x +"_background").toString()));
//
//                                    }
//                                    categoryPageModelList.add(new HomePageModel(0,sliderModelList));
//
//                                }else if((long)documentSnapshot.get("view_type")==1){
//                                    categoryPageModelList.add(new HomePageModel(1,documentSnapshot.get("strip_add_banner").toString(),documentSnapshot.get("strip_add_background").toString()));
//
//                                }else if((long)documentSnapshot.get("view_type")==2){
//
//                                    long no_of_products= (long)documentSnapshot.get("no_of_products");
//                                    for(long x=1;x<(no_of_products+1);x++){
////                                        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_id_"+x).toString(),
////                                                documentSnapshot.get("product_image_"+x).toString(),
////                                                documentSnapshot.get("product_title_"+x).toString(),
////                                                documentSnapshot.get("product_subtitle_"+x).toString(),
////                                                documentSnapshot.get("product_price_"+x).toString()
////                                        ));
//                                    }
//                                }else if((long)documentSnapshot.get("view_type")==3){
//
//                                }
//
//
//                            }
//                            homePageAdapter.notifyDataSetChanged();
//                            if(HomeFragment.swipeRefreshLayout !=null) {
//                                HomeFragment.swipeRefreshLayout.setRefreshing(false);
//                            }
//                        }else{
//                            String error =  task.getException().getMessage();
//                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
    public static void loadWishList(final Context context, Dialog dialog, final boolean loadProductData){

        String URL= CommonApiConstant.backEndUrl+"/user/wishlist-items/"+CommonApiConstant.currentUser;

        VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.GET, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                if(dialog!=null){
                    dialog.dismiss();
                }
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    wishList.clear();
                    wishlistModelList.clear();
                    int no_of_cart_items= result.getJSONArray("result").length();
                    if(no_of_cart_items==0 && MyWishlistFragment.emptyWishListLayout !=null && MyWishlistFragment.wishlistRecycleView!=null ){
                        MyWishlistFragment.emptyWishListLayout.setVisibility(View.VISIBLE);
                        MyWishlistFragment.wishlistRecycleView.setVisibility(View.GONE);
                    }
                    for(int count=0;count<no_of_cart_items;count++){

                       JSONObject wishListDetails = result.getJSONArray("result").getJSONObject(count);
                        if(!wishList.contains(wishListDetails.get("product_id").toString())){
                            wishList.add(wishListDetails.get("product_id").toString());
                            if(DBQueries.wishList.contains(ProductDetailsActivity.productId)){
                                ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST=true;
                                if(ProductDetailsActivity.addToWishlist !=null){
                                    ProductDetailsActivity.addToWishlist.setSupportImageTintList(context.getResources().getColorStateList(R.color.colorPrimary));
                                }
                            }else{
                                if(ProductDetailsActivity.addToWishlist !=null){
                                    ProductDetailsActivity.addToWishlist.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                                }
                                ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST=false;
                            }
                            String URL1= CommonApiConstant.backEndUrl+"/product/products-by-product-id/"+wishListDetails.get("product_id").toString();
                            VolleyMultipartRequest multipartRequest2 = new VolleyMultipartRequest(Request.Method.GET, URL1, new Response.Listener<NetworkResponse>() {
                                @Override
                                public void onResponse(NetworkResponse response) {
                                    if(dialog!=null){
                                        dialog.dismiss();
                                    }
                                    String resultResponse = new String(response.data);
                                    try {
                                        JSONObject result = new JSONObject(resultResponse);
                                        JSONObject productDetails= result.getJSONArray("result").getJSONObject(0);
                                        wishlistModelList.add(new WishlistModel(productDetails.get("product_image").toString().trim(),
                                                productDetails.get("product_name").toString(),
                                                URLDecoder.decode(  productDetails.get("product_hindi_name").toString(), "utf-8"),
                                                productDetails.get("price").toString(),
                                                productDetails.get("selling_price").toString(),
                                                (Boolean)productDetails.getBoolean("in_stock"),
                                                productDetails.get("catalog_name").toString(),
                                                productDetails.get("product_category").toString()
                                        ));
                                        if(MyWishlistFragment.wishlistAdapter !=null){
                                            MyWishlistFragment.wishlistAdapter.notifyDataSetChanged();
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
                                    if(dialog!=null){
                                        dialog.dismiss();
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
                                                DBQueries.signOut(context);
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
                                    Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
                                    return params;
                                }
                            };
                            VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest2);
                        }
                    }
//                    if(wishlistModelList.size()==0){
//                        Toast.makeText(context,R.string.no_wishlist_items_found,Toast.LENGTH_LONG).show();
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(dialog!=null){
                    dialog.dismiss();
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
                            DBQueries.signOut(context);
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
                Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization","Bearer "+AppPref.getUserToken(context));
                return headers;
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
                return params;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest1);

//        firebaseFirestore.collection("USERS")
//                .document(FirebaseAuth.getInstance().getUid())
//                .collection("USER_DATA")
//                .document("MY_WISHLIST")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if(task.isSuccessful()){
//
//                          for(long x=0;x<((long)task.getResult().get("list_size"));x++){
//                              wishList.add(task.getResult().get("product_id_"+x).toString());
//                              if(DBQueries.wishList.contains(ProductDetailsActivity.productId)){
//                                  ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST=true;
//                                  if(ProductDetailsActivity.addToWishlist !=null){
//                                      ProductDetailsActivity.addToWishlist.setSupportImageTintList(context.getResources().getColorStateList(R.color.colorPrimary));
//                                  }
//                             }else{
//                                  if(ProductDetailsActivity.addToWishlist !=null){
//                                      ProductDetailsActivity.addToWishlist.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
//                                  }
//                                 ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST=false;
//                              }
//                              if(loadProductData) {
//                                  firebaseFirestore.collection("PRODUCTS")
//                                          .document(task.getResult().get("product_id_" + x).toString())
//                                          .get()
//                                          .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                              @Override
//                                              public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                  if (task.isSuccessful()) {
//                                                      wishlistModelList.add(new WishlistModel(task.getResult().get("product_image").toString(),
//                                                              task.getResult().get("product_name").toString(),
//                                                              task.getResult().get("product_hindi_name").toString(),
//                                                              task.getResult().get("price").toString(),
//                                                              (Boolean) task.getResult().get("in_stock")
//                                                      ));
//                                                      if(MyWishlistFragment.wishlistAdapter !=null){
//                                                          MyWishlistFragment.wishlistAdapter.notifyDataSetChanged();
//                                                      }
//                                                  } else {
//                                                      String error = task.getException().getMessage();
//                                                      Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
//                                                  }
//                                              }
//                                          });
//                              }
//                          }
//                        }else{
//                            String error= task.getException().getMessage();
//                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//        dialog.dismiss();
    }
    public static void removeFromWishList(final int index, final Context context){

        Map<String,String> removeItemFromWishlist= new HashMap<>();
//        removeItemFromWishlist.put("email","pemaseervi@gmail.com");
//        removeItemFromWishlist.put("index",String.valueOf(index));
//        removeItemFromWishlist.put("product_name",DBQueries.wishList.get(index));

        removeItemFromWishlist.put("product_id",DBQueries.wishList.get(index));
        removeItemFromWishlist.put("user_id",CommonApiConstant.currentUser);
        String URL= CommonApiConstant.backEndUrl+"/user/remove-from-wishlist";

        VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.PUT, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                if(ProductDetailsActivity.addToWishlist !=null){
                    ProductDetailsActivity.addToWishlist.setEnabled(true);
                }
                String resultResponse = new String(response.data);

                try {
                    JSONObject result = new JSONObject(resultResponse);
                    wishList.remove(index);
                    if(wishlistModelList.size() !=0){
                        wishlistModelList.remove(index);
                        if(MyWishlistFragment.wishlistAdapter!=null){
                            MyWishlistFragment.wishlistAdapter.notifyDataSetChanged();
                        }
                    }
                    if(wishlistModelList.size()==0 &&  MyWishlistFragment.emptyWishListLayout!=null){
                        MyWishlistFragment.emptyWishListLayout.setVisibility(View.VISIBLE);

                    }
                    ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST=false;
                    Toast.makeText(context,context.getResources().getString(R.string.remove_wishlist_msg),Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(ProductDetailsActivity.addToWishlist !=null){
                    ProductDetailsActivity.addToWishlist.setSupportImageTintList(context.getResources().getColorStateList(R.color.colorPrimary));

                }
                if(ProductDetailsActivity.addToWishlist !=null){
                    ProductDetailsActivity.addToWishlist.setEnabled(true);
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
                            DBQueries.signOut(context);
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
                Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization","Bearer "+AppPref.getUserToken(context));
                return headers;
            }
            @Override
            protected Map<String, String> getParams() {
                return removeItemFromWishlist;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest1);


//        wishList.remove(index);
//        Map<String,Object> updateWishList = new HashMap<>();
//
//        for(int x=0;x<wishList.size();x++){
//            updateWishList.put("product_id_"+x,wishList.get(x));
//        }
//        updateWishList.put("list_size",(long)wishList.size());
//
//        firebaseFirestore.collection("USERS")
//                .document(FirebaseAuth.getInstance().getUid())
//                .collection("USER_DATA")
//                .document("MY_WISHLIST")
//                .set(updateWishList)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()){
//                           if(wishlistModelList.size() !=0){
//                               wishlistModelList.remove(index);
//                               MyWishlistFragment.wishlistAdapter.notifyDataSetChanged();
//                           }
//                            ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST=false;
//                           Toast.makeText(context,"Removed successfully",Toast.LENGTH_SHORT).show();
//                        }else{
//                            if(ProductDetailsActivity.addToWishlist !=null){
//                                ProductDetailsActivity.addToWishlist.setSupportImageTintList(context.getResources().getColorStateList(R.color.colorPrimary));
//
//                            }
//                            String error = task.getException().getMessage();
//                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
//                        }
//                        if(ProductDetailsActivity.addToWishlist !=null){
//                            ProductDetailsActivity.addToWishlist.setEnabled(true);
//                        }
//                    }
//                });
    }
    public static void loadCartList(final Context context, Dialog dialog, final TextView badgeCount,final TextView cartTotalAmount){
      String URL= CommonApiConstant.backEndUrl+"/user/cart-items/"+CommonApiConstant.currentUser;
        VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.GET, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                if(dialog!=null){
                    dialog.dismiss();
                }
                cartList.clear();
                cartItemModelList.clear();
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    int no_of_cart_items= result.getJSONArray("result").length();
                    for(int count=0;count<no_of_cart_items;count++){
                        JSONObject cartDetails=result.getJSONArray("result").getJSONObject(count);
                        if(!cartList.contains(cartDetails.get("product_id").toString())){
                            cartList.add(cartDetails.get("product_id").toString().trim());
                            final int productCartQuantity=Integer.parseInt(cartDetails.get("product_quantity").toString());
                            String URL1= CommonApiConstant.backEndUrl+"/product/products-by-product-id/"+cartDetails.get("product_id").toString();
                            VolleyMultipartRequest multipartRequest2 = new VolleyMultipartRequest(Request.Method.GET, URL1, new Response.Listener<NetworkResponse>() {
                                @Override
                                public void onResponse(NetworkResponse response) {
                                    if(dialog!=null){
                                        dialog.dismiss();
                                    }
                                    String resultResponse = new String(response.data);
                                    try {
                                        JSONObject result = new JSONObject(resultResponse);
                                        JSONObject productDetails= result.getJSONArray("result").getJSONObject(0);
                                        cartItemModelList.add(new CartItemModel(0,productDetails.get("product_name").toString().trim(),
                                                URLDecoder.decode(  productDetails.get("product_hindi_name").toString(), "utf-8"),
                                                productDetails.get("price").toString(),
                                                productDetails.get("selling_price").toString(),
                                                productDetails.get("product_image").toString(),
                                                        productCartQuantity,
                                                productDetails.get("catalog_name").toString(),
                                                productDetails.get("product_category").toString()
                                                )
                                        );
                                        if(cartList.size() ==0){
                                            cartItemModelList.clear();
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
                                    if(dialog!=null){
                                        dialog.dismiss();
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
                                                DBQueries.signOut(context);
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
                                    Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    return params;
                                }
                            };

                            VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest2);
                        }
                    }
//                    if(cartItemModelList.size()==0){
//                        Toast.makeText(context,R.string.no_cart_items_found,Toast.LENGTH_LONG).show();
//                    }
                    if(badgeCount!=null){
                        if(cartList.size() !=0){
                            badgeCount.setVisibility(View.VISIBLE);
                        }else{
                            badgeCount.setVisibility(View.INVISIBLE);
                        }
                        if(DBQueries.cartList.size() < 99){
                            badgeCount.setText(String.valueOf(DBQueries.cartList.size()));
                        }else{
                            badgeCount.setText("99");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(dialog!=null){
                    dialog.dismiss();
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
                            DBQueries.signOut(context);
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
                Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization","Bearer "+AppPref.getUserToken(context));
                return headers;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest1);
//        firebaseFirestore.collection("USERS")
//                .document(FirebaseAuth.getInstance().getUid())
//                .collection("USER_DATA")
//                .document("MY_CART")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if(task.isSuccessful()){
//                            cartList.clear();
//                            cartItemModelList.clear();
//                            for( long x=0;x<((long)task.getResult().get("list_size"));x++){
//                               if(!cartList.contains(task.getResult().get("product_id_"+x).toString())){
////                                   Log.d("pema","product_id_"+x+"added");
//                                   cartList.add(task.getResult().get("product_id_"+x).toString());
//                                   final int productCartQuantity=Integer.parseInt(task.getResult().get("product_cart_quantity_"+x).toString());
//                                   firebaseFirestore.collection("PRODUCTS")
//                                           .document(task.getResult().get("product_id_" + x).toString())
//                                           .get()
//                                           .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                               @Override
//                                               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                   if (task.isSuccessful()) {
////
//                                                           cartItemModelList.add(new CartItemModel(0,task.getResult().get("product_name").toString(),
//                                                                           task.getResult().get("product_hindi_name").toString(),
//                                                                           task.getResult().get("price").toString(),
//                                                                           task.getResult().get("product_image").toString(),
//                                                                           productCartQuantity
//                                                                   )
//                                                           );
////                                                        if(cartList.size() ==1){
////                                                            cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
//////                                                            LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
////                                                            parent.setVisibility(View.VISIBLE);
////                                                        }
//                                                       if(cartList.size() ==0){
//                                                           cartItemModelList.clear();
//                                                       }
////                                                        MyCartFragment.cartAdapter.notifyDataSetChanged();
//                                                   } else {
//                                                       String error = task.getException().getMessage();
//                                                       Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
//                                                   }
//                                               }
//                                           });
//                               }
////                                if(DBQueries.cartList.contains(ProductDetailsActivity.productId)){
////                                    ProductDetailsActivity.ALREADY_ADDED_TO_CART=true;
////                                }else{
////                                    ProductDetailsActivity.ALREADY_ADDED_TO_CART=false;
////                                }
////                                if(loadProductData) {
//
////                                }
//                            }
//                            if(badgeCount!=null){
//                                if(cartList.size() !=0){
//                                    badgeCount.setVisibility(View.VISIBLE);
//                                }else{
//                                    badgeCount.setVisibility(View.INVISIBLE);
//                                }
//                                if(DBQueries.cartList.size() < 99){
//                                    badgeCount.setText(String.valueOf(DBQueries.cartList.size()));
//                                }else{
//                                    badgeCount.setText("99");
//                                }
//                            }
//                        }else{
//                            String error= task.getException().getMessage();
//                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//        dialog.dismiss();
    }
    public static  void removeFromCartList(final int index, final  Context context, final TextView cartTotalAmount,final Dialog loadingDialog){
        if(loadingDialog !=null){
            loadingDialog.show();
        }

        Map<String,String> removeItemFromCart= new HashMap<>();
//                for(int x=0;x<cartList.size();x++){
//                    removeItemFromCart.put("email","pemaseervi@gmail.com");
//                    removeItemFromCart.put("product_id_" + x, cartList.get(x));
//                    removeItemFromCart.put("product_cart_quantity_" + x, String.valueOf(cartItemModelList.get(x).getProductQuantityForOrder()));
//            }
//        removeItemFromCart.put("list_size",String.valueOf((long)cartList.size()));
         removeItemFromCart.put("user_id",CommonApiConstant.currentUser);
         removeItemFromCart.put("product_id",cartList.get(index));


        String URL= CommonApiConstant.backEndUrl+"/user/remove-from-cart";

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

                    if(cartItemModelList.size() !=0){
                        cartList.remove(index);
                        cartItemModelList.remove(index);
                        MyCartFragment.cartAdapter.notifyDataSetChanged();
                        if(CartActivity.cartAdapter!=null){
                            CartActivity.cartAdapter.notifyDataSetChanged();
                        }
                        if(DeliveryActivity.cartAdapter!=null){
                            DeliveryActivity.cartAdapter.notifyDataSetChanged();
                        }
                    }
                    if(DBQueries.cartItemModelList.size()==0 && CartActivity.cartItemsRecycleView !=null &&   CartActivity.linearLayout!=null && CartActivity.emptyCartLayout!=null ){
                        CartActivity.cartItemsRecycleView.setVisibility(View.GONE);
                        CartActivity.linearLayout.setVisibility(View.GONE);
                        CartActivity.emptyCartLayout.setVisibility(View.VISIBLE);
                    }
                    if(DBQueries.cartItemModelList.size()==0 && MyCartFragment.emptyCartLayout!=null){
                        MyCartFragment.emptyCartLayout.setVisibility(View.VISIBLE);
                    }

                    Toast.makeText(context,"Product removed from cart successfully",Toast.LENGTH_SHORT).show();
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
                            DBQueries.signOut(context);
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
                Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization","Bearer "+AppPref.getUserToken(context));
                return headers;
            }
            @Override
            protected Map<String, String> getParams() {
                return removeItemFromCart;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest1);






//        Map<String,Object> updateCartList = new HashMap<>();
//
//        for(int x=0;x<cartList.size();x++){
//                updateCartList.put("product_id_" + x, cartList.get(x));
//                updateCartList.put("product_cart_quantity_" + x, String.valueOf(cartItemModelList.get(x).getProductQuantityForOrder()));
//            }
//        updateCartList.put("list_size",(long)cartList.size());
//
//        firebaseFirestore.collection("USERS")
//                .document(FirebaseAuth.getInstance().getUid())
//                .collection("USER_DATA")
//                .document("MY_CART")
//                .set(updateCartList)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()){
////                            if(cartList.size()==0){
////                                LinearLayout parent =(LinearLayout) cartTotalAmount.getParent().getParent();
////                                parent.setVisibility(View.GONE);
////                                cartItemModelList.clear();
////                            }
////                            ProductDetailsActivity.ALREADY_ADDED_TO_CART=false;
//
//                            Toast.makeText(context,"Removed successfully",Toast.LENGTH_SHORT).show();
//                        }else{
//                            String error = task.getException().getMessage();
//                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
//
//                        }
//                        if(addToCart !=null){
//                            addToCart.setEnabled(true);
//                        }
//                        if(loadingDialog !=null){
//                            loadingDialog.dismiss();
//                        }
//                    }
//                });

    }
    public static  void loadAddresses(final Context context, final Dialog loadingDialog, final boolean goToDeliveryActivity ){
        addressModelList.clear();

        String URL= CommonApiConstant.backEndUrl+"/user/addresses/"+CommonApiConstant.currentUser;

        VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.GET, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                if(loadingDialog!=null){
                    loadingDialog.dismiss();
                }
                String resultResponse = new String(response.data);
                try {
                    Intent deliveryIntent;
                    JSONObject result = new JSONObject(resultResponse);

                    int no_of_addresses= result.getJSONArray("result").length();
                    if(no_of_addresses == 0){
                        deliveryIntent = new Intent(context, AddAddressActivity.class);
                        deliveryIntent.putExtra("INTENT","deliveryIntent");
                    }else{
                        for(int count=0;count<no_of_addresses;count++){

                            JSONObject addressDetails = result.getJSONArray("result").getJSONObject(count);
                            Boolean is_selected;
//                            if(addressDetails.get("is_selected").toString().equals("null")){
//                                is_selected=true;
//                            }else{
                                is_selected=(Boolean) addressDetails.get("is_selected");
//                            }
                            if(DBQueries.language.equals("hi")){
                                try{
                                    addressModelList.add(new AddressModel(
                                            URLDecoder.decode(addressDetails.get("city").toString(),"utf-8"),
                                            URLDecoder.decode(addressDetails.get("locality").toString(),"utf-8"),
                                            URLDecoder.decode(addressDetails.get("flat_number").toString(),"utf-8"),
                                            addressDetails.get("pincode").toString(),
                                            URLDecoder.decode(addressDetails.get("landmark").toString(),"utf-8"),
                                            URLDecoder.decode(addressDetails.get("name").toString(),"utf-8"),
                                            addressDetails.get("mobile_number").toString(),
                                            addressDetails.get("alternate_mobile_number").toString(),
                                            addressDetails.get("state").toString(),
                                            is_selected,
                                            addressDetails.get("address_id").toString()
                                    ));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                            }else{
                                addressModelList.add(new AddressModel(addressDetails.get("city").toString(),
                                        addressDetails.get("locality").toString(),
                                        addressDetails.get("flat_number").toString(),
                                        addressDetails.get("pincode").toString(),
                                        addressDetails.get("landmark").toString(),
                                        addressDetails.get("name").toString(),
                                        addressDetails.get("mobile_number").toString(),
                                        addressDetails.get("alternate_mobile_number").toString(),
                                        addressDetails.get("state").toString(),
                                        is_selected,
                                        addressDetails.get("address_id").toString()
                                ));
                            }

                            if(is_selected){
                                selectedAddress =Integer.parseInt(String.valueOf( count));
                            }


                        }
                        deliveryIntent = new Intent(context, DeliveryActivity.class);
                    }
                    if(goToDeliveryActivity){
                        deliveryIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        context.startActivity(deliveryIntent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(loadingDialog!=null){
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
                            DBQueries.signOut(context);
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
                Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization","Bearer "+AppPref.getUserToken(context));
                return headers;
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
                return params;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest1);

//        firebaseFirestore.collection("USERS")
//                .document(FirebaseAuth.getInstance().getUid())
//                .collection("USER_DATA")
//                .document("MY_ADDRESSES")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if(task.isSuccessful()){
//                            Intent deliveryIntent;
//                            if((long)task.getResult().get("list_size")==0){
//                                deliveryIntent = new Intent(context, AddAddressActivity.class);
//                                deliveryIntent.putExtra("INTENT","deliveryIntent");
//
//                            }else{
//
//                                for(long position=1;position<(long)task.getResult().get("list_size")+1;position++){
//                                    addressModelList.add(new AddressModel(task.getResult().get("city_"+position).toString(),
//                                            task.getResult().get("locality_"+position).toString(),
//                                            task.getResult().get("flat_number_"+position).toString(),
//                                            task.getResult().get("pincode_"+position).toString(),
//                                            task.getResult().get("landmark_"+position).toString(),
//                                            task.getResult().get("name_"+position).toString(),
//                                            task.getResult().get("mobile_number_"+position).toString(),
//                                            task.getResult().get("alternate_mobile_number_"+position).toString(),
//                                            task.getResult().get("state_"+position).toString(),
//                                            (Boolean)task.getResult().get("selected_"+position)
//                                            ));
//                                    if((Boolean) task.getResult().get("selected_"+position)){
//                                        selectedAddress =Integer.parseInt(String.valueOf( position -1));
//                                    }
//                                }
////                                if(goToDeliveryActivity){
//                                    deliveryIntent = new Intent(context, DeliveryActivity.class);
//
////                                }
//
//                            }
//                            if(goToDeliveryActivity){
//                                deliveryIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                                context.startActivity(deliveryIntent);
//                            }
//                        }else{
//                            String error= task.getException().getMessage();
//                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
//                        }
//                        loadingDialog.dismiss();
//                    }
//                });
    }
//    public static  void loadCategoryWiseData(final Context context){
//        firebaseFirestore.collection("PRODUCTS")
//                .whereEqualTo("product_category","Ghamela")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
////                            task.getResult().getDocuments().get(0).get("product_name")
//                                  int no_of_products=  (int)task.getResult().getDocuments().size();
//                            for(int x=0;x<(no_of_products);x++){
//                                categoryWiseProducts.add(new CategoryWiseProductModel(
//                                        task.getResult().getDocuments().get(x).get("product_name").toString(),
//                                        Integer.parseInt(task.getResult().getDocuments().get(x).get("price").toString()),
//                                        1,
//                                        task.getResult().getDocuments().get(x).get("product_image").toString()
//                                ));
//                            }
//                        }else{
//                            String error= task.getException().getMessage();
//                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
    public static void loadCategoryWiseTop8Data(final CategoryHomePageAdapter homePageAdapter , final Context context, final int type, final String categoryName, final String categoryHindiName){
        String URL=CommonApiConstant.backEndUrl+"/product/categorywise-top-eight-product-data/"+categoryName;
        VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.GET, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    int no_of_catalogs= result.getJSONArray("result").length();
                    List<HorizontalProductScrollModel>categoryWiseTop8Products=new ArrayList<>();
                    for(int count=0;count<(no_of_catalogs);count++){
                        categoryWiseTop8Products.add(new HorizontalProductScrollModel(
                                "",
                                result.getJSONArray("result").getJSONObject(count).get("product_image").toString(),
                                result.getJSONArray("result").getJSONObject(count).get("product_name").toString(),
                                result.getJSONArray("result").getJSONObject(count).get("product_hindi_name").toString(),
                                "",
                                result.getJSONArray("result").getJSONObject(count).get("price").toString(),
                                result.getJSONArray("result").getJSONObject(count).get("selling_price").toString(),
                                result.getJSONArray("result").getJSONObject(count).get("catalog_name").toString(),
                                result.getJSONArray("result").getJSONObject(count).get("product_category").toString()

                                ));
                    }
                    if(categoryWiseTop8Products.size()>0){
                        categoryWiseTop8ProductsArrayList.add(categoryWiseTop8Products);
                        if(categoryWiseTop8Products.size()>4){
                            if(DBQueries.language.equals("hi")){
                                categoryPageModelList.add(new HomePageModel(type,context.getResources().getString(R.string.trending_msg)+" "+categoryHindiName +" "+context.getResources().getString(R.string.product),"#ffffff",categoryWiseTop8Products));
                            }else{
                                categoryPageModelList.add(new HomePageModel(type,"#Trending "+categoryName +" products for today","#ffffff",categoryWiseTop8Products));
                            }

                        }else{
                            if(DBQueries.language.equals("hi")){
                                categoryPageModelList.add(new HomePageModel(2,context.getResources().getString(R.string.trending_msg)+" "+categoryHindiName +" "+context.getResources().getString(R.string.product),"#ffffff",categoryWiseTop8Products));

                            }else{
                                categoryPageModelList.add(new HomePageModel(2,"#Trending "+categoryName +" products for today","#ffffff",categoryWiseTop8Products));
                            }
                            }
                          }
                    if(homePageAdapter!=null){
                        homePageAdapter.notifyDataSetChanged();
                    }
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
                            DBQueries.signOut(context);
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
                Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
                return params;
            }

        };

        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest1);


//
//        firebaseFirestore.collection("PRODUCTS")
//                .whereEqualTo("product_category",categoryName)
//                .orderBy("no_of_sales_today", Query.Direction.DESCENDING)
//                .limit(8)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            List<HorizontalProductScrollModel>categoryWiseTop8Products=new ArrayList<>();
//
////                            task.getResult().getDocuments().get(0).get("product_name")
//                            int no_of_products=  (int)task.getResult().getDocuments().size();
//                            for(int x=0;x<(no_of_products);x++){
////                                categoryWiseTop8Products.clear();
////                                horizontalProductScrollModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_id_"+x).toString(),
////                                        documentSnapshot.get("product_image_"+x).toString(),
////                                        documentSnapshot.get("product_title_"+x).toString(),
////                                        documentSnapshot.get("product_subtitle_"+x).toString(),
////                                        documentSnapshot.get("product_price_"+x).toString()
////                                ));
//                                categoryWiseTop8Products.add(new HorizontalProductScrollModel(
//                                        "",
//                                        task.getResult().getDocuments().get(x).get("product_image").toString(),
//                                        task.getResult().getDocuments().get(x).get("product_name").toString(),
//                                        task.getResult().getDocuments().get(x).get("product_hindi_name").toString(),
//                                        "",
//                                        task.getResult().getDocuments().get(x).get("price").toString()
//
//                                ));
//                            }
//                            if(categoryWiseTop8Products.size()>0){
//                                categoryWiseTop8ProductsArrayList.add(categoryWiseTop8Products);
//                                categoryPageModelList.add(new HomePageModel(type,"#Trending "+categoryName +" products for today","#ffffff",categoryWiseTop8Products));
//                            }
//                            if(homePageAdapter!=null){
//                                homePageAdapter.notifyDataSetChanged();
//                            }
//
//                        }else{
//                            String error= task.getException().getMessage();
//                            Log.d("dghs",error);
//                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
    }
    public static void loadCatalogWiseTop8Data(final HomePageAdapter homePageAdapter , final Context context, final int type, final String catalogName,final String catalogHindiName){
        String URL=CommonApiConstant.backEndUrl+"/product/catalogwise-top-eight-product-data/"+catalogName;
        VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.GET, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    int no_of_catalogs= result.getJSONArray("result").length();
                    List<HorizontalProductScrollModel>catalogWiseTop8Products=new ArrayList<>();
                    for(int count=0;count<(no_of_catalogs);count++){
                        catalogWiseTop8Products.add(new HorizontalProductScrollModel(
                                result.getJSONArray("result").getJSONObject(count).get("product_name").toString(),
                                result.getJSONArray("result").getJSONObject(count).get("product_image").toString(),
                                result.getJSONArray("result").getJSONObject(count).get("product_name").toString(),
                                result.getJSONArray("result").getJSONObject(count).get("product_hindi_name").toString(),
                                "",
                                result.getJSONArray("result").getJSONObject(count).get("price").toString(),
                                result.getJSONArray("result").getJSONObject(count).get("selling_price").toString(),
                                result.getJSONArray("result").getJSONObject(count).get("catalog_name").toString(),
                                result.getJSONArray("result").getJSONObject(count).get("product_category").toString()
                        ));
                    }
                    if( catalogWiseTop8Products.size()>0){
                       catalogWiseTop8ProductsArrayList.add(catalogWiseTop8Products);
                        if(catalogWiseTop8Products.size()>4){
                            if(DBQueries.language.equals("hi")){
                                homePageModelList.add(new HomePageModel(type,context.getResources().getString(R.string.trending_msg) +" "+catalogHindiName+" "+context.getResources().getString(R.string.product),"#ffffff",catalogWiseTop8Products));
                            }else{
                                homePageModelList.add(new HomePageModel(type,"#Trending "+catalogName+" products for today","#ffffff",catalogWiseTop8Products));
                            }
                          }else{
                            if(DBQueries.language.equals("hi")){
                                homePageModelList.add(new HomePageModel(2, context.getResources().getString(R.string.trending_msg) +" "+catalogHindiName +" "+context.getResources().getString(R.string.product),"#ffffff",catalogWiseTop8Products));
                            }else{
                                homePageModelList.add(new HomePageModel(2,"#Trending "+catalogName+" products for today","#ffffff",catalogWiseTop8Products));
                            }
                        }
                    }
                    if(homePageAdapter!=null){
                        homePageAdapter.notifyDataSetChanged();
                    }
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
                            DBQueries.signOut(context);
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
                Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
                return params;
            }

        };

        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest1);



//        firebaseFirestore.collection("PRODUCTS")
//                .whereEqualTo("catalog_name",catalogName)
//                .orderBy("no_of_sales_today", Query.Direction.DESCENDING)
//                .limit(8)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
////                            task.getResult().getDocuments().get(0).get("product_name")
//                            int no_of_products=  (int)task.getResult().getDocuments().size();
//                            List<HorizontalProductScrollModel>catalogWiseTop8Products=new ArrayList<>();
//
//                            for(int x=0;x<(no_of_products);x++){
//
////                                horizontalProductScrollModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_id_"+x).toString(),
////                                        documentSnapshot.get("product_image_"+x).toString(),
////                                        documentSnapshot.get("product_title_"+x).toString(),
////                                        documentSnapshot.get("product_subtitle_"+x).toString(),
////                                        documentSnapshot.get("product_price_"+x).toString()
////                                ));
//                                catalogWiseTop8Products.add(new HorizontalProductScrollModel(
//                                        "",
//                                        task.getResult().getDocuments().get(x).get("product_image").toString(),
//                                        task.getResult().getDocuments().get(x).get("product_name").toString(),
//                                        task.getResult().getDocuments().get(x).get("product_hindi_name").toString(),
//                                        "",
//                                        task.getResult().getDocuments().get(x).get("price").toString()
//
//                                ));
//
//                            }
//                            if( catalogWiseTop8Products.size()>0){
//                                homePageModelList.add(new HomePageModel(type,"#Trending "+catalogName+" products for today","#ffffff",catalogWiseTop8Products));
//                            }
//                            if(homePageAdapter!=null){
//                                homePageAdapter.notifyDataSetChanged();
//                            }
//                        }else{
//                            String error= task.getException().getMessage();
//                            Log.d("dghs1",error);
//                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
    }
    public static void addToCart(final Context context, final String productId, final String productName,final String productHindiName, final String productPrice,final String sellingPrice, final String productImage, final int productCartQuantity, final Dialog loadingDialog, final String product_catalog, final String product_category){
        if(loadingDialog !=null){
            loadingDialog.show();
        }
        Map<String,String> addProductToCart= new HashMap<>();
//        addProductToCart.put("email","pemaseervi@gmail.com");
//        addProductToCart.put("product_id_"+String.valueOf(DBQueries.cartList.size()),productId);
//        addProductToCart.put("product_cart_quantity_"+String.valueOf((DBQueries.cartList.size())),String.valueOf(productCartQuantity));
//        addProductToCart.put("list_size",String.valueOf((long)(DBQueries.cartList.size()+1)));

         addProductToCart.put("user_id",CommonApiConstant.currentUser);
         addProductToCart.put("product_id",productName);
         addProductToCart.put("product_quantity",String.valueOf(productCartQuantity));
        String URL= CommonApiConstant.backEndUrl+"/user/add-to-cart";

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
                    if(DBQueries.cartList.contains(productName)){
                        Toast.makeText(context,context.getResources().getString(R.string.already_added_to_cart_msg),Toast.LENGTH_SHORT).show();
                    }else{
                        DBQueries.cartItemModelList.add(new CartItemModel(0,productName,
                                productHindiName,
                                productPrice,
                                sellingPrice,
                                productImage,
                                productCartQuantity,
                                product_catalog,
                                product_category

                        ));
                        DBQueries.cartList.add(productName);
                        if(ProductDetailsActivity.badgeCount!=null){
                            if(DBQueries.cartList.size() < 99){
                                ProductDetailsActivity.badgeCount.setText(String.valueOf(DBQueries.cartList.size()));
                            }else{
                                ProductDetailsActivity.badgeCount.setText("99");
                            }
                        }
                        Toast.makeText(context,context.getResources().getString(R.string.cartMsg),Toast.LENGTH_SHORT).show();

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
                            DBQueries.signOut(context);
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
                Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization","Bearer "+AppPref.getUserToken(context));
                return headers;
            }
            @Override
            protected Map<String, String> getParams() {
                return addProductToCart;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest1);



//        firebaseFirestore.collection("USERS").document(DBQueries.currentUser.getUid())
//                .collection("USER_DATA")
//                .document("MY_CART")
//                .update(addProductToCart)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()){
////                                                if(DBQueries.cartItemModelList.size() !=0){
//                            DBQueries.cartItemModelList.add(new CartItemModel(0,productName,
//                                    productHindiName,
//                                    productPrice,
//                                   productImage,
//                                    productCartQuantity
//
//                            ));
////
////                                                }
//
////                            ProductDetailsActivity.ALREADY_ADDED_TO_CART=true;
//                            DBQueries.cartList.add(productId);
//                            Toast.makeText(context,"Product added successfully",Toast.LENGTH_SHORT).show();
//                        }else{
//                            String error = task.getException().getMessage();
//                            Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
//                        }
//                        if(addToCart !=null){
//                            addToCart.setEnabled(true);
//                        }
//                        if(loadingDialog !=null){
//                            loadingDialog.dismiss();
//                        }
//                    }
//                });
    }


    public static void loadOrdersByUser(final Context context, final OrdersAdapter ordersAdapter,final Dialog loadingDialog){
        if(loadingDialog!=null){
            loadingDialog.show();
        }

        String URL= CommonApiConstant.backEndUrl+"/order/saved-orders/"+CommonApiConstant.currentUser;

        VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.GET, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                if(loadingDialog !=null){
                    loadingDialog.dismiss();
                }
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    int no_of_orders= result.getJSONArray("result").length();
                    ordersItemModelList.clear();
                    nonFilteredOrdersItemModelList.clear();
                    if(no_of_orders==0){
                        OrderFragment.noOrdersFound.setVisibility(View.VISIBLE);
                    }else{
                        OrderFragment.noOrdersFound.setVisibility(View.GONE);
                    }
                    for(int count=0;count<(no_of_orders);count++){
                        String orderId=result.getJSONArray("result").getJSONObject(count).get("order_id").toString();
                        String URL1= CommonApiConstant.backEndUrl+"/order/details/"+CommonApiConstant.currentUser+"/"+orderId;

                        VolleyMultipartRequest multipartRequest2 = new VolleyMultipartRequest(Request.Method.GET, URL1, new Response.Listener<NetworkResponse>() {
                            @Override
                            public void onResponse(NetworkResponse response) {
                                if(loadingDialog !=null){
                                    loadingDialog.dismiss();
                                }
                                String resultResponse = new String(response.data);
                                try {
                                    JSONObject result = new JSONObject(resultResponse);
                                    int no_of_orders= result.getJSONArray("result").length();
                                   for(int count1=0;count1<(no_of_orders);count1++){
                                        boolean isLandmarkAvail=false;
                                        String orderId=result.getJSONArray("result").getJSONObject(count1).get("order_id").toString();
                                        String flat_number=URLDecoder.decode(result.getJSONArray("result").getJSONObject(count1).get("flat_number").toString(),"utf-8");
                                        String locality=URLDecoder.decode(result.getJSONArray("result").getJSONObject(count1).get("locality").toString(),"utf-8");
                                        String landmark=URLDecoder.decode(result.getJSONArray("result").getJSONObject(count1).get("landmark").toString(),"utf-8");
                                        if(landmark.equals("")){
                                            isLandmarkAvail=false;
                                        }else{
                                            isLandmarkAvail=true;
                                        }
                                        String city=URLDecoder.decode(result.getJSONArray("result").getJSONObject(count1).get("city").toString(),"utf-8");
                                        String state=result.getJSONArray("result").getJSONObject(count1).get("state").toString();
                                        String mobile_number=result.getJSONArray("result").getJSONObject(count1).get("mobile_number").toString();
                                        String alternate_mobile=result.getJSONArray("result").getJSONObject(count1).get("alternate_mobile").toString();
                                        String ordered_date=result.getJSONArray("result").getJSONObject(count1).get("ordered_date").toString();
                                        String packed_date=result.getJSONArray("result").getJSONObject(count1).get("packed_date").toString();
                                        String shipped_date=result.getJSONArray("result").getJSONObject(count1).get("shipped_date").toString();
                                        String delivered_date=result.getJSONArray("result").getJSONObject(count1).get("delivered_date").toString();
                                        String cancelled_date=result.getJSONArray("result").getJSONObject(count1).get("cancelled_date").toString();
                                        String full_name=URLDecoder.decode(result.getJSONArray("result").getJSONObject(count1).get("full_name").toString(),"utf-8");
                                        String payment_method=result.getJSONArray("result").getJSONObject(count1).get("payment_method").toString();
                                        String pincode=result.getJSONArray("result").getJSONObject(count1).get("pincode").toString();
                                        String total_items=result.getJSONArray("result").getJSONObject(count1).get("total_items").toString();
                                        String total_amount=result.getJSONArray("result").getJSONObject(count1).get("total_amount").toString();
                                        String order_image=result.getJSONArray("result").getJSONObject(count1).get("order_image").toString();
                                        String order_status=result.getJSONArray("result").getJSONObject(count1).get("order_status").toString();
                                        ordersItemModelList.add(new OrdersItemModel(order_image,
                                                order_status,
                                                flat_number+", "+locality+", "+landmark+", "+city+", "+state,
                                                ordered_date,
                                                packed_date,
                                                shipped_date,
                                                delivered_date,
                                                cancelled_date,
                                                full_name+"- "+mobile_number+" "+alternate_mobile,
                                                payment_method,
                                                pincode,
                                                Long.parseLong(total_items),
                                                Long.parseLong(total_amount),
                                                orderId
                                        ));
                                       nonFilteredOrdersItemModelList.add(new OrdersItemModel(order_image,
                                               order_status,
                                               flat_number+", "+locality+", "+landmark+", "+city+", "+state,
                                               ordered_date,
                                               packed_date,
                                               shipped_date,
                                               delivered_date,
                                               cancelled_date,
                                               full_name+"- "+mobile_number+" "+alternate_mobile,
                                               payment_method,
                                               pincode,
                                               Long.parseLong(total_items),
                                               Long.parseLong(total_amount),
                                               orderId
                                       ));
                                        if(ordersAdapter!=null){
                                            ordersAdapter.notifyDataSetChanged();
                                        }
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
                                            DBQueries.signOut(context);
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
                                Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Authorization","Bearer "+AppPref.getUserToken(context));
                                return headers;
                            }
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String,String>params= new HashMap<>();
                                return params;
                            }
                        };
                        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest2);
//                        String orderId=result.getJSONArray("result").getJSONObject(count).get("order_id").toString();
//                        String address=result.getJSONArray("result").getJSONObject(count).get("address").toString();
//                        String ordered_date=result.getJSONArray("result").getJSONObject(count).get("ordered_date").toString();
//                        String packed_date=result.getJSONArray("result").getJSONObject(count).get("packed_date").toString();
//                        String shipped_date=result.getJSONArray("result").getJSONObject(count).get("shipped_date").toString();
//                        String delivered_date=result.getJSONArray("result").getJSONObject(count).get("delivered_date").toString();
//                        String cancelled_date=result.getJSONArray("result").getJSONObject(count).get("cancelled_date").toString();
//                        String full_name=result.getJSONArray("result").getJSONObject(count).get("full_name").toString();
//                        String payment_method=result.getJSONArray("result").getJSONObject(count).get("payment_method").toString();
//                        String pincode=result.getJSONArray("result").getJSONObject(count).get("pincode").toString();
//                        String total_items=result.getJSONArray("result").getJSONObject(count).get("total_items").toString();
//                        String total_amount=result.getJSONArray("result").getJSONObject(count).get("total_amount").toString();
//                        String order_image=result.getJSONArray("result").getJSONObject(count).get("order_image").toString();
//                        String order_status=result.getJSONArray("result").getJSONObject(count).get("order_status").toString();
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        ordersItemModelList.add(new OrdersItemModel(order_image,
//                                order_status,
//                                address,
//                                new Date(),
//                                new Date(),
//                                new Date(),
//                                new Date(),
//                                new Date(),
//                                full_name,
//                                payment_method,
//                                pincode,
//                                Long.parseLong(total_items),
//                                Long.parseLong(total_amount),
//                                orderId
//                        ));
//                        if(ordersAdapter!=null){
//                            ordersAdapter.notifyDataSetChanged();
//                        }
                    }
//                    if(ordersItemModelList.size()==0){
//                        Toast.makeText(context,R.string.no_order_found,Toast.LENGTH_LONG).show();
//                    }
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
                            DBQueries.signOut(context);
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
                Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization","Bearer "+AppPref.getUserToken(context));
                return headers;
            }
            @Override
            protected Map<String, String> getParams() {

                Map<String,String>params= new HashMap<>();

                return params;

            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest1);
    }
    public static void loadOrders(final Context context, @Nullable final OrderItemDetailsAdapter myOrderAdapter , final Dialog loadingDialog,final String orderId){
        if(loadingDialog!=null){
            loadingDialog.show();
        }

        String URL= CommonApiConstant.backEndUrl+"/order/item-details/"+CommonApiConstant.currentUser+"/"+orderId;

        VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.GET, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                if(loadingDialog !=null){
                    loadingDialog.dismiss();
                }
                String resultResponse = new String(response.data);

                try {
                    myOrderItemModelList.clear();
                    JSONObject result = new JSONObject(resultResponse);
                    int no_of_orders= result.getJSONArray("result").length();
                    for(int count=0;count<(no_of_orders);count++){
                        String product_id=result.getJSONArray("result").getJSONObject(count).get("product_id").toString();
                        String order_id=result.getJSONArray("result").getJSONObject(count).get("order_id").toString();
                        String product_price=result.getJSONArray("result").getJSONObject(count).get("product_price").toString();
                        String product_quantity=result.getJSONArray("result").getJSONObject(count).get("product_quantity").toString();
                        String user_id=result.getJSONArray("result").getJSONObject(count).get("user_id").toString();
                        String product_title=result.getJSONArray("result").getJSONObject(count).get("product_title").toString();
                        String product_hindi_title=URLDecoder.decode(  result.getJSONArray("result").getJSONObject(count).get("product_hindi_title").toString(), "utf-8");
                        String product_image=result.getJSONArray("result").getJSONObject(count).get("product_image").toString();
                       final MyOrderItemModel myOrderItemModel = new MyOrderItemModel(
                                product_id,
                                order_id,
                                product_price,
                                Long.parseLong(product_quantity),
                                user_id,
                                product_title,
                                product_hindi_title,
                                product_image



                        );


                        myOrderItemModelList.add(myOrderItemModel);
                    }
                    if(myOrderAdapter!=null){
                        myOrderAdapter.notifyDataSetChanged();
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
                            DBQueries.signOut(context);
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
                Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization","Bearer "+AppPref.getUserToken(context));
                return headers;
            }
            @Override
            protected Map<String, String> getParams() {

                Map<String,String>params= new HashMap<>();

                return params;

            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest1);
//        firebaseFirestore.collection("ORDERS")
//                                       .document(orderId)
//                                       .collection("OrderedItems")
//                                       .get()
//                                       .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                           @Override
//                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                               if(task.isSuccessful()){
//                                                   for(DocumentSnapshot orderItems:task.getResult().getDocuments()){
//                                                       final MyOrderItemModel myOrderItemModel = new MyOrderItemModel(
//                                                               orderItems.getString("Product Id"),
//                                                               orderItems.getString("Order Id"),
//                                                               orderItems.getString("Product Price"),
//                                                               orderItems.getLong("Product Quantity"),
//
////                                                               orderItems.getString("Address"),
////                                                               orderItems.getDate("Ordered date"),
////                                                               orderItems.getDate("Packed date"),
////                                                               orderItems.getDate("Shipped date"),
////                                                               orderItems.getDate("Delivered date"),
////                                                               orderItems.getDate("Cancelled date"),
////                                                               orderItems.getString("FullName"),
////                                                               orderItems.getString("Payment Method"),
////                                                               orderItems.getString("Pincode"),
//
//
//                                                               orderItems.getString("User Id"),
//                                                               orderItems.getString("Product Title"),
//                                                               orderItems.getString("Product Hindi Title"),
//                                                               orderItems.getString("Product Image")
//
//
//                                                       );
//
//
//                                                       myOrderItemModelList.add(myOrderItemModel);
//                                                   }
//                                                   if(myOrderAdapter!=null){
//                                                       myOrderAdapter.notifyDataSetChanged();
//                                                   }
//
//
//                                               }else{
//                                                   String error =  task.getException().getMessage();
//                                                   Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
//                                               }
//                                               if(loadingDialog !=null){
//                                                   loadingDialog.dismiss();
//                                               }
//                                           }
//                                       });
    }

    public static void checkNotifications(final Context context,boolean remove,@Nullable final TextView notifyCount){
        String URL= CommonApiConstant.backEndUrl+"/user/notifications/"+CommonApiConstant.currentUser;
        VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.GET, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    notificationModelList.clear();
                    int unread=0;
                    JSONObject result = new JSONObject(resultResponse);
                    int no_of_notifications= result.getJSONArray("result").length();
                    for(int count=0;count<no_of_notifications;count++){
                        JSONObject notificationDetails=result.getJSONArray("result").getJSONObject(count);
                        notificationModelList.add(0,new NotificationModel(
                                notificationDetails.get("image").toString(),
                                notificationDetails.get("title").toString(),
                                notificationDetails.get("body").toString(),
                                notificationDetails.getBoolean("is_readed"),
                                notificationDetails.getString("updated_on")

                        ));
                        if(!notificationDetails.getBoolean("is_readed")){
                            unread++;
                            if(notifyCount !=null){
                                if(unread >0){
                                    notifyCount.setVisibility(View.VISIBLE);
                                    if(unread < 99){
                                        notifyCount.setText(String.valueOf(unread));
                                    }else{
                                        notifyCount.setText("99");
                                    }
                                }else{
                                    notifyCount.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                    }
                    if(NotificationActivity.adapter !=null){
                        NotificationActivity.adapter.notifyDataSetChanged();
                    }
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
                            DBQueries.signOut(context);
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
                Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization","Bearer "+AppPref.getUserToken(context));
                return headers;
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
                return params;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest1);



//
//        if(remove){
//             if(registration!=null){
//                 registration.remove();
//             }
//        }else{
//             registration= firebaseFirestore.collection("USERS")
//                     .document(FirebaseAuth.getInstance().getUid())
//                     .collection("USER_DATA")
//                     .document("MY_NOTIFICATIONS")
//                     .addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                         @Override
//                         public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                             if(documentSnapshot !=null && documentSnapshot.exists()){
//                                 notificationModelList.clear();
//                                 int unread=0;
//                                 for(long x=0;x<(long)documentSnapshot.get("list_size");x++){
//                                     notificationModelList.add(0,new NotificationModel(
//                                             documentSnapshot.get("Image_"+x).toString(),
//                                             documentSnapshot.get("Body_"+x).toString(),
//                                             documentSnapshot.getBoolean("Readed_"+x)
//
//                                     ));
//                                     if(!documentSnapshot.getBoolean("Readed_"+x)){
//                                         unread++;
//                                         if(notifyCount !=null){
//                                             if(unread >0){
//                                                 notifyCount.setVisibility(View.VISIBLE);
//                                                 if(unread < 99){
//                                                     notifyCount.setText(String.valueOf(unread));
//                                                 }else{
//                                                     notifyCount.setText("99");
//                                                 }
//                                             }else{
//                                                 notifyCount.setVisibility(View.INVISIBLE);
//                                             }
//                                         }
//                                     }
//                                 }
//                                 if(NotificationActivity.adapter !=null){
//                                     NotificationActivity.adapter.notifyDataSetChanged();
//                                 }
//                             }
//                         }
//                     });
//        }

    }

    public static void getAllOrderStatus(final Context context, final OrderStatusAdapter orderStatusAdapter, final Dialog loadingDialog){
        DBQueries.orderStatusModelList.clear();
        String URL= CommonApiConstant.backEndUrl+"/order/get-all-order-status";
        VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.GET, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    int no_of_order_status= result.getJSONArray("result").length();
                    for(int count=0;count<no_of_order_status;count++){
                        DBQueries.orderStatusModelList.add(new OrderStatusModel(result.getJSONArray("result").get(count).toString()));
                    }
                    if(orderStatusAdapter!=null){
                        orderStatusAdapter.notifyDataSetChanged();
                    }
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
                            DBQueries.signOut(context);
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
                Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization","Bearer "+AppPref.getUserToken(context));
                return headers;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest1);

    }
    public static void addToWishList(final Context context, final FloatingActionButton addToWishlist,final String productId, String productImage, String productHindiName, String price, String sellingPrice, Boolean inStock, String catalogName, String productCategory){

        Map<String,String> addProductID= new HashMap<>();
        addProductID.put("product_id",productId);
        addProductID.put("user_id",CommonApiConstant.currentUser);
        String URL= CommonApiConstant.backEndUrl+"/user/add-to-wishlist";

        VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.PUT, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    if(DBQueries.wishlistModelList.size() !=0){
                        DBQueries.wishlistModelList.add(new WishlistModel(productImage,
                                productId,
                                URLDecoder.decode(  productHindiName, "utf-8"),
                                price,
                                sellingPrice,
                                inStock,
                                catalogName,
                                productCategory
                        ));
                    }
                    addToWishlist.setSupportImageTintList(context.getResources().getColorStateList(R.color.colorPrimary));
                    DBQueries.wishList.add(productId);
                    Toast.makeText(context,context.getResources().getString(R.string.wishlist_msg),Toast.LENGTH_SHORT).show();
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
                            DBQueries.signOut(context);
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
                Toast.makeText(context,errorMessage,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization","Bearer "+ AppPref.getUserToken(context));
                return headers;
            }
            @Override
            protected Map<String, String> getParams() {
                return addProductID;
            }
        };
        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest1);
//
    }
    public static void clearData(){
        categoryModelList.clear();
        homePageModelList.clear();
        wishList.clear();
        wishlistModelList.clear();
        cartList.clear();
        cartItemModelList.clear();
        addressModelList.clear();
        categoryWiseProducts.clear();
        categoryModelListByCatalog.clear();
        catalogModelList.clear();
        categoryPageModelList.clear();
        notificationModelList.clear();
        myOrderItemModelList.clear();
        horizontalProductScrollModelList.clear();
    }
    public static void signOut(Context context){
        DBQueries.clearData();
        CommonApiConstant.currentUser =null;
        SharedPreferenceHelper helper = SharedPreferenceHelper.getInstance(context);
        helper.clearAllSharedPreferences(context, AppPref.APP_PREF);
        Intent registerIntent = new Intent(context, PhoneNumberActivity.class);
        registerIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(registerIntent);
        ((Activity) context).finish();
    }
}
