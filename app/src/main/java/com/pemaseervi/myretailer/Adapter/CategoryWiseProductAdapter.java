package com.pemaseervi.myretailer.Adapter;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pemaseervi.myretailer.CategoryWiseAllProductsActivity;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.Model.CategoryWiseProductModel;
import com.pemaseervi.myretailer.ProductDetailsActivity;
import com.pemaseervi.myretailer.R;
import com.pemaseervi.myretailer.utility.CommonApiConstant;
import com.pemaseervi.myretailer.utility.Utility;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static com.pemaseervi.myretailer.CategoryWiseAllProductsActivity.totalCartAmount;
import static com.pemaseervi.myretailer.CategoryWiseAllProductsActivity.totalCartItems;
import static com.pemaseervi.myretailer.CategoryWiseAllProductsActivity.total_added_products_amount_in_cart;
import static com.pemaseervi.myretailer.CategoryWiseAllProductsActivity.total_added_products_in_cart;
import static com.pemaseervi.myretailer.CategoryWiseAllProductsActivity.viewCartLinearLayout;

public class CategoryWiseProductAdapter extends BaseAdapter {
   public static List<CategoryWiseProductModel> categoryWiseProductModelList;

    public CategoryWiseProductAdapter(List<CategoryWiseProductModel> categoryWiseProductModelList) {
        this.categoryWiseProductModelList = categoryWiseProductModelList;
    }

    @Override
    public int getCount() {
        return categoryWiseProductModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView,final ViewGroup viewGroup) {
        View view;
//        if(convertView==null){
            // Log.d("i",String.valueOf(position));
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.horizontal_scroll_item_layout,viewGroup,false);

//        }else{
//            view=convertView;
//        }
            view.setElevation(0);
            view.setBackgroundColor(Color.parseColor("#ffffff"));


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent productDetailsIntent=new Intent(viewGroup.getContext(), ProductDetailsActivity.class);
                    productDetailsIntent.putExtra("product_id",categoryWiseProductModelList.get(position).getProductTitle());
                    productDetailsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                    viewGroup.getContext().startActivity(productDetailsIntent);
                }
            });
            final ImageView productImage = view.findViewById(R.id.horizontal_scroll_product_image);
            final TextView productTitle = view.findViewById(R.id.horizontal_scroll_product_title);
            final TextView productQuantityForCart=view.findViewById(R.id.ordered_quantity);
            final TextView productPrice = view.findViewById(R.id.horizontal_scroll_price);
            final ImageView incrementProductQuantity=view.findViewById(R.id.increment_product_quantity);
            final ImageView decrementProductQuantity=view.findViewById(R.id.decrement_product_quantity);
            FloatingActionButton addToWishlist=view.findViewById(R.id.hs_add_to_wishlist_btn);
            LinearLayout incrementDecrementProductLayout=view.findViewById(R.id.increment_decrement_product_layout);
            incrementDecrementProductLayout.setVisibility(View.VISIBLE);
            final TextView addToCartCategoryBtn=view.findViewById(R.id.add_to_cart_category_btn);
          //  final LinearLayout viewCart = viewCartLinearLayout.findViewById(R.id.view_cart);
//            final TextView total_added_products_in_cart=  viewCartLinearLayout.findViewById(R.id.total_added_products_in_cart);
//            final TextView total_added_products_amount_in_cart=viewCartLinearLayout.findViewById(R.id.total_added_products_amount_in_cart);
            TextView cuttedPrice=view.findViewById(R.id.hsl_cutted_price);
            TextView tvDiscount=view.findViewById(R.id.hsl_discount);
//            if(DBQueries.cartList.size()>0){
//                CategoryWiseAllProductsActivity.viewCart.setVisibility(View.VISIBLE);
//                total_added_products_in_cart.setText(String.valueOf(totalCartItems)+" "+view.getContext().getResources().getString(R.string.item_count));
//                total_added_products_amount_in_cart.setText(Utility.changeToIndianCurrency(totalCartAmount)+" "+view.getContext().getResources().getString(R.string.plus_taxes));
//            }
//            if(convertView==null){


//            }
            incrementProductQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    incrementProductQuantity.setEnabled(false);
                    int x= Integer.parseInt(productQuantityForCart.getText().toString());
                    x++;
                    productQuantityForCart.setText(String.valueOf(x));
                    int sz=DBQueries.cartItemModelList.size();
                    for(int count=0;count<sz;count++){
                        if(DBQueries.cartItemModelList.get(count).getProductTitle().equals(categoryWiseProductModelList.get(position).getProductTitle())){
                            DBQueries.cartItemModelList.get(count).setProductQuantityForOrder(x);
                        }
                    }
                    totalCartItems++;
                    totalCartAmount = totalCartAmount + (long)((Integer.parseInt(categoryWiseProductModelList.get(position).getSellingPrice())));
                    total_added_products_in_cart.setText(String.valueOf(totalCartItems)+" "+view.getContext().getResources().getString(R.string.item_count));
                    total_added_products_amount_in_cart.setText(Utility.changeToIndianCurrency(totalCartAmount) +" "+view.getContext().getResources().getString(R.string.plus_taxes));
