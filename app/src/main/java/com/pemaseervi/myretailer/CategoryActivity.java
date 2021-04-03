package com.pemaseervi.myretailer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.pemaseervi.myretailer.Adapter.CategoryHomePageAdapter;
import com.pemaseervi.myretailer.Adapter.CategoryPageAdapter;
import com.pemaseervi.myretailer.Firebase.DBQueries;

public class CategoryActivity extends AppCompatActivity {

//    private RecyclerView categoryRecyclerView;
    private GridView categoryGridView;
    public static SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView categoryRecyclerView;
    private CategoryPageAdapter categoryAdapter;
    private RecyclerView categoryPageRecyclerView;
    private CategoryHomePageAdapter categoryHomePageAdapter;
    String  title="";
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(DBQueries.language!=null){
            if(DBQueries.language.equals("hi")){
                title= getIntent().getStringExtra("categoryHindiName");
            }else{
                title= getIntent().getStringExtra("categoryName");
            }
        }
        categoryName=getIntent().getStringExtra("categoryName");
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout= findViewById(R.id.category_refresh_layout);
        categoryRecyclerView= findViewById(R.id.category_recycler_view);
        categoryPageRecyclerView= findViewById(R.id.category_page_recycle_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        categoryRecyclerView.setLayoutManager(linearLayoutManager);
        LinearLayoutManager linearLayoutManager1= new LinearLayoutManager(this);
        linearLayoutManager1.setOrientation(RecyclerView.VERTICAL);
        categoryPageRecyclerView.setLayoutManager(linearLayoutManager1);
        categoryAdapter =  new CategoryPageAdapter(DBQueries.categoryModelListByCatalog);


//        if(DBQueries.categoryModelListByCatalog.size() ==0){
        categoryHomePageAdapter = new CategoryHomePageAdapter(DBQueries.categoryPageModelList);
            DBQueries.loadCategoriesByCatalog(categoryHomePageAdapter,categoryAdapter,this,categoryName);
//            DBQueries.loadCatalogWiseTop8Data(categoryHomePageAdapter,this,2,title);
//        }else{

//        }


//            DBQueries.loadCatalogFragmentData(categoryHomePageAdapter,this,categoryName);

        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryAdapter.notifyDataSetChanged();
        categoryPageRecyclerView.setAdapter(categoryHomePageAdapter);
        categoryHomePageAdapter.notifyDataSetChanged();






//        if(DBQueries.categoryWiseTop8Products.size()==0){
//            DBQueries.loadCategoryWiseTop8Data(categoryPageAdapter,this,2,"Ghamela");
//            DBQueries.loadCategoryWiseTop8Data(categoryPageAdapter,this,3,"Can");
//        }






//        categoryGridView= findViewById(R.id.categorywise_grid_view);
//        DBQueries.categoryWiseProducts.clear();
//        FirebaseFirestore.getInstance().collection("PRODUCTS")
//                .whereEqualTo("product_category",getIntent().getStringExtra("categoryName"))
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
////                            task.getResult().getDocuments().get(0).get("product_name")
//                            int no_of_products=  (int)task.getResult().getDocuments().size();
//                            for(int x=0;x<(no_of_products);x++){
//                                DBQueries.categoryWiseProducts.add(new CategoryWiseProductModel(
//                                        task.getResult().getDocuments().get(x).get("product_name").toString(),
//                                        Integer.parseInt(task.getResult().getDocuments().get(x).get("price").toString()),
//                                        1,
//                                        task.getResult().getDocuments().get(x).get("product_image").toString()
//                                ));
//                            }
//                            if(DBQueries.categoryWiseProducts.size()>0) {
//                                CategoryWiseProductAdapter categoryWiseProductAdapter = new CategoryWiseProductAdapter(DBQueries.categoryWiseProducts);
//                                categoryGridView.setAdapter(categoryWiseProductAdapter);
//                                categoryWiseProductAdapter.notifyDataSetChanged();
//                            }
//                        }else{
//                            String error= task.getException().getMessage();
//                            Toast.makeText(CategoryActivity.this,error,Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

        ////////////swipe refresh layput

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);

//                categoryModelList.clear();
//                homePageModelList.clear();
//                horizontalProductScrollModelList.clear();
                DBQueries.clearData();

                DBQueries.loadCategoriesByCatalog(categoryHomePageAdapter,categoryAdapter,CategoryActivity.this,categoryName);
//                DBQueries.loadCategoryWiseTop8Data(categoryHomePageAdapter,CategoryActivity.this,2,title);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        ////////////swipe refresh layout
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_icon, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.category_search_icon){
            //todo:search icon
            Intent searchIntent= new Intent(CategoryActivity.this, SearchActivity.class);
            searchIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(searchIntent);

            return true;
        }else if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
