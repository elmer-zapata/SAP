package com.tierconnect.riot.simulator.dao;

import com.tierconnect.riot.simulator.entities.ListZoneTypeResponse;


import java.io.IOException;
import java.net.URISyntaxException;


/**
 * Created by angelchambi on 3/7/16.
 * DAO ZONE
 */
public class ZoneTypeDAO extends DAO{
    protected final String ZONE_END_POINT;

    public ZoneTypeDAO(){
        super();
        ZONE_END_POINT = "/riot-core-services/api/zoneType";
    }

    public ListZoneTypeResponse getAllZonesType() throws IOException, URISyntaxException{

        return (ListZoneTypeResponse)httpClientService.httpRequest(ZONE_END_POINT,
                                                                   null,
                                                                   "GET",
                                                                   null,
                                                                   ListZoneTypeResponse.class);

    }

//    public void setAllZoneType() throws IOException, URISyntaxException{
//        httpClientService.httpRequest(ZONE_END_POINT,)
//    }
}
