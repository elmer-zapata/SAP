package com.tierconnect.riot.simulator.dao;


import com.tierconnect.riot.simulator.entities.Result;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelchambi on 3/7/16.
 * DAO ZONE
 */
public class ImportDAO extends DAO{

    private String importPath;

    public ImportDAO(String pathField, String fileNameWithEx){
        super("/riot-core-services/api/fileManagement/import/thing");
        importPath = Paths.get(System.getProperty("user.dir"), pathField, fileNameWithEx).toString();
    }

    public ImportDAO(String host, int port, String user, String pathFileImport, String fileNameWithEx){
        super(host, port, ("/riot-core-services/api/fileManagement/import/thing"), user);
        importPath = Paths.get(System.getProperty("user.dir"), pathFileImport, fileNameWithEx).toString();
    }

    public Result importFile(String type, String thingTypeCode, Boolean runRules) throws IOException, URISyntaxException{
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("thingTypeCode", thingTypeCode));
        nameValuePairList.add(new BasicNameValuePair("runRules", runRules.toString()));

        return (Result)httpClientService.httpRequest(nameValuePairList, "POST", importPath, Result.class, type);
    }
}
