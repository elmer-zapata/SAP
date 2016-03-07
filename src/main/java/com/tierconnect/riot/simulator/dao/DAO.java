package com.tierconnect.riot.simulator.dao;


import com.tierconnect.riot.simulator.utils.HttpClientService;

/**
 * Created by angelchambi on 3/7/16.
 * Generic Class to create DAO conection to REST Service
 */
public class DAO{

    protected HttpClientService httpClientService;

    public DAO(){
        httpClientService = new HttpClientService("saturn.mojix.com", 8080, "root");
    }

    public DAO(String host, int port, String user){
        httpClientService = new HttpClientService(host, port, user);
    }
}
