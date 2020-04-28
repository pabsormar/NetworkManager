package org.deafsapps.android.networkmanager.domain;

import com.google.gson.annotations.SerializedName;

public class JokeItem {

    private int id;
    private String joke;
    @SerializedName("categories")
    private String[] cats;

    public JokeItem(int id, String joke, String[] cats) {
        this.id = id;
        this.joke = joke;
        this.cats = cats;
    }

    public int getId() {
        return id;
    }

    public String getJoke() {
        return joke;
    }

    public String[] getCats() {
        return cats;
    }

}