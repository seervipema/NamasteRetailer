package com.pemaseervi.myretailer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.pemaseervi.myretailer.Adapter.SearchAdapter;
import com.pemaseervi.myretailer.Firebase.DBQueries;
import com.pemaseervi.myretailer.Model.SearchModel;
import com.pemaseervi.myretailer.utility.CommonApiConstant;
import com.pemaseervi.myretailer.utility.VolleyMultipartRequest;
import com.pemaseervi.myretailer.utility.VolleySingleton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;


public class SearchActivity extends AppCompatActivity {

    private androidx.appcompat.widget.SearchView searchView;
    private TextView tvProductNotFound;
    private RecyclerView recyclerView;
    private Disposable disposable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchView =findViewById(R.id.search_view);
        tvProductNotFound = findViewById(R.id.notification_title);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        final List<SearchModel> list = new ArrayList<>();
        final List<String> ids = new ArrayList<>();

        SearchAdapter searchAdapter = new SearchAdapter(list);
        recyclerView.setAdapter(searchAdapter);
        disposable= fromview(searchView).debounce(150, TimeUnit.MILLISECONDS)
                .filter(text->!text.isEmpty() && text.length()>=1)
                .map(text->text.trim())
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(newText->{
                    JSONObject payload= new JSONObject();
                    JSONObject query= new JSONObject();
                    JSONObject bool =new JSONObject();
                    JSONArray must= new JSONArray();
                    JSONObject mustObject= new JSONObject();
                    JSONObject wildcard = new JSONObject();
                    JSONObject product_name = new JSONObject();

                    try{
                        product_name.put("value","*"+newText+"*");
                        wildcard.put("product_name",product_name);
                        mustObject.put("wildcard",wildcard);
                        must.put(mustObject);
                        bool.put("must",must);
                        query.put("bool",bool);
                        payload.put("query",query);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    String URL= CommonApiConstant.backEndUrl+"/elastic/search";
                    list.clear();
//                    searchAdapter.notifyDataSetChanged();
                    VolleyMultipartRequest multipartRequest1 = new VolleyMultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            String resultResponse = new String(response.data);
                            try {
                                JSONObject result = new JSONObject(resultResponse);
                                int no_of_products=result.getJSONObject("body").getJSONObject("hits").getJSONArray("hits").length();
                                if(  no_of_products>0){
                                    for(int count=0;count<(no_of_products);count++){
                                        String product_name=result.getJSONObject("body").getJSONObject("hits").getJSONArray("hits").getJSONObject(count).getJSONObject("_source").get("product_name").toString();
                                        String product_hindi_name=result.getJSONObject("body").getJSONObject("hits").getJSONArray("hits").getJSONObject(count).getJSONObject("_source").get("product_hindi_name").toString();
                                        String product_image=result.getJSONObject("body").getJSONObject("hits").getJSONArray("hits").getJSONObject(count).getJSONObject("_source").get("product_image").toString();
                                        String price = result.getJSONObject("body").getJSONObject("hits").getJSONArray("hits").getJSONObject(count).getJSONObject("_source").get("price").toString();
                                        Boolean in_stock = result.getJSONObject("body").getJSONObject("hits").getJSONArray("hits").getJSONObject(count).getJSONObject("_source").getBoolean("in_stock");
                                        SearchModel model=new SearchModel(product_image,
                                                product_name,
                                                product_hindi_name,
                                                price,
                                                (Boolean)in_stock
                                        );
                                        list.add(model);
                                        searchAdapter.notifyDataSetChanged();
                                    }
                                }else{
                                    tvProductNotFound.setVisibility(View.VISIBLE);
                                    tvProductNotFound.setText(getResources().getString(R.string.product_not_found));
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
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
                                        DBQueries.signOut(getBaseContext());
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
                            Toast.makeText(SearchActivity.this,errorMessage,Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
                            params.put("payload",payload.toString());
                            return params;
                        }

                    };

                    VolleySingleton.getInstance(SearchActivity.this).addToRequestQueue(multipartRequest1);

                });

//        final Adapter adapter = new Adapter(list);
//        adapter.setFromSearch(true);
//        recyclerView.setAdapter(adapter);

//        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(final String query) {
//                list.clear();
//                ids.clear();
//                final String[] tags = query.toLowerCase().split(" ");
//                for(final String tag:tags){
//                    tag.trim();
////                    FirebaseFirestore.getInstance().collection("PRODUCTS")
////                            .whereArrayContains("tags",tag)
////                            .get()
////                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////                                @Override
////                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                                    if(task.isSuccessful()){
////                                        for(DocumentSnapshot documentSnapshot: task.getResult().getDocuments()){
////                                            WishlistModel model=new WishlistModel(documentSnapshot.get("product_image").toString(),
////                                                    documentSnapshot.get("product_name").toString(),
////                                                    documentSnapshot.get("product_hindi_name").toString(),
////                                                    documentSnapshot.get("price").toString(),
////                                                    (Boolean) documentSnapshot.get("in_stock")
////                                            );
////                                            model.setTags((ArrayList<String>) documentSnapshot.get("tags"));
////                                            if(!ids.contains(model.getProductTitle())){
////                                                list.add(model);
////                                                ids.add(model.getProductTitle());
////
////                                            }
////                                        }
////                                        if(tag.equals(tags[tags.length-1])){
////                                            if(list.size()==0){
////                                                textView.setVisibility(View.VISIBLE);
////                                                recyclerView.setVisibility(View.GONE);
////                                            }else{
////                                                textView.setVisibility(View.GONE);
////                                                recyclerView.setVisibility(View.VISIBLE);
////                                                adapter.getFilter().filter(query);
////                                            }
////
////                                        }
////
////                                    }else{
////                                        Toast.makeText(SearchActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
////                                    }
////                                }
////                            });
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
    }
    private static Observable<String> fromview(androidx.appcompat.widget.SearchView searchView) {
        final PublishSubject<String> subject = PublishSubject.create();

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                subject.onComplete();
                searchView.clearFocus(); //if you want to close keyboard
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                subject.onNext(text);
                return false;
            }
        });

        return subject;
    }
//    class Adapter extends WishlistAdapter implements Filterable{
//        private List<WishlistModel> originalList;
//        public Adapter(List<WishlistModel> wishlistModelList) {
//            super(wishlistModelList);
//            originalList= wishlistModelList;
//        }
//
//        @Override
//        public Filter getFilter() {
//            return new Filter() {
//                @Override
//                protected FilterResults performFiltering(CharSequence charSequence) {
//                    List<WishlistModel> filteredList=new ArrayList<>();;
//                    FilterResults results = new FilterResults();
//                    final String[] tags = charSequence.toString().toLowerCase().split(" ");
//
//                    for(WishlistModel model:originalList){
//                        ArrayList<String> presentTags= new ArrayList<>();
//                        for(String tag:tags){
//                            if(model.getTags().contains(tag)){
//                                presentTags.add(tag);
//                            }
//                        }
//                        model.setTags(presentTags);
//                    }
//                    for(int i = tags.length;i>0;i--){
//                        for(WishlistModel model:originalList){
//                            if(model.getTags().size() == i){
//                                filteredList.add(model);
//                            }
//                        }
//                    }
//                    results.values = filteredList;
//                    results.count = filteredList.size();
//
//
//                    return results;
//                }
//
//                @Override
//                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//                    if(filterResults.count >0){
//                        setWishlistModelList((List<WishlistModel>) filterResults.values);
//                    }
//                   notifyDataSetChanged();
//                }
//            };
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) disposable.dispose();
    }
}
