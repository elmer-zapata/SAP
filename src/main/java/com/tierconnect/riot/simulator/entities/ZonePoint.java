package com.tierconnect.riot.simulator.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by angelchambi on 3/8/16.
 */
public class ZonePoint{

    @JsonProperty("id")
    private int id;

    @JsonProperty("arrayIndex")
    private int arrayIndex;

    @JsonProperty("x")
    private Double x;

    @JsonProperty("y")
    private Double y;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getArrayIndex(){
        return arrayIndex;
    }

    public void setArrayIndex(int arrayIndex){
        this.arrayIndex = arrayIndex;
    }

    public Double getX(){
        return x;
    }

    public void setX(Double x){
        this.x = x;
    }

    public Double getY(){
        return y;
    }

    public void setY(Double y){
        this.y = y;
    }
}
