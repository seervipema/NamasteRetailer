
package com.pemaseervi.wholesaler.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParentCategory {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("val")
    @Expose
    private String val;
    @SerializedName("lang")
    @Expose
    private String lang;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

}
