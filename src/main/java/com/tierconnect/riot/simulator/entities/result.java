package com.tierconnect.riot.simulator.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Entity Result
 * Created by angelchambi on 3/14/16.
 */
public class Result{

    @JsonProperty("results")
    List<String> listResult;

    public List<String> getlistResult(){
        return listResult;
    }

    public void setlistResult(List<String> listResult){
        this.listResult = listResult;
    }

}
