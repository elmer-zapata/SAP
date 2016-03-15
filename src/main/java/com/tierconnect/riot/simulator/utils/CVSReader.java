package com.tierconnect.riot.simulator.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by angelchambi on 3/7/16.
 * Class is to read CSV files.
 */

public class CVSReader{
    private String cvsFullPath;
    private String cvsSplitBy;
    private Boolean firstLineHeader;

    public CVSReader(String path, String fileNameWithEx, String cvsSplitByField, Boolean firstLineHeaderField){

        cvsSplitBy = cvsSplitByField;
        firstLineHeader = firstLineHeaderField;
        cvsFullPath = Paths.get(System.getProperty("user.dir"), path, fileNameWithEx).toString();
    }

    public List<String[]> parse(){

        BufferedReader bufferedReader = null;
        List<String[]> parseFile = null;
        String line;

        try{
            bufferedReader = new BufferedReader(new FileReader(cvsFullPath));
            parseFile = new ArrayList<>();
            while((line = bufferedReader.readLine()) != null){
                if (firstLineHeader) {
                    firstLineHeader = false;
                    continue;
                }
                if (! line.equals("")) {
                    String[] arrayStringLine = line.split((cvsSplitBy.equals(""))?",":cvsSplitBy);
                    parseFile.add(arrayStringLine);
                }
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        finally{
            if (bufferedReader != null) {
                try{
                    bufferedReader.close();
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        return parseFile;
    }
}
