package com.tierconnect.riot.simulator.dao;

import com.tierconnect.riot.simulator.entities.Group;
import com.tierconnect.riot.simulator.entities.ListZoneTypeResponse;
import com.tierconnect.riot.simulator.entities.ZoneProperty;
import com.tierconnect.riot.simulator.entities.ZoneType;
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
public class ZoneTypeDAO extends DAO{

    public ZoneTypeDAO(){
        super("/riot-core-services/api/zoneType");
    }

    public ZoneTypeDAO(String host, int port, String user){
        super(host, port, ("/riot-core-services/api/zoneType"), user);
    }

    public ListZoneTypeResponse getAllZonesType() throws IOException, URISyntaxException{

        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("pageSize", "-1"));
        return (ListZoneTypeResponse)httpClientService.httpRequest(nameValuePairList,
                                                                   "GET",
                                                                   null,
                                                                   ListZoneTypeResponse.class,
                                                                   "");

    }

    public ZoneType setZoneType(ZoneType zoneType, int idGroup) throws IOException, URISyntaxException{
        Group group = new Group();
        group.setId(idGroup);
        zoneType.setGroup(group);
        zoneType.setZonePropertyList(new ArrayList<ZoneProperty>());
        return (ZoneType)httpClientService.httpRequest(null, "PUT", zoneType, ZoneType.class, "");
    }
}
