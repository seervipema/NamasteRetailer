
package com.pemaseervi.wholesaler.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Desc {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("lang")
    @Expose
    private String lang;
    @SerializedName("val")
    @Expose
    private String val;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

}
