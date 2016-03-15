package com.tierconnect.riot.simulator.controllers;

import com.tierconnect.riot.simulator.dao.ImportDAO;
import com.tierconnect.riot.simulator.entities.ListResult;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;


/**
 * Controlller import files
 * Created by angelchambi on 3/15/16.
 */
public class ImportController{

    //    String host, int port, String user, String pathField, String fileNameWithEx
    public static ListResult importFiles(String host,
                                         int port,
                                         String user,
                                         String pathFileImport,
                                         String type,
                                         Boolean rules) throws IOException, URISyntaxException{
        ListResult listResult = null;
        try{

            File folder = new File(pathFileImport);
            File[] listOfFiles = folder.listFiles();
            listResult = new ListResult();
            for(int i = 0; i < listOfFiles.length; i++){
                if (listOfFiles[i].isFile()) {
                    System.out.println("File upload to ViZix: " + listOfFiles[i].getName());
                    ImportDAO importDAO = new ImportDAO(host, port, user, pathFileImport, listOfFiles[i].getName());
                    listResult.getResultList().add(importDAO.importFile(type, listOfFiles[i].getName(), rules));
                    listResult.setTotal(listResult.getTotal() + 1);
                }
                else if (listOfFiles[i].isDirectory()) {
                    System.out.println("Directory " + listOfFiles[i].getName());
                }
            }
        }
        catch(IOException | URISyntaxException exception){
            System.out.println("Error ImportFiles: " + exception.toString());

        }
        return listResult;
    }
}

