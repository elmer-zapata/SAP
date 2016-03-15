package com.tierconnect.riot.simulator;

import com.tierconnect.riot.simulator.controllers.ImportController;
import com.tierconnect.riot.simulator.controllers.ZoneController;
import com.tierconnect.riot.simulator.controllers.ZoneTypeController;

import java.io.IOException;
import java.net.URISyntaxException;


/**
 * Created by angelchambi on 3/7/16.
 * Main Method MIGRATOR DATA
 */
public class Main{

    public static void main(String[] arg) throws IOException, URISyntaxException{

        final String TO_HOST = "localhost";
        final int TO_PORT = 8080;
        final String TO_USER = "root";
        final int GROUP_ID = 9;
        final String PATH_IMPORT = "/src/test/resources/csv/";
        final String THING_TYPE = "thing";


        try{
            ZoneTypeController.zoneTypeMigration(TO_HOST, TO_PORT, TO_USER, GROUP_ID);
            ZoneController.zoneMigration(TO_HOST, TO_PORT, TO_USER, GROUP_ID);
            ImportController.importFiles(TO_HOST, TO_PORT, TO_USER, PATH_IMPORT, THING_TYPE, false);

        }
        catch(IOException | URISyntaxException e){
            e.printStackTrace();
        }
    }
}