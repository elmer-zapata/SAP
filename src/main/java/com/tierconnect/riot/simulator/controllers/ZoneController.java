package com.tierconnect.riot.simulator.controllers;

import com.tierconnect.riot.simulator.dao.ZoneDAO;
import com.tierconnect.riot.simulator.dao.ZoneTypeDAO;
import com.tierconnect.riot.simulator.entities.ListZoneResponse;
import com.tierconnect.riot.simulator.entities.ListZoneTypeResponse;
import com.tierconnect.riot.simulator.entities.Zone;
import com.tierconnect.riot.simulator.entities.ZoneType;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by angelchambi on 3/7/16.
 * Zone Type Error Controller
 */
public class ZoneController{

    public static void zoneMigration(String host, int port, String user, int groupId)
    throws IOException, URISyntaxException{

        try{
            ZoneDAO zoneDAO= new ZoneDAO();
            ListZoneResponse listZoneResponse = zoneDAO.getAllZones();

            int numberZonesMigrated = 0;
            int numberZonesError = 0;

            for(int i = 0; i < listZoneResponse.getTotal(); i++){
                ZoneDAO zoneDAOTo = new ZoneDAO(host, port, user);
                Zone zoneField = zoneDAOTo.setZone(listZoneResponse.getZoneList().get(i), groupId);
                if (zoneField != null) {
                    System.out.println("Migrate Zone  Success:  Id: "
                                       + zoneField.getId()
                                       + " Name: "
                                       + zoneField.getName());
                    numberZonesMigrated++;
                }
                else {
                    System.out.println("Migrate Zone  Error:  Name: " + listZoneResponse.getZoneList()
                                                                                                .get(i)
                                                                                                .getName());
                    numberZonesError++;
                }
            }
            System.out.println("number Zones Migrated: " + numberZonesMigrated +
                               " and number Zones Error:" + numberZonesError);
        }
        catch(IOException | URISyntaxException exception){
            System.out.println("Error zoneMigration: " + exception.toString());
        }
    }
}
