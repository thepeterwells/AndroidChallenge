package com.paandw.peter.androidchallenge;

/**
 * Java object for Kingdoms pulled from https://challenge2015.myriadapps.com/api/v1/kingdoms
 * Created by Peter Wells on 3/14/2017.
 */

public class Kingdom {
    private int id;
    private String name, image;

    public int getID(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getImage(){
        return image;
    }

    public String toString(){
        return "" + id + "\n" + name + "\n" + image + "\n\n";
    }

}
