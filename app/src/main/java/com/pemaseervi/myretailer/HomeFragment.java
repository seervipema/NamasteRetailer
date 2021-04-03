package com.pemaseervi.myretailer;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pemaseervi.myretailer.Adapter.CategoryAdapter;
import com.pemaseervi.myretailer.Adapter.HomePageAdapter;
import com.pemaseervi.myretailer.ui.home.HomeViewModel;

import static com.pemaseervi.myretailer.Firebase.DBQueries.categoryModelList;
import static com.pemaseervi.myretailer.Firebase.DBQueries.homePageModelList;
import static com.pemaseervi.myretailer.Firebase.DBQueries.horizontalProductScrollModelList;
import static com.pemaseervi.myretailer.Firebase.DBQueries.loadCategories;
import static com.pemaseervi.myretailer.Firebase.DBQueries.loadFragmentData;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    public static SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private RecyclerView homePageRecyclerView;
    // private  List<CategoryModel> categoryModelList;
    private HomePageAdapter homePageAdapter;
    private ImageView noInternetConnection;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home2, container, false);


        View root = inflater.inflate(R.layout.fragment_home2, container, false);
        categoryRecyclerView=root.findViewById(R.id.category_recycler_view);
        noInternetConnection=root.findViewById(R.id.no_internet_connection);
        swipeRefreshLayout=root.findViewById(R.id.refresh_layout);

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryRecyclerView.setLayoutManager(linearLayoutManager);

        categoryAdapter = new CategoryAdapter(categoryModelList);

//        if(categoryModelList.size()==0){
            loadCategories(categoryAdapter,getContext());
//        }else{
            categoryAdapter = new CategoryAdapter(categoryModelList);
            categoryAdapter.notifyDataSetChanged();
//        }
        categoryRecyclerView.setAdapter(categoryAdapter);
//        firebaseFirestore=FirebaseFirestore.getInstance();
//        firebaseFirestore.collection("CATEGORIES").orderBy("index").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                           for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
//                               categoryModelList.add(new CategoryModel(documentSnapshot.get("image").toString(),documentSnapshot.get("category_name").toString()));
//                           }
//                            categoryAdapter.notifyDataSetChanged();
//                        }else{
//                            String error =  task.getException().getMessage();
//                            Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

        /////////////////bannerSlider/////////
//        List<SliderModel>  sliderModelList= new ArrayList<SliderModel>();
//        sliderModelList.add(new SliderModel(R.mipmap.cart_black,"#077AE4"));
//        sliderModelList.add(new SliderModel(R.mipmap.ic_mail_outline_24px,"#077AE4"));
//        ////////////////original list
//        sliderModelList.add(new SliderModel(R.mipmap.ic_power_settings_new_24px,"#077AE4"));
//        sliderModelList.add(new SliderModel(R.mipmap.red_email,"#077AE4"));
//        sliderModelList.add(new SliderModel(R.mipmap.profile_placeholder,"#077AE4"));
//        sliderModelList.add(new SliderModel(R.mipmap.cart_black,"#077AE4"));
//        sliderModelList.add(new SliderModel(R.mipmap.ic_mail_outline_24px,"#077AE4"));
//        /////////////////////endof original list
//        sliderModelList.add(new SliderModel(R.mipmap.ic_power_settings_new_24px,"#077AE4"));
//        sliderModelList.add(new SliderModel(R.mipmap.red_email,"#077AE4"));





        ///////////////horizontal product layout

//        List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<HorizontalProductScrollModel>();
//        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.forgot_password_image,"Redmi 5A","SD 625 Proccessor","Rs.665"));
//        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.mipmap.path,"Redmi 5A","SD 625 Proccessor","Rs.665"));
//        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.mipmap.red_email,"Redmi 5A","SD 625 Proccessor","Rs.665"));
//        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.ic_menu_camera,"Redmi 5A","SD 625 Proccessor","Rs.665"));
//        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.ic_menu_slideshow,"Redmi 5A","SD 625 Proccessor","Rs.665"));
//        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.ic_menu_gallery,"Redmi 5A","SD 625 Proccessor","Rs.665"));
//        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.ic_menu_share,"Redmi 5A","SD 625 Proccessor","Rs.665"));
//        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.ic_menu_send,"Redmi 5A","SD 625 Proccessor","Rs.665"));
//        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(R.drawable.ic_menu_camera,"Redmi 5A","SD 625 Proccessor","Rs.665"));



        //////////////horizontal product layout


        homePageRecyclerView = root.findViewById(R.id.home_page_recycle_view);
        LinearLayoutManager testingLinearLayout= new LinearLayoutManager(getContext());
        testingLinearLayout.setOrientation(RecyclerView.VERTICAL);
        homePageRecyclerView.setLayoutManager(testingLinearLayout);

        homePageAdapter = new HomePageAdapter(homePageModelList);

