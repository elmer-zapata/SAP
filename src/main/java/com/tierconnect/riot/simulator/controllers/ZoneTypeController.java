package com.tierconnect.riot.simulator.controllers;

import com.tierconnect.riot.simulator.dao.ZoneTypeDAO;
import com.tierconnect.riot.simulator.entities.ListZoneTypeResponse;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by angelchambi on 3/7/16.
 * Zone Type Error Controller
 */
public class ZoneTypeController{

    public static void zoneTypeMigrator() throws IOException, URISyntaxException{

        try{
            ZoneTypeDAO zoneTypeDAOFrom = new ZoneTypeDAO();
            ListZoneTypeResponse listZoneTypeResponse = zoneTypeDAOFrom.getAllZonesType();
            System.out.print("asdasd" + listZoneTypeResponse.toString());
            //            ZoneTypeDAO zoneTypeDAOTo = new ZoneTypeDAO();
            //            zoneTypeDAOTo.
        }
        catch(IOException | URISyntaxException exception){
            System.out.println("Error:" + exception.toString());
        }
    }
}
