package com.pemaseervi.myretailer.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pemaseervi.myretailer.CategoryWiseAllProductsActivity;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.Model.CategoryModel;
import com.pemaseervi.myretailer.R;

import java.util.List;

public class CategoryPageAdapter extends RecyclerView.Adapter<CategoryPageAdapter.ViewHolder> {

    private List<CategoryModel> categoryModelList;
    private int lastPosition =-1;

    public CategoryPageAdapter(List<CategoryModel> categoryModelList) {
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public CategoryPageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false);


        return new CategoryPageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryPageAdapter.ViewHolder holder, int position) {
        String icon = categoryModelList.get(position).getCategoryIconlink();
        String name = categoryModelList.get(position).getCategoryName();
        String hindiName= categoryModelList.get(position).getCategoryHindiName();
        holder.setCategory(name,hindiName,position);
        holder.setCategoryIcon(icon);
        if (lastPosition < position){
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition=position;
        }

    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView categoryIcon;
        private TextView categoryName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIcon= itemView.findViewById(R.id.category_icon);
            categoryName = itemView.findViewById(R.id.category_text);
        }
        private void setCategoryIcon(String iconUrl){
            if(!iconUrl.equals("")){
                Glide.with(itemView.getContext()).load(iconUrl).apply(new RequestOptions().placeholder(R.mipmap.category_placeholder)).into(categoryIcon);

            }else if(iconUrl.equals("")){
                categoryIcon.setImageResource(R.mipmap.home_icon);
            }
        }
        private void setCategory(final String name,final String hindiName,final int position){
            if(DBQueries.language!=null){
                if(DBQueries.language.equals("hi")){
                    categoryName.setText(hindiName);
                }else{
                    categoryName.setText(name);
                }
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                           DBQueries.loadCategoryWiseData(itemView.getContext());
//                        if(DBQueries.currentUser !=null){
                            Intent categoryIntent = new Intent(itemView.getContext(), CategoryWiseAllProductsActivity.class);
                            categoryIntent.putExtra("categoryName", name);
                            categoryIntent.putExtra("categoryHindiName", hindiName);
                            categoryIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                    itemView.getContext().startActivity(categoryIntent);
//                        }else{
//                            HomeActivity.signInDialog.show();
//                        }


                }
            });
        }
    }
}