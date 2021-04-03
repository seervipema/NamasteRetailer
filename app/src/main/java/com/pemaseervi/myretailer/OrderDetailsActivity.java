package com.pemaseervi.myretailer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pemaseervi.myretailer.Adapter.OrderItemDetailsAdapter;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.utility.Utility;

public class OrderDetailsActivity extends AppCompatActivity {

    private TextView productTitle;
    private ImageView productImage;
    private TextView productOrderedQuantity;
    private TextView productPrice;
    private TextView orderedTitle,orderedDate,packedTitle,packedDate,shippedTitle,shippedDate,deliveredTitle,deliveredDate;
    private ImageView orderedIndicator,packedIndicator,shippedIndicator,deliveredIndicator;
    private ProgressBar ordered_packed_progress_bar,packed_shipped_progress_bar,shipped_delivered_progress_bar;
    private TextView orderedBody,packedBody,shippedBody,deliveredBody;
    private TextView shippingDetailsFullname,shippingDetailsAddress,shippingDetailsPincode;

    private TextView totalItems,totalItemPrice,totalPrice;
    private RecyclerView myOrderRecyclerView;
    private Dialog loadingDialog;
    OrderItemDetailsAdapter myOrderAdapter;
    private TextView tvOrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.orders_details_heading);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        productTitle= findViewById(R.id.product_title);
        productImage = findViewById(R.id.product_image);
        productOrderedQuantity = findViewById(R.id.ordered_quantity);
        productPrice= findViewById(R.id.product_price);
        orderedTitle= findViewById(R.id.ordered_title);
        orderedDate = findViewById(R.id.ordered_date);
        packedTitle = findViewById(R.id.packed_title);
        packedDate = findViewById(R.id.packed_date);
        shippedTitle = findViewById(R.id.shipping_title);
        shippedDate = findViewById(R.id.shipping_date);
        deliveredTitle = findViewById(R.id.delivery_title);
        deliveredDate = findViewById(R.id.delivery_date);
        orderedIndicator = findViewById(R.id.ordered_indicator);
        packedIndicator = findViewById(R.id.packed_indicator);
        shippedIndicator = findViewById(R.id.shipping_indicator);
        deliveredIndicator  = findViewById(R.id.delivered_indicator);
        ordered_packed_progress_bar = findViewById(R.id.ordered_packed_progress);
        packed_shipped_progress_bar = findViewById(R.id.packed_shipping_progressbar);
        shipped_delivered_progress_bar = findViewById(R.id.shipping_delivered_progressbar);
        orderedBody= findViewById(R.id.ordered_body);
        packedBody = findViewById(R.id.packed_body);
        shippedBody = findViewById(R.id.shipping_body);
        deliveredBody = findViewById(R.id.delivery_body);

        shippingDetailsFullname= findViewById(R.id.fullName);
        shippingDetailsAddress = findViewById(R.id.address);
        shippingDetailsPincode = findViewById(R.id.pincode);

        totalItems = findViewById(R.id.total_items);
        totalItemPrice = findViewById(R.id.total_item_price);
        totalPrice = findViewById(R.id.total_price);
        tvOrderId= findViewById(R.id.tv_order_id);
        myOrderRecyclerView=findViewById(R.id.per_order_recycle_view);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        myOrderRecyclerView.setLayoutManager(linearLayoutManager);
        loadingDialog= new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);


        int position = getIntent().getIntExtra("position",0);

         myOrderAdapter = new OrderItemDetailsAdapter(DBQueries.myOrderItemModelList);

        myOrderRecyclerView.setAdapter(myOrderAdapter);
        if(DBQueries.myOrderItemModelList.size() ==0){
            DBQueries.loadOrders(this,myOrderAdapter,loadingDialog,getIntent().getStringExtra("orderId"));
        }


