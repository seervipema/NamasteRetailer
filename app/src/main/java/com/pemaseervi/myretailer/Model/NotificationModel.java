package com.pemaseervi.myretailer.Model;

public class NotificationModel {
    private String image,body,title,date;
    private boolean readed;

    public NotificationModel(String image,String title, String body, boolean readed, String date) {
        this.image = image;
        this.body = body;
        this.readed = readed;
        this.title=title;
        this.date=date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }
}
