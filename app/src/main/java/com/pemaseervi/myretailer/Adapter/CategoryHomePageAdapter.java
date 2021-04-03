package com.pemaseervi.myretailer.Adapter;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.Model.HomePageModel;
import com.pemaseervi.myretailer.Model.HorizontalProductScrollModel;
import com.pemaseervi.myretailer.Model.SliderModel;
import com.pemaseervi.myretailer.ProductDetailsActivity;
import com.pemaseervi.myretailer.R;
import com.pemaseervi.myretailer.ViewAllActivity;
import com.pemaseervi.myretailer.utility.Utility;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CategoryHomePageAdapter extends RecyclerView.Adapter {

    private List<HomePageModel> homePageModelList;
    private RecyclerView.RecycledViewPool recycledViewPool;
    private int lastPosition =-1;
    int i;


    public CategoryHomePageAdapter(List<HomePageModel> homePageModelList) {
        this.homePageModelList = homePageModelList;
        recycledViewPool= new RecyclerView.RecycledViewPool();
    }

    @Override
    public int getItemViewType(int position) {
        switch (homePageModelList.get(position).getType()) {
            case 0:
                return HomePageModel.BANNER_SLIDER;
            case 1:
                return HomePageModel.STRIP_AD_BANNER;
            case 2:
                return HomePageModel.HORIZONTAL_PRODUCT_VIEW;
            case 3:
                return HomePageModel.GRID_PRODUCT_VIEW;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case HomePageModel.BANNER_SLIDER:
                View bannerSliderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sliding_ad_layout, parent, false);
                return new BannerSliderViewHolder(bannerSliderView);
            case HomePageModel.STRIP_AD_BANNER:
                View stripAddView = LayoutInflater.from(parent.getContext()).inflate(R.layout.strip_add_layout, parent, false);
                return new StripAddViewHolder(stripAddView);
            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                View horizontalProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_layout, parent, false);
                return new HorizontalProductViewHolder(horizontalProductView);
            case HomePageModel.GRID_PRODUCT_VIEW:
                View gridProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_product_layout, parent, false);
                return new GridProductViewHolder(gridProductView);
            default:
                return null;
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (homePageModelList.get(position).getType()) {
            case HomePageModel.BANNER_SLIDER:
                List<SliderModel> sliderModelList = homePageModelList.get(position).getSliderModelList();
                ((BannerSliderViewHolder) holder).setBannerSliderViewPager(sliderModelList);
                break;
            case HomePageModel.STRIP_AD_BANNER:
                String resource = homePageModelList.get(position).getResource();
                String color = homePageModelList.get(position).getBackgroundColor();
                ((StripAddViewHolder) holder).setStripAdd(resource, color);
                break;
            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                String layout_color = homePageModelList.get(position).getBackgroundColor();
                String horizontalLayoutTitle = homePageModelList.get(position).getTitle();

                List<HorizontalProductScrollModel> horizontalProductScrollModelList = homePageModelList.get(position).getHorizontalProductScrollModelList();
                ((HorizontalProductViewHolder) holder).setHorizontalProductLayout(horizontalProductScrollModelList, horizontalLayoutTitle, layout_color,position);
                break;
            case HomePageModel.GRID_PRODUCT_VIEW:
                String gridLayoutTitle = homePageModelList.get(position).getTitle();
                List<HorizontalProductScrollModel> gridProductScrollModelList = homePageModelList.get(position).getHorizontalProductScrollModelList();
                try {
                    ((GridProductViewHolder) holder).setGridProductLayout(gridProductScrollModelList, gridLayoutTitle,position);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            default:
                return;
        }
        if (lastPosition < position){
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition=position;
        }
    }

    @Override
    public int getItemCount() {
        return homePageModelList.size();
    }

    public class BannerSliderViewHolder extends RecyclerView.ViewHolder {

        private ViewPager bannerSliderViewPager;
        private int currentPage ;
        private Timer timer;
        final private long DELAY_TIME = 3000;
        final private long PERIOD_TIME = 3000;
        private List<SliderModel> arrangedList;

        public BannerSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerSliderViewPager = itemView.findViewById(R.id.banner_slider_view_pager);


        }

        private void pageLooper(List<SliderModel> sliderModelList) {
//            if (currentPage == sliderModelList.size() - 2) {
//                currentPage = 2;
//                bannerSliderViewPager.setCurrentItem(currentPage, false);
//            }
            if (currentPage == 1) {
                currentPage = sliderModelList.size() - 3;
                bannerSliderViewPager.setCurrentItem(currentPage, false);
            }
        }

        private void startBannerSlideShow(final List<SliderModel> sliderModelList) {
            final Handler handler = new Handler();
            final Runnable update = new Runnable() {
                @Override
                public void run() {
                    if (currentPage >= sliderModelList.size()) {
                        currentPage = 1;
                    }
                    bannerSliderViewPager.setCurrentItem(currentPage++, true);
                }
            };
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            }, DELAY_TIME, PERIOD_TIME);
        }

        private void stopBannerSliderShow() {
            timer.cancel();
        }

        private void setBannerSliderViewPager(final List<SliderModel> sliderModelList) {
//            currentPage=1;
            if(timer !=null){
                timer.cancel();
            }
//            arrangedList= new ArrayList<>();
//            for(int x=0;x<sliderModelList.size();x++){
//                arrangedList.add(x,sliderModelList.get(x));
//            }
//            arrangedList.add(0,sliderModelList.get(sliderModelList.size()-2));
//            arrangedList.add(1,sliderModelList.get(sliderModelList.size()-1));
//            arrangedList.add(sliderModelList.get(0));
            SliderAdapter sliderAdapter = new SliderAdapter(sliderModelList);
            bannerSliderViewPager.setAdapter(sliderAdapter);
            bannerSliderViewPager.setClipToPadding(false);
            bannerSliderViewPager.setPageMargin(20);




//            bannerSliderViewPager.setCurrentItem(currentPage);
            ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        // pageLooper(arrangedList);
                    }
                }
            };
            bannerSliderViewPager.addOnPageChangeListener(onPageChangeListener);
            startBannerSlideShow(sliderModelList);

            bannerSliderViewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    //  pageLooper(arrangedList);
                    stopBannerSliderShow();
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        startBannerSlideShow(sliderModelList);
                    }
                    return false;
                }
            });
        }


    }

    public class StripAddViewHolder extends RecyclerView.ViewHolder {

        private ImageView stripAddImage;
        private ConstraintLayout stripAddContainer;

        public StripAddViewHolder(@NonNull View itemView) {
            super(itemView);
            stripAddImage = itemView.findViewById(R.id.strip_add_image);
            stripAddContainer = itemView.findViewById(R.id.strip_add_container);
        }

        private void setStripAdd(String resource, String color) {

            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.mipmap.category_placeholder)).into(stripAddImage);
            stripAddContainer.setBackgroundColor(Color.parseColor(color));
        }
    }

    public class HorizontalProductViewHolder extends RecyclerView.ViewHolder{
        private TextView horizontalLayoutTitle;
        private Button horizontalViewAllBtn;
        private RecyclerView horizontalRecyclerView;
        private ConstraintLayout constraintLayout;
        public HorizontalProductViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout=itemView.findViewById(R.id.container);
            horizontalLayoutTitle= itemView.findViewById(R.id.horizontal_scroll_layout_title);
            horizontalViewAllBtn= itemView.findViewById(R.id.horizontal_scroll_viewAll_layout_button);
            horizontalRecyclerView= itemView.findViewById(R.id.horizontal_scroll_recycleview);
            horizontalRecyclerView.setRecycledViewPool(recycledViewPool);
        }

        private void setHorizontalProductLayout(List<HorizontalProductScrollModel> horizontalProductScrollModelList, String title, String color, final int position){

            constraintLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
            horizontalLayoutTitle.setText(title);
            if(horizontalProductScrollModelList.size()>6){
                horizontalViewAllBtn.setVisibility(View.VISIBLE);
                horizontalViewAllBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        if(DBQueries.currentUser !=null){
                        Intent viewAllIntent = new Intent(itemView.getContext(), ViewAllActivity.class);
                        viewAllIntent.putExtra("layout_code",0);
                        viewAllIntent.putExtra("coming_from","CategoryHomePageAdapter");
                        viewAllIntent.putExtra("position",position);
                        viewAllIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                        itemView.getContext().startActivity(viewAllIntent);
//                        }else{
//                            HomeActivity.signInDialog.show();
//                        }

                    }
                });
            }else{
                horizontalViewAllBtn.setVisibility(View.INVISIBLE);
            }
            HorizontalProductScrollAdapter horizontalProductScrollAdapter = new HorizontalProductScrollAdapter(horizontalProductScrollModelList);
            LinearLayoutManager linearLayoutManagerHorizontal = new LinearLayoutManager(itemView.getContext());
            linearLayoutManagerHorizontal.setOrientation(LinearLayoutManager.HORIZONTAL);
            horizontalRecyclerView.setLayoutManager(linearLayoutManagerHorizontal);
            horizontalRecyclerView.setAdapter(horizontalProductScrollAdapter);
            horizontalProductScrollAdapter.notifyDataSetChanged();
        }
    }

    public class GridProductViewHolder extends  RecyclerView.ViewHolder{
        private TextView gridLayoutTitle;
        private  Button gridLayoutViewAllBtn;
        private GridLayout gridProductLayout;
        public GridProductViewHolder(@NonNull View itemView) {
            super(itemView);
            gridLayoutTitle = itemView.findViewById(R.id.grid_product_layout_title);
            gridLayoutViewAllBtn = itemView.findViewById(R.id.grid_product_layout_view_all_btn);
            gridProductLayout = itemView.findViewById(R.id.grid_layout);
        }
        private void setGridProductLayout(final List<HorizontalProductScrollModel> horizontalProductScrollModelList, String title, final int position) throws UnsupportedEncodingException {
            gridLayoutTitle.setText(title);
            if(horizontalProductScrollModelList.size()>=4){
                gridProductLayout.setVisibility(View.VISIBLE);
                gridLayoutTitle.setVisibility(View.VISIBLE);
                gridLayoutViewAllBtn.setVisibility(View.VISIBLE);
            }else{
                gridProductLayout.setVisibility(View.GONE);
                gridLayoutTitle.setVisibility(View.GONE);
                gridLayoutViewAllBtn.setVisibility(View.GONE);
            }
            for(int x=0;x<4;x++){
                ImageView productImage = gridProductLayout.getChildAt(x).findViewById(R.id.horizontal_scroll_product_image);
                final TextView productTitle = gridProductLayout.getChildAt(x).findViewById(R.id.horizontal_scroll_product_title);
//                TextView productDescription = gridProductLayout.getChildAt(x).findViewById(R.id.horizontal_scroll_product_desc);
                TextView productPrice = gridProductLayout.getChildAt(x).findViewById(R.id.horizontal_scroll_price);
                TextView cuttedPrice=gridProductLayout.getChildAt(x).findViewById(R.id.hsl_cutted_price);
                TextView tvDiscount=gridProductLayout.getChildAt(x).findViewById(R.id.hsl_discount);
                FloatingActionButton addToWishlist=gridProductLayout.getChildAt(x).findViewById(R.id.hs_add_to_wishlist_btn);
                if(x<=horizontalProductScrollModelList.size()-1) {
                    if (!horizontalProductScrollModelList.get(x).getProductImage().equals("null")) {

                        Glide.with(itemView.getContext()).load(horizontalProductScrollModelList.get(x).getProductImage()).apply(new RequestOptions().placeholder(R.mipmap.category_placeholder)).into(productImage);
                        if(DBQueries.language.equals("hi")){
                            productTitle.setText(horizontalProductScrollModelList.get(x).getProductHindiName());
                        }else{
                            productTitle.setText(horizontalProductScrollModelList.get(x).getProductTitle());
                        }
//                        productDescription.setText(horizontalProductScrollModelList.get(x).getProductDescription());
                        productPrice.setText(Utility.changeToIndianCurrency(Integer.parseInt(horizontalProductScrollModelList.get(x).getSellingPrice())));
                        cuttedPrice.setText(Utility.changeToIndianCurrency(Integer.parseInt(horizontalProductScrollModelList.get(x).getProductPrice())));
                        int price=Integer.parseInt(horizontalProductScrollModelList.get(x).getProductPrice());
                        int selling_price=Integer.parseInt(horizontalProductScrollModelList.get(x).getSellingPrice());
                        tvDiscount.setText(String.valueOf((int)(((double)(price-selling_price)/(double)price)*100.0))+itemView.getResources().getString(R.string.discount));
                        if(DBQueries.wishList.contains(horizontalProductScrollModelList.get(x).getProductTitle())){
                            addToWishlist.setSupportImageTintList(itemView.getContext().getResources().getColorStateList(R.color.colorPrimary));
                        }else{
                            addToWishlist.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                        }
                        addToWishlist.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int position1=0;
                                for(int count=0;count<(horizontalProductScrollModelList.size());count++){
                                    try {
                                        if(DBQueries.language.equals("hi")){
                                            if(productTitle.getText().toString().trim().equals(horizontalProductScrollModelList.get(count).getProductHindiName().toString().trim())){
                                                position1=count;
                                            }
                                        }else{
                                            if(productTitle.getText().toString().trim().equals(horizontalProductScrollModelList.get(count).getProductTitle().toString().trim())){
                                                position1=count;
                                            }
                                        }
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if(DBQueries.wishList.contains(horizontalProductScrollModelList.get(position1).getProductTitle())){
                                    int index =DBQueries.wishList.indexOf(horizontalProductScrollModelList.get(position1).getProductTitle());
                                    DBQueries.removeFromWishList(index,itemView.getContext());
                                    addToWishlist.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                                }else{
                                    try {
                                        DBQueries.addToWishList(itemView.getContext(),addToWishlist,horizontalProductScrollModelList.get(position1).getProductTitle(),
                                                horizontalProductScrollModelList.get(position1).getProductImage(),
                                                horizontalProductScrollModelList.get(position1).getProductHindiName(),
                                                horizontalProductScrollModelList.get(position1).getProductPrice(),
                                                horizontalProductScrollModelList.get(position1).getSellingPrice(),
                                                true,
                                                horizontalProductScrollModelList.get(position1).getCatalogName(),
                                                horizontalProductScrollModelList.get(position1).getProductCategory());
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                        gridProductLayout.getChildAt(x).setBackgroundColor(Color.parseColor("#ffffff"));
                        if (!title.equals("")) {


                            gridProductLayout.getChildAt( i = x).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int position1=0;
                                    TextView productTitle = view.findViewById(R.id.horizontal_scroll_product_title);
                                    for(int count=0;count<(horizontalProductScrollModelList.size());count++){
                                        try {
                                            if(DBQueries.language.equals("hi")){
                                                if(productTitle.getText().toString().equals(horizontalProductScrollModelList.get(count).getProductHindiName().toString())){
                                                   position1=count;
                                                }
                                            }else{
                                                if(productTitle.getText().toString().equals(horizontalProductScrollModelList.get(count).getProductTitle().toString())){
                                                   position1=count;
                                                }
                                            }
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                                    productDetailsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                    productDetailsIntent.putExtra("product_id", horizontalProductScrollModelList.get(position1).getProductTitle());
                                    try {
                                        productDetailsIntent.putExtra("product_hindi_name", horizontalProductScrollModelList.get(position1).getProductHindiName());
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }

                                    itemView.getContext().startActivity(productDetailsIntent);
                                }
                            });
                        }
                    }
                }

            }
            if(!title.equals("")) {
                gridLayoutViewAllBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        if(DBQueries.currentUser !=null){
                        Intent viewAllIntent = new Intent(itemView.getContext(), ViewAllActivity.class);
                        viewAllIntent.putExtra("coming_from","CategoryHomePageAdapter");
                        viewAllIntent.putExtra("layout_code", 1);
                        viewAllIntent.putExtra("position",position);
                        viewAllIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                        itemView.getContext().startActivity(viewAllIntent);
//                        }else{
//                            HomeActivity.signInDialog.show();
//                        }
                    }
                });
            }

            ////glide code
            // Glide.with(itemView.getContext()).load(horizontalProductScrollModelList)
        }
    }

}
