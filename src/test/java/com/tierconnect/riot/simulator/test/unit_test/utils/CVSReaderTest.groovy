package com.tierconnect.riot.simulator.test.unit_test.utils

import com.tierconnect.riot.simulator.utils.CVSReader
import org.junit.Test

/**
 * Created by angelchambi on 3/7/16.
 * Test to upload CVS
 */
class CVSReaderTest extends GroovyTestCase {
    void setUp() {
        super.setUp()

    }

    void tearDown() {

    }

    @Test
    void testParse() {
        CVSReader cvsReader = new CVSReader();
        String PATH_CSV = "/Users/angelchambi/Dev/java/riot-simulator/FMCSAP/src/test/java/resources/csv/";
        cvsReader.parse(PATH_CSV, "customer.csv", ",");
    }
}