//        if(homePageModelList.size()==0){
            loadFragmentData(homePageAdapter,getContext());
//        }else{
            homePageAdapter = new HomePageAdapter(homePageModelList);
            homePageAdapter.notifyDataSetChanged();
//        }
        homePageRecyclerView.setAdapter(homePageAdapter);

//        firebaseFirestore.collection("CATEGORIES")
//                .document("HOME")
//                .collection("TOP_DEALS")
//                .orderBy("index").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            for(QueryDocumentSnapshot documentSnapshot:task.getResult()){
//                                if((long)documentSnapshot.get("view_type")==0){
//                                    List<SliderModel> sliderModelList = new ArrayList<>();
//                                    long no_of_banners= (long)documentSnapshot.get("no_of_banners");
//                                    for(long x=1;x<(no_of_banners+1);x++){
//                                        sliderModelList.add(new SliderModel(documentSnapshot.get("banner"+x).toString(),documentSnapshot.get("banner"+ x +"_background").toString()));
//
//                                    }
//                                    homePageModelList.add(new HomePageModel(0,sliderModelList));
//
//                                }else if((long)documentSnapshot.get("view_type")==1){
//                                    homePageModelList.add(new HomePageModel(1,documentSnapshot.get("strip_ad_banner").toString(),documentSnapshot.get("strip_add_background").toString()));
//
//                                }else if((long)documentSnapshot.get("view_type")==2){
//                                    List<HorizontalProductScrollModel> horizontalProductScrollModelList=new ArrayList<>();
//                                    long no_of_products= (long)documentSnapshot.get("no_of_products");
//                                    for(long x=1;x<(no_of_products+1);x++){
//                                    horizontalProductScrollModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_id_"+x).toString(),
//                                            documentSnapshot.get("product_image_"+x).toString(),
//                                            documentSnapshot.get("product_title_"+x).toString(),
//                                            documentSnapshot.get("product_subtitle_"+x).toString(),
//                                            documentSnapshot.get("product_price_"+x).toString()
//                                            ));
//                                    }
//                                    homePageModelList.add(new HomePageModel(2,documentSnapshot.get("layout_title").toString(),documentSnapshot.get("layout_background").toString(),horizontalProductScrollModelList));
//                                }else if((long)documentSnapshot.get("view_type")==3){
//
//                                }
//
//
//                            }
//                            homePageAdapter.notifyDataSetChanged();
//                        }else{
//                            String error =  task.getException().getMessage();
//                            Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });


//        homePageModelList.add(new HomePageModel(0,sliderModelList));
//        homePageModelList.add(new HomePageModel(1,R.drawable.forgot_password_image,"#000000"));
//        homePageModelList.add(new HomePageModel(2,"Deals of the day",horizontalProductScrollModelList));
//        homePageModelList.add(new HomePageModel(3,"Deals of the day",horizontalProductScrollModelList));
//        homePageModelList.add(new HomePageModel(1,R.drawable.forgot_password_image,"#000000"));
//        homePageModelList.add(new HomePageModel(3,"Deals of the day",horizontalProductScrollModelList));
//        homePageModelList.add(new HomePageModel(2,"Deals of the day",horizontalProductScrollModelList));
//        homePageModelList.add(new HomePageModel(1,R.drawable.ic_menu_send,"#ffff00"));
//        homePageModelList.add(new HomePageModel(0,sliderModelList));
//        homePageModelList.add(new HomePageModel(1,R.drawable.ic_menu_gallery,"#ff0000"));


        /////////////////////////////////////////



//        categoryModelList.add(new CategoryModel("link","home"));
//        categoryModelList.add(new CategoryModel("link","Electronics"));
//        categoryModelList.add(new CategoryModel("link","Appliances"));
//        categoryModelList.add(new CategoryModel("link","Furniture"));
//        categoryModelList.add(new CategoryModel("link","Fashion"));
//        categoryModelList.add(new CategoryModel("link","Toys"));
//        categoryModelList.add(new CategoryModel("link","Sports"));
//        categoryModelList.add(new CategoryModel("link","shoes"));


        ////////////swipe refresh layput
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                categoryModelList.clear();
                homePageModelList.clear();
                horizontalProductScrollModelList.clear();
                loadCategories(categoryAdapter,getContext());
                loadFragmentData( homePageAdapter,getContext());

            }
        });
        ////////////swipe refresh layout
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        categoryModelList.clear();
        homePageModelList.clear();
        horizontalProductScrollModelList.clear();
        loadCategories(categoryAdapter,getContext());
        loadFragmentData( homePageAdapter,getContext());
    }
}
