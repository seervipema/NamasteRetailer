package com.pemaseervi.myretailer.Adapter;

import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pemaseervi.myretailer.CartActivity;
import com.pemaseervi.myretailer.CategoryWiseAllProductsActivity;
import com.pemaseervi.myretailer.DeliveryActivity;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.Model.CartItemModel;
import com.pemaseervi.myretailer.MyCartFragment;
import com.pemaseervi.myretailer.R;
import com.pemaseervi.myretailer.utility.Utility;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter {

    List<CartItemModel> cartItemModelList;
    private TextView cartTotalAmount;
    private boolean showDeleteBtn;
    private boolean isDeliveryActivity;
    private int lastPosition=0;

    public CartAdapter(List<CartItemModel> cartItemModelList,TextView cartTotalAmount,Boolean showDeleteBtn,Boolean isDeliveryActivity) {
        this.cartItemModelList = cartItemModelList;
        this.cartTotalAmount=cartTotalAmount;
        this.showDeleteBtn=showDeleteBtn;
        this.isDeliveryActivity=isDeliveryActivity;

    }

    @Override
    public int getItemViewType(int position) {
        switch (cartItemModelList.get(position).getType()) {
            case 0:
                return CartItemModel.CART_ITEM;
            case 1:
                return CartItemModel.TOTAL_AMOUNT;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case CartItemModel.CART_ITEM:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
                return new CartItemViewHolder(view);
            case CartItemModel.TOTAL_AMOUNT:
                View cartTotalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_totol_amount_layout, parent, false);
                return new CartTotalAmountViewHolder(cartTotalView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (cartItemModelList.get(position).getType()) {
            case CartItemModel.CART_ITEM:
                String resource = cartItemModelList.get(position).getProduct_image();
                String title = cartItemModelList.get(position).getProductTitle();
                String hindiTitle= cartItemModelList.get(position).getProductHindiTitle();
//                int freeCoupens = cartItemModelList.get(position).getFreeCoupen();
                String productPrice  = cartItemModelList.get(position).getSellingPrice();
                int productCartQuantity = cartItemModelList.get(position).getProductQuantityForOrder();
//                Boolean inStock = cartItemModelList.get(position).getInStock();

//                String cuttedPrice = cartItemModelList.get(position).getCuttedPrice();
//                int offersAplied = cartItemModelList.get(position).getOffersApplied();
//                ,freeCoupens,
//                    ,cuttedPrice,offersAplied
                ((CartItemViewHolder)holder).setItemDetails(resource,title,hindiTitle,productPrice,position,productCartQuantity);
                break;
            case CartItemModel.TOTAL_AMOUNT:
                int totalItems=0;
                int totalItemPrice=0;
                String deliveryPrice;
                int productQuantityForOrder= cartItemModelList.get(position).getProductQuantityForOrder();
                int totalAmount;
                int savedAmount =0;
                for(int x=0;x<cartItemModelList.size();x++){
                    if(cartItemModelList.get(x).getType() == CartItemModel.CART_ITEM ){
                         totalItems=totalItems+cartItemModelList.get(x).getProductQuantityForOrder();
                         totalItemPrice=totalItemPrice+ Integer.parseInt(cartItemModelList.get(x).getSellingPrice().toString())*(cartItemModelList.get(x).getProductQuantityForOrder());
                    }
                }
//                if(totalItemPrice > 500){
                    deliveryPrice="FREE";
                    totalAmount=totalItemPrice;
//                }else{
//                    deliveryPrice="FREE";
//                    totalAmount= totalItemPrice;
//                }
//                String totalItems= cartItemModelList.get(position).getTotalItems();
//                String totalItemPrice= cartItemModelList.get(position).getTotalItemPrice();
//                String deliveryPrice= cartItemModelList.get(position).getDeliveryAmount();
//                String totalAmount= cartItemModelList.get(position).getTotalAmountCart();
//                String savedAmount= cartItemModelList.get(position).getSavedAmount();
                lastPosition=position;
                cartItemModelList.get(position).setTotalAmount(totalAmount);
                cartItemModelList.get(position).setTotalItemPrice(totalItemPrice);
                cartItemModelList.get(position).setDeliveryPrice(deliveryPrice);
                cartItemModelList.get(position).setTotalItems(totalItems);
                cartItemModelList.get(position).setSavedAmount(savedAmount);
                ((CartTotalAmountViewHolder)holder).setTotalAmount(totalItems,totalItemPrice,deliveryPrice,totalAmount,savedAmount);
                break;
            default:
                return;
        }
    }

    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout addRemoveLayout;
        private ImageView productImage, freeCoupenIcon;
        private ImageView incrementProductQuantity,decrementProductQuantity;
        private TextView cartProductQuantity;
        private TextView productTitle, freeCoupens, productPrice, cuttedPrice, offersApllied, coupenApplied;
//                , productQuantity;
        private LinearLayout removeCartItem;
        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            incrementProductQuantity= itemView.findViewById(R.id.increment_product_quantity);
            decrementProductQuantity= itemView.findViewById(R.id.decrement_product_quantity);
            cartProductQuantity= itemView.findViewById(R.id.categorywise_cart_product_quantity);
            addRemoveLayout= itemView.findViewById(R.id.add_remove_layout);
//            freeCoupens=itemView.findViewById(R.id.tv_free_coupen);
//            freeCoupenIcon = itemView.findViewById(R.id.free_coupen_icon);
            productPrice = itemView.findViewById(R.id.product_price);
//            cuttedPrice = itemView.findViewById(R.id.cutted_price);
//            coupenApplied = itemView.findViewById(R.id.coupens_applied);
//            productQuantity = itemView.findViewById(R.id.product_quantity);
           removeCartItem =itemView.findViewById(R.id.remove_item_btn);
//            offersApllied=itemView.findViewById(R.id.offers_applied);
        }
//        int freeCoupensNumber,
//        , String cuttedPriceText, int offersAppliedNumber
        private void setItemDetails(String resource, String title,String hindiTitle, String productPriceText,final int index,int productCartQuanity) {
//            productImage.setImageResource(resource);
            if(!resource.equals("null")){
                Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.mipmap.category_placeholder)).into(productImage);

            }
            if(DBQueries.language.equals("hi")){
                productTitle.setText(hindiTitle);
            }else{
                productTitle.setText(title);
            }

//            if (freeCoupensNumber > 0) {
//                freeCoupenIcon.setVisibility(View.VISIBLE);
//                freeCoupens.setVisibility(View.VISIBLE);
//                if (freeCoupensNumber == 1) {
//                    freeCoupens.setText("free " + freeCoupensNumber + " coupen");
//                } else {
//                    freeCoupens.setText("free " + freeCoupensNumber + " coupens");
//                }
//            } else {
//                freeCoupenIcon.setVisibility(View.INVISIBLE);
//                freeCoupens.setVisibility(View.INVISIBLE);
//            }
            cartProductQuantity.setText(String.valueOf(productCartQuanity));
            boolean inStock=true;
             if(inStock){
                 productPrice.setText(Utility.changeToIndianCurrency(Integer.parseInt(productPriceText)));
                 productPrice.setTextColor(Color.parseColor("#000000"));
                 incrementProductQuantity.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                        int x= Integer.parseInt(cartProductQuantity.getText().toString());
                        x++;
                        cartProductQuantity.setText(String.valueOf(x));
                        cartItemModelList.get(index).setProductQuantityForOrder(x);
                         CategoryWiseAllProductsActivity.totalCartAmount+=Integer.parseInt(cartItemModelList.get(index).getSellingPrice().toString());
                         MyCartFragment.totalCartAmount +=Integer.parseInt(cartItemModelList.get(index).getSellingPrice().toString());
                         if(CartActivity.totalAmount !=null) {
                             CartActivity.totalAmount.setText(Utility.changeToIndianCurrency(CategoryWiseAllProductsActivity.totalCartAmount));
                         }
                         if(isDeliveryActivity){
                             DeliveryActivity.totalCartAmount+=Integer.parseInt(cartItemModelList.get(index).getSellingPrice().toString());
                             DeliveryActivity.totalCartItems++;
                             DeliveryActivity.totalAmount.setText(Utility.changeToIndianCurrency(CategoryWiseAllProductsActivity.totalCartAmount));

                            if(DeliveryActivity.cartItemModelList.get(lastPosition).getType()==CartItemModel.TOTAL_AMOUNT){

                             cartItemModelList.get(DeliveryActivity.cartItemModelList.size()-1).setTotalItems(DeliveryActivity.totalCartItems);
                             cartItemModelList.get(DeliveryActivity.cartItemModelList.size()-1).setTotalAmount(DeliveryActivity.totalCartAmount);
                              DeliveryActivity.cartAdapter.notifyDataSetChanged();
                            }
                         }
//                         if(MyCartFragment.totalAmount!=null){
//                             MyCartFragment.totalAmount.setText("Rs."+MyCartFragment.totalCartAmount+"/-");
//                         }
                     }
                 });
                 decrementProductQuantity.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                         int x= Integer.parseInt(cartProductQuantity.getText().toString());
                         if(x>0){
                             x--;
                             if(x==0){
                                 AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext(),R.style.Theme_AppCompat_Dialog_Alert);
                                 builder.setMessage(R.string.remove_item_from_delivery);
                                 builder.setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
                                     public void onClick(DialogInterface dialog, int id) {
                                         // User clicked OK button
                                         DBQueries.removeFromCartList(index,itemView.getContext(),cartTotalAmount,CartActivity.loadingDialog);
                                         cartProductQuantity.setText(String.valueOf(0));
                                         cartItemModelList.get(index).setProductQuantityForOrder(0);
                                         CategoryWiseAllProductsActivity.totalCartAmount-=Integer.parseInt(cartItemModelList.get(index).getSellingPrice().toString());
                                         MyCartFragment.totalCartAmount -=Integer.parseInt(cartItemModelList.get(index).getSellingPrice().toString());
                                         if(CartActivity.totalAmount !=null){
                                             CartActivity.totalAmount.setText(Utility.changeToIndianCurrency(CategoryWiseAllProductsActivity.totalCartAmount));
                                         }
                                         if(MyCartFragment.totalAmount!=null){
                                             MyCartFragment.totalAmount.setText(Utility.changeToIndianCurrency(MyCartFragment.totalCartAmount));
                                         }
                                         if(isDeliveryActivity){

                                             DeliveryActivity.totalCartAmount-=Integer.parseInt(cartItemModelList.get(index).getSellingPrice().toString());
                                             DeliveryActivity.totalCartItems--;
                                             DeliveryActivity.totalAmount.setText(Utility.changeToIndianCurrency(CategoryWiseAllProductsActivity.totalCartAmount));
                                             if(DeliveryActivity.cartItemModelList.get(lastPosition).getType()==CartItemModel.TOTAL_AMOUNT){

                                                 cartItemModelList.get(DeliveryActivity.cartItemModelList.size()-1).setTotalItems(DeliveryActivity.totalCartItems);
                                                 cartItemModelList.get(DeliveryActivity.cartItemModelList.size()-1).setTotalAmount(DeliveryActivity.totalCartAmount);
                                                 DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                             }
                                         }
                                         if(DeliveryActivity.cartAdapter!=null){
                                             DeliveryActivity.cartItemModelList.remove(index);
                                             DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                         }
                                     }
                                 });
                                 builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
                                     public void onClick(DialogInterface dialog, int id) {

                                     }
                                 });
                                 builder.show();
                             }
                             if(x>0){
                                 cartProductQuantity.setText(String.valueOf(x));
                                 cartItemModelList.get(index).setProductQuantityForOrder(x);
                                 CategoryWiseAllProductsActivity.totalCartAmount-=Integer.parseInt(cartItemModelList.get(index).getSellingPrice().toString());
                                 MyCartFragment.totalCartAmount -=Integer.parseInt(cartItemModelList.get(index).getSellingPrice().toString());
                                 if(CartActivity.totalAmount !=null){
                                     CartActivity.totalAmount.setText(Utility.changeToIndianCurrency(CategoryWiseAllProductsActivity.totalCartAmount));
                                 }
                                 if(isDeliveryActivity){
                                     DeliveryActivity.totalCartAmount-=Integer.parseInt(cartItemModelList.get(index).getSellingPrice().toString());
                                     DeliveryActivity.totalCartItems--;
                                     DeliveryActivity.totalAmount.setText(Utility.changeToIndianCurrency(CategoryWiseAllProductsActivity.totalCartAmount));
                                     if(DeliveryActivity.cartItemModelList.get(lastPosition).getType()==CartItemModel.TOTAL_AMOUNT){

                                         cartItemModelList.get(DeliveryActivity.cartItemModelList.size()-1).setTotalItems(DeliveryActivity.totalCartItems);
                                         cartItemModelList.get(DeliveryActivity.cartItemModelList.size()-1).setTotalAmount(DeliveryActivity.totalCartAmount);
                                         DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                     }
                                 }
                             }
                             }
                         }