//                    incrementProductQuantity.setEnabled(true);
                }
            });
            decrementProductQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    decrementProductQuantity.setEnabled(false);
                    int x= Integer.parseInt(productQuantityForCart.getText().toString());
                    if(x>1){
                        x--;
                        if(totalCartItems >0){
                            totalCartItems--;
                        }
                        if(totalCartAmount >0){
                            totalCartAmount = totalCartAmount - (long)((Integer.parseInt(categoryWiseProductModelList.get(position).getSellingPrice())));
                        }
                    }
                    productQuantityForCart.setText(String.valueOf(x));
                    int sz=DBQueries.cartItemModelList.size();
                    for(int count=0;count<sz;count++){
                        if(DBQueries.cartItemModelList.get(count).getProductTitle().equals(categoryWiseProductModelList.get(position).getProductTitle())){
                            DBQueries.cartItemModelList.get(count).setProductQuantityForOrder(x);
                        }
                    }
                    total_added_products_in_cart.setText(String.valueOf(totalCartItems)+" "+view.getContext().getResources().getString(R.string.item_count));
                    total_added_products_amount_in_cart.setText(Utility.changeToIndianCurrency(totalCartAmount)+" "+view.getContext().getResources().getString(R.string.plus_taxes));
//                    decrementProductQuantity.setEnabled(true);
                }
            });
            addToCartCategoryBtn.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         if(CommonApiConstant.currentUser !=null){
                             DBQueries.addToCart(view.getContext(),productTitle.getText().toString(),
                                     categoryWiseProductModelList.get(position).getProductTitle(),
                                     categoryWiseProductModelList.get(position).getProductHindiTitle(),
                                     String.valueOf(categoryWiseProductModelList.get(position).getProductPrice()),
                                     categoryWiseProductModelList.get(position).getSellingPrice(),
                                     categoryWiseProductModelList.get(position).getProductImageResource(),
                                    1,
                                     CategoryWiseAllProductsActivity.loadingDialog,
                                     categoryWiseProductModelList.get(position).getProduct_catalog(),
                                     categoryWiseProductModelList.get(position).getProduct_category()
                             );
                             incrementProductQuantity.setVisibility(View.VISIBLE);
                             decrementProductQuantity.setVisibility(View.VISIBLE);
                             productQuantityForCart.setVisibility(View.VISIBLE);
                             addToCartCategoryBtn.setVisibility(View.GONE);
                             viewCartLinearLayout.setVisibility(View.VISIBLE);
                             productQuantityForCart.setText("1");
                             totalCartItems=totalCartItems+(long)Integer.parseInt("1");
                             totalCartAmount = totalCartAmount + (long)((Integer.parseInt(categoryWiseProductModelList.get(position).getSellingPrice()))*Integer.parseInt("1"));
                             total_added_products_in_cart.setText(String.valueOf(totalCartItems)+" "+ view.getContext().getResources().getString(R.string.item_count));
                             total_added_products_amount_in_cart.setText(Utility.changeToIndianCurrency(totalCartAmount)+" "+view.getContext().getResources().getString(R.string.plus_taxes));

//                             if(DBQueries.cartList.contains(categoryWiseProductModelList.get(position).getProductTitle().trim())){
//                                 Toast.makeText(view.getContext(),R.string.already_added_to_cart_msg,Toast.LENGTH_SHORT).show();
//                                 incrementProductQuantity.setVisibility(View.VISIBLE);
//                                 decrementProductQuantity.setVisibility(View.VISIBLE);
//                                 productQuantityForCart.setVisibility(View.VISIBLE);
//                                 addToCartCategoryBtn.setVisibility(View.GONE);
//                                 viewCartLinearLayout.setVisibility(View.VISIBLE);
//                                 int sz=DBQueries.cartItemModelList.size();
//                                 for(int i=0;i<sz;i++){
//                                     if(DBQueries.cartItemModelList.get(i).getProductTitle().trim().equals(categoryWiseProductModelList.get(position).getProductTitle().trim())){
//                                         productQuantityForCart.setText(String.valueOf(DBQueries.cartItemModelList.get(i).getProductQuantityForOrder()));
//                                     }
//                                 }
//                             }else{
//                                 addToCartCategoryBtn.setEnabled(false);
//                                 final Dialog quantityDialog=new Dialog(viewGroup.getContext());
//                                 quantityDialog.setContentView(R.layout.quantity_dialog);
//                                 quantityDialog.setCancelable(false);
//                                 quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//                                 final EditText quantityNo= quantityDialog.findViewById(R.id.quantity_no);
//                                 Button cancelBtn=quantityDialog.findViewById(R.id.cancel_btn),Okbtn=quantityDialog.findViewById(R.id.ok_btn);
//                                 cancelBtn.setOnClickListener(new View.OnClickListener() {
//                                     @Override
//                                     public void onClick(View view) {
//                                         quantityDialog.dismiss();
//                                     }
//                                 });
//                                 Okbtn.setOnClickListener(new View.OnClickListener() {
//                                     @Override
//                                     public void onClick(View view) {
////                                 DBQueries.cartItemModelList.get(index).setProductQuantity(Long.valueOf(pro))
//                                         //  productQuantity.setText("Qty: "+ quantityNo.getText());
//
//
////                                     else{
//
//                                         DBQueries.addToCart(view.getContext(),productTitle.getText().toString(),
//                                                 categoryWiseProductModelList.get(position).getProductTitle(),
//                                                 categoryWiseProductModelList.get(position).getProductHindiTitle(),
//                                                 String.valueOf(categoryWiseProductModelList.get(position).getProductPrice()),
//                                                 categoryWiseProductModelList.get(position).getSellingPrice(),
//                                                 categoryWiseProductModelList.get(position).getProductImageResource(),
//                                                 Integer.parseInt(quantityNo.getText().toString()),
//                                                 CategoryWiseAllProductsActivity.loadingDialog,
//                                                 categoryWiseProductModelList.get(position).getProduct_catalog(),
//                                                 categoryWiseProductModelList.get(position).getProduct_category()
//                                         );
//                                         incrementProductQuantity.setVisibility(View.VISIBLE);
//                                         decrementProductQuantity.setVisibility(View.VISIBLE);
//                                         productQuantityForCart.setVisibility(View.VISIBLE);
//                                         addToCartCategoryBtn.setVisibility(View.GONE);
//                                         viewCartLinearLayout.setVisibility(View.VISIBLE);
//                                         productQuantityForCart.setText(quantityNo.getText().toString());
//                                         totalCartItems=totalCartItems+(long)Integer.parseInt(quantityNo.getText().toString());
//                                         totalCartAmount = totalCartAmount + (long)((Integer.parseInt(categoryWiseProductModelList.get(position).getSellingPrice()))*Integer.parseInt(quantityNo.getText().toString()));
//                                         total_added_products_in_cart.setText(String.valueOf(totalCartItems)+" "+ view.getContext().getResources().getString(R.string.item_count));
//                                         total_added_products_amount_in_cart.setText(Utility.changeToIndianCurrency(totalCartAmount)+" "+view.getContext().getResources().getString(R.string.plus_taxes));
//
////                                     }
//                                         quantityDialog.dismiss();
//                                     }
//                                 });
//                                 quantityDialog.show();
//                                 addToCartCategoryBtn.setEnabled(true);
//                             }
                         }else{
                             CategoryWiseAllProductsActivity.signInDialog.show();
                         }
                     }
                 });
            Glide.with(view.getContext()).load(categoryWiseProductModelList.get(position).getProductImageResource()).apply(new RequestOptions().placeholder(R.mipmap.category_placeholder)).into(productImage);
            if(DBQueries.language.equals("hi")){
                productTitle.setText(categoryWiseProductModelList.get(position).getProductHindiTitle());
            }else{
                productTitle.setText(categoryWiseProductModelList.get(position).getProductTitle());
            }
            ConstraintSet set = new ConstraintSet();
            ConstraintLayout parentConstraintView=view.findViewById(R.id.hsl_item_layout);
            set.clone(parentConstraintView);
            set.connect(tvDiscount.getId(),ConstraintSet.BOTTOM,ConstraintSet.GONE,ConstraintSet.BOTTOM,0);
            set.connect(tvDiscount.getId(),ConstraintSet.END,ConstraintSet.PARENT_ID,ConstraintSet.END,0);
            set.connect(tvDiscount.getId(),ConstraintSet.START,R.id.horizontal_scroll_price,ConstraintSet.START,0);
            set.connect(tvDiscount.getId(),ConstraintSet.TOP,R.id.horizontal_scroll_price,ConstraintSet.BOTTOM,0);
            set.applyTo(parentConstraintView);
            int price=categoryWiseProductModelList.get(position).getProductPrice();
            int selling_price=Integer.parseInt(categoryWiseProductModelList.get(position).getSellingPrice());
            tvDiscount.setText(String.valueOf((int)(((double)(price-selling_price)/(double)price)*100.0))+view.getResources().getString(R.string.discount));
            cuttedPrice.setText(Utility.changeToIndianCurrency(categoryWiseProductModelList.get(position).getProductPrice()));
            productPrice.setText(Utility.changeToIndianCurrency(Integer.parseInt(categoryWiseProductModelList.get(position).getSellingPrice())));
            total_added_products_in_cart.setText(String.valueOf(totalCartItems)+" "+view.getResources().getString(R.string.item_count));
            total_added_products_amount_in_cart.setText(Utility.changeToIndianCurrency(totalCartAmount)+" "+view.getResources().getString(R.string.plus_taxes));