//        productTitle.setText(myOrderItemModelList.get(position).getProductTitle());
//        Glide.with(OrderDetailsActivity.this).load(myOrderItemModelList.get(position).getProductImage()).into(productImage);
//        productPrice.setText("Rs."+ myOrderItemModelList.get(position).getProductPrice()+"/-");
//        productOrderedQuantity.setText("Quantity: "+ myOrderItemModelList.get(position).getProductQuantity());
        String orderStatus= DBQueries.ordersItemModelList.get(position).getOrderStatus();
       // Toast.makeText(OrderDetailsActivity.this,orderStatus,Toast.LENGTH_LONG).show();
        switch (orderStatus){
            case "Ordered":
            case "Accepted":
                orderedDate.setText(String.valueOf(DBQueries.ordersItemModelList.get(position).getOrderedDate()));
                orderedIndicator.setImageTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));

                ordered_packed_progress_bar.setProgressTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));

                packedDate.setText("");
                shippedDate.setText("");
                deliveredDate.setText("");

                orderedBody.setText(R.string.ordered_msg);
                packedBody.setText("");
                shippedBody.setText("");
                deliveredBody.setText("");
                break;
            case "Packed":
                orderedDate.setText(String.valueOf(DBQueries.ordersItemModelList.get(position).getOrderedDate()));
                packedDate.setText(String.valueOf(DBQueries.ordersItemModelList.get(position).getPackedDate()));
                orderedIndicator.setImageTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));
                packedIndicator.setImageTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));

                ordered_packed_progress_bar.setProgressTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));
                packed_shipped_progress_bar.setProgressTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));

                shippedDate.setText("");
                deliveredDate.setText("");

                orderedBody.setText(R.string.ordered_msg);
                packedBody.setText(R.string.packed_msg);

                shippedBody.setText("");
                deliveredBody.setText("");
                break;
            case "Shipped":
                orderedDate.setText(String.valueOf(DBQueries.ordersItemModelList.get(position).getOrderedDate()));
                packedDate.setText(String.valueOf(DBQueries.ordersItemModelList.get(position).getPackedDate()));
                shippedDate.setText(String.valueOf(DBQueries.ordersItemModelList.get(position).getShippedDate()));
                orderedIndicator.setImageTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));
                packedIndicator.setImageTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));
                shippedIndicator.setImageTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));

                ordered_packed_progress_bar.setProgressTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));
                packed_shipped_progress_bar.setProgressTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));
                shipped_delivered_progress_bar.setProgressTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));

                deliveredDate.setText("");

                orderedBody.setText(R.string.ordered_msg);
                packedBody.setText(R.string.packed_msg);
                shippedBody.setText(R.string.shipped_msg);


                deliveredBody.setText("");
                break;
            case "Out for delivery":
                orderedDate.setText(String.valueOf(DBQueries.ordersItemModelList.get(position).getOrderedDate()));
                packedDate.setText(String.valueOf(DBQueries.ordersItemModelList.get(position).getPackedDate()));
                shippedDate.setText(String.valueOf(DBQueries.ordersItemModelList.get(position).getShippedDate()));
                deliveredDate.setText(String.valueOf(DBQueries.ordersItemModelList.get(position).getDeliveredDate()));
                orderedIndicator.setImageTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));
                packedIndicator.setImageTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));
                shippedIndicator.setImageTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));
                deliveredIndicator.setImageTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));

                deliveredTitle.setText(R.string.out_for_delivery);
                deliveredBody.setText(R.string.out_for_delivery_msg);
                break;
            case "Delivered":
                orderedDate.setText(String.valueOf(DBQueries.ordersItemModelList.get(position).getOrderedDate()));
                packedDate.setText(String.valueOf(DBQueries.ordersItemModelList.get(position).getPackedDate()));
                shippedDate.setText(String.valueOf(DBQueries.ordersItemModelList.get(position).getShippedDate()));
                deliveredDate.setText(String.valueOf(DBQueries.ordersItemModelList.get(position).getDeliveredDate()));
                orderedIndicator.setImageTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));
                packedIndicator.setImageTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));
                shippedIndicator.setImageTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));
                deliveredIndicator.setImageTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));
                ordered_packed_progress_bar.setProgressTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));
                packed_shipped_progress_bar.setProgressTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));
                shipped_delivered_progress_bar.setProgressTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));

                break;
            case "Not Delivered":
                orderedDate.setText(String.valueOf(DBQueries.ordersItemModelList.get(position).getOrderedDate()));
                packedDate.setText(String.valueOf(DBQueries.ordersItemModelList.get(position).getPackedDate()));
                shippedDate.setText(String.valueOf(DBQueries.ordersItemModelList.get(position).getShippedDate()));
                deliveredDate.setText(String.valueOf(DBQueries.ordersItemModelList.get(position).getDeliveredDate()));
                orderedIndicator.setImageTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));
                packedIndicator.setImageTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));
                shippedIndicator.setImageTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));
                deliveredIndicator.setImageTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));
                ordered_packed_progress_bar.setProgressTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));
                packed_shipped_progress_bar.setProgressTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));
                shipped_delivered_progress_bar.setProgressTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.successGreen)));

                deliveredTitle.setText("Order marked as not delivered");
                deliveredBody.setText("Order is not delivered yet.");
                break;
            case "Declined":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.btnRed)));
                orderedDate.setText(String.valueOf(DBQueries.ordersItemModelList.get(position).getOrderedDate()));
                orderedTitle.setText("Declined");
                packedDate.setText("");
                shippedDate.setText("");
                deliveredDate.setText("");
                orderedBody.setText("Order has been declined.");
                packedBody.setText("");
                shippedBody.setText("");
                deliveredBody.setText("");
                break;    
            case "Cancelled":
                orderedDate.setText(String.valueOf(DBQueries.ordersItemModelList.get(position).getCancelledDate()));
                orderedTitle.setText(R.string.cancelled_msg);
                orderedIndicator.setImageTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.btnRed)));
                packedDate.setText("");
                shippedDate.setText("");
                deliveredDate.setText("");

                orderedBody.setText(R.string.concelled_body_msg);
                packedBody.setText("");
                shippedBody.setText("");
                deliveredBody.setText("");
                break;
            default:
//                date=myOrderItemModelList.get(position).getCancelledDate();
                break;

        }

//        DBQueries.myOrderItemModelList.get
        tvOrderId.setText(DBQueries.ordersItemModelList.get(position).getOrderId());
        shippingDetailsFullname.setText(DBQueries.ordersItemModelList.get(position).getFullName());
        shippingDetailsAddress.setText(DBQueries.ordersItemModelList.get(position).getAddress());
        shippingDetailsPincode.setText(DBQueries.ordersItemModelList.get(position).getPincode());
        totalItems.setText(getResources().getString(R.string.price_heading)+"( "+DBQueries.ordersItemModelList.get(position).getTotalItems() +" "+getResources().getString(R.string.items_heading)+" )");
        totalItemPrice.setText(Utility.changeToIndianCurrency(DBQueries.ordersItemModelList.get(position).getTotalAmount()));
        totalPrice.setText(Utility.changeToIndianCurrency(DBQueries.ordersItemModelList.get(position).getTotalAmount()));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(myOrderAdapter!=null){
            myOrderAdapter.notifyDataSetChanged();
        }
    }
}
