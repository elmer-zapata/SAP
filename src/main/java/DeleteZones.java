/**
 * Created by Elmer on 05/02/2016.
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
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

public class DeleteZones {

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

    public static void deleteSomething(String endpoint) throws IOException {


        HttpClient client = HttpClientBuilder.create().build();
        HttpDelete request=new HttpDelete(endpoint);

        ObjectMapper objectMapper = new ObjectMapper();
        // add request header
        // request.addHeader("User-Agent", USER_AGENT);
        request.addHeader("content-type", "application/json");
        request.addHeader("Api_key","root");
        String jsonBody="";





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

     //   List<Map<String,Object>> objlist=(List)finalResult.get("results");


    }

    public static void deleteZones(String hostGet,String portGet,String hostPut,String portPut,String idFacilityMap,String groupForSearch) throws IOException {
        Map<String,Object>zones=getSomething(hostGet+":"+portGet+"/riot-core-services/api/zone/?pageSize=-1&where=localMap.id%3D"+idFacilityMap+"&extra=localMap%2Cgroup%2CzoneGroup%2CzoneType");
        System.out.println(hostGet+":"+portGet+"/riot-core-services/api/zone/?pageSize=1&where=localMap.id%3D"+idFacilityMap);

        List<Map<String,Object>>listZone=(List)zones.get("results");
        //System.out.print(zones);
        for (int i=0;i<listZone.size();i++){
            String code=listZone.get(i).get("code").toString();
            System.out.println("codez"+code);
            Map<String,Object> zoneForDelete=getSomething(hostPut+":8080/riot-core-services/api/zone/?where=code%3D"+code);
            List<Map<String,Object>> zfd=(List)zoneForDelete.get("results");
            System.out.println(zfd);
            if(zfd.size()>0)
            {String idForDelete=zfd.get(0).get("id").toString();
            deleteSomething(hostPut+":8080/riot-core-services/api/zone/"+idForDelete);}


        }

    }

    public static void main(String[]arg) throws IOException {
        Scanner lee=new Scanner(System.in);
        int num_records=100;
        double probability=0.9;
        String host="dev.riotplatform.com";

        String port="8080";
        String zoneExit="PoS";
        String zoneIn="Entrance";
        String thingTypeCode="customer_code";
        String idFacilityMap="56";
        long current =System.currentTimeMillis();

        deleteZones("http://saturn.mojix.com", "8080", "http://" + host, "8080", idFacilityMap, "Retail.Main.Store");


        //List<Map<String,Object>> objZones=(List)zones.get("results");


    }

}