//            if(convertView==null){

                if(categoryWiseProductModelList.get(position).getProductQuantityForCart()!=0){
                    incrementProductQuantity.setVisibility(View.VISIBLE);
                    decrementProductQuantity.setVisibility(View.VISIBLE);
                    productQuantityForCart.setVisibility(View.VISIBLE);
                    addToCartCategoryBtn.setVisibility(View.GONE);
                    viewCartLinearLayout.setVisibility(View.VISIBLE);
                    addToCartCategoryBtn.setEnabled(false);
                    productQuantityForCart.setText(String.valueOf(categoryWiseProductModelList.get(position).getProductQuantityForCart()));
                }
//            }
//            int sz=DBQueries.cartList.size();
//            for(int i=0;i<(sz);i++){
//                if(DBQueries.cartList.get(i).trim().equals(categoryWiseProductModelList.get(position).getProductTitle().trim())){
//                    incrementProductQuantity.setVisibility(View.VISIBLE);
//                    decrementProductQuantity.setVisibility(View.VISIBLE);
//                    productQuantityForCart.setVisibility(View.VISIBLE);
//                    addToCartCategoryBtn.setVisibility(View.GONE);
//                    viewCartLinearLayout.setVisibility(View.VISIBLE);
//                    addToCartCategoryBtn.setEnabled(false);
//                    productQuantityForCart.setText(String.valueOf(DBQueries.cartItemModelList.get(DBQueries.cartList.indexOf(categoryWiseProductModelList.get(position).getProductTitle())).getProductQuantityForOrder()));
//                }
//            }
//        if(DBQueries.cartList.contains(categoryWiseProductModelList.get(position).getProductTitle().trim())){
//            incrementProductQuantity.setVisibility(View.VISIBLE);
//            decrementProductQuantity.setVisibility(View.VISIBLE);
//            productQuantityForCart.setVisibility(View.VISIBLE);
//            addToCartCategoryBtn.setVisibility(View.GONE);
//            viewCartLinearLayout.setVisibility(View.VISIBLE);
//            addToCartCategoryBtn.setEnabled(false);
//            productQuantityForCart.setText(String.valueOf(DBQueries.cartItemModelList.get(DBQueries.cartList.indexOf(categoryWiseProductModelList.get(position).getProductTitle())).getProductQuantityForOrder()));
//        }else{
//            addToCartCategoryBtn.setVisibility(View.VISIBLE);
//            addToCartCategoryBtn.setEnabled(true);
//        }
        if(DBQueries.wishList.contains(categoryWiseProductModelList.get(position).getProductTitle())){
            addToWishlist.setSupportImageTintList(view.getContext().getResources().getColorStateList(R.color.colorPrimary));
        }else{
            addToWishlist.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
        }
        addToWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DBQueries.wishList.contains(categoryWiseProductModelList.get(position).getProductTitle())){
                    int index =DBQueries.wishList.indexOf(categoryWiseProductModelList.get(position).getProductTitle());
                    DBQueries.removeFromWishList(index,view.getContext());
                    addToWishlist.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                }else{

                        DBQueries.addToWishList(view.getContext(),addToWishlist,categoryWiseProductModelList.get(position).getProductTitle(),
                                categoryWiseProductModelList.get(position).getProductImageResource(),
                                categoryWiseProductModelList.get(position).getProductHindiTitle(),
                                String.valueOf(categoryWiseProductModelList.get(position).getProductPrice()),
                                categoryWiseProductModelList.get(position).getSellingPrice(),
                                true,
                                categoryWiseProductModelList.get(position).getProduct_catalog(),
                                categoryWiseProductModelList.get(position).getProduct_category());

                }
            }
        });
        return view;
    }
}
