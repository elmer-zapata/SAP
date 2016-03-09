package com.tierconnect.riot.simulator.dao;


import com.tierconnect.riot.simulator.utils.HttpClientService;

/**
 * Created by angelchambi on 3/7/16.
 * Generic Class to create DAO conection to REST Service
 */
public class DAO{

    protected HttpClientService httpClientService;
    protected final String END_POINT;

    public DAO(String endPointField){
        END_POINT = endPointField;
        httpClientService = new HttpClientService("saturn.mojix.com", 8080, END_POINT, "root");
    }

    public DAO(String host, int port, String endPointField, String user){
        END_POINT = endPointField;
        httpClientService = new HttpClientService(host, port, END_POINT, user);
    }
}
