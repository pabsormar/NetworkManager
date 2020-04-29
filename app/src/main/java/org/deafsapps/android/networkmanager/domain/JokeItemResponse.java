package org.deafsapps.android.networkmanager.domain;

import com.google.gson.annotations.SerializedName;

public class JokeItemResponse {

    private String type;
    @SerializedName("value")
    private JokeItem jokeItem;

    public JokeItemResponse(String type, JokeItem value) {
        this.type = type;
        this.jokeItem = value;
    }

    public String getType() {
        return type;
    }

    public JokeItem getJokeItem() {
        return jokeItem;
    }

}