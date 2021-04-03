package com.pemaseervi.myretailer.Adapter;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

public class GridProductLayoutAdapter extends BaseAdapter {

    List<HorizontalProductScrollModel> horizontalProductScrollModelList;

    public GridProductLayoutAdapter(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    @Override
    public int getCount() {
        return horizontalProductScrollModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View convertView, final ViewGroup viewGroup) {
        View view ;
        if(convertView == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.horizontal_scroll_item_layout,viewGroup,false); 
        }else{
            view = convertView;
        }
          view.setElevation(0);
            view.setBackgroundColor(Color.parseColor("#ffffff"));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent productDetailsIntent=new Intent(viewGroup.getContext(), ProductDetailsActivity.class);
                    productDetailsIntent.putExtra("product_id",horizontalProductScrollModelList.get(i).getProductTitle());
                    try {
                        productDetailsIntent.putExtra("product_hindi_name",horizontalProductScrollModelList.get(i).getProductHindiName());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    productDetailsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                    viewGroup.getContext().startActivity(productDetailsIntent);
                }
            });

            ImageView productImage = view.findViewById(R.id.horizontal_scroll_product_image);
            TextView productTitle = view.findViewById(R.id.horizontal_scroll_product_title);
//            TextView productDescription = view.findViewById(R.id.horizontal_scroll_product_desc);
            TextView productPrice = view.findViewById(R.id.horizontal_scroll_price);
            TextView cuttedPrice=view.findViewById(R.id.hsl_cutted_price);
            TextView tvDiscount=view.findViewById(R.id.hsl_discount);
            FloatingActionButton addToWishlist=view.findViewById(R.id.hs_add_to_wishlist_btn);


           // productImage.setImageResource(horizontalProductScrollModelList.get(i).getProductImage());
            Glide.with(view.getContext()).load(horizontalProductScrollModelList.get(i).getProductImage()).apply(new RequestOptions().placeholder(R.mipmap.category_placeholder)).into(productImage);
            if(DBQueries.language.equals("hi")){
                try {
                    productTitle.setText(horizontalProductScrollModelList.get(i).getProductHindiName());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }else{
                productTitle.setText(horizontalProductScrollModelList.get(i).getProductTitle());
            }
            int price=Integer.parseInt(horizontalProductScrollModelList.get(i).getProductPrice());
            int selling_price=Integer.parseInt(horizontalProductScrollModelList.get(i).getSellingPrice());
            tvDiscount.setText(String.valueOf((int)(((double)(price-selling_price)/(double)price)*100.0))+view.getResources().getString(R.string.discount));

//            productDescription.setText(horizontalProductScrollModelList.get(i).getProductDescription());
            productPrice.setText(Utility.changeToIndianCurrency(Integer.parseInt(horizontalProductScrollModelList.get(i).getSellingPrice())));
            cuttedPrice.setText(Utility.changeToIndianCurrency(Integer.parseInt(horizontalProductScrollModelList.get(i).getProductPrice())));
        if(DBQueries.wishList.contains(horizontalProductScrollModelList.get(i).getProductTitle())){
            addToWishlist.setSupportImageTintList(view.getContext().getResources().getColorStateList(R.color.colorPrimary));
        }else{
            addToWishlist.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
        }
        addToWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DBQueries.wishList.contains(horizontalProductScrollModelList.get(i).getProductTitle())){
                    int index =DBQueries.wishList.indexOf(horizontalProductScrollModelList.get(i).getProductTitle());
                    DBQueries.removeFromWishList(index,view.getContext());
                    addToWishlist.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                }else{
                    try {
                        DBQueries.addToWishList(view.getContext(),addToWishlist,horizontalProductScrollModelList.get(i).getProductTitle(),
                                horizontalProductScrollModelList.get(i).getProductImage(),
                                horizontalProductScrollModelList.get(i).getProductHindiName(),
                                horizontalProductScrollModelList.get(i).getProductPrice(),
                                horizontalProductScrollModelList.get(i).getSellingPrice(),
                                true,
                                horizontalProductScrollModelList.get(i).getCatalogName(),
                                horizontalProductScrollModelList.get(i).getProductCategory());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return view;
    }
}
