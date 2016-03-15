package com.tierconnect.riot.simulator.utils

import com.tierconnect.riot.simulator.utils.CVSReader
import org.junit.Test

/**
 * Created by angelchambi on 3/7/16.
 * Test to upload CVS
 */
class CVSReaderTest extends GroovyTestCase {
    void setUp() {
        super.setUp();
    }

    void tearDown() {

    }

    @Test
    void testParse() {
        String pathField = "/src/test/resources/csv/";
        String fileName = "customers.csv";
        CVSReader cvsReader = new CVSReader(pathField, fileName, ",", true);
        List<String[]> result = cvsReader.parse();
        assertEquals(result.size(), 25);
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).length, 11);
            assertEquals((result.get(i))[0], "product_code");
            assertEquals((result.get(i))[1], "Product" + (i + 1));
            assertEquals((result.get(i))[2], "Product" + (i + 1));
            assertEquals((result.get(i))[3], ">ViZix.retail>Retail.Main.Store");
            assertEquals((result.get(i))[4], "");
            assertEquals((result.get(i))[5], "");
            assertEquals((result.get(i))[6], "");
            assertEquals((result.get(i))[7], "");
            assertEquals((result.get(i))[8], "");
            assertEquals((result.get(i))[9], "");
            assertEquals((result.get(i))[10], "SKU" + String.format("%03d", (i + 1)));
        }
    }

    @Test
    void testParseWithOutSeparator() {
        String pathField = "/src/test/resources/csv/";
        String fileName = "customers.csv";
        CVSReader cvsReader = new CVSReader(pathField, fileName, "", true);
        List<String[]> result = cvsReader.parse();
        assertEquals(result.size(), 25);
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).length, 11);
            assertEquals((result.get(i))[0], "product_code");
            assertEquals((result.get(i))[1], "Product" + (i + 1));
            assertEquals((result.get(i))[2], "Product" + (i + 1));
            assertEquals((result.get(i))[3], ">ViZix.retail>Retail.Main.Store");
            assertEquals((result.get(i))[4], "");
            assertEquals((result.get(i))[5], "");
            assertEquals((result.get(i))[6], "");
            assertEquals((result.get(i))[7], "");
            assertEquals((result.get(i))[8], "");
            assertEquals((result.get(i))[9], "");
            assertEquals((result.get(i))[10], "SKU" + String.format("%03d", (i + 1)));

        }
    }

    @Test
    void testParseWithOther() {
        String pathField = "/src/test/resources/csv/";
        String fileName = "customersSeparator-.csv";
        CVSReader cvsReader = new CVSReader(pathField, fileName, "-", true);
        List<String[]> result = cvsReader.parse();
        assertEquals(result.size(), 4);
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i).length, 11);
        }
    }

    @Test
    void testParseFileWithSpaces() {
        String pathField = "/src/test/resources/csv/";
        String fileName = "customersWithSpaces.csv";
        CVSReader cvsReader = new CVSReader(pathField, fileName, ",", true);
        List<String[]> result = cvsReader.parse();
        assertEquals(result.size(), 4);
    }

    @Test
    void testParseFileHeadEmptyAndSpaces() {
        String pathField = "/src/test/resources/csv/";
        String fileName = "customHeaderEmptyAndSpaces.csv";
        CVSReader cvsReader = new CVSReader(pathField, fileName, ",", true);
        List<String[]> result = cvsReader.parse();
        assertEquals(result.size(), 3);
    }

    @Test
    void testParseFileHeadEmptyAndSpacesHeaderFalse() {
        String pathField = "/src/test/resources/csv/";
        String fileName = "customHeaderEmptyAndSpaces.csv";
        CVSReader cvsReader = new CVSReader(pathField, fileName, ",", false);
        List<String[]> result = cvsReader.parse();
        assertEquals(result.size(), 3);
    }
}
