package com.tierconnect.riot.simulator.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by angelchambi on 3/8/16.
 * Zone Entity
 */
public class ZoneProperty{

    @JsonProperty("name")
    private String name;
    @JsonProperty("type")
    private int type;
    @JsonProperty("enabled")
    private Boolean enabled;
}
