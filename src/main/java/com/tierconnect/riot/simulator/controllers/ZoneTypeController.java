package com.tierconnect.riot.simulator.controllers;

import com.tierconnect.riot.simulator.dao.ZoneTypeDAO;
import com.tierconnect.riot.simulator.entities.ListZoneTypeResponse;
import com.tierconnect.riot.simulator.entities.ZoneType;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by angelchambi on 3/7/16.
 * Zone Type Error Controller
 */
public class ZoneTypeController{

    public static void zoneTypeMigration(String host, int port, String user, int groupId)
    throws IOException, URISyntaxException{

        try{
            ZoneTypeDAO zoneTypeDAOFrom = new ZoneTypeDAO();
            ListZoneTypeResponse listZoneTypeResponse = zoneTypeDAOFrom.getAllZonesType();

            int numberZonesMigrated = 0;
            int numberZonesError = 0;

            for(int i = 0; i < listZoneTypeResponse.getTotal(); i++){
                ZoneTypeDAO zoneTypeDAOTo = new ZoneTypeDAO(host, port, user);
                ZoneType zoneTypeField = zoneTypeDAOTo.setZoneType(listZoneTypeResponse.getZoneTypeList().get(i),
                                                                   groupId);
                if (zoneTypeField != null) {
                    System.out.println("Migrate Zone Type Success:  Id: "
                                       + zoneTypeField.getId()
                                       + " Name: "
                                       + zoneTypeField.getName());
                    numberZonesMigrated++;
                }
                else {
                    System.out.println("Migrate Zone Type Error:  Name: " + listZoneTypeResponse.getZoneTypeList()
                                                                                                .get(i)
                                                                                                .getName());
                    numberZonesError++;
                }
            }
            System.out.println("number Zones Types Migrated: " + numberZonesMigrated +
                               " and number Zones Types Error:" + numberZonesError);
        }
        catch(IOException | URISyntaxException exception){
            System.out.println("Error zoneTypeMigration :" + exception.toString());
        }
    }
}
