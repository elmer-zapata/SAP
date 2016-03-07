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

}