//                         if(MyCartFragment.totalAmount!=null){
//                             MyCartFragment.totalAmount.setText("Rs."+MyCartFragment.totalCartAmount+"/-");
//                         }


                 });
//                 productQuantity.setOnClickListener(new View.OnClickListener() {
//                     @Override
//                     public void onClick(View view) {
//                         final Dialog quantityDialog=new Dialog(itemView.getContext());
//                         quantityDialog.setContentView(R.layout.quantity_dialog);
//                         quantityDialog.setCancelable(false);
//                         quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//                         final EditText quantityNo= quantityDialog.findViewById(R.id.quantity_no);
//                         Button cancelBtn=quantityDialog.findViewById(R.id.cancel_btn),Okbtn=quantityDialog.findViewById(R.id.ok_btn);
//                         cancelBtn.setOnClickListener(new View.OnClickListener() {
//                             @Override
//                             public void onClick(View view) {
//                                 quantityDialog.dismiss();
//                             }
//                         });
//                         Okbtn.setOnClickListener(new View.OnClickListener() {
//                             @Override
//                             public void onClick(View view) {
////                                 DBQueries.cartItemModelList.get(index).setProductQuantity(Long.valueOf(pro))
//                                 productQuantity.setText("Qty: "+ quantityNo.getText());
//                                 quantityDialog.dismiss();
//                             }
//                         });
//                         quantityDialog.show();
//                     }
//                 });

             }else{
                 addRemoveLayout.setVisibility(View.INVISIBLE);
                 productPrice.setText(itemView.getContext().getResources().getString(R.string.out_of_stock_msg));
                 productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
//                 productQuantity.setText("Qty: "+ "0");
//                 productQuantity.setVisibility(View.INVISIBLE);
//                 productQuantity.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#70000000")));
//                 productQuantity.setTextColor(Color.parseColor("#70000000"));
//                 productQuantity.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#70000000")));
             }

