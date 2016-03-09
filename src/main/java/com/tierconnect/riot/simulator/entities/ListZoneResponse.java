package com.tierconnect.riot.simulator.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by angelchambi on 3/7/16.
 * ListZone Mapper
 */

public class ListZoneResponse{

    @JsonProperty("total")
    private int total;
    @JsonProperty("results")
    private List<Zone> zoneList;

    public int getTotal(){
        return total;
    }

    public void setTotal(int total){
        this.total = total;
    }

    public List<Zone> getZoneList(){
        return zoneList;
    }

    public void setZoneTypeList(List<Zone> zoneList){
        this.zoneList = zoneList;
    }
}
