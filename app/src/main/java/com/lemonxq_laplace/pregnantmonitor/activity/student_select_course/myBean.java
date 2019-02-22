package com.lemonxq_laplace.pregnantmonitor.activity.student_select_course;

/**
 * Created by 28062 on 2018/6/12.
 */

public class myBean {
    public String text;
    public int ImageID;


    public myBean(String text,int imageID){
        this.ImageID = imageID;
        this.text = text;

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImageID() {
        return ImageID;
    }

    public void setImageID(int imageID) {
        ImageID = imageID;
    }


}
