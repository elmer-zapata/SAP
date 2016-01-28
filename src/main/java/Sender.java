package main.java;

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
    /*public static Map sendMessageToSAP(Map message, User user, String tCode, long operationId) {
        String sapUri = ConfigurationService.getAsString(user, "fmcSapUrl");
        String sapUsername = ConfigurationService.getAsString(user, "fmcSapUsername");
        String sapPassword = ConfigurationService.getAsString(user, "fmcSapPassword");
        File logFile = ThingController.getLogFile(user, tCode);
        return sendMessageToSAP(message, sapUri, sapUsername, sapPassword, logFile, tCode, operationId);

    }*/
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

        List<Map<String,Object>> objlist=(List)finalResult.get("results");
        Map<String,Object> es=objlist.get(0);
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
//        Map<String,Object> es=objlist.get(0);
       // System.out.print(es.get("zonePoints"));

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
    public static void modifyudf(){
        /*try {
            Map message = new HashMap<>();
            Map messageDetail = new HashMap<>();
            Map messageDetailZone = new HashMap<>();
            message.put("group", thingForProced.get("group"));
            message.put("name", thingForProced.get("name"));
            message.put("thingTypeCode", thingForProced.get("thingTypeCode"));
            message.put("Action","01");
            message.put("udf", messageDetail);
            messageDetail.put("zone", messageDetailZone);
            messageDetailZone.put("value", es.get("name"));
            messageDetail.put("Customers","");

            patchSomething(host + ":" + port + "/riot-core-services/api/thing/" + thingForProced.get("id"), message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

*/

    }

    public static String groupThing(String idGroup,String host, String port) throws IOException {
        Map<String,Object>group=getSomething("http://"+host+":"+port+"/riot-core-services/api/group/?where=id%3D"+idGroup);

        List<Map<String,Object>> grou=(List)group.get("results");
        if(group==null)System.out.print("si");
       // System.out.print(group);
        return grou.get(0).get("hierarchyName").toString();

    }

    public static void main(String[]arg) throws IOException {
        Scanner lee=new Scanner(System.in);
         int num_records=10;
         double probability=0.9;
        String host="10.100.1.195";

        String port="8080";
        String zoneExit="PoS";
        String zoneIn="Entrance";
        String thingTypeCode="customer_code";
       /* System.out.println("Ingrese el host");
        host=lee.nextLine();
        System.out.println("Ingrese el puerto");
        port=lee.nextLine();
        System.out.println("Type the In Zone");
        zoneIn=lee.nextLine();
        System.out.println("Type the out Zone");
        zoneExit=lee.nextLine();
        System.out.println("Type the thingTypeCode");
        thingTypeCode=lee.nextLine();*/
       long current =System.currentTimeMillis();
        Map<String,Object>zones=getSomething("http://"+host+":"+port+"/riot-core-services/api/zone/?pageSize=-1&where=!(name%3D"+zoneIn+")%26!(name%3D"+zoneExit+")");

        Map<String,Object>things=getSomething("http://"+
                host+":"+port+"/riot-core-services/api/thing/?pageSize=-1&where=thingType.thingTypeCode%3D"+thingTypeCode+"&extra=thingType%2Cgroup");
        //System.out.print(things);

        List<Map<String,Object>> objZones=(List)zones.get("results");
        List<Map<String,Object>> objThings=(List)things.get("results");

//        Map<String,Object> dd=(Map)objThings.get(0).get("thingType");
  //      Map<String,Object> dd2=(Map)objThings.get(0).get("group");

//        System.out.print(dd);
  //      System.out.print(dd2.get("hierarchyName"));
    //    System.out.print(objThings.get(0).get("name").toString());
      //  System.out.print(objThings.get(0).get("id").toString());



        //modifyZone(host, port, dd2.get("hierarchyName").toString(), objThings.get(0).get("name").toString(), dd.get("thingTypeCode").toString(), "Enance", objThings.get(0).get("id").toString());


        //aqui empieza la simulacion

        for( int i = 0; i <num_records ;i++ ) {
            //necesito el thing para moverlo
                Map<String,Object> thingForProced=objThings.get(i);
                Map<String,Object> thingTypeforProced=(Map)thingForProced.get("thingType");
                Map<String,Object> groupForthing=(Map)thingForProced.get("group");
            int randomMoves=(int)(Math.random()*objZones.size());
            Timestamp stamp = new Timestamp(current);
            java.sql.Date date = new java.sql.Date(stamp.getTime());
            Calendar cal = Calendar.getInstance();
            cal.setTime ( date );

            //hacer el primer movimiento a la zona o zonas de entrada

            for (int j=1;j<=randomMoves;j++) {
                int randomZone=(int)(Math.random()*objZones.size());
                //sacar una zona a la suerte

                Map<String,Object> es=objZones.get(randomZone);
                int randomTime=(int)(Math.random()*11);
                cal.add(Calendar.MINUTE, randomTime);
                java.sql.Date date2 = new java.sql.Date(cal.getTime().getTime());

                //logger.info(date2);
                long initialTime=date2.getTime();
                //aqui hacer el update del thing si es q la probabilidad nos deja
                String TimePush=String.valueOf(initialTime);

                Map<String,Object> thingsInZones=getSomething("http://"+host+":"+port+""+"/riot-core-services/api/things/?pageSize=-1&where=children.zone.value.name%3D"+es.get("name").toString()+"&treeView=false");
                List<Map<String,Object>> listThings=(List)thingsInZones.get("results");
                if(listThings!=null) {
                    int thingInZoneRandom=(int)(Math.random()*listThings.size());
                    //Map<String,Object>children=(Map)listThings.get(thingInZoneRandom).get("children");
                    String thingTypeChildren=listThings.get(thingInZoneRandom).get("thingTypeCode").toString();
                    String nameChild=listThings.get(thingInZoneRandom).get("name").toString();

                   // System.out.print(listThings.get(thingInZoneRandom).get("groupId"));

                    String groupthing=groupThing(listThings.get(thingInZoneRandom).get("groupId").toString(),host,port);

                    if(probability+Math.random()>1) {
                        try {
                            Map message = new HashMap<>();
                            Map messageDetail = new HashMap<>();
                            Map messageDetailCustomer = new HashMap<>();
                            message.put("group", groupthing);
                            message.put("name", nameChild);
                            message.put("thingTypeCode", thingTypeChildren);
                            message.put("udfs", messageDetail);
                            messageDetail.put("Customers",messageDetailCustomer);
                            messageDetailCustomer.put("value", thingForProced.get("name"));
                            messageDetailCustomer.put("time", TimePush);
                            System.out.println("message" + message);
                            System.out.println("patch"+"http://"+host + ":" + port + "/riot-core-services/api/thing/" + thingForProced.get("id"));
                            patchSomething("http://"+host + ":" + port + "/riot-core-services/api/thing/" + listThings.get(thingInZoneRandom).get("id").toString(), message);
                            modifyZone(host,port,groupForthing.get("hierarchyName").toString(),thingForProced.get("name").toString(),thingTypeforProced.get("thingTypeCode").toString(),es.get("code").toString(),thingForProced.get("id").toString(),TimePush);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }else{
                        try {
                            modifyZone(host,port,groupForthing.get("hierarchyName").toString(),thingForProced.get("name").toString(),thingTypeforProced.get("thingTypeCode").toString(),es.get("code").toString(),thingForProced.get("id").toString(),TimePush);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }
                else{
                    modifyZone(host,port,groupForthing.get("hierarchyName").toString(),thingForProced.get("name").toString(),thingTypeforProced.get("thingTypeCode").toString(),es.get("code").toString(),thingForProced.get("id").toString(),TimePush);
                }
            }



        }

    }
}
