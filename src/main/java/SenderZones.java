/**
 * Created by ezapata on 28-Jan-16.
 */

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
public class SenderZones {

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
    public static String getDate(long timeStamp) {
        DateTimeFormatter isoDateFormat = ISODateTimeFormat.dateTime();
        String isoDateStr = isoDateFormat.print(new DateTime(timeStamp));
        return isoDateStr;
    }



    public static Map getSomething(String endpoint) throws IOException {

        System.out.println(endpoint);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(endpoint);
        ObjectMapper objectMapper = new ObjectMapper();
        // add request header
        // request.addHeader("User-Agent", USER_AGENT);
        request.addHeader("content-type", "application/json");
        request.addHeader("Api_key","root");
        HttpResponse response = client.execute(request);
        Map finalResult = null;
        boolean error = false;
        String errorMessage = null;
        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());
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
                //List<Object> result=objectMapper.readValue(responseString,List.class);
                //     Map aux = (Map) mapResult.get("RFIDEquipmentDetails_MT");
                //List lis=(List)result.get("Points");
                //System.out.print("entro");
                finalResult = mapResult;


            } catch (Exception ex) {
                error = true;
                errorMessage = ex.getMessage();
            }
        }  else {
            error = true;
            errorMessage = "Empty Response from SAP";
        }

       // List<Map<String,Object>> objlist=(List)finalResult.get("results");
        //Map<String,Object> es=objlist.get(0);
        //  System.out.print(es.get("zonePoints"));
        return finalResult;
    }

    public static void patchSomething(String endpoint,Map message) throws IOException {


        HttpClient client = HttpClientBuilder.create().build();
        HttpPatch request=new HttpPatch(endpoint);

        ObjectMapper objectMapper = new ObjectMapper();
        // add request header
        // request.addHeader("User-Agent", USER_AGENT);
        request.addHeader("content-type", "application/json");
        request.addHeader("Api_key","root");
        String jsonBody="";
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
        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());
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
        }  else {
            error = true;
            errorMessage = "Empty Response from SAP";
        }

        List<Map<String,Object>> objlist=(List)finalResult.get("results");


    }


    public static void postSomething(String endpoint,Map message) throws IOException {


        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request=new HttpPost(endpoint);

        ObjectMapper objectMapper = new ObjectMapper();
        // add request header
        // request.addHeader("User-Agent", USER_AGENT);
        request.addHeader("content-type", "application/json");
        request.addHeader("Api_key","root");
        String jsonBody="";
        if (message != null) {
            System.out.println("el mensage no es null");
            jsonBody = objectMapper.writeValueAsString(message);
            jsonBody="["+jsonBody+"]";
            System.out.println("json en el patch"+jsonBody);
            //jsonBody=jsonBody.replace("=",":");
        }
        request.setEntity(new StringEntity(jsonBody));

        HttpResponse response = client.execute(request);
        Map finalResult = null;
        boolean error = false;
        String errorMessage = null;
        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());
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
        }  else {
            error = true;
            errorMessage = "Empty Response from SAP";
        }

       // List<Map<String,Object>> objlist=(List)finalResult.get("results");


    }




    public static void modifyZone(String host,String port,String group,String name,String thingTypeCode,String zonaName,String idThing,String timePush){

        try {
            Map message = new HashMap<>();
            Map messageDetail = new HashMap<>();
            Map messageDetailZone = new HashMap<>();
            message.put("group", group);
            message.put("name", name);
            message.put("serialNumber", name);
            message.put("thingTypeCode", thingTypeCode);
            message.put("udfs", messageDetail);
            messageDetail.put("zone", messageDetailZone);
            messageDetailZone.put("value", zonaName);
            messageDetailZone.put("time",timePush);


            // System.out.println("messa" + message.toString());
            System.out.println("http://" + host + ":" + port + "/riot-core-services/api/thing/" + idThing);
            patchSomething("http://" + host + ":" + port + "/riot-core-services/api/thing/" + idThing, message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    public static String groupThing(String idGroup,String host, String port) throws IOException {
        Map<String,Object>group=getSomething("http://"+host+":"+port+"/riot-core-services/api/group/?where=id%3D"+idGroup);

        List<Map<String,Object>> grou=(List)group.get("results");
        if(group==null)System.out.print("si");
        // System.out.print(group);
        return grou.get(0).get("hierarchyName").toString();

    }

    public static String getSomeValueGroup(String name,String host,String port,String something)throws IOException{
        Map<String,Object>group=getSomething(host+":8080/riot-core-services/api/group/?where=code%3D"+name);
        List<Map<String,Object>> grou=(List)group.get("results");
        return  grou.get(0).get(something).toString();
    }

    public static void migrateZones(String hostGet,String portGet,String hostPut,String portPut,String idFacilityMap,String groupForSearch) throws IOException {
        Map<String,Object>zones=getSomething(hostGet+":"+portGet+"/riot-core-services/api/zone/?pageSize=-1&where=localMap.id%3D"+idFacilityMap+"&extra=localMap%2Cgroup%2CzoneGroup%2CzoneType");
        System.out.print(hostGet+":"+portGet+"/riot-core-services/api/zone/?pageSize=1&where=localMap.id%3D"+idFacilityMap);
        Map<String,Object>zoneType=getSomething(hostPut+":8080/riot-core-services/api/zoneType/?where=id%3D2");

        String idGroup=getSomeValueGroup(groupForSearch,hostPut,portPut,"id");
        Map<String,Object>zoneGroup=getSomething(hostPut+":8080/riot-core-services/api/zoneGroup/?where=group.id%3D"+idGroup);
        System.out.print(zoneGroup);
        List<Map<String,Object>> listZoneGroup=(List)zoneGroup.get("results");
        String idZG=listZoneGroup.get(0).get("id").toString();

        Map<String,Object>localMap=getSomething(hostPut+":8080/riot-core-services/api/localMap/?where=name%3DMain%20Store");
        List<Map<String,Object>> map=(List)localMap.get("results");
        String idMap=map.get(0).get("id").toString();


        List<Map<String,Object>>listZoneType=(List)zoneType.get("results");

        List<Map<String,Object>>listZone=(List)zones.get("results");
        //System.out.print(zones);
        for (int i=0;i<listZone.size();i++){
            Map<String,Object> oneZone=listZone.get(i);
            try {
                System.out.print("entor");
                Map message = new HashMap<>();
                Map messageZonePoints = new HashMap<>();
                Map messageDetailZone = new HashMap<>();
                Map<String,Object>group=(Map)oneZone.get("group");
                //Map<String,Object>group=(Map)oneZone.get("localMap");
                //Map<String,Object>group=(Map)oneZone.get("zoneGroup");

                message.put("zonePoints",oneZone.get("zonePoints"));
                message.put("name", oneZone.get("name"));
                message.put("code",oneZone.get("code"));
                message.put("description", oneZone.get("description"));
                message.put("color",oneZone.get("color"));
                message.put("localMap.id",Integer.parseInt(idMap));
                message.put("zoneGroup.id",Integer.parseInt(idZG));
                message.put("group.id",Integer.parseInt(idGroup));
                message.put("zoneType",listZoneType.get(0));

                System.out.println(message);


                postSomething(hostPut + ":" + portPut + "/riot-core-services/api/zone/", message);
            } catch (Exception ex) {
                ex.printStackTrace();
            }


        }

    }
    public static void main(String[]arg) throws IOException {
        Scanner lee=new Scanner(System.in);
        int num_records=100;
        double probability=0.9;
        String host="qa.riotplatform.com";

        String port="8080";
        String zoneExit="PoS";
        String zoneIn="Entrance";
        String thingTypeCode="customer_code";
        String idFacilityMap="56";
        long current =System.currentTimeMillis();

        migrateZones("http://saturn.mojix.com","8080","http://"+host,"8080",idFacilityMap,"Retail.Main.Store");


        //List<Map<String,Object>> objZones=(List)zones.get("results");







    }
}

