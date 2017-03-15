package com.paandw.peter.androidchallenge;

/**
 * Detailed information for individual quests
 * Created by Peter Wells on 3/15/2017.
 */

public class QuestInfo {
    private int id;
    private String name, description, image;
    private QuestGiver giver;

    public int getID(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getImage(){
        return image;
    }

    public String getDescription(){
        return description;
    }

    public QuestGiver getGiver(){
        return giver;
    }
}
