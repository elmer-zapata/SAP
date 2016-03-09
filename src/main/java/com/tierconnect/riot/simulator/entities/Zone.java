package com.tierconnect.riot.simulator.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by angelchambi on 3/8/16.
 */
public class Zone{

    @JsonProperty("id")
    private int id;

    @JsonProperty("color")
    private String color;

    @JsonProperty("description")
    private String description;

    @JsonProperty("name")
    private String name;

    @JsonProperty("code")
    private String code;

    @JsonProperty("zonePoints")
    private List<ZonePoint> zonePointList;


    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getColor(){
        return color;
    }

    public void setColor(String color){
        this.color = color;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getCode(){
        return code;
    }

    public void setCode(String code){
        this.code = code;
    }

    public List<ZonePoint> getZonePointList(){
        return zonePointList;
    }

    public void setZonePointList(List<ZonePoint> zonePointList){
        this.zonePointList = zonePointList;
    }
}
