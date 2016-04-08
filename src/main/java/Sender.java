

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.omg.CORBA.UserException;

import java.io.*;
import java.sql.*;
import java.util.*;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * Created by ezapata on 08-Oct-15.
 */
public class Sender {
    static DefaultHttpClient client;

    static {
        TrustStrategy acceptingTrustStrategy = new TrustStrategy() {

            @Override
            public boolean isTrusted(X509Certificate[] certificate, String authType) {
                return true;
            }
        };
        SSLSocketFactory sf = null;
        try {
            sf = new SSLSocketFactory(acceptingTrustStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", 8080, sf));
        registry.register(new Scheme("http", 50000, sf));
        registry.register(new Scheme("https", 443, sf));
        registry.register(new Scheme("https", 50001, sf));
        ClientConnectionManager ccm = new PoolingClientConnectionManager(registry);
        client = new DefaultHttpClient(ccm);
    }


    public static Map getSomething(String endpoint) throws IOException {

        System.out.println(endpoint);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(endpoint);
        ObjectMapper objectMapper = new ObjectMapper();
        request.addHeader("content-type", "application/json");
        request.addHeader("Api_key", "root");
        HttpResponse response = client.execute(request);
        Map finalResult = null;
        System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
        HttpEntity entity = response.getEntity();
        StringBuffer responseStringBuffer = new StringBuffer();
        if (entity != null && entity.getContent() != null) {
            BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                responseStringBuffer.append(inputLine);
            }
            in.close();
        }
        String responseString = responseStringBuffer.toString();
        if (StringUtils.isNotBlank(responseString)) {
            Map<String, Object> mapResult;
            try {
                mapResult = objectMapper.readValue(responseString, HashMap.class);
                finalResult = mapResult;
            } catch (Exception ex) {
            }
        }
        return finalResult;
    }

    public static void patchSomething(String endpoint, Map message) throws IOException {


        HttpClient client = HttpClientBuilder.create().build();
        HttpPatch request = new HttpPatch(endpoint);

        ObjectMapper objectMapper = new ObjectMapper();
        // add request header
        // request.addHeader("User-Agent", USER_AGENT);
        request.addHeader("content-type", "application/json");
        request.addHeader("Api_key", "root");
        String jsonBody = "";
        if (message != null) {
            System.out.println("el mensage no es null");
            jsonBody = objectMapper.writeValueAsString(message);
            //System.out.println("json en el patch"+jsonBody);
            //jsonBody=jsonBody.replace("=",":");
        }
        request.setEntity(new StringEntity(jsonBody));

        HttpResponse response = client.execute(request);
        Map finalResult = null;
        boolean error = false;
        String errorMessage = null;
        System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
        HttpEntity entity = response.getEntity();
        StringBuffer responseStringBuffer = new StringBuffer();
        if (entity != null && entity.getContent() != null) {
            BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                responseStringBuffer.append(inputLine);
            }
            in.close();
        }
        String responseString = responseStringBuffer.toString();
        int statusCode = response.getStatusLine().getStatusCode();
        if (StringUtils.isNotBlank(responseString)) {
            Map<String, Object> mapResult = new HashMap<String, Object>();
            try {
                mapResult = objectMapper.readValue(responseString, HashMap.class);
                //        System.out.print("entro");
                finalResult = mapResult;


            } catch (Exception ex) {
                error = true;
                errorMessage = ex.getMessage();
            }
        } else {
            error = true;
            errorMessage = "Empty Response from SAP";
        }

        List<Map<String, Object>> objlist = (List) finalResult.get("results");
        //        Map<String,Object> es=objlist.get(0);
        // System.out.print(es.get("zonePoints"));

    }

    public static void modifyZone(String host,
                                  String port,
                                  String group,
                                  String name,
                                  String serialNumber,
                                  String thingTypeCode,
                                  String zonaName,
                                  String idThing,
                                  String timePush) {

        try {
            Map message = new HashMap<>();
            Map messageDetail = new HashMap<>();
            Map messageDetailZone = new HashMap<>();
            message.put("group", group);
            message.put("name", name);
            message.put("serialNumber", serialNumber);
            message.put("thingTypeCode", thingTypeCode);
            message.put("udfs", messageDetail);
            message.put("time", timePush);
            messageDetail.put("zone", messageDetailZone);
            messageDetailZone.put("value", zonaName);
            messageDetailZone.put("time", timePush);


            System.out.println("Changing zone of thing " + message);
            patchSomething("http://" + host + ":" + port + "/riot-core-services/api/thing/" + idThing, message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void modifyUdfString(String host,
                                       String port,
                                       String group,
                                       String name,
                                       String thingTypeCode,
                                       String zonaName,
                                       String idThing,
                                       String timePush,
                                       String key) {
        try {
            Map message = new HashMap<>();
            Map messageDetail = new HashMap<>();
            Map messageDetailZone = new HashMap<>();
            message.put("group", group);
            message.put("name", name);
            message.put("serialNumber", name);
            message.put("thingTypeCode", thingTypeCode);
            message.put("udfs", messageDetail);
            messageDetail.put(key, messageDetailZone);
            messageDetailZone.put("value", zonaName);
            messageDetailZone.put("time", timePush);

            System.out.println("Si entro");
            System.out.println(message);
            System.out.println(idThing);
            patchSomething("http://" + host + ":" + port + "/riot-core-services/api/thing/" + idThing, message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static String groupThing(String idGroup, String host, String port) throws IOException {
        Map<String, Object> group = getSomething("http://"
                + host
                + ":"
                + port
                + "/riot-core-services/api/group/?where=id%3D"
                + idGroup);

        List<Map<String, Object>> grou = (List) group.get("results");
        if (group == null) {
            System.out.println("si");
        }
        // System.out.print(group);
        return grou.get(0).get("hierarchyName").toString();

    }

    public static void main(String[] arg) throws IOException {

        int numRecords = 200;
        int clientZoneMovMax = 300;
        int minTimeInZoneSec = 30;
        int maxTimeInZoneSec = 300;
        int simulationHours = 12;
        int clientIntervalSec = 30;

        //probability shipping product and no shipping
        double changeTakeProb = 0.25;
        double purchaseProb = 0.50;
        double fittingRoomProb = 0.75;
        double chanceSeeProdInZone = 0.15;

        String host = "localhost";
        String port = "8080";

        String thingTypeCode = "customer_code";

        String zoneInCode = "Main.Entrance";
        String zoneExitCode = "Main.Exit";
        String Fitting1 = "Fitting.Room.1";
        String Fitting2 = "Fitting.Room.2";

        zoneInCode = zoneInCode.replace(" ", "%20");
        zoneExitCode = zoneExitCode.replace(" ", "%20");

        String pathField = "/src/main/resources/xls/";

        String fileName = "graph_matrix.xls";
        String xlsSheet = "graph_matrix";

        Map<String, Object> zones = getSomething("http://"
                + host
                + ":"
                + port
                + "/riot-core-services/api/zone/?"
                + "pageSize=-1&"
                + "where=!(code%3D"
                + zoneInCode
                + ")%26!(code%3D"
                + zoneExitCode
                + ")%26localMap.id%3D2");

        //get de todos los customers
        Map<String, Object> customerThings = getSomething("http://"
                +
                host
                + ":"
                + port
                + "/riot-core-services/api/thing/?pageSize=-1&where=thingType.thingTypeCode%3D"
                + thingTypeCode
                + "&extra=thingType%2Cgroup");

        Map<String, Object> thingsRFID = getSomething("http://"
                +
                host
                + ":"
                + port
                + "/riot-core-services/api/thing/?"
                + "pageSize=-1&"
                + "where=thingType.thingTypeCode%3Dretail.RFID.tag&"
                + "extra=thingType%2Cgroup");

        //Map list of thing customers, zones and RFID's

        List<Map<String, Object>> objCustomerThings = (List) customerThings.get("results");
        List<Map<String, Object>> objZones = (List) zones.get("results");
        List<Map<String, Object>> objThingsRFID = (List) thingsRFID.get("results");


        System.out.println("Number of RFID's: " + objThingsRFID.size() + " Number of Zones: " + objZones.size());

        //set random zones to products in map
        System.out.println("******BEGIN MODIFY ZONES**********");
        for (int k = 0; k < objThingsRFID.size(); k++) {
            modifyZone(host,
                    port,
                    ">ViZix.retail>Retail.Main.Store",
                    objThingsRFID.get(k).get("name").toString(),
                    objThingsRFID.get(k).get("serial").toString(),
                    "retail.RFID.tag",
                    objZones.get((int) (Math.random() * objZones.size())).get("code").toString(),
                    objThingsRFID.get(k).get("id").toString(),
                    null);
        }

        Simulator.simulate(objCustomerThings,
                host,
                port,
                changeTakeProb,
                chanceSeeProdInZone,
                purchaseProb,
                fittingRoomProb,
                clientZoneMovMax,
                minTimeInZoneSec,
                maxTimeInZoneSec,
                pathField,
                fileName,
                xlsSheet,
                zoneInCode,
                zoneExitCode,
                Fitting1,
                Fitting2,
                simulationHours,
                clientIntervalSec);
    }
}
