package org.deafsapps.android.networkmanager.domain;

import com.google.gson.annotations.SerializedName;

public class JokeCategoriesResponse {

    private String type;
    @SerializedName("value")
    private String[] categories;

    public JokeCategoriesResponse(String type, String[] categories) {
        this.type = type;
        this.categories = categories;
    }

    public String[] getCategories() {
        return categories;
    }

}