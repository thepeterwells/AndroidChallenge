package com.paandw.peter.androidchallenge;

import java.util.ArrayList;
import java.util.List;

/**
 * This gets all the specific information for a kingdom
 * Created by Peter Wells on 3/15/2017.
 */

public class KingdomInfo {
    private int id, population;
    private String name, image, climate;
    private ArrayList<QuestInfo> quests;

    public int getID(){
        return id;
    }

    public int getPopulation(){
        return population;
    }

    public String getName(){
        return name;
    }

    public String getImage(){
        return image;
    }

    public String getClimate(){
        return climate;
    }

    public ArrayList<QuestInfo> getQuests(){
        return quests;
    }
}
