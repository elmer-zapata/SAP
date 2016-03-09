package com.tierconnect.riot.simulator.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


/**
 * Created by angelchambi on 3/7/16.
 * Zone Type Entity
 */

public class ZoneType{

    @JsonProperty("id")
    private int id;

    @JsonProperty("zoneTypeCode")
    private String zoneTypeCode;

    @JsonProperty("description")
    private String description;

    @JsonProperty("name")
    private String name;

    @JsonProperty("zoneProperties")
    private List<ZoneProperty> zonePropertyList;

    @JsonProperty("group")
    private Group group;



    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getZoneTypeCode(){
        return this.zoneTypeCode;
    }

    public void setZoneTypeCode(String zoneTypeCode){
        this.zoneTypeCode = zoneTypeCode;
    }

    public String getDescription(){
        return this.description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public List<ZoneProperty> getZonePropertyList(){
        return zonePropertyList;
    }

    public void setZonePropertyList(List<ZoneProperty> zonePropertyList){
        this.zonePropertyList = zonePropertyList;
    }

    public Group getGroup(){
        return group;
    }

    public void setGroup(Group group){
        this.group = group;
    }

}
