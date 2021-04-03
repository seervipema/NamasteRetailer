package com.pemaseervi.myretailer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

public class CartActivity extends AppCompatActivity {
    public static RecyclerView cartItemsRecycleView;
    private Button continueBtn;
    public static Dialog loadingDialog;
    public static CartAdapter cartAdapter;
    public static TextView totalAmount;
    public  static ConstraintLayout emptyCartLayout;
    public static LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        cartItemsRecycleView = findViewById(R.id.cart_items_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        cartItemsRecycleView.setLayoutManager(linearLayoutManager);
        Toolbar toolbar = findViewById(R.id.toolbar);
        emptyCartLayout= findViewById(R.id.empty_cart_layout);
        linearLayout=findViewById(R.id.linearLayout5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.my_cart);
        continueBtn=findViewById(R.id.delivery_continue_btn);
        totalAmount= findViewById(R.id.total_delivery_amount);
        loadingDialog= new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        cartAdapter = new CartAdapter(cartItemModelList,totalAmount,true,false);
        MyCartFragment.cartAdapter=cartAdapter;
        cartItemsRecycleView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();
        CategoryWiseAllProductsActivity.totalCartItems = 0;
        CategoryWiseAllProductsActivity.totalCartAmount = 0;
        int sz=(int)DBQueries.cartItemModelList.size();
        if(sz>0){
            for(int x=0;x<sz;x++){
                CategoryWiseAllProductsActivity.totalCartItems +=DBQueries.cartItemModelList.get(x).getProductQuantityForOrder();
                CategoryWiseAllProductsActivity.totalCartAmount+= (DBQueries.cartItemModelList.get(x).getProductQuantityForOrder() * Integer.parseInt(DBQueries.cartItemModelList.get(x).getSellingPrice().toString() ));
            }
        }
        if(sz==0){
            cartItemsRecycleView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            emptyCartLayout.setVisibility(View.VISIBLE);
        }
        totalAmount.setText(Utility.changeToIndianCurrency(CategoryWiseAllProductsActivity.totalCartAmount));
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeliveryActivity.cartItemModelList= new ArrayList<>();
                DeliveryActivity.fromCart=true;
                for(int x = 0; x<(long) DBQueries.cartItemModelList.size(); x++){
                    CartItemModel cartItemModel = DBQueries.cartItemModelList.get(x);
//                    if(cartItemModel.getInStock()){
                        DeliveryActivity.cartItemModelList.add(cartItemModel);
//                    }
                }
                DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                loadingDialog.show();
                if(DBQueries.addressModelList.size()==0){
                    DBQueries.loadAddresses(CartActivity.this,loadingDialog,true);
                }else{
                    loadingDialog.dismiss();
                    Intent deliveryIntent = new Intent(CartActivity.this,DeliveryActivity.class);
                    deliveryIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(deliveryIntent);
                }
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        if(cartItemModelList.size() ==0){
            DBQueries.cartList.clear();
            DBQueries.loadCartList(this, loadingDialog ,new TextView(this),new TextView(this));
        }else{
//            if(cartItemModelList.get(DBQueries.cartItemModelList.size()-1).getType() == CartItemModel.TOTAL_AMOUNT){
                LinearLayout parent =(LinearLayout) totalAmount.getParent().getParent();
                parent.setVisibility(View.VISIBLE);
           // }
            loadingDialog.dismiss();
        }
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
            //todo:search icon
            Intent searchIntent= new Intent(CartActivity.this, SearchActivity.class);
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
        if(DBQueries.cartItemModelList.size()==0){
            cartItemsRecycleView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            emptyCartLayout.setVisibility(View.VISIBLE);
        }
    }
}
