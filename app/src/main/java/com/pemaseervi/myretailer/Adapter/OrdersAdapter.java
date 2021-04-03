package com.pemaseervi.myretailer.Adapter;

import android.content.Intent;
import android.content.res.ColorStateList;
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
import com.pemaseervi.myretailer.Model.OrdersItemModel;
import com.pemaseervi.myretailer.OrderDetailsActivity;
import com.pemaseervi.myretailer.R;
import com.pemaseervi.myretailer.utility.Utility;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    private List<OrdersItemModel> myOrderItemModelList;


    public OrdersAdapter(List<OrdersItemModel> myOrderItemModelList) {
        this.myOrderItemModelList = myOrderItemModelList;
    }

    @NonNull
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_layout,parent,false);
        return new OrdersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.ViewHolder holder, int position) {

        String resource= myOrderItemModelList.get(position).getProductImage();
//       int rating = myOrderItemModelList.get(position).getRating();
        String title=myOrderItemModelList.get(position).getOrderId();
        String orderStatus = myOrderItemModelList.get(position).getOrderStatus();
        Long totalAmount= myOrderItemModelList.get(position).getTotalAmount();
        Long totalItems= myOrderItemModelList.get(position).getTotalItems();
        String orderId= myOrderItemModelList.get(position).getOrderId();
        String date;
        switch (orderStatus){
            case "Ordered":
            case "Accepted":
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
        holder.setData(resource,title,orderStatus,date,position,totalAmount,totalItems,orderId);
    }

    @Override
    public int getItemCount() {
        return myOrderItemModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView productImage,orderIndicator;
        private TextView deliveryStatus;
        private TextView productTitle;
        private TextView totalAmount;
        private TextView totalItems;
        private  int index=0;
        private String ORDERID;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.product_title);
            productImage=itemView.findViewById(R.id.product_image);
            orderIndicator=itemView.findViewById(R.id.order_indicator);
            deliveryStatus=itemView.findViewById(R.id.order_delivered_date);
            totalAmount = itemView.findViewById(R.id.total_order_amount);
            totalItems= itemView.findViewById(R.id.total_order_items);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent orderDetailsIntent = new Intent(itemView.getContext(), OrderDetailsActivity.class);
                    orderDetailsIntent.putExtra("position",index);
                    orderDetailsIntent.putExtra("orderId",ORDERID);
                    DBQueries.myOrderItemModelList.clear();
                    orderDetailsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                    itemView.getContext().startActivity(orderDetailsIntent);
                }
            });


        }
        private void setData(String resource,String title,String orderStatus,String date,int position,long _totalAmount,long _totalItems,String orderId){
            index=position;
            ORDERID=orderId;
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.mipmap.category_placeholder)).into(productImage);
            productTitle.setText(itemView.getContext().getResources().getString(R.string.order_id)+" " +title);
            totalAmount.setText(Utility.changeToIndianCurrency(_totalAmount));
            totalItems.setText(_totalItems+" "+itemView.getContext().getResources().getString(R.string.item_count));
            if(orderStatus.equals("Cancelled") || orderStatus.equals("Declined")){
                orderIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.btnRed)));
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
    }

}