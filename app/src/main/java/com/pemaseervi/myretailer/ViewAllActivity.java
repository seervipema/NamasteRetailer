package com.pemaseervi.myretailer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.pemaseervi.myretailer.Adapter.GridProductLayoutAdapter;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.utility.CommonApiConstant;

import static com.pemaseervi.myretailer.Firebase.DBQueries.loadWishList;
import static com.pemaseervi.myretailer.Firebase.DBQueries.wishlistModelList;

public class ViewAllActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridView gridView;
    private Dialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.view_all_activity_heading);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView= findViewById(R.id.recycler_view);
        gridView =findViewById(R.id.grid_view);
        loadingDialog= new Dialog(ViewAllActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        recyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        int layout_code= getIntent().getIntExtra("layout_code",-1);
        int position = getIntent().getIntExtra("position",-1);
        String coming_from=getIntent().getStringExtra("coming_from");

        if(CommonApiConstant.currentUser !=null){
            if(wishlistModelList.size() ==0){
                DBQueries.wishList.clear();
                loadWishList(ViewAllActivity.this, loadingDialog, true);
            }else{
                loadingDialog.dismiss();
            }
        }

//        if(layout_code == 0) {
            if(!coming_from.equals("CategoryHomePageAdapter")){
                gridView.setVisibility(View.VISIBLE);
                GridProductLayoutAdapter adapter1 = new GridProductLayoutAdapter(DBQueries.catalogWiseTop8ProductsArrayList.get(position));
                gridView.setAdapter(adapter1);
                adapter1.notifyDataSetChanged();
                loadingDialog.dismiss();
            }else{
                gridView.setVisibility(View.VISIBLE);
                GridProductLayoutAdapter adapter1 = new GridProductLayoutAdapter(DBQueries.categoryWiseTop8ProductsArrayList.get(position));
                gridView.setAdapter(adapter1);
                adapter1.notifyDataSetChanged();
                loadingDialog.dismiss();
            }
//        }else {
//            gridView.setVisibility(View.VISIBLE);
//
//            GridProductLayoutAdapter gridProductLayoutAdapter = new GridProductLayoutAdapter(DBQueries.homePageModelList.get(position).getHorizontalProductScrollModelList());
//            gridView.setAdapter(gridProductLayoutAdapter);
//            gridProductLayoutAdapter.notifyDataSetChanged();
//            loadingDialog.dismiss();
//        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();
        if(id== android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
