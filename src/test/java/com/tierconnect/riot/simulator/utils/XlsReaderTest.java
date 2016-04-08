package com.tierconnect.riot.simulator.utils;

import com.sun.tools.javac.util.Convert;
import org.apache.commons.lang.math.NumberUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test to Load Xls
 * Created by angelchambi on 3/24/16.
 */
public class XlsReaderTest{

    @Before
    public void setUp() throws Exception{

    }

    @After
    public void tearDown() throws Exception{

    }

    @Test
    public void testLoadXls() throws Exception{
        String pathField = "/src/test/resources/xls/";
        String fileName = "graph_matrix.xls";
        XlsReader xlsReader = new XlsReader(pathField, fileName);
        StringBuilder sb = new StringBuilder();
        sb.append("Accessories=5-line-");
        sb.append("Black and white=4-line-");
        sb.append("Blue section=5-line-");
        sb.append("Casual Wear=5-line-");
        sb.append("Coats=5-line-");
        sb.append("Cocktail=3-line-");
        sb.append("Dresses=4-line-");
        sb.append("Dresses Fall Collection=3-line-");
        sb.append("Esscentials=4-line-");
        sb.append("Evening cloathing=4-line-");
        sb.append("Fitting Room 1=5-line-");
        sb.append("Fitting Room 2=2-line-");
        sb.append("Gifts 2=6-line-");
        sb.append("High heels=4-line-");
        sb.append("Hosiery=4-line-");
        sb.append("Jackets=7-line-");
        sb.append("Jeans=4-line-");
        sb.append("Large Sweaters=5-line-");
        sb.append("Leather pants=5-line-");
        sb.append("Main Entrance=3-line-");
        sb.append("Main Exit=3-line-");
        sb.append("Market 1=5-line-");
        sb.append("Market 2=5-line-");
        sb.append("Mens wear=2-line-");
        sb.append("Mens Wear 2=2-line-");
        sb.append("Mens wear 3=4-line-");
        sb.append("Office=4-line-");
        sb.append("Pants=5-line-");
        sb.append("Seasonal=6-line-");
        sb.append("Skirts Fall Collection=4-line-");
        sb.append("Sweaters Fall Collection=4-line-");
        String[] lines = sb.toString().split("-line-");
        Map<String, String> mapZones = new HashMap<>();
        for(String pair : lines)                        //iterate over the pairs
        {
            String[] entry = pair.split("=");                   //split the pairs to get key and value
            mapZones.put(entry[0].trim(), entry[1].trim());          //add them to the hashmap and trim whitespaces
        }
        List<List<String>> result = xlsReader.loadXls("graph_matrix");
        Assert.assertEquals(result.size(), 32);
        for(List<String> item : result){
            int sumRow = 0;
            String zoneName = "";
            for(String stringItem : item){
                if (NumberUtils.isNumber(stringItem)) {
                    sumRow += NumberUtils.toDouble(stringItem);
                }
                else if (stringItem.compareTo("i/j") != 0) {
                    zoneName = stringItem;
                }
            }
            Assert.assertEquals(item.size(), 32);
            if (sumRow != 0) {
                Assert.assertEquals(NumberUtils.toDouble(mapZones.get(zoneName)), sumRow, 0);
            }

        }
        Assert.assertEquals(result.size(), 32);
    }


}