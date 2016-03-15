package com.tierconnect.riot.simulator.controllers

import com.tierconnect.riot.simulator.dao.ImportDAO
import com.tierconnect.riot.simulator.entities.ListResult
import com.tierconnect.riot.simulator.entities.Result
import org.hamcrest.CoreMatchers
import org.junit.Assert
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

/**
 * Created by angelchambi on 3/15/16.
 */
class ImportControllerTest extends GroovyTestCase {
    void setUp() {
        super.setUp()

    }

    void tearDown() {

    }

    void testImportFiles() {
        String host = "localhost";
        int port = 8080;
        String user = "root";
        String pathFileImport = "/src/test/resources/csv/";

        ListResult listResult = ImportController.importFiles(host, pathFileImport, user, pathFileImport, "thing", false);

        assertNotNull(listResult);
        assertNotNull(listResult.getTotal());
        assertNotNull(listResult.getResultList());
        List<Result> resultList = listResult.getResultList();
        for (int i = 0; i < listResult.getTotal(); i++) {
            Result result = resultList.get(i);
            for (int j = 0; j < result.getlistResult().size(); j++) {
                assertThat(result.getlistResult().get(j), containsString("foo"));
            }
        }
    }
}
