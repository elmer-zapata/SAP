package com.tierconnect.riot.simulator.utils

/**
 * Unit Test Load XLS.
 * Created by angelchambi on 3/15/16.
 */
class XLSReaderTest extends GroovyTestCase {
    void setUp() {
        super.setUp()

    }

    void tearDown() {

    }

    void testLoadXls() {
        String pathField = "/src/test/resources/xls/";
        String fileName = "graph_matrix.xls";
        XLSReader xlsReader = new XLSReader(pathField, fileName);
        //List<String[]> result =
        xlsReader.loadXls("graph_matrix");
    }
}