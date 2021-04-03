package com.pemaseervi.myretailer;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.utility.AppHelper;
import com.pemaseervi.myretailer.utility.AppPref;
import com.pemaseervi.myretailer.utility.CommonApiConstant;
import com.pemaseervi.myretailer.utility.FileUtils;
import com.pemaseervi.myretailer.utility.ImageCompression;
import com.pemaseervi.myretailer.utility.SharedPreferenceHelper;
import com.pemaseervi.myretailer.utility.Utility;
import com.pemaseervi.myretailer.utility.VolleyMultipartRequest;
import com.pemaseervi.myretailer.utility.VolleySingleton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {


    public MyAccountFragment() {
        // Required empty public constructor
    }
    public static final int MANAGE_ADDRESS=1;
    private Button viewAllAddressBtn,signOutBtn;
    private CircleImageView profileView,currentOrderImage;
    private TextView name,email,tvCurrentOrderStatus;
    private LinearLayout layoutContainer,recentOrderContainer;
    private Dialog loadingDialog;
    private ImageView orderedIndicator,packedIndicator,shippedIndicator,delieveredIndicator;
    private ProgressBar o_p_progress,p_s_progress,s_d_progress;
    private TextView yourRecentOrderStatus;
    private TextView addressname,address,pincode;
     private FloatingActionButton floatingActionButton;
    private Uri filePath;
    private Bitmap bitmap;
    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_account, container, false);
        viewAllAddressBtn=view.findViewById(R.id.view_all_addresses_btn);
        signOutBtn=view.findViewById(R.id.sign_out_btn);
        loadingDialog= new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        profileView = view.findViewById(R.id.profile_image);
        name = view.findViewById(R.id.username);
        email = view.findViewById(R.id.user_email);
        floatingActionButton=view.findViewById(R.id.setting_btn);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

//        currentOrderImage = view.findViewById(R.id.current_order_image);
//        tvCurrentOrderStatus = view.findViewById(R.id.current_order_status);
//        orderedIndicator = view.findViewById(R.id.ordered_indicator);
//        packedIndicator = view.findViewById(R.id.packed_indicator);
//        shippedIndicator = view.findViewById(R.id.shipped_indicator);
//        delieveredIndicator = view.findViewById(R.id.delivered_indicator);
//        o_p_progress = view.findViewById(R.id.order_packed_progress);
//        p_s_progress = view.findViewById(R.id.packed_shipped_progress);
//        s_d_progress= view.findViewById(R.id.shipped_delivered_progress);
//        yourRecentOrderStatus = view.findViewById(R.id.your_recent_order_title);
//        recentOrderContainer = view.findViewById(R.id.recent_orders_container);
        addressname = view.findViewById(R.id.address_fullname);
        address = view.findViewById(R.id.address);
        pincode = view.findViewById(R.id.address_pincode);



//        layoutContainer = view.findViewById(R.id.layout_container);
//        layoutContainer.getChildAt(1).setVisibility(View.GONE);
//        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialogInterface) {
//                for(MyOrderItemModel orderItemModel:DBQueries.myOrderItemModelList){
//                    if(!orderItemModel.getOrderStatus().equals("Delivered") && !orderItemModel.getOrderStatus().equals("Cancelled")){
//                        layoutContainer.getChildAt(1).setVisibility(View.VISIBLE);
//                        Glide.with(getContext()).load(orderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.mipmap.category_placeholder)).into(currentOrderImage);
//                        tvCurrentOrderStatus.setText(orderItemModel.getOrderStatus());
//
//                        switch (orderItemModel.getOrderStatus()){
//                            case "Ordered":
//                                orderedIndicator.setImageTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.successGreen)));
//                                break;
//                            case "Packed":
//                                orderedIndicator.setImageTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.successGreen)));
//                                packedIndicator.setImageTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.successGreen)));
//
//                                o_p_progress.setProgressTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.successGreen)));
//
//
//
//                                break;
//                            case "Shipped":
//                                orderedIndicator.setImageTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.successGreen)));
//                                packedIndicator.setImageTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.successGreen)));
//                                shippedIndicator.setImageTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.successGreen)));
//
//                                o_p_progress.setProgressTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.successGreen)));
//                                p_s_progress.setProgressTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.successGreen)));
//
//
//
//                                break;
//                            case "Out for delivery":
//                                orderedIndicator.setImageTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.successGreen)));
//                                packedIndicator.setImageTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.successGreen)));
//                                shippedIndicator.setImageTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.successGreen)));
//                                delieveredIndicator.setImageTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.successGreen)));
//                                o_p_progress.setProgressTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.successGreen)));
//                                p_s_progress.setProgressTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.successGreen)));
//                                s_d_progress.setProgressTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.successGreen)));
//
//                                break;
//                            default:
////                date=myOrderItemModelList.get(position).getCancelledDate();
//                                break;
//
//                        }
//                    }
//                }
//                int i=0;
//                for(MyOrderItemModel myOrderItemModel: myOrderItemModelList){
//                    if(i<4){
//                        if(myOrderItemModel.getOrderStatus().equals("Delivered")){
//                            Glide.with(getContext()).load(myOrderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.mipmap.category_placeholder)).into((CircleImageView)recentOrderContainer.getChildAt(i));
//                            i++;
//                        }
//
//                    }else{
//                        break;
//
//                    }
//                }
//                if(i==0){
//                    yourRecentOrderStatus.setText(R.string.no_recent_orders_msg);
//                }
//                if(i<3){
//                    for(int x=i;x<4;x++){
//                        recentOrderContainer.getChildAt(x).setVisibility(View.GONE);
//                    }
//                }
//                loadingDialog.show();
//                loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialogInterface) {
//                        loadingDialog.setOnDismissListener(null);
//                        if(DBQueries.addressModelList.size() ==0){
//                            addressname.setText(R.string.no_address_found_msg);
//                            address.setText("-");
//                            pincode.setText("-");
//                        }else{
//                            String nameText,mobileNumber;
//                            nameText=DBQueries.addressModelList.get(DBQueries.selectedAddress).getName();
//                            mobileNumber=DBQueries.addressModelList.get(DBQueries.selectedAddress).getMobileNumber();
//                            if(DBQueries.addressModelList.get(DBQueries.selectedAddress).getAlternateMobileNumber().equals("")){
//                                addressname.setText(nameText+" - "+mobileNumber);
//                            }else{
//                                addressname.setText(nameText+" - "+mobileNumber + " or "+DBQueries.addressModelList.get(DBQueries.selectedAddress).getAlternateMobileNumber());
//                            }
//                           String flatNumber= DBQueries.addressModelList.get(DBQueries.selectedAddress).getFlatNumber();
//                           String locality= DBQueries.addressModelList.get(DBQueries.selectedAddress).getLocality();
//                           String landmark= DBQueries.addressModelList.get(DBQueries.selectedAddress).getLandmark();
//                           String  city= DBQueries.addressModelList.get(DBQueries.selectedAddress).getCity();
//                           String  state= DBQueries.addressModelList.get(DBQueries.selectedAddress).getState();
//                            if(landmark.equals("")){
//                                address.setText(flatNumber+", "+locality+", "+city+", "+state);
//                            }else{
//                                address.setText(flatNumber+", "+locality+", "+landmark+", "+city+", "+state);
//                            }
//                            pincode.setText(DBQueries.addressModelList.get(DBQueries.selectedAddress).getPincode());
//                            }
//
//
//                    }
//                });
//                DBQueries.loadAddresses(getContext(),loadingDialog,false);
//                return;
//
//
//            }
//        });
//        if(DBQueries.currentUser !=null){
//          DBQueries.loadOrders(getContext(),null,loadingDialog,DBQueries.ordersItemModelList.get(0).getOrderId());
//
//        }
                loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        loadingDialog.setOnDismissListener(null);
                        if(DBQueries.addressModelList.size() ==0){
                            addressname.setText(R.string.no_address_found_msg);
                            address.setText("-");
                            pincode.setText("-");
                        }else{
                            String nameText,mobileNumber;
                            nameText=DBQueries.addressModelList.get(DBQueries.selectedAddress).getName();
                            mobileNumber=DBQueries.addressModelList.get(DBQueries.selectedAddress).getMobileNumber();
                            if(DBQueries.addressModelList.get(DBQueries.selectedAddress).getAlternateMobileNumber().equals("")){
                                addressname.setText(nameText+" - "+mobileNumber);
                            }else{
                                addressname.setText(nameText+" - "+mobileNumber + " or "+DBQueries.addressModelList.get(DBQueries.selectedAddress).getAlternateMobileNumber());
                            }
                           String flatNumber= DBQueries.addressModelList.get(DBQueries.selectedAddress).getFlatNumber();
                           String locality= DBQueries.addressModelList.get(DBQueries.selectedAddress).getLocality();
                           String landmark= DBQueries.addressModelList.get(DBQueries.selectedAddress).getLandmark();
                           String  city= DBQueries.addressModelList.get(DBQueries.selectedAddress).getCity();
                           String  state= DBQueries.addressModelList.get(DBQueries.selectedAddress).getState();
                            if(landmark.equals("")){
                                address.setText(flatNumber+", "+locality+", "+city+", "+state);
                            }else{
                                address.setText(flatNumber+", "+locality+", "+landmark+", "+city+", "+state);
                            }
                            pincode.setText(DBQueries.addressModelList.get(DBQueries.selectedAddress).getPincode());
                            }


                    }
                });
                DBQueries.loadAddresses(getContext(),loadingDialog,false);

        name.setText(DBQueries.fullname);
        email.setText(DBQueries.email);
        if(DBQueries.profile!=null){
            if(!DBQueries.profile.equals("")){
                Glide.with(getContext()).load(DBQueries.profile).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).apply(new RequestOptions().placeholder(R.mipmap.profile_placeholder)).into(profileView);

            }
        }
        viewAllAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myAddressesActivity = new Intent(getContext(),MyAddressesActivity.class);
                myAddressesActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(myAddressesActivity);
                myAddressesActivity.putExtra("MODE",MANAGE_ADDRESS);
            }
        });
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        DBQueries.clearData();
                        CommonApiConstant.currentUser =null;
                        SharedPreferenceHelper helper = SharedPreferenceHelper.getInstance(getActivity());
                        helper.clearAllSharedPreferences(getActivity(), AppPref.APP_PREF);
                        Intent registerIntent = new Intent(getContext(),PhoneNumberActivity.class);
                        registerIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(registerIntent);
                        getActivity().finish();

            }
        });
        return view;
    }
    private void selectImage() {
        boolean result = Utility.checkPermission(getView().getContext());
        boolean result1= Utility.checkWritePermission(getView().getContext());
        if(result && result1){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }else{
            Toast.makeText(getView().getContext(),"Please give permission to read external storage",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(getView().getContext(), "Access Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case Utility.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(getView().getContext(), "Access Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode ==RESULT_OK && data!=null && data.getData()!=null){
//            filePath =data.getData();
            FileUtils fileUtils= new FileUtils(getView().getContext());
            ImageCompression imageCompression= new ImageCompression(getView().getContext());
            filePath= Uri.fromFile(new File(ImageCompression.compressImage(FileUtils.getPath(data.getData()))));
              try {
                    bitmap = MediaStore.Images.Media.getBitmap(getView().getContext().getContentResolver(),filePath);
                    profileView.setImageBitmap(bitmap);

                    Map<String,String>userData= new HashMap<>();
                    userData.put("user_id",CommonApiConstant.currentUser);


                    String URL= CommonApiConstant.backEndUrl+"/user/profile-pic";

                    VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.PUT, URL, new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {

                            String resultResponse = new String(response.data);
                            try {
                                JSONObject result = new JSONObject(resultResponse);
                                String message = result.getString("message");
                                if(HomeActivity.profileView!=null){
                                    Glide.with(getView().getContext()).load(DBQueries.profile).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).apply(new RequestOptions().placeholder(R.mipmap.profile_placeholder)).into(HomeActivity.profileView);
                                }
                                Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
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
                                        DBQueries.signOut(getActivity().getBaseContext());
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
                            Toast.makeText(getActivity(),errorMessage,Toast.LENGTH_LONG).show();
                        }
                    }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Authorization","Bearer "+ AppPref.getUserToken(getActivity()));
                            return headers;
                        }
                        @Override
                        protected Map<String, String> getParams() {
                            return userData;
                        }
                        @Override
                        protected Map<String, DataPart> getByteData() {
                            Map<String, DataPart> params = new HashMap<>();
                            // file name could found file base or direct access from real path
                            // for now just get bitmap data from ImageView
                            params.put("profile_pic", new DataPart(CommonApiConstant.currentUser+".jpg", AppHelper.getFileDataFromDrawable(getActivity().getBaseContext(), profileView.getDrawable()), "image/jpeg"));
//                        params.put("cover", new DataPart("file_cover.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mCoverImage.getDrawable()), "image/jpeg"));

                            return params;
                        }
                    };
                    VolleySingleton.getInstance(getActivity()).addToRequestQueue(multipartRequest1);


                }catch (IOException e){
                    e.printStackTrace();
                }


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(DBQueries.addressModelList.size()!=0){
            String nameText,mobileNumber;
            nameText=DBQueries.addressModelList.get(DBQueries.selectedAddress).getName();
            mobileNumber=DBQueries.addressModelList.get(DBQueries.selectedAddress).getMobileNumber();
            if(DBQueries.addressModelList.get(DBQueries.selectedAddress).getAlternateMobileNumber().equals("")){
                addressname.setText(nameText+" - "+mobileNumber);
            }else{
                addressname.setText(nameText+" - "+mobileNumber + " or "+DBQueries.addressModelList.get(DBQueries.selectedAddress).getAlternateMobileNumber());
            }
            String flatNumber= DBQueries.addressModelList.get(DBQueries.selectedAddress).getFlatNumber();
            String locality= DBQueries.addressModelList.get(DBQueries.selectedAddress).getLocality();
            String landmark= DBQueries.addressModelList.get(DBQueries.selectedAddress).getLandmark();
            String  city= DBQueries.addressModelList.get(DBQueries.selectedAddress).getCity();
            String  state= DBQueries.addressModelList.get(DBQueries.selectedAddress).getState();
            if(landmark.equals("")){
                address.setText(flatNumber+", "+locality+", "+city+", "+state);
            }else{
                address.setText(flatNumber+", "+locality+", "+landmark+", "+city+", "+state);
            }
            pincode.setText(DBQueries.addressModelList.get(DBQueries.selectedAddress).getPincode());
        }else{
            addressname.setText(R.string.no_address_found_msg);
            address.setText("-");
            pincode.setText("-");
        }

    }
}
