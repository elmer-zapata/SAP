package com.tierconnect.riot.simulator.dao

import com.tierconnect.riot.simulator.entities.Result

/**
 * test Unit Upload Thing File
 * Created by angelchambi on 3/14/16.
 */
class ImportDAOTest extends GroovyTestCase {
    void setUp() {
        super.setUp()

    }

    void tearDown() {

    }

    void testImport() {
        String host = "localhost";
        int port = 8080;
        String user = "root";
        String pathFileImport = "/src/test/resources/csv/";
        String fileNameWithEx = "customer.csv";
        ImportDAO importDAO = new ImportDAO(host, port, user, pathFileImport, fileNameWithEx);
        Result result = importDAO.importFile("thing", "customer_code", false);
        assertNotNull(result);
        assertNotNull(result.getlistResult());
        for (int i = 0; i < result.getlistResult().size(); i++) {
            assertEquals(result.getlistResult().get(i), "Line");
        }
    }
}
