package com.tierconnect.riot.simulator.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by angelchambi on 3/7/16.
 * Static Class with convert Response HTTP to String
 */
public class HttpResponseConverter{
    protected static String convertStreamToString(InputStream is){

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try{
            while((line = reader.readLine()) != null){
                sb.append(line).append("\n");
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        finally{
            try{
                is.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
