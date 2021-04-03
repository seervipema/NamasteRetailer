package com.pemaseervi.myretailer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.utility.AppPref;
import com.pemaseervi.myretailer.utility.CommonApiConstant;
import com.pemaseervi.myretailer.utility.VolleyMultipartRequest;
import com.pemaseervi.myretailer.utility.VolleySingleton;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.pemaseervi.myretailer.CategoryWiseAllProductsActivity.totalCartAmount;

public class HomeActivity extends AppCompatActivity  {

    //implements NavigationView.OnNavigationItemSelectedListener
    private AppBarConfiguration mAppBarConfiguration;
    private static final int HOME_FRAGMENT=0;
    public static  final int CART_FRAGMENT=1;
    public static Activity mainActivity;
    private  static final int ORDERS_FRAGMENT=2;
    private static final int WISHLIST_FRAGMENT=3;
    private static  final int  ACCOUNT_FRAGMENT=4;
    private static final int LANGUAGE_FRAGMENT =5 ;


    public static ShimmerFrameLayout shimmerFrameLayout;
    private NavigationView navigationView;
    public static FrameLayout frameLayout;
    public static Dialog signInDialog;
    public static int currentFragment=-1;
    private ImageView actionBarLogo;
    public static TextView badgeCount;
    private int scrollFlags;
    public static Boolean showCart = false;
    private com.google.android.material.appbar.AppBarLayout.LayoutParams params;
    public static CircleImageView profileView;
    private TextView fullname,email;
    private ImageView addProfileIcon;
    private boolean connectionAvailable = true;
    private CheckInternetConnection checkInternetConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        actionBarLogo=findViewById(R.id.action_bar_logo);
        setSupportActionBar(toolbar);
        frameLayout=findViewById(R.id.main_frameLayout);
         params =(AppBarLayout.LayoutParams)toolbar.getLayoutParams();
        scrollFlags= params.getScrollFlags();
       // setFragment(new HomeFragment(),HOME_FRAGMENT);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        DBQueries.language=Locale.getDefault().getLanguage();
        navigationView = findViewById(R.id.nav_view);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        profileView = navigationView.getHeaderView(0).findViewById(R.id.main_profile_image);
        fullname = navigationView.getHeaderView(0).findViewById(R.id.main_fullName);
        email = navigationView.getHeaderView(0).findViewById(R.id.main_email);
        addProfileIcon = navigationView.getHeaderView(0).findViewById(R.id.add_profile_icon);

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

//       navigationView.setNavigationItemSelectedListener(this);
//       navigationView.getMenu().getItem(0).setChecked(true);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

//        NavigationUI.setupWithNavController(navigationView, navController);