//            cuttedPrice.setText(cuttedPriceText);
//            if (offersAppliedNumber > 0) {
//                offersApllied.setVisibility(View.VISIBLE);
//                offersApllied.setText(offersAppliedNumber + " offers applied");
//            } else {
//                offersApllied.setVisibility(View.INVISIBLE);
//            }
//             productQuantity.setVisibility(View.VISIBLE);

            if(showDeleteBtn){
                removeCartItem.setVisibility(View.VISIBLE);
                itemView.findViewById(R.id.divider14).setVisibility(View.VISIBLE);
            }else{
                removeCartItem.setVisibility(View.GONE);
                itemView.findViewById(R.id.divider14).setVisibility(View.GONE);
            }
            removeCartItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext(),R.style.Theme_AppCompat_Dialog_Alert);
                    builder.setMessage(R.string.remove_item_from_delivery);
                    builder.setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
//                            removeCartItem.setEnabled(false);
                            CategoryWiseAllProductsActivity.totalCartAmount-= cartItemModelList.get(index).getProductQuantityForOrder() * Integer.parseInt(cartItemModelList.get(index).getSellingPrice());
                            MyCartFragment.totalCartAmount -=cartItemModelList.get(index).getProductQuantityForOrder() * Integer.parseInt(cartItemModelList.get(index).getSellingPrice());
                            if(CartActivity.totalAmount !=null){
                                CartActivity.totalAmount.setText(Utility.changeToIndianCurrency(CategoryWiseAllProductsActivity.totalCartAmount));
                            }
                            if(MyCartFragment.totalAmount!=null){
                                MyCartFragment.totalAmount.setText(Utility.changeToIndianCurrency(MyCartFragment.totalCartAmount));
                            }
                            DBQueries.removeFromCartList(index,itemView.getContext(),cartTotalAmount,CartActivity.loadingDialog);
                            notifyItemChanged(index);
                        }
                    });
                    builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    builder.show();

                }
            });

        }
    }

    class CartTotalAmountViewHolder extends RecyclerView.ViewHolder {

        private TextView totalItems, totalItemPrice, deliveryPrice, totalAmount, savedAmount;

        public CartTotalAmountViewHolder(@NonNull View itemView) {
            super(itemView);
            totalItems = itemView.findViewById(R.id.total_items);
            totalItemPrice = itemView.findViewById(R.id.total_item_price);
            deliveryPrice = itemView.findViewById(R.id.delivery_price);
            totalAmount = itemView.findViewById(R.id.total_price);
            savedAmount = itemView.findViewById(R.id.saved_amount);
        }

        public void setTotalAmount(int totalItemText, int totalItemPriceText, String deliveryPriceText, int totalAmountText, int savedAmountText) {
            totalItems.setText(itemView.getContext().getResources().getString(R.string.price_heading)+"("+totalItemText+" "+ itemView.getContext().getResources().getString(R.string.item_count)+")");
            totalItemPrice.setText(Utility.changeToIndianCurrency(totalItemPriceText));
            if(deliveryPriceText.equals("FREE")){
                deliveryPrice.setText(deliveryPriceText);
            }else{
                deliveryPrice.setText(Utility.changeToIndianCurrency(Integer.parseInt(deliveryPriceText)));
            }
            totalAmount.setText(Utility.changeToIndianCurrency(totalAmountText));
            cartTotalAmount.setText(Utility.changeToIndianCurrency(totalAmountText));
            savedAmount.setText("You saved "+Utility.changeToIndianCurrency(totalAmountText) +" on this order");
//            LinearLayout parent =(LinearLayout) cartTotalAmount.getParent().getParent();
//            if(totalItemPriceText ==0){
//                DBQueries.cartItemModelList.remove(DBQueries.cartItemModelList.size()-1);
//                parent.setVisibility(View.GONE);
//            }else{
//                parent.setVisibility(View.VISIBLE);
//            }


        }
    }
}
