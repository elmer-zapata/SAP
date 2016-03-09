package com.tierconnect.riot.simulator.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by angelchambi on 3/8/16.
 */
public class Group{
    @JsonProperty("id")
    public int id;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

}
