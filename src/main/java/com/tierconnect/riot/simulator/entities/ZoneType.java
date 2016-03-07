package com.tierconnect.riot.simulator.entities;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by angelchambi on 3/7/16.
 */

public class ZoneType{

    @JsonProperty("id")
    private int id;
    @JsonProperty("zoneTypeCode")
    public String zoneTypeCode;
    @JsonProperty("description")
    public String description;
    @JsonProperty("name")
    public String name;
}
