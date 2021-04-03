package com.pemaseervi.myretailer.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.Model.MyOrderItemModel;
import com.pemaseervi.myretailer.OrderDetailsActivity;
import com.pemaseervi.myretailer.R;
import com.pemaseervi.myretailer.utility.Utility;

import java.util.List;

public class OrderItemDetailsAdapter extends RecyclerView.Adapter<OrderItemDetailsAdapter.ViewHolder> {

    private List<MyOrderItemModel> myOrderItemModelList;


    public OrderItemDetailsAdapter(List<MyOrderItemModel> myOrderItemModelList) {
        this.myOrderItemModelList = myOrderItemModelList;
    }

    @NonNull
    @Override
    public OrderItemDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_only_layout,parent,false);
        return new OrderItemDetailsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemDetailsAdapter.ViewHolder holder, int position) {

        String resource= myOrderItemModelList.get(position).getProductImage();
//       int rating = myOrderItemModelList.get(position).getRating();
        String title=myOrderItemModelList.get(position).getProductTitle();
        String hindiTitle = myOrderItemModelList.get(position).getProductHindiTitle();
//        Long totalAmount= myOrderItemModelList.get(position).get;
        String productPrice = myOrderItemModelList.get(position).getProductPrice();
        Long totalItems= myOrderItemModelList.get(position).getProductQuantity();
        holder.setData(resource,title,hindiTitle,0,totalItems,productPrice);
    }

    @Override
    public int getItemCount() {
        return myOrderItemModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView productImage;
        private TextView productTitle;
        private TextView productPrice;
        private TextView totalItems;
        private  int index=0;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.product_title);
            productImage=itemView.findViewById(R.id.product_image);
            productPrice=itemView.findViewById(R.id.product_price);
            totalItems= itemView.findViewById(R.id.ordered_quantity);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent orderDetailsIntent = new Intent(itemView.getContext(), OrderDetailsActivity.class);
                    orderDetailsIntent.putExtra("position",index);
                    orderDetailsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                    itemView.getContext().startActivity(orderDetailsIntent);
                }
            });


        }
        private void setData(String resource,String title,String hindiTitle,long _totalAmount,long _totalItems,String _productPrice){

            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.mipmap.category_placeholder)).into(productImage);


            if(DBQueries.language.equals("hi")){
                productTitle.setText(hindiTitle);
            }else{
                productTitle.setText(title);
            }
            totalItems.setText(_totalItems+" "+itemView.getResources().getString(R.string.item_count));
            productPrice.setText(Utility.changeToIndianCurrency(Integer.parseInt(_productPrice)));
//            if(orderStatus.equals("Cancelled")){
//                orderIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.colorPrimary)));
//            }else{
//                orderIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.successGreen)));
//
//            }
//            deliveryStatus.setText(orderStatus + " on " + String.valueOf(date));
            /////////ratings layour
//           setRating(rating);
//            for(int x=0;x<rateNowContainer.getChildCount();x++){
//                final int starPosition=x;
//                rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        setRating(starPosition);
//                    }
//                });
//
//            }
//            ///////////ratings layout
        }
    }
}