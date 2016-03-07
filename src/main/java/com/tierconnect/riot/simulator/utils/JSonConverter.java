package com.tierconnect.riot.simulator.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by angelchambi on 3/7/16.
 * Json Convert StringJson to Object
 * and Object to StringJson
 */
public class JSonConverter{

    public static Object jsonStringToObject(String JsonString, Class clazz) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        //JSON from String to Object
        return mapper.readValue(JsonString, clazz);

    }

    public static String objectToJsonString(Object object) throws IOException{
        ObjectMapper mapper = new ObjectMapper();

        //Object to JSON in String
        return mapper.writeValueAsString(object);
    }
}
