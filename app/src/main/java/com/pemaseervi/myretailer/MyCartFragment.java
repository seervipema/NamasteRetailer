package com.pemaseervi.myretailer;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pemaseervi.myretailer.Adapter.CartAdapter;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.Model.CartItemModel;
import com.pemaseervi.myretailer.utility.Utility;

import java.util.ArrayList;

import static com.pemaseervi.myretailer.Firebase.DBQueries.cartItemModelList;
import static com.pemaseervi.myretailer.Firebase.DBQueries.loadCartList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyCartFragment extends Fragment {


    public MyCartFragment() {
        // Required empty public constructor
    }

    private RecyclerView cartItemsRecycleView;
    private Button continueBtn;
    private Dialog loadingDialog;
    public static CartAdapter cartAdapter;
    public static TextView totalAmount;
    public static int totalCartItems=0;
    public static int totalCartAmount=0;
    public static ConstraintLayout emptyCartLayout;
    private LinearLayout linearLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_cart, container, false);
        cartItemsRecycleView = view.findViewById(R.id.cart_items_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cartItemsRecycleView.setLayoutManager(linearLayoutManager);
        continueBtn=view.findViewById(R.id.delivery_continue_btn);
        totalAmount= view.findViewById(R.id.total_delivery_amount);
        emptyCartLayout= view.findViewById(R.id.empty_cart_layout);
        linearLayout=view.findViewById(R.id.linearLayout5);
        loadingDialog= new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
//        Toast.makeText(getContext() ,String.valueOf(cartItemModelList.size()),Toast.LENGTH_SHORT).show();
//        List<CartItemModel> cartItemModelList= new ArrayList<>();
//        cartItemModelList.add(new CartItemModel(0,"Pixel 2","Rs.4999/-","Rs.5999/-",R.drawable.forgot_password_image,2,1,0,0));
//        cartItemModelList.add(new CartItemModel(0,"Pixel 2","Rs.4999/-","Rs.5999/-",R.drawable.forgot_password_image,3,1,2,0));
//        cartItemModelList.add(new CartItemModel(0,"Pixel 2","Rs.4999/-","Rs.5999/-",R.drawable.forgot_password_image,4,1,2,0));
//        cartItemModelList.add(new CartItemModel(1,"Price (3 items)","Rs.16999/-","Free","Rs.18999/-","Rs.40000/-"));
//         if(cartItemModelList.size()>0){
//             cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
//         }

         cartAdapter = new CartAdapter(cartItemModelList,totalAmount,true,false);
        cartItemsRecycleView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();
        int sz=(int)DBQueries.cartItemModelList.size();
        if(sz==0){
            cartItemsRecycleView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            emptyCartLayout.setVisibility(View.VISIBLE);
        }
        if(sz>0){
            totalCartAmount=0;
            for(int x=0;x<sz;x++){
//                totalCartItems=totalCartItems + DBQueries.cartItemModelList.get(x).getProductQuantityForOrder();
                totalCartAmount= totalCartAmount + (DBQueries.cartItemModelList.get(x).getProductQuantityForOrder() * Integer.parseInt(DBQueries.cartItemModelList.get(x).getSellingPrice()));
            }
        }
        totalAmount.setText(Utility.changeToIndianCurrency(totalCartAmount));
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeliveryActivity.cartItemModelList= new ArrayList<>();
                DeliveryActivity.fromCart=true;
                for(int x=0;x<(long) DBQueries.cartItemModelList.size();x++){
                    CartItemModel cartItemModel = DBQueries.cartItemModelList.get(x);

                        DeliveryActivity.cartItemModelList.add(cartItemModel);

                }
                DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                loadingDialog.show();
                if(DBQueries.addressModelList.size()==0){
                    DBQueries.loadAddresses(getContext(),loadingDialog,true);
                }else{
                    loadingDialog.dismiss();
                    Intent deliveryIntent = new Intent(getContext(),DeliveryActivity.class);
                    startActivity(deliveryIntent);
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(cartItemModelList.size() ==0){
            DBQueries.cartList.clear();
            loadCartList(getContext(), loadingDialog, new TextView(getContext()),totalAmount);
        }else{
             if(cartItemModelList.size() > 1){

                 LinearLayout parent =(LinearLayout) totalAmount.getParent().getParent();
                 parent.setVisibility(View.VISIBLE);
             }
            loadingDialog.dismiss();
        }
    }
}
