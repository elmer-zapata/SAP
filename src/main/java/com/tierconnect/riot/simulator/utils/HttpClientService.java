package com.tierconnect.riot.simulator.utils;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by angelchambi on 3/7/16.
 * Class implement http client to connect at rest service
 */
public class HttpClientService{

    private HttpClient httpClient;
    private final String USER_SERVICE;
    private HttpRequestBase request;
    private URIBuilder builder;

    public HttpClientService(String host, int port, String endpoint, String userService){

        httpClient = HttpClientBuilder.create().build();
        request = null;
        USER_SERVICE = userService;
        builder = new URIBuilder();
        builder.setHost(host);
        builder.setScheme("http");
        builder.setPort(port);
        builder.setPath(endpoint);
    }

    public Object httpRequest(List<NameValuePair> params,
                              String method,
                              Object objectInput,
                              Class classResponse,
                              String type) throws IOException, URISyntaxException{
        if (params != null) {
            for(NameValuePair nameValuePair : params){
                builder.setParameter(nameValuePair.getName(), nameValuePair.getValue());

            }
        }
        URI uri = builder.build();
        switch(method){
            case "GET":
                request = new HttpGet(uri);
                break;
            case "POST":
                request = new HttpPost(uri);
                if (objectInput != null) {

                    if (objectInput instanceof String) {

                        File file = new File((String)objectInput);
                        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
                        multipartEntityBuilder.addBinaryBody(type,
                                                             file,
                                                             ContentType.create("text/csv"),
                                                             file.getName());
                        ((HttpPost)request).setEntity(multipartEntityBuilder.build());
                    }
                    else {
                        StringEntity jsonEntity = new StringEntity(JSonConverter.objectToJsonString(objectInput));
                        ((HttpPost)request).setEntity(jsonEntity);
                        request.addHeader("content-type", "application/json");
                    }
                }
                break;
            case "PUT":
                request = new HttpPut(uri);
                if (objectInput != null) {

                    StringEntity jsonEntity = new StringEntity(JSonConverter.objectToJsonString(objectInput));
                    ((HttpPut)request).setEntity(jsonEntity);
                    ((HttpPut)request).setEntity(jsonEntity);
                    request.addHeader("content-type", "application/json");

                }
                break;
        }

        HttpResponse response;
        Object result = null;
        try{
            request.addHeader("Api_key", USER_SERVICE);
            response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                if (response.getStatusLine().getStatusCode() == 200
                    || response.getStatusLine().getStatusCode() == 201) {
                    // A Simple JSON Response Read
                    InputStream inputStream = entity.getContent();
                    result = JSonConverter.jsonStringToObject(HttpResponseConverter.convertStreamToString(inputStream),
                                                              classResponse);
                    inputStream.close();
                }

            }
        }
        catch(ClientProtocolException cpEx){
            // TODO Auto-generated catch block
            cpEx.printStackTrace();
        }
        return result;
    }


}