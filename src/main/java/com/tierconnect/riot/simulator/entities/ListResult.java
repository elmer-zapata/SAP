package com.tierconnect.riot.simulator.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelchambi on 3/7/16.
 * ListResult Mapper
 */

public class ListResult{

    @JsonProperty("total") private int total;
    @JsonProperty("results") private List<Result> resultList;

    public int getTotal(){
        return total;
    }

    public void setTotal(int total){
        this.total = total;
    }

    public List<Result> getResultList(){
        return resultList;
    }

    public void setResultList(List<Result> resultList){
        this.resultList = resultList;
    }

    public ListResult(){
        this.total = 0;
        this.resultList = new ArrayList<>();
    }
}
