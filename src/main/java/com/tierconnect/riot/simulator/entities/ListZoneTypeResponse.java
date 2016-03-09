package com.tierconnect.riot.simulator.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by angelchambi on 3/7/16.
 * ListZone Mapper
 */

public class ListZoneTypeResponse{

    @JsonProperty("total")
    private int total;
    @JsonProperty("results")
    private List<ZoneType> zoneTypeList;

    public int getTotal(){
        return total;
    }

    public void setTotal(int total){
        this.total = total;
    }

    public List<ZoneType> getZoneTypeList(){
        return zoneTypeList;
    }

    public void setZoneTypeList(List<ZoneType> zoneTypeList){
        this.zoneTypeList = zoneTypeList;
    }
}
