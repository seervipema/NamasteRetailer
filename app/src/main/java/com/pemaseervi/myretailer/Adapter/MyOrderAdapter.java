package com.pemaseervi.myretailer.Adapter;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.Model.MyOrderItemModel;
import com.pemaseervi.myretailer.OrderDetailsActivity;
import com.pemaseervi.myretailer.R;

import java.util.Date;
import java.util.List;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {

    private List<MyOrderItemModel> myOrderItemModelList;


    public MyOrderAdapter(List<MyOrderItemModel> myOrderItemModelList) {
        this.myOrderItemModelList = myOrderItemModelList;
    }

    @NonNull
    @Override
    public MyOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderAdapter.ViewHolder holder, int position) {

       String resource= myOrderItemModelList.get(position).getProductImage();
//       int rating = myOrderItemModelList.get(position).getRating();
       String title=myOrderItemModelList.get(position).getProductTitle();
       String hindiTitle= myOrderItemModelList.get(position).getProductHindiTitle();
       String orderStatus = myOrderItemModelList.get(position).getOrderStatus();
       Date date;
       switch (orderStatus){
           case "Ordered":
               date=myOrderItemModelList.get(position).getOrderedDate();
               break;
           case "Packed":
               date=myOrderItemModelList.get(position).getPackedDate();
               break;
           case "Shipped":
               date=myOrderItemModelList.get(position).getShippedDate();
               break;
           case "Delivered":
               date=myOrderItemModelList.get(position).getDeliveredDate();
               break;
           case "Cancelled":
               date=myOrderItemModelList.get(position).getCancelledDate();
               break;
               default:
                   date=myOrderItemModelList.get(position).getCancelledDate();
                   break;

       }
       holder.setData(resource,title,hindiTitle,orderStatus,date,position);
    }

    @Override
    public int getItemCount() {
        return myOrderItemModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView productImage,orderIndicator;
        private TextView deliveryStatus;
        private TextView productTitle;
        private LinearLayout rateNowContainer;
        private  int index=0;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
           productTitle = itemView.findViewById(R.id.product_title);
            productImage=itemView.findViewById(R.id.product_image);
            orderIndicator=itemView.findViewById(R.id.order_indicator);
            deliveryStatus=itemView.findViewById(R.id.order_delivered_date);
            rateNowContainer=itemView.findViewById(R.id.rate_now_container);

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
        private void setData(String resource,String title,String hindiTitle,String orderStatus,Date date,int position){
            index=position;
            Glide.with(itemView.getContext()).load(resource).into(productImage);
            if(DBQueries.language.equals("hi")){
                productTitle.setText(hindiTitle);
            }else{
                productTitle.setText(title);
            }

            if(orderStatus.equals("Cancelled")){
                orderIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.colorPrimary)));
            }else{
                orderIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.successGreen)));

            }
            deliveryStatus.setText(orderStatus + " on " + String.valueOf(date));
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

        private void setRating(int starPosition) {
            for(int x=0;x<rateNowContainer.getChildCount();x++){
                ImageView starBtn =(ImageView)rateNowContainer.getChildAt(x);
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
                if(x<=starPosition){
                    starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
                }
            }
        }
    }
}
