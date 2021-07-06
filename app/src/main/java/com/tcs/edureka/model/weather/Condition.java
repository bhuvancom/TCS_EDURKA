package com.tcs.edureka.model.weather;

import com.google.gson.annotations.SerializedName;

public class Condition {
    @SerializedName("text")
    private String condition;

    @SerializedName("icon")
    private String imgUrl;

    public Condition() {
    }

    public Condition(String condition, String imgUrl) {
        this.condition = condition;
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "Condition{" +
                "condition='" + condition + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

}
