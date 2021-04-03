package com.pemaseervi.myretailer;


import android.app.Dialog;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pemaseervi.myretailer.Adapter.WishlistAdapter;
import com.pemaseervi.myretailer.Firebase.DBQueries;

import static com.pemaseervi.myretailer.Firebase.DBQueries.loadWishList;
import static com.pemaseervi.myretailer.Firebase.DBQueries.wishlistModelList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyWishlistFragment extends Fragment {



    public MyWishlistFragment() {
        // Required empty public constructor
    }

    public static RecyclerView wishlistRecycleView;
    private Dialog loadingDialog;
    public static   WishlistAdapter wishlistAdapter;
    public static ConstraintLayout emptyWishListLayout;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_wishlist, container, false);
        wishlistRecycleView=view.findViewById(R.id.my_wishlist_recycleview);
        emptyWishListLayout= view.findViewById(R.id.empty_wish_list_layout);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        wishlistRecycleView.setLayoutManager(linearLayoutManager);
        loadingDialog= new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
//        List<WishlistModel> wishlistModelList = new ArrayList<>();
//        wishlistModelList.add(new WishlistModel(R.drawable.forgot_password_image,3,3,"Pixel 2","4.7","Rs.4999/-","Rs.5999/-","Case on delivery method available"));
//        wishlistModelList.add(new WishlistModel(R.drawable.forgot_password_image,1,23,"Pixel 2","4.2","Rs.4999/-","Rs.5999/-","Case on delivery method available"));
//        wishlistModelList.add(new WishlistModel(R.drawable.forgot_password_image,2,43,"Pixel 2","3.7","Rs.4999/-","Rs.5999/-","Case on delivery method available"));
//        wishlistModelList.add(new WishlistModel(R.drawable.forgot_password_image,0,53,"Pixel 2","2.7","Rs.4999/-","Rs.5999/-","Case on delivery method available"));
       if(wishlistModelList.size() ==0){
           DBQueries.wishList.clear();
           loadWishList(getContext(), loadingDialog, true);
       }else{
           loadingDialog.dismiss();
       }
        wishlistAdapter = new WishlistAdapter(wishlistModelList);
        wishlistRecycleView.setAdapter(wishlistAdapter);
        wishlistAdapter.notifyDataSetChanged();
        return view;
    }

}
