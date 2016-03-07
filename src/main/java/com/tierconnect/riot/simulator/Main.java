package com.tierconnect.riot.simulator;

import com.tierconnect.riot.simulator.controllers.ZoneTypeController;

import java.io.IOException;
import java.net.URISyntaxException;


/**
 * Created by angelchambi on 3/7/16.
 * Main Method MIGRATOR DATA
 */
public class Main{

    public static void main(String[] arg) throws IOException, URISyntaxException{
        try{
            ZoneTypeController.zoneTypeMigrator();
        }
        catch(IOException | URISyntaxException e){
            e.printStackTrace();
        }

    }
}