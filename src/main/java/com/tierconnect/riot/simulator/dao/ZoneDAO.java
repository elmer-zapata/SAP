package com.tierconnect.riot.simulator.dao;

import com.tierconnect.riot.simulator.entities.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelchambi on 3/7/16.
 * DAO ZONE
 */
public class ZoneDAO extends DAO{

    public ZoneDAO(){
        super("/riot-core-services/api/zone");
    }

    public ZoneDAO(String host, int port, String user){
        super(host, port, ("/riot-core-services/api/zone"), user);
    }

    public ListZoneResponse getAllZones() throws IOException, URISyntaxException{

        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("pageSize", "-1"));
        return (ListZoneResponse)httpClientService.httpRequest(nameValuePairList,
                                                               "GET",
                                                               null,
                                                               ListZoneResponse.class,
                                                               "");

    }

    public Zone setZone(Zone zone, int idGroup) throws IOException, URISyntaxException{
        //        Group group = new Group();
        //        group.setId(idGroup);
        //        zoneType.setGroup(group);
        //        zoneType.setZonePropertyList(new ArrayList<ZoneProperty>());
        //        return (ZoneType)httpClientService.httpRequest(null, "PUT", zoneType, ZoneType.class);
        return null;
    }
}
