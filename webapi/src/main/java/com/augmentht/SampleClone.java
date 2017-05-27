package com.augmentht;

/**
 * Created by dkochar on 5/20/2017.
 */
import java.util.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SampleClone {

    static String svcUsername = "Augme-dbe1-AugmentAPI-test";
    static String svcPassword = "@1gMbnTh6@atHt%C4n!5!Gy1^cmfnt";
    static String appname = "AugmentHealthTechnology.AugmentAPI.TestApp";
    static String ehrUsername = "jmedici";
    static String ehrPassword = "password01";
    static String URL = "http://twlatestga.unitysandbox.com";
    static String Token = "";

    HttpPost method = null;
    HttpEntity entity = null;
    HttpClient client = null;
    HttpResponse resp = null;

    public static void main(String[] args) {
        try {
            SampleClone unity = new SampleClone();

			/* read app settings from a file instead of hard-coding */
            //unity.readSettings("unity-example.properties");

			/* GetSecurityToken */
            Token = unity.getToken();

            if (Token.startsWith("error:") || Token.startsWith("Error:")) {
                System.out.println("Error getting security token (" + Token + "). " +
                        "Application will exit.");
                System.exit(-1);
            }

            System.out.println("Using Unity security token: " + Token + " \n");

			/* authenticate EHR username and password before calling other EHR actions */
            String userAuth =
                    unity.Magic(
                            "GetUserAuthentication", ehrUsername, appname, "", Token,
                            ehrPassword, "", "", "", "", "", null);

			/* uncomment to see full output from GetUserAuthentication: */
            // System.out.println("\nRaw response from GetUserAuthentication:");
            // unity.displayJson(userAuth);
            System.out.println(userAuth);
            /* Look for ValidUser = YES; display ErrorMessage if NO */
            JsonArray uaa = new JsonParser().parse(userAuth).getAsJsonArray().get(0).getAsJsonObject().get("getuserauthenticationinfo").getAsJsonArray();

            String validUser = uaa.get(0).getAsJsonObject().get("ValidUser").toString();

            if (validUser.contains("YES")) {
                System.out.println("EHR user is valid.");
            } else {
                String errorMsg = uaa.get(0).getAsJsonObject().get("ErrorMessage").toString();
                System.out.println("EHR user is invalid: " + errorMsg);
            }

			/* Call GetServerInfo Magic action; patient ID, Parameter1-6, and data not used */
            String serverInfo =
                    unity.Magic(
                            "GetServerInfo", ehrUsername, appname, "", Token,
                            "", "", "", "", "", "", null);

            System.out.println("\nRaw response from GetServerInfo:");
            unity.displayJson(serverInfo);

			/* prompt for patient ID, call GetPatient action */
            InputStreamReader istream = new InputStreamReader(System.in);
            BufferedReader bufRead = new BufferedReader(istream);
            System.out.println("\nEnter a Patient ID to display (e.g., 324): ");
            String searchId = bufRead.readLine();

            if (!searchId.isEmpty()) {
				/* patient ID goes in Magic action's PatientID field; other fields unused for this example */
                String getPatient =
                        unity.Magic(
                                "GetPatient", ehrUsername, appname, searchId, Token,
                                "", "", "", "", "", "", null);
                System.out.println("\nRaw response from GetPatient:");
                unity.displayJson(getPatient);
            }
        }
            catch (Exception drat)
            {
                drat.printStackTrace( );
            }
    }

    public String Magic(String action, String appuser, String appname,
                        String patientid, String token, String param1, String param2,
                        String param3, String param4, String param5, String param6,
                        byte[] data) throws Exception {
		/* build the Json string */
        Gson gson = new Gson();

        JsonObject jo = new JsonObject();
        jo.addProperty("Action", action);
        jo.addProperty("Appname", appname);
        jo.addProperty("AppUserID", appuser);
        jo.addProperty("PatientID", patientid);
        jo.addProperty("Token", token);
        jo.addProperty("Parameter1", param1);
        jo.addProperty("Parameter2", param2);
        jo.addProperty("Parameter3", param3);
        jo.addProperty("Parameter4", param4);
        jo.addProperty("Parameter5", param5);
        jo.addProperty("Parameter6", param6);
        jo.addProperty("Data", gson.toJson(data));

        String requestBody = jo.toString();

		/* send Json to Unity endpoint */
        client = HttpClientBuilder.create().build();

        try {
            method = new HttpPost(new URI(URL + "/Unity/unityservice.svc/json/MagicJson"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        method.setEntity(stringEntity);
        HttpResponse response = client.execute(method);

        InputStream content = response.getEntity().getContent();
        String Json = getStringFromStream(content);
        return Json;
    }

    /* get the Unity security token on startup */
    private String getToken() throws Exception {
		/* build {"Username":"un", "Password":"pw"} string */
        JsonObject jo = new JsonObject();
        jo.addProperty("Username", svcUsername);
        jo.addProperty("Password", svcPassword);
        String reqBody = jo.toString();

        client = HttpClientBuilder.create().build();

        try {
            method = new HttpPost(new URI(URL + "/Unity/unityservice.svc/json/GetToken"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        StringEntity stringEntity = new StringEntity(reqBody, ContentType.APPLICATION_JSON);
        method.setEntity(stringEntity);
        HttpResponse response = client.execute(method);

        InputStream content = response.getEntity().getContent();
        String token = getStringFromStream(content);
        return token;
    }

    /* retire the Unity security token */
    private String forgetToken() throws Exception {
        JsonObject jo = new JsonObject();
        jo.addProperty("Token", Token);
        jo.addProperty("Appname", appname);

        String reqBody = jo.toString();

        client = HttpClientBuilder.create().build();

        try {
            method = new HttpPost(new URI(URL + "/Unity/unityservice.svc/json/RetireToken"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        StringEntity stringEntity = new StringEntity(reqBody, ContentType.APPLICATION_JSON);
        method.setEntity(stringEntity);
        HttpResponse response = client.execute(method);

        InputStream content = response.getEntity().getContent();
        String msg = getStringFromStream(content);
        return msg;
    }

    public String getStringFromStream(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String rData = null;

        try {
            int read;
            byte[] next = new byte[256];
            while ((read = in.read(next)) != -1) {
                out.write(next, 0, read);
            }

            byte[] byteArray = out.toByteArray();
            rData = new String(byteArray, Charset.forName("UTF-8"));

        } catch (Exception ex) {
            System.out.println("Exception occurred while reading stream: " + ex);
        }
        return rData;
    }

    /* for this example, just print the Json; in practice, process it */
    public void displayJson(String Json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JsonParser jp = new JsonParser();
        JsonElement parse = jp.parse(Json);

        JsonArray asJsonArray = parse.getAsJsonArray();

        for (JsonElement jse : asJsonArray) {
            String prettyGson = gson.toJson(jse);
            System.out.println(prettyGson);
        }
    }

    public void readSettings(String filename) {
		/* read server info/credentials from properties file */
        try {
            Properties props = new Properties();
            FileInputStream propfile = new FileInputStream(filename);
            props.load(propfile);
            propfile.close();

            svcUsername = props.getProperty("svc.username");
            svcPassword = props.getProperty("svc.password");
            appname = props.getProperty("appname");
            URL = props.getProperty("unity.endpoint");
            ehrUsername = props.getProperty("ehr.username");
            ehrPassword = props.getProperty("ehr.password");
        } catch (Exception drat) {
            System.out.println("Trouble reading application properties: ");
            drat.printStackTrace();
            System.exit(-1);
        }
    }
}