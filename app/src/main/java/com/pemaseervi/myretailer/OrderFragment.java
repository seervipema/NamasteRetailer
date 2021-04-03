package com.pemaseervi.myretailer;


import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pemaseervi.myretailer.Adapter.OrderStatusAdapter;
import com.pemaseervi.myretailer.Adapter.OrdersAdapter;
import com.pemaseervi.myretailer.Firebase.DBQueries;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment {


    public OrderFragment() {
        // Required empty public constructor
    }
    private RecyclerView myOrderRecyclerView;
    private Dialog loadingDialog;
    public static OrdersAdapter myOrderAdapter;
    public static RecyclerView orderStatusRecycleView;
    public static LinearLayoutManager linearLayoutManagerForOrderStatus;
    public static TextView noOrdersFound;
    public static OrderStatusAdapter orderStatusAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_order, container, false);
        myOrderRecyclerView=view.findViewById(R.id.orders);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        myOrderRecyclerView.setLayoutManager(linearLayoutManager);
        orderStatusRecycleView=view.findViewById(R.id.order_status_recycleview);
        noOrdersFound=view.findViewById(R.id.tv_no_orders_found);

        loadingDialog= new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);


        linearLayoutManagerForOrderStatus=new LinearLayoutManager(getContext());
        linearLayoutManagerForOrderStatus.setOrientation(RecyclerView.HORIZONTAL);
        orderStatusRecycleView.setLayoutManager(linearLayoutManagerForOrderStatus);
        orderStatusAdapter= new OrderStatusAdapter(DBQueries.orderStatusModelList);
        orderStatusRecycleView.setAdapter(orderStatusAdapter);
        DBQueries.getAllOrderStatus(getContext(),orderStatusAdapter,loadingDialog);

        myOrderAdapter = new OrdersAdapter(DBQueries.ordersItemModelList);
        myOrderRecyclerView.setAdapter(myOrderAdapter);
//        if(DBQueries.ordersItemModelList.size() ==0){
            DBQueries.loadOrdersByUser(getContext(),myOrderAdapter,loadingDialog);
//        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(myOrderAdapter!=null){
            myOrderAdapter.notifyDataSetChanged();
        }
    }
        @Override
    public void onDestroy() {
        super.onDestroy();
        if(DBQueries.isNotificationClicked){
            DBQueries.isNotificationClicked=false;
        }
//        iSocket.disconnect();
    }
}
