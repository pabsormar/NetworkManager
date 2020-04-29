package org.deafsapps.android.networkmanager.domain;

import com.google.gson.annotations.SerializedName;

public class JokesCountResponse {

    private String type;
    @SerializedName("value")
    private int jokesCount;

    public JokesCountResponse(String type, int jokesCount) {
        this.type = type;
        this.jokesCount = jokesCount;
    }

    public int getJokesCount() {
        return jokesCount;
    }

}
