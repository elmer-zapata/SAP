package com.tierconnect.riot.simulator.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by angelchambi on 3/7/16.
 * Class is to read CSV files.
 */

public class CVSReader{
    public static void parse(String path, String fileNameWithExtension, String cvsSplitBy){
        String csvFile = path + fileNameWithExtension;
        BufferedReader bufferedReader = null;
        String line;

        try{
            bufferedReader = new BufferedReader(new FileReader(csvFile));
            while((line = bufferedReader.readLine()) != null){
                // use the separator
                String[] country = line.split(cvsSplitBy);
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
    }
}
