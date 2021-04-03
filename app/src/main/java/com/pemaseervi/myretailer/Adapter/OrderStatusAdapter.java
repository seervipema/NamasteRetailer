package com.pemaseervi.myretailer.Adapter;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.Model.OrderStatusModel;
import com.pemaseervi.myretailer.Model.OrdersItemModel;
import com.pemaseervi.myretailer.OrderFragment;
import com.pemaseervi.myretailer.R;

import java.util.List;
import java.util.stream.Collectors;

import static com.pemaseervi.myretailer.OrderFragment.myOrderAdapter;

public class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.ViewHolder> {

    private List<OrderStatusModel> orderStatusModelList;
    private int selected_position=0;
    public OrderStatusAdapter(List<OrderStatusModel> orderStatusModelList) {
        this.orderStatusModelList = orderStatusModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_status_item_layout,parent,false);
        return new OrderStatusAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String orderStatus=orderStatusModelList.get(position).getOrderStatus();
        if(selected_position ==position){
            Drawable myDrawable = holder.itemView.getContext().getResources().getDrawable(R.drawable.textlines);
            holder.itemView.setBackground(myDrawable);
        }else{
            Drawable myDrawable = holder.itemView.getContext().getResources().getDrawable(R.color.colorAccent);
            holder.itemView.setBackground(myDrawable);
        }
        holder.setData(orderStatus,position);
    }

    @Override
    public int getItemCount() {
        return orderStatusModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView orderStatusForFiltering;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderStatusForFiltering=itemView.findViewById(R.id.order_status_for_filtering);
        }
        private void setData(String orderStatus, int position){
            orderStatusForFiltering.setText(orderStatus);
            itemView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View view) {
                    notifyItemChanged(selected_position);
                    selected_position=getAdapterPosition();
                    notifyItemChanged(selected_position);
//                    if(DBQueries.selectedStatusPosition!=0){
//
//                        if( linearLayoutManagerForOrderStatus.findViewByPosition(DBQueries.selectedStatusPosition)!=null){
////                            OrderFragment.orderStatusRecycleView.findViewHolderForAdapterPosition(DBQueries.selectedStatusPosition).itemView.setBackground(itemView.getContext().getDrawable(R.color.colorAccent));
//
//                            linearLayoutManagerForOrderStatus.findViewByPosition(DBQueries.selectedStatusPosition).setBackground(itemView.getContext().getDrawable(R.color.colorAccent));
//                        }
//                        }
//                    DBQueries.selectedStatusPosition=getAdapterPosition();
                    if(orderStatusForFiltering.getText().toString().equals("All")){
                        DBQueries.ordersItemModelList.clear();
                        for(int i=0;i<(DBQueries.nonFilteredOrdersItemModelList.size());i++){
                            DBQueries.ordersItemModelList.add(DBQueries.nonFilteredOrdersItemModelList.get(i));
                        }
                        if(DBQueries.nonFilteredOrdersItemModelList.size()==0){
                            OrderFragment.noOrdersFound.setVisibility(View.VISIBLE);
                        }else{
                            OrderFragment.noOrdersFound.setVisibility(View.GONE);
                        }
                        if(myOrderAdapter!=null){
                            myOrderAdapter.notifyDataSetChanged();
                        }
                    }else{
                        List<OrdersItemModel> filteredResult=DBQueries.nonFilteredOrdersItemModelList.stream().filter(element-> element.getOrderStatus().trim().toString().equals(orderStatusForFiltering.getText().toString())).collect(Collectors.toList());
                        DBQueries.ordersItemModelList.clear();
                        if(filteredResult.size()==0){
                            OrderFragment.noOrdersFound.setVisibility(View.VISIBLE);
                        }else{
                            OrderFragment.noOrdersFound.setVisibility(View.GONE);
                        }
                        for(int i=0;i<(filteredResult.size());i++){
                            DBQueries.ordersItemModelList.add(filteredResult.get(i));
                        }
                        if(myOrderAdapter!=null){
                            myOrderAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }
}
