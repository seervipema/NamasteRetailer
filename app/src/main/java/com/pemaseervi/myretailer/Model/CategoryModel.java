package com.pemaseervi.myretailer.Model;

public class CategoryModel {
    private String CategoryIconlink;
    private String categoryName,categoryHindiName;

    public CategoryModel(String categoryIconlink, String categoryName, String categoryHindiName) {
        CategoryIconlink = categoryIconlink;
        this.categoryName = categoryName;
        this.categoryHindiName=categoryHindiName;
    }

    public String getCategoryIconlink() {
        return CategoryIconlink;
    }

    public void setCategoryIconlink(String categoryIconlink) {
        CategoryIconlink = categoryIconlink;
    }

    public String getCategoryHindiName() {
        return categoryHindiName;
    }

    public void setCategoryHindiName(String categoryHindiName) {
        this.categoryHindiName = categoryHindiName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
