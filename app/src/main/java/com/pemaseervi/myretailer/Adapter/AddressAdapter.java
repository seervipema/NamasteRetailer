package com.pemaseervi.myretailer.Adapter;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.pemaseervi.myretailer.AddAddressActivity;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.Model.AddressModel;
import com.pemaseervi.myretailer.MyAddressesActivity;
import com.pemaseervi.myretailer.R;
import com.pemaseervi.myretailer.utility.AppPref;
import com.pemaseervi.myretailer.utility.CommonApiConstant;
import com.pemaseervi.myretailer.utility.VolleyMultipartRequest;
import com.pemaseervi.myretailer.utility.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pemaseervi.myretailer.DeliveryActivity.SELECT_ADDRESS;
import static com.pemaseervi.myretailer.MyAccountFragment.MANAGE_ADDRESS;
import static com.pemaseervi.myretailer.MyAddressesActivity.refreshItem;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

   private List<AddressModel> addressModelList ;
   private int  MODE;
   private int preSelectedPosition;
   private boolean refresh = false;
    public AddressAdapter(List<AddressModel> addressModelList,int MODE) {
        this.addressModelList = addressModelList;
        this.MODE=MODE;
        this.preSelectedPosition= DBQueries.selectedAddress;
    }

    @NonNull
    @Override
    public AddressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.addresses_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.ViewHolder holder, int position) {

        String city = addressModelList.get(position).getCity();
        String locality = addressModelList.get(position).getLocality();
        String flatNumber = addressModelList.get(position).getFlatNumber();
        String pincode = addressModelList.get(position).getPincode();
        String landmark = addressModelList.get(position).getLandmark();
        String name = addressModelList.get(position).getName();
        String mobileNumber = addressModelList.get(position).getMobileNumber();
        String alternateMobileNumber = addressModelList.get(position).getAlternateMobileNumber();
        String state = addressModelList.get(position).getState();
        Boolean selected=  addressModelList.get(position).getSelected();


        holder.setData(name,city,pincode,selected,position,mobileNumber,alternateMobileNumber,flatNumber,locality,landmark,state);


    }

    @Override
    public int getItemCount() {
        return addressModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView fullname,address,pincode,editAddressBtn,deleteAddresssBtn;
        private ImageView icon;
        private LinearLayout optionContainer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullname=itemView.findViewById(R.id.name);
            address=itemView.findViewById(R.id.address);
            pincode=itemView.findViewById(R.id.pincode);
            icon= itemView.findViewById(R.id.icon_view);
            optionContainer=itemView.findViewById(R.id.option_container);
            editAddressBtn= itemView.findViewById(R.id.edit_address_btn);
            deleteAddresssBtn= itemView.findViewById(R.id.delete_address_btn);
        }
        private void setData(String username, String city, String userPincode, final Boolean selected, final int position,String mobileNumber,String alternateMobileNumber,String flatNumber,String locality,String landmark,String state){

            if(alternateMobileNumber.equals("")){
                fullname.setText(username+" - "+mobileNumber);
            }else{
                fullname.setText(username+" - "+mobileNumber + " or "+alternateMobileNumber);
            }
            if(landmark.equals("")){
                address.setText(flatNumber+", "+locality+", "+city+", "+state);
            }else{
                address.setText(flatNumber+", "+locality+", "+landmark+", "+city+", "+state);
            }
            pincode.setText(userPincode);
            editAddressBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent addAddressIntent= new Intent(itemView.getContext(), AddAddressActivity.class);
                    addAddressIntent.putExtra("INTENT","update_address");
                    addAddressIntent.putExtra("position",position);
                    addAddressIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    itemView.getContext().startActivity(addAddressIntent);
                }
            });
            deleteAddresssBtn.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext(),R.style.Theme_AppCompat_Dialog_Alert);
                    builder.setMessage(R.string.delete_address_msg);
                    builder.setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            Map<String,String> addAddress = new HashMap();
                            addAddress.put("user_id",CommonApiConstant.currentUser);
                            addAddress.put("address_id",addressModelList.get(position).getAddress_id());
                            String URL= CommonApiConstant.backEndUrl+"/user/address/"+CommonApiConstant.currentUser+"/"+addressModelList.get(position).getAddress_id();
                            Dialog loadingDialog;
                            loadingDialog= new Dialog(itemView.getContext());
                            loadingDialog.setContentView(R.layout.loading_progress_dialog);
                            loadingDialog.setCancelable(false);
                            loadingDialog.getWindow().setBackgroundDrawable(itemView.getContext().getDrawable(R.drawable.slider_background));
                            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

                            VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.DELETE, URL, new Response.Listener<NetworkResponse>() {
                                @Override
                                public void onResponse(NetworkResponse response) {
                                    loadingDialog.dismiss();
                                    String resultResponse = new String(response.data);
                                    try {
                                        JSONObject result = new JSONObject(resultResponse);
                                        Toast.makeText(itemView.getContext(),result.getString("message").toString(),Toast.LENGTH_LONG).show();
                                        MyAddressesActivity.refreshItem(DBQueries.selectedAddress,DBQueries.addressModelList.size() -1);
                                        for(int count=0;count<(DBQueries.addressModelList.size());count++){
                                            if(DBQueries.addressModelList.get(count).getAddress_id().equals(addAddress.get("address_id"))){
                                                DBQueries.addressModelList.remove(count);
                                                break;
                                            }
                                        }
                                        MyAddressesActivity.addressAdapter.notifyDataSetChanged();
                                        MyAddressesActivity.addressesSaved.setText(String.valueOf(DBQueries.addressModelList.size())+" "+itemView.getContext().getResources().getString(R.string.saved_addresses_msg));
                                        if(DBQueries.addressModelList.size()==0){
                                            if(MyAddressesActivity.deliverHereBtn!=null){
                                                MyAddressesActivity.deliverHereBtn.setVisibility(View.GONE);
                                            }
                                        }else{
                                            if(MyAddressesActivity.deliverHereBtn!=null){
                                                MyAddressesActivity.deliverHereBtn.setVisibility(View.VISIBLE);
                                            }
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    loadingDialog.dismiss();
                                    NetworkResponse networkResponse = error.networkResponse;
                                    String errorMessage = "Unknown error";
                                    if (networkResponse == null) {
                                        if (error.getClass().equals(TimeoutError.class)) {
                                            errorMessage = "Request timeout";
                                        } else if (error.getClass().equals(NoConnectionError.class)) {
                                            errorMessage = "Failed to connect server";
                                        }
                                    } else {
                                        String result = new String(networkResponse.data);
                                        try {
                                            JSONObject response = new JSONObject(result);
                                            String message = response.getString("message");

                                            Log.e("Error Message", message);

                                            if (networkResponse.statusCode == 404) {
                                                errorMessage = "Resource not found";
                                            } else if (networkResponse.statusCode == 401) {
                                                DBQueries.signOut(itemView.getContext());
                                                errorMessage = message+" Please login again";
                                            } else if (networkResponse.statusCode == 400) {
                                                errorMessage = message+ " Check your inputs";
                                            } else if (networkResponse.statusCode == 500) {
                                                errorMessage = message+" Something is getting wrong";
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    Log.i("Error", errorMessage);
                                    error.printStackTrace();
                                    Toast.makeText(itemView.getContext(),errorMessage,Toast.LENGTH_LONG).show();
                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() {
                                    return addAddress;
                                }
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> headers = new HashMap<>();
                                    headers.put("Authorization","Bearer "+ AppPref.getUserToken(itemView.getContext()));
                                    return headers;
                                }
                            };
                            VolleySingleton.getInstance(itemView.getContext()).addToRequestQueue(multipartRequest1);
//
                        }
                    });
                    builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    builder.show();
                }
            });
            if(MODE == SELECT_ADDRESS){
                icon.setImageResource(R.mipmap.check);
                if(selected){
                    icon.setVisibility(View.VISIBLE);
                    preSelectedPosition =position;
                }else{
                    icon.setVisibility(View.GONE);
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       if(preSelectedPosition !=position) {
                            addressModelList.get(position).setSelected(true);
                            addressModelList.get(preSelectedPosition).setSelected(false);
                            refreshItem(preSelectedPosition, position);
                            preSelectedPosition = position;
                            DBQueries.selectedAddress=position;
                        }
                    }
                });

            }else if(MODE==MANAGE_ADDRESS){
               optionContainer.setVisibility(View.GONE);
               optionContainer.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { //////// edit address
                        Intent addAddressIntent= new Intent(itemView.getContext(), AddAddressActivity.class);
                        addAddressIntent.putExtra("INTENT","update_address");
                        addAddressIntent.putExtra("position",position);
                        addAddressIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                        itemView.getContext().startActivity(addAddressIntent);
                    }
                });
                optionContainer.getChildAt(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) { ///remove address

                    }
                });
               icon.setImageResource(R.mipmap.option_round);
               icon.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       optionContainer.setVisibility(View.VISIBLE);
                       if(refresh){
                           refreshItem(preSelectedPosition,preSelectedPosition);
                       }else{
                           refresh = true;
                       }
                       refreshItem(preSelectedPosition,preSelectedPosition);
                       preSelectedPosition =position;
                   }
               });
               itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       refreshItem(preSelectedPosition,preSelectedPosition);
                       preSelectedPosition=-1;
                   }
               });
            }
        }
    }
}
