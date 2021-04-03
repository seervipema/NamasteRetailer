package com.pemaseervi.myretailer.Adapter;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.Model.HorizontalProductScrollModel;
import com.pemaseervi.myretailer.ProductDetailsActivity;
import com.pemaseervi.myretailer.R;
import com.pemaseervi.myretailer.utility.Utility;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class HorizontalProductScrollAdapter extends RecyclerView.Adapter<HorizontalProductScrollAdapter.ViewHolder> {

    private List<HorizontalProductScrollModel> horizontalProductScrollModelList;

    public HorizontalProductScrollAdapter(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    @NonNull
    @Override
    public HorizontalProductScrollAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_item_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalProductScrollAdapter.ViewHolder holder, int position) {
           String resource = horizontalProductScrollModelList.get(position).getProductImage();
           String title = horizontalProductScrollModelList.get(position).getProductTitle();
        String hindiTitle= null;
        try {
            hindiTitle = horizontalProductScrollModelList.get(position).getProductHindiName();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String description = horizontalProductScrollModelList.get(position).getProductDescription();
        String price = horizontalProductScrollModelList.get(position).getProductPrice();
        String selling_price=horizontalProductScrollModelList.get(position).getSellingPrice();
        String catalog_name=horizontalProductScrollModelList.get(position).getCatalogName();
        String product_category=horizontalProductScrollModelList.get(position).getProductCategory();
          holder.setData(resource,title,hindiTitle,description,price,selling_price,catalog_name,product_category);

    }

    @Override
    public int getItemCount() {
        if(horizontalProductScrollModelList.size()>8){
            return 8;
        }else {
            return horizontalProductScrollModelList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productTitle,productDescription,productPrice,cuttedPrice,tvDiscount;
        private ConstraintLayout constraintLayout;
        private FloatingActionButton addToWishlist;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            productImage=itemView.findViewById(R.id.horizontal_scroll_product_image);
            productTitle=itemView.findViewById(R.id.horizontal_scroll_product_title);
//            productDescription=itemView.findViewById(R.id.horizontal_scroll_product_desc);
            productPrice=itemView.findViewById(R.id.horizontal_scroll_price);
            cuttedPrice=itemView.findViewById(R.id.hsl_cutted_price);
            tvDiscount=itemView.findViewById(R.id.hsl_discount);
            constraintLayout= itemView.findViewById(R.id.hsl_item_layout);
            addToWishlist=itemView.findViewById(R.id.hs_add_to_wishlist_btn);



        }
        private void setData(String resource, final String title, final String hindiTitle, String description, String price,String selling_price,String catalogName,String productCategory){
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.mipmap.category_placeholder)).into(productImage);
            if(DBQueries.language.equals("hi")){
                productTitle.setText(hindiTitle);
            }else{
                productTitle.setText(title);
            }
            if(DBQueries.wishList.contains(title)){
                addToWishlist.setSupportImageTintList(itemView.getContext().getResources().getColorStateList(R.color.colorPrimary));
            }else{
                addToWishlist.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
            }
            addToWishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(DBQueries.wishList.contains(title)){
                        int index =DBQueries.wishList.indexOf(title);
                        DBQueries.removeFromWishList(index,itemView.getContext());
                        addToWishlist.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                    }else{
                        DBQueries.addToWishList(itemView.getContext(),addToWishlist,title,resource,hindiTitle,price,selling_price,true,catalogName,productCategory);
                    }
                }
            });
            ConstraintLayout.LayoutParams layoutParams= new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
             layoutParams.leftMargin=8;
             layoutParams.topMargin=8;
             layoutParams.bottomMargin=8;
             layoutParams.width=450;
             constraintLayout.setLayoutParams(layoutParams);
//            productDescription.setText(description);
            productPrice.setText(Utility.changeToIndianCurrency(Integer.parseInt(selling_price)));
            cuttedPrice.setText(Utility.changeToIndianCurrency(Integer.parseInt(price)));
            tvDiscount.setText(String.valueOf((int)(((double)(Integer.parseInt(price)-Integer.parseInt(selling_price))/(double)(Integer.parseInt(price)))*100.0))+itemView.getResources().getString(R.string.discount));


            if(!title.equals("")) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                        productDetailsIntent.putExtra("product_id", title);
                        productDetailsIntent.putExtra("product_hindi_name", hindiTitle);
                        productDetailsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                        itemView.getContext().startActivity(productDetailsIntent);
                    }
                });
            }
        }

    }
}
