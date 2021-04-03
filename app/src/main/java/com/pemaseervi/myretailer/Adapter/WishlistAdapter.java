package com.pemaseervi.myretailer.Adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pemaseervi.myretailer.CategoryWiseAllProductsActivity;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.Model.WishlistModel;
import com.pemaseervi.myretailer.ProductDetailsActivity;
import com.pemaseervi.myretailer.R;
import com.pemaseervi.myretailer.utility.Utility;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    List<WishlistModel> wishlistModelList;
    private Boolean fromSearch=false;
//    Boolean isWishlist ;


    public Boolean getFromSearch() {
        return fromSearch;
    }

    public void setFromSearch(Boolean fromSearch) {
        this.fromSearch = fromSearch;
    }

    public List<WishlistModel> getWishlistModelList() {
        return wishlistModelList;
    }

    public void setWishlistModelList(List<WishlistModel> wishlistModelList) {
        this.wishlistModelList = wishlistModelList;
    }

    public WishlistAdapter(List<WishlistModel> wishlistModelList) {
        this.wishlistModelList = wishlistModelList;
//        this.isWishlist = isWishlist;
    }

    @NonNull
    @Override
    public WishlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistAdapter.ViewHolder holder, int position) {
        String resource= wishlistModelList.get(position).getProductImage();
        String title= wishlistModelList.get(position).getProductTitle();
        String hindiTitle= wishlistModelList.get(position).getProductHindiTitle();
//        int freeCoupens= wishlistModelList.get(position).getFreeCoupens();
//        String rating= wishlistModelList.get(position).getRating();
//        int totalRatings= wishlistModelList.get(position).getTotalRating();
        boolean inStock= wishlistModelList.get(position).isInStock();
        String productPrice= wishlistModelList.get(position).getProduct_price();
        String sellingPrice=wishlistModelList.get(position).getSellingPrice();
        String product_catalog=wishlistModelList.get(position).getProduct_catalog();
        String product_category=wishlistModelList.get(position).getProduct_category();
//        String cuttedPrice= wishlistModelList.get(position).getCutted_price();
//        String paymentMethod= wishlistModelList.get(position).getPaymentMethod();
//        ,freeCoupens,rating,totalRatings,
//        ,cuttedPrice,paymentMethod
        holder.setData(resource,title,hindiTitle,productPrice,sellingPrice,position,inStock,product_catalog,product_category);
    }

    @Override
    public int getItemCount() {
        return wishlistModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView productImage,coupenIcon,deleteBtn;
        private View priceCut;
        private TextView productTitle,freeCoupens,productPrice,cuttedPrice,paymentMethod,rating,totalRatings;
        private LinearLayout addTocartBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage=itemView.findViewById(R.id.product_image);
            productTitle=itemView.findViewById(R.id.product_title);
//            freeCoupens=itemView.findViewById(R.id.free_coupen);
//            coupenIcon=itemView.findViewById(R.id.coupen_icon);
//            rating=itemView.findViewById(R.id.tv_product_rating_miniview);
//            totalRatings=itemView.findViewById(R.id.total_ratings);
//            priceCut=itemView.findViewById(R.id.price_cute);
            productPrice=itemView.findViewById(R.id.product_price);
            cuttedPrice=itemView.findViewById(R.id.cutted_price);
//            paymentMethod=itemView.findViewById(R.id.payment_method);
            deleteBtn=itemView.findViewById(R.id.delete_button);
            addTocartBtn=itemView.findViewById(R.id.add_to_cart_item_btn);
        }
//        int freeCoupensNumber,String averageRate,int totalRatingsNumber,
//        ,String cutPrice, String paymentMethodValue
        private void setData(String resource, final String title, final String hindiTitle, String price,String selling_price, final int index, boolean inStock,final String product_catalog, final String product_category){
            if(!resource.equals("null")){
                Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.mipmap.category_placeholder)).into(productImage);

            }
            if(DBQueries.language.equals("hi")){
                productTitle.setText(hindiTitle);
            }else{
                productTitle.setText(title);
            }

//            if(freeCoupensNumber!=0){
//                coupenIcon.setVisibility(View.VISIBLE);
//                if(freeCoupensNumber ==1){
//                    freeCoupens.setText("free "+freeCoupensNumber+" coupen");
//                }else{
//                    freeCoupens.setText("free "+freeCoupensNumber+" coupens");
//
//                }
//            }else{
//                coupenIcon.setVisibility(View.INVISIBLE);
//                freeCoupens.setVisibility(View.INVISIBLE);
//            }
//            rating.setText(averageRate);
//            totalRatings.setText(totalRatingsNumber+"(ratings)");
//            if(inStock){
                productPrice.setText(Utility.changeToIndianCurrency(Integer.parseInt(selling_price)));
                productPrice.setTextColor(Color.parseColor("#000000"));
//            }else{
//                productPrice.setText(R.string.out_of_stock_msg);
//                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
//
//            }
            cuttedPrice.setText(Utility.changeToIndianCurrency(Integer.parseInt(price)));
//            paymentMethod.setText(paymentMethodValue);
//            if(!fromSearch){
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        deleteBtn.setEnabled(false);
                        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext(),R.style.Theme_AppCompat_Dialog_Alert);
                        builder.setMessage(R.string.remove_item_from_delivery);
                        builder.setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK button
                                DBQueries.removeFromWishList(index,itemView.getContext());
                            }
                        });
                        builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                        builder.show();
                    }
                });
//            }
            if(fromSearch){
                deleteBtn.setVisibility(View.INVISIBLE);
            }else{
                deleteBtn.setVisibility(View.VISIBLE);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(fromSearch){
                        ProductDetailsActivity.fromSearch=true;
                    }
                    Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                    productDetailsIntent.putExtra("product_id",title);
                    productDetailsIntent.putExtra("product_hindi_name",hindiTitle);
                    productDetailsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                    itemView.getContext().startActivity(productDetailsIntent);
                }
            });
            addTocartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext(),R.style.Theme_AppCompat_Dialog_Alert);
                    builder.setMessage(R.string.add_to_cart_msg);
                    builder.setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            if(DBQueries.cartList.contains(title)){
                                Toast.makeText(itemView.getContext(),"Already added to cart",Toast.LENGTH_SHORT).show();
                            }else{
                                DBQueries.addToCart(view.getContext(),
                                        title,
                                        title,
                                        hindiTitle,
                                        price,
                                        selling_price,
                                        resource,
                                        1,
                                        CategoryWiseAllProductsActivity.loadingDialog,
                                        product_catalog,
                                        product_category

                                );
                            }
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
}
