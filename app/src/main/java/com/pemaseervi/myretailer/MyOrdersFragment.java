package com.pemaseervi.myretailer;


import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pemaseervi.myretailer.Adapter.MyOrderAdapter;
import com.pemaseervi.myretailer.Adapter.OrderStatusAdapter;
import com.pemaseervi.myretailer.Firebase.DBQueries;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyOrdersFragment extends Fragment {


    public MyOrdersFragment() {
        // Required empty public constructor
    }
    private RecyclerView myOrderRecyclerView;
    private Dialog loadingDialog;
    private RecyclerView orderStatusRecycleView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_my_orders, container, false);
        myOrderRecyclerView=view.findViewById(R.id.my_orders_recyclerview);
        orderStatusRecycleView=view.findViewById(R.id.order_status_recycleview);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        myOrderRecyclerView.setLayoutManager(linearLayoutManager);
        LinearLayoutManager linearLayoutManagerForOrderStatus=new LinearLayoutManager(getContext());
        linearLayoutManagerForOrderStatus.setOrientation(RecyclerView.HORIZONTAL);
        orderStatusRecycleView.setLayoutManager(linearLayoutManagerForOrderStatus);

        loadingDialog= new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        OrderStatusAdapter orderStatusAdapter= new OrderStatusAdapter(DBQueries.orderStatusModelList);
        orderStatusRecycleView.setAdapter(orderStatusAdapter);
        MyOrderAdapter myOrderAdapter = new MyOrderAdapter(DBQueries.myOrderItemModelList);

        myOrderRecyclerView.setAdapter(myOrderAdapter);
//        if(DBQueries.myOrderItemModelList.size() ==0){
//            DBQueries.loadOrders(getContext(),myOrderAdapter,loadingDialog);
//        }

        return view;
    }

}