        //ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//       if(DBQueries.currentUser == null){
//           navigationView.getMenu().getItem(navigationView.getMenu().size()-1).setEnabled(false);
//       }else{
//           navigationView.getMenu().getItem(navigationView.getMenu().size()-1).setEnabled(true);
//       }
        /////////////////////////
        checkInternetConnection= new CheckInternetConnection();
        checkInternetConnection.addConnectionChangeListener(new ConnectionChangeListener() {
            @Override
            public void onConnectionChanged(boolean isConnectionAvailable) {
                if(connectionAvailable && !isConnectionAvailable) {
                    Toast toast= Toast.makeText(HomeActivity.this, "Internet connection unavailable!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP|Gravity.LEFT, 200, 150);
                    toast.show();
                    connectionAvailable = false;
                }
                else if(!connectionAvailable && isConnectionAvailable) {
                    Toast toast= Toast.makeText(HomeActivity.this, "Internet connection is back again.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP|Gravity.LEFT, 200, 150);
                    toast.show();
                    connectionAvailable = true;
                }
            }
        });
        /////////////////////////
         signInDialog= new Dialog(HomeActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);
        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        Button signInDialogBtn=signInDialog.findViewById(R.id.sign_in_btn);
        Button signUpDialogBtn=signInDialog.findViewById(R.id.sign_up_btn);
        final Intent registerIntent = new Intent(HomeActivity.this,RegisterActivity.class);

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
//        Toast.makeText(HomeActivity.this ,String.valueOf(showCart),Toast.LENGTH_SHORT).show();
//        if(showCart){
//               mainActivity=this;
////               drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//               getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//               getSupportActionBar().setTitle("My Cart");
//               actionBarLogo.setVisibility(View.GONE);
//               invalidateOptionsMenu();
//               currentFragment=CART_FRAGMENT;
//               Navigation.findNavController(HomeActivity.this, R.id.nav_host_fragment).navigate(R.id.action_nav_myMall_to_nav_cart);
//
//        }else{
//            R.id.nav_home,R.id.nav_cart,R.id.nav_orders,R.id.nav_wishlist,R.id.nav_account
            int orderFragment=getIntent().getIntExtra("orderFragment",-1);
        if(orderFragment!=-1)
        {
            Navigation.findNavController(HomeActivity.this, R.id.nav_host_fragment).navigate(R.id.nav_orders);

        }

            mAppBarConfiguration = new AppBarConfiguration.Builder(
                   R.id.nav_myMall,R.id.nav_cart,R.id.nav_orders,R.id.nav_wishlist,R.id.nav_account,R.id.nav_share)
                    .setDrawerLayout(drawer)
                    .build();

            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationView navView =findViewById(R.id.nav_view);
            NavigationUI.setupWithNavController(navView,navController);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
                @Override
                public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {

//                    if(DBQueries.currentUser == null){
//                        signInDialog.show();
//                    }else{
                        //                   Toast.makeText(HomeActivity.this,"pema"+destination.getId(),Toast.LENGTH_LONG).show();
                        if(destination.getId() == R.id.nav_myMall){

                            currentFragment = HOME_FRAGMENT;
                            actionBarLogo.setVisibility(View.VISIBLE);
                            getSupportActionBar().setDisplayShowTitleEnabled(true);
                            invalidateOptionsMenu();
                        }else if(destination.getId() == R.id.nav_orders){
                            actionBarLogo.setVisibility(View.GONE);
                            getSupportActionBar().setDisplayShowTitleEnabled(true);
                            invalidateOptionsMenu();
                            DBQueries.ordersItemModelList.clear();
                            DBQueries.myOrderItemModelList.clear();
                            currentFragment=ORDERS_FRAGMENT;
                        }else if(destination.getId() == R.id.nav_cart){
                            actionBarLogo.setVisibility(View.GONE);
                            getSupportActionBar().setDisplayShowTitleEnabled(true);
                            invalidateOptionsMenu();
                            currentFragment=CART_FRAGMENT;
                        }else if(destination.getId() == R.id.nav_wishlist){
                            actionBarLogo.setVisibility(View.GONE);
                            getSupportActionBar().setDisplayShowTitleEnabled(true);
                            invalidateOptionsMenu();
                            currentFragment=WISHLIST_FRAGMENT;
                        }else if(destination.getId() == R.id.nav_account){
                            actionBarLogo.setVisibility(View.GONE);
                            getSupportActionBar().setDisplayShowTitleEnabled(true);
                            invalidateOptionsMenu();
                            currentFragment=ACCOUNT_FRAGMENT;
                        }else if(destination.getId() == R.id.action_nav_myMall_to_selectLangaugeFragment){
                            actionBarLogo.setVisibility(View.GONE);
                            getSupportActionBar().setDisplayShowTitleEnabled(true);
                            invalidateOptionsMenu();
                            currentFragment=LANGUAGE_FRAGMENT;
                        }else if(destination.getId()==R.id.nav_view){
                            actionBarLogo.setVisibility(View.GONE);
                            getSupportActionBar().setDisplayShowTitleEnabled(true);
                            invalidateOptionsMenu();
                        }
                    }

//                }
            });

        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
//        shimmerFrameLayout.startShimmer();
        if(shimmerFrameLayout!=null){
            shimmerFrameLayout.stopShimmer();
        }
        if(DBQueries.isNotificationClicked){
            Navigation.findNavController(HomeActivity.this, R.id.nav_host_fragment).navigate(R.id.nav_orders);

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);

    }
    @Override
    protected void onStop() {
        super.onStop();
        checkInternetConnection.removeConnectionChangeListener();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
//       Toast.makeText(HomeActivity.this ,String.valueOf(DBQueries.cartList.size()),Toast.LENGTH_SHORT).show();

        if(currentFragment == HOME_FRAGMENT){


            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getMenuInflater().inflate(R.menu.home, menu);
            MenuItem cartItem = menu.findItem(R.id.nav_cart);
//           MenuItem searchItem =  menu.findItem(R.id.search_icon);
//            if(DBQueries.cartList.size() > 0){


                cartItem.setActionView(R.layout.badge_layout);
                ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
                badgeIcon.setImageResource(R.mipmap.cart_white);
                badgeCount =cartItem.getActionView().findViewById(R.id.badge_count);
                if(CommonApiConstant.currentUser !=null){
                    if(DBQueries.cartList.size() == 0){
                        DBQueries.loadCartList(HomeActivity.this,new Dialog(HomeActivity.this), badgeCount,new TextView(HomeActivity.this));
                    }else{
                        badgeCount.setVisibility(View.VISIBLE);
                        if(DBQueries.cartList.size() < 99){
                            badgeCount.setText(String.valueOf(DBQueries.cartList.size()));
                        }else{
                            badgeCount.setText("99");
                        }
                    }
                }
            MenuItem notifyItem = menu.findItem(R.id.notificationActivity);
            notifyItem.setActionView(R.layout.badge_layout);
            ImageView notifyIcon = notifyItem.getActionView().findViewById(R.id.badge_icon);
            notifyIcon.setImageResource(R.mipmap.bell);
            TextView notifyCount =notifyItem.getActionView().findViewById(R.id.badge_count);

            if(CommonApiConstant.currentUser !=null){
                DBQueries.checkNotifications(HomeActivity.this,false,notifyCount);
            }
                cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(CommonApiConstant.currentUser == null){
                            signInDialog.show();
                        }else{
                            Intent cartIntent= new Intent(view.getContext(), CartActivity.class);
                            cartIntent.putExtra("totalCartAmount",totalCartAmount);
                            cartIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(cartIntent);
                        }
                    }
                });
            notifyItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(CommonApiConstant.currentUser == null){
                        signInDialog.show();
                    }else{
                        Intent notifyIntent= new Intent(view.getContext(), NotificationActivity.class);
                        notifyIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(notifyIntent);
                    }
                }
            });
//            }else{
//                     cartItem.setActionView(null);
//            }
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(CommonApiConstant.currentUser ==null){
            signInDialog.show();
        }else{
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                    || super.onSupportNavigateUp();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
//        int id=item.getItemId();
//        if(id==R.id.main_search_icon){
//           return true;
//        }else if(id== R.id.main_notification_icon){
//            return true;
//        }else if(id== R.id.nav_cart){
//            Toast.makeText(HomeActivity.this,"Fdfhkhsdf",Toast.LENGTH_SHORT).show();
//            if(DBQueries.currentUser == null){
//                signInDialog.show();
//            }else{
//                goToFragment("My Cart",new MyCartFragment(),CART_FRAGMENT);
//            }
//            return true;
//        }
//        else if(id == android.R.id.home){
//            if(showCart){
//                mainActivity=null;
//                showCart=false;
//                finish();
//                return true;
//            }
//        }
//        return super.onOptionsItemSelected(item);
    }

//    private void goToFragment(String title,Fragment fragment,int currentFragment) {
//        actionBarLogo.setVisibility(View.GONE);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        getSupportActionBar().setTitle(title);
//        invalidateOptionsMenu();
//        setFragment(fragment,currentFragment);
//        if(currentFragment == CART_FRAGMENT || showCart){
//        params.setScrollFlags(0);
//        navigationView.getMenu().getItem(3).setChecked(true);
//        }else{
//            params.setScrollFlags(scrollFlags);
//        }
//    }

//    private void setFragment(Fragment fragment,int fragmentNumber){
//
//        if(fragmentNumber != currentFragment){
//            currentFragment=fragmentNumber;
//            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//            fragmentTransaction.setCustomAnimations(R.anim.fade_in,R.anim.fade_out);
//            fragmentTransaction.replace(frameLayout.getId(),fragment);
//            fragmentTransaction.commit();
//        }
//    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer= (DrawerLayout) findViewById(R.id.drawer_layout);
//        if(drawer.isDrawerOpen(GravityCompat.START)){
//            drawer.closeDrawer(GravityCompat.START);
//        }else{
//            if(currentFragment == HOME_FRAGMENT){
//                currentFragment=-1;
//                super.onBackPressed();
//            }else{
//                if(showCart){
//                    mainActivity=null;
//                   showCart=false;
//                   finish();
//                }else{
//                actionBarLogo.setVisibility(View.VISIBLE);
////                invalidateOptionsMenu();
////                setFragment(new HomeFragment(),HOME_FRAGMENT);
//                navigationView.getMenu().getItem(0).setChecked(true);
//            }
//            }
//        }
//    }

//    MenuItem menuItem;

//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        menuItem=item;
//
//
//        if(DBQueries.currentUser !=null) {
//            drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
//                @Override
//                public void onDrawerClosed(View drawerView) {
//                    super.onDrawerClosed(drawerView);
//                    int id = menuItem.getItemId();
////                    if (id == R.id.nav_rewards) {
////
////                    } else
//                    if (id == R.id.nav_wishlist) {
//                           currentFragment=WISHLIST_FRAGMENT;
////                        goToFragment("My Wishlist", new MyWishlistFragment(), WISHLIST_FRAGMENT);
////                        Navigation.findNavController(HomeActivity.this, R.id.nav_host_fragment).navigate(R.id.wishlist);
//                    } else if (id == R.id.nav_orders) {
//                          currentFragment=ORDERS_FRAGMENT;
////                        goToFragment("My orders", new MyOrdersFragment(), ORDERS_FRAGMENT);
//                    } else if (id == R.id.nav_account) {
//                        currentFragment=-1;
////                        goToFragment("My Account", new MyAccountFragment(), ACCOUNT_FRAGMENT);
//                    } else if (id == R.id.nav_cart) {
////                        goToFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);
//                    } else if (id == R.id.nav_myMall) {
//                        currentFragment=HOME_FRAGMENT;
//                        actionBarLogo.setVisibility(View.VISIBLE);
//                        invalidateOptionsMenu();
////                        setFragment(new HomeFragment(), HOME_FRAGMENT);
//
//                    }else if(id == R.id.nav_sign_out){
//                        DBQueries.clearData();
//                        FirebaseAuth.getInstance().signOut();
//                        DBQueries.currentUser=null;
//                        Intent registerIntent = new Intent(HomeActivity.this,RegisterActivity.class);
//                        startActivity(registerIntent);
//                        finish();
//                    }
//                }
//            });
//            return true;
//        }else{
//            signInDialog.show();
//            return false;
//        }
//    }



    @Override
    protected void onPause() {
        super.onPause();
         if(CommonApiConstant.currentUser!=null){
             DBQueries.checkNotifications(HomeActivity.this,true,null);
         }
         shimmerFrameLayout.stopShimmer();
         shimmerFrameLayout.setVisibility(View.GONE);

    }

    @Override
    protected void onStart() {
        super.onStart();
//        if(currentUser ==null){
//            navigationView.getMenu().getItem(navigationView.getMenu().size() -1).setEnabled(false);
//        }else{
//            navigationView.getMenu().getItem(navigationView.getMenu().size()-1).setEnabled(true);
//        }
//        if(currentUser !=null){
//            if(DBQueries.cartList.size() == 0){
//                DBQueries.loadCartList(HomeActivity.this,new Dialog(HomeActivity.this), badgeCount);
//            }
//        }
        if(CommonApiConstant.currentUser !=null){
            String URL= CommonApiConstant.backEndUrl+"/user/user-details/"+CommonApiConstant.currentUser;

            VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.GET, URL, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {

                    String resultResponse = new String(response.data);

                    try {

                        JSONObject result = new JSONObject(resultResponse);

                        if(result.getJSONObject("result").getString("complete_name").equals("null")){
                            DBQueries.fullname="";
                        }else{
                            DBQueries.fullname= result.getJSONObject("result").getString("complete_name");
                        }
                        if(result.getJSONObject("result").getString("user_name").equals("null")){
                            DBQueries.email="";
                        }else{
                            DBQueries.email= result.getJSONObject("result").getString("user_name");
                        }
                        if(result.getJSONObject("result").getString("profile_pic").equals("null")){
                            DBQueries.profile="";
                        }else{
                            DBQueries.profile= result.getJSONObject("result").getString("profile_pic");
                        }
                        fullname.setText(DBQueries.fullname);
                        email.setText(DBQueries.email);
                        if(DBQueries.profile.equals("")){
                            addProfileIcon.setVisibility(View.VISIBLE);

                        }else{
                            addProfileIcon.setVisibility(View.INVISIBLE);
                            Glide.with(HomeActivity.this).load(DBQueries.profile).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).apply(new RequestOptions().placeholder(R.mipmap.profile_placeholder)).into(profileView);

                        }
                        addProfileIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                NavController navController = Navigation.findNavController(HomeActivity.this, R.id.nav_host_fragment);
                                 navController.navigate(R.id.nav_account);
                            }
                        });






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
                    Toast.makeText(HomeActivity.this,errorMessage,Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {

                    Map<String,String>params= new HashMap<>();

                    return params;

                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization","Bearer "+ AppPref.getUserToken(HomeActivity.this));
                    return headers;
                }
            };
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest1);
//            FirebaseFirestore.getInstance().collection("USERS")
//                    .document(currentUser.getUid())
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            if(task.isSuccessful()){
//                                DBQueries.fullname= task.getResult().getString("fullname");
//                                DBQueries.email= task.getResult().getString("email");
//                                DBQueries.profile= task.getResult().getString("profile");
//                                fullname.setText(DBQueries.fullname);
//                                email.setText(DBQueries.email);
//                                if(DBQueries.profile.equals("")){
//                                    addProfileIcon.setVisibility(View.VISIBLE);
//
//                                }else{
//                                    addProfileIcon.setVisibility(View.INVISIBLE);
//                                    Glide.with(HomeActivity.this).load(DBQueries.profile).apply(new RequestOptions().placeholder(R.mipmap.profile_placeholder)).into(profileView);
//
//                                }
//                            }else{
//                                Toast.makeText(HomeActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
        }

        invalidateOptionsMenu();
    }


}
