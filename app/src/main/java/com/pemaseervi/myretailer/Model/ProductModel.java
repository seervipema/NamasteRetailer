
package com.pemaseervi.wholesaler.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductModel {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("product_name")
    @Expose
    private List<ProductName> productName = null;
    @SerializedName("product_category")
    @Expose
    private List<ProductCategory> productCategory = null;
    @SerializedName("parent_category")
    @Expose
    private List<ParentCategory> parentCategory = null;
    @SerializedName("desc")
    @Expose
    private List<Desc> desc = null;
    @SerializedName("assets")
    @Expose
    private List<Object> assets = null;
    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("product_quantity")
    @Expose
    private Integer productQuantity;
    @SerializedName("product_stock_count")
    @Expose
    private Integer productStockCount;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ProductName> getProductName() {
        return productName;
    }

    public void setProductName(List<ProductName> productName) {
        this.productName = productName;
    }

    public List<ProductCategory> getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(List<ProductCategory> productCategory) {
        this.productCategory = productCategory;
    }

    public List<ParentCategory> getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(List<ParentCategory> parentCategory) {
        this.parentCategory = parentCategory;
    }

    public List<Desc> getDesc() {
        return desc;
    }

    public void setDesc(List<Desc> desc) {
        this.desc = desc;
    }

    public List<Object> getAssets() {
        return assets;
    }

    public void setAssets(List<Object> assets) {
        this.assets = assets;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }

    public Integer getProductStockCount() {
        return productStockCount;
    }

    public void setProductStockCount(Integer productStockCount) {
        this.productStockCount = productStockCount;
    }

}
