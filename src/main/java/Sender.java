package main.java;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.omg.CORBA.UserException;

import java.io.*;
import java.util.*;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

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
        client = new DefaultHttpClient();
    }
    public static String getDate(long timeStamp) {
        DateTimeFormatter isoDateFormat = ISODateTimeFormat.dateTime();
        String isoDateStr = isoDateFormat.print(new DateTime(timeStamp));
        return isoDateStr;
    }

    public static Map sendMessageToSAP(Map message, String url, String username, String password, File logFile, String tCode, long operationId)  {
        ObjectMapper objectMapper = new ObjectMapper();
//        String hardCodedResponse = "{\"RFIDEquipmentDetails_MT\":{\"Description\":\"GV 2 1/16-10K MAN STDD\",\"UserStatus\":\"0006\",\"SystemStatus\":\"AVLB\",\"AssetNum\":null,\"ValidFromDate\":\"2012-02-16\",\"ValidToDate\":\"9999-12-31\",\"SerialNum\":\"000000000000501887\",\"MaterialNum\":\"90-130-316\",\"CategoryCode\":\"S\",\"Owner\":null,\"Administrator\":null,\"CurrentLocation\":null,\"MaintPlant\":\"7370\",\"ServiceCallStatus\":\"0\"}}";
////        String hardCodedResponse = "{\"RFIDEquipmentDetails_MT\":{\"Description\":\"GV 4 1/16-15K HYD STDD\",\"UserStatus\":\"0006\",\"SystemStatus\":\"AVLB\",\"AssetNum\":\"000006024918\",\"ValidFromDate\":\"2013-11-22\",\"ValidToDate\":\"9999-12-31\",\"SerialNum\":\"2006-01-276T\",\"MaterialNum\":\"P150640\",\"CategoryCode\":\"R\",\"Owner\":null,\"Administrator\":null,\"CurrentLocation\":null,\"MaintPlant\":\"7320\",\"ServiceCallStatus\":\"0\"}}\n";
//        int i = 10;
//        if (i == 10) {
//            try {
//                Map<String, Object> hardCodedResult = objectMapper.readValue(hardCodedResponse, HashMap.class);
//                return hardCodedResult;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        long startTime = System.currentTimeMillis();
        Map finalResult = null;
        boolean error = false;
        String errorMessage = null;
        try {
            if (logFile != null) {
                //logger.warn("writing log file: " + logFile.getAbsolutePath());

                FileUtils.writeStringToFile(logFile, "TransactionCode: " + tCode + "\r\n");
                FileUtils.writeStringToFile(logFile, "OperationId:" + operationId + "\r\n");
                FileUtils.writeStringToFile(logFile, "Sap User: " + username + "\r\n");
            }
            if (url == null) {
                //throw new UserException("invalid url for sap synchronization " + url);
                System.out.println("invalid URL");
            }
            HttpPost httpPost = new HttpPost(url);
            String jsonBody = "{}";
            if (message != null) {
                System.out.println("el mensage no es null");
                jsonBody = objectMapper.writeValueAsString(message);
            }


            System.out.println("Sending message to SAP, transactionCode: " + tCode + ", operation: " + operationId + ", input: " + jsonBody);
            httpPost.setEntity(new StringEntity(jsonBody));

            HttpUriRequest request = httpPost;
            request.addHeader("content-type", "application/json");
            String encoding = Base64.encodeBase64String((username + ":" + password).getBytes());
            request.setHeader("Authorization", "Basic " + encoding);

            if (logFile != null) {
                FileUtils.writeStringToFile(logFile, "Input:\r\n");
                FileUtils.writeStringToFile(logFile, jsonBody + "\r\n");
            }

            HttpResponse httpResponse = client.execute(request);

            HttpEntity entity = httpResponse.getEntity();
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
            int statusCode = httpResponse.getStatusLine().getStatusCode();

           // logger.warn("Receiving message from SAP, transactionCode: " + tCode+ ", operation: " + operationId + ", output: "+ responseString);

            if (logFile != null) {
                FileUtils.writeStringToFile(logFile, "Output: Status " + statusCode + " Body" + "\r\n");
                FileUtils.writeStringToFile(logFile, responseString + "\r\n");
            }

            if (StringUtils.isNotBlank(responseString)) {
                Map<String, Object> mapResult = new HashMap<String, Object>();
                try {
                    mapResult = objectMapper.readValue(responseString, HashMap.class);
                    if (statusCode == 401 || statusCode == 403) {
                        error = true;
                        errorMessage = "Invalid Credentials to connect to SAP";
                    } else if (statusCode == 200 || statusCode == 201) {
                        String error_ = (String) mapResult.get("error");
                        if (StringUtils.isNotEmpty(error_)) {
                            error = true;
                            errorMessage = error_;
                        }
                        Map aux = (Map) mapResult.get("RFIDEquipmentDetails_MT");
                        if (aux != null) {
                            error_ = (String) aux.get("ErrorMessage");
                            if (StringUtils.isNotEmpty(error_)) {
                                error = true;
                                errorMessage = error_;
                            }
                        }
                    } else {
                        error = true;
                    }
                    finalResult = mapResult;
                } catch (Exception ex) {
                    error = true;
                    errorMessage = ex.getMessage();
                }
            }  else {
                error = true;
                errorMessage = "Empty Response from SAP";
            }
        } catch (Exception ex) {
            //logger.warn("Error Sending message to SAP, transactionCode: " + tCode+ ", operation: " + operationId + ", exception: "+ (StringUtils.isNotEmpty(ex.getMessage())? ex.getMessage() : ex.getClass().getSimpleName()));
            ex.printStackTrace();
            //ex.getStackTrace();
            try {
                if (logFile != null) {
                    FileUtils.writeStringToFile(logFile, "Status: exception \r\n");
                    FileUtils.writeStringToFile(logFile, ExceptionUtils.getStackTrace(ex) + "\r\n");
                }
            } catch (IOException e) {
              //  logger.error(e.getMessage());
            }
            if (ex instanceof java.net.UnknownHostException || ex instanceof UserException || ex instanceof IllegalStateException) {
                System.out.println(ex.getMessage());
                //throw new UserException(ex.getMessage());
            } else {
                throw new RuntimeException(StringUtils.isNotEmpty(ex.getMessage())? ex.getMessage(): ex.getClass().getSimpleName());
            }
        } finally {
            try {
                if (logFile != null) {
                    FileUtils.writeStringToFile(logFile, "Status: " + (error ? " error " + errorMessage : " success" + "\r\n"));
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            long finalTime = System.currentTimeMillis();
            try {
                if (logFile != null) {
                    FileUtils.writeStringToFile(logFile, "Time: start " + startTime + " end " + finalTime + " elapsed " + (finalTime - startTime) + "\r\n");
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return finalResult;
    }
    public static void main(String[]arg){
       File logFile = null;
        try {
            logFile = File.createTempFile("restFile", ".json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String url = "http://kbg1pit0.kongsberg.fmcweb.com:50000/AdvantcoRESTAdapter/RESTServlet?channel=RFID_Equipment_REST_Sync_S_CC&service=RFID_QA";
        String username = "RFIDAPPLUSER";
        String password = "rfid4FMC";
        String tCode="transaction";
///Prueba con el csv
        File input=new File("C:\\FMC.csv");
        FileReader fr=null;
        try {
             fr=new FileReader(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(fr);
        String linea;
        try {
            while((linea=br.readLine())!=null) {
                System.out.println(linea);
                String total[]=linea.split(",");
                try {
                    Map message;
                    message = new HashMap< >();
                    Map messageDetail = new HashMap< >();
                    message.put("RFIDEquipmentTag_MT", messageDetail);
                    messageDetail.put("TagID", total[11]);
                    messageDetail.put("EquipmentNum", total[12]);
                    messageDetail.put("DateTime", getDate(Long.parseLong(total[10])));
                    messageDetail.put("Plant", total[0]);
                    messageDetail.put("User", total[4]);
                    messageDetail.put("Action", "02");
                    sendMessageToSAP(message, url, username, password, logFile, tCode, 2L);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    Map message = new HashMap<>();
                    Map messageDetail = new HashMap<>();
                    message.put("RFIDEquipmentTag_MT", messageDetail);
                    messageDetail.put("TagID", total[11]);
                    messageDetail.put("EquipmentNum", total[12]);
                    messageDetail.put("DateTime", getDate(Long.parseLong(total[10])));
                    messageDetail.put("Plant", total[0]);
                    messageDetail.put("User", total[4]);
                    messageDetail.put("Action","01");
                    sendMessageToSAP(message, url, username, password, logFile, tCode, 1L);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
             /*   try {
                    Map message = new HashMap<>();
                    Map messageDetail = new HashMap<>();
                    message.put("RFIDEquipmentStatus_MT", messageDetail);
                    messageDetail.put("TagID", "201309228726030001019599");
                    messageDetail.put("EquipmentNum", "10619276");
                    messageDetail.put("DateTime", getDate(new Date().getTime()));
                    messageDetail.put("Plant", "7395");
                    messageDetail.put("User", "root");
                    messageDetail.put("ScanZone", "7395_NI");
                    sendMessageToSAP(message, url, username, password, logFile, tCode, 3L);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//Aqui acaba la prueba


/*
        try {
            Map message;
            message = new HashMap< >();
            Map messageDetail = new HashMap< >();
            message.put("RFIDEquipmentTag_MT", messageDetail);
            messageDetail.put("TagID", "201309228726030001019599");
            messageDetail.put("EquipmentNum", "10619276");
            messageDetail.put("DateTime", getDate(new Date().getTime()));
            messageDetail.put("Plant", "7395");
            messageDetail.put("User", "root");
            messageDetail.put("Action", "02");
            sendMessageToSAP(message, url, username, password, logFile, tCode, 1L);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            Map message = new HashMap<>();
            Map messageDetail = new HashMap<>();
            message.put("RFIDEquipmentTag_MT", messageDetail);
            messageDetail.put("TagID", "201309228726030001019599");
            messageDetail.put("EquipmentNum", "10619276");
            messageDetail.put("DateTime", getDate(new Date().getTime()));
            messageDetail.put("Plant", "7395");
            messageDetail.put("User", "root");
            messageDetail.put("Action","01");
            sendMessageToSAP(message, url, username, password, logFile, tCode, 2L);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            Map message = new HashMap<>();
            Map messageDetail = new HashMap<>();
            message.put("RFIDEquipmentStatus_MT", messageDetail);
            messageDetail.put("TagID", "201309228726030001019599");
            messageDetail.put("EquipmentNum", "10619276");
            messageDetail.put("DateTime", getDate(new Date().getTime()));
            messageDetail.put("Plant", "7395");
            messageDetail.put("User", "root");
            messageDetail.put("ScanZone", "7395_NI");
            sendMessageToSAP(message, url, username, password, logFile, tCode, 3L);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

*/
    }
}
