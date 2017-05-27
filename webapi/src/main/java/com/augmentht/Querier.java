package com.augmentht;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.FileInputStream;
import java.net.URI;
import java.util.Properties;

/**Class to get data from external api
 * Created by dkochar on 5/18/2017.
 */
public class Querier {



    private String appname;

    private String ehrUsername;
    private String ehrPassword;
    private String svcusername;
    private String svcpassword;
    private final URI baseuri = UriBuilder.fromUri("http://prolatestga.unitysandbox.com").build();
    private final ClientConfig config = new ClientConfig();
    private final Client client  = ClientBuilder.newClient(config);
    private final WebTarget target = client.target(baseuri);

    //Request bin target for testing
    private final URI testuri = UriBuilder.fromUri("https://requestb.in/w0zosjw1").build();
    private final ClientConfig tconfig = new ClientConfig();
    private final Client tclient = ClientBuilder.newClient(tconfig);
    private final WebTarget ttarget = tclient.target(testuri);


    private void setProps(){
        try {
            Properties props = new Properties();
            FileInputStream propfile = new FileInputStream("webapi/src/main/java/datasource.properties");
            props.load(propfile);
            propfile.close();
            ehrUsername = props.getProperty("ehrUsername");
            ehrPassword = props.getProperty("ehrPassword");
            appname = props.getProperty("appname");
            svcusername = props.getProperty("svcusername");
            svcpassword = props.getProperty("svcpassword");

        }catch (Exception e){
            System.out.println(e.getMessage());
            return;
        }
        finally {

        }
    }



    private final com.google.gson.Gson gson = new Gson();




    public static void main(String... args){
        Querier query = new Querier();
        query.go();

    }
    public void go(){
        this.setProps();
       //String token = "16E5BF18-A0C2-46C4-AF7A-B8CB3EFD3C3B";
        try{
            String token = this.getToken();

       System.out.println("token received : "+token);
       String authresponse = this.userAuth(token);
       System.out.println("Authorisation response : "+authresponse);
       String serverInfo = this.getServerInfo(token);
      System.out.println("server Info : "+serverInfo);
       String patientInfo = this.getPatientbyID(token,"54250");
      System.out.println("Patient Info: "+patientInfo);
      String providerInfo = this.getProvider(token,"1");
      System.out.println("Provider Info: "+providerInfo);
    }catch (Exception e){
            System.out.println("...");
        }
    }

    String getServerInfo(String token){
        Response response = this.doRequest("GetServerInfo",ehrUsername,appname,"",token,"","","","","","",null);
        return response.readEntity(String.class);
    }

    String getPatientbyID(String token, String patientID){
        Response response = this.doRequest("GetPatient",ehrUsername,appname,patientID,token,"","","","","","",null);
        return response.readEntity(String.class);
    }

    String getToken(){
        JsonObject jo = new JsonObject();
        jo.addProperty("Username",svcusername);
        jo.addProperty("Password",svcpassword);
        //jo.addProperty("OriginalToken",""); //Sleuth sends this too
        Response response = target.path("/Unity/unityservice.svc/json/GetToken").request().post(Entity.entity(jo.toString(), MediaType.APPLICATION_JSON));
        return response.readEntity(String.class);
    }

    String userAuth(String token){
      Response response = this.doRequest("GetUserAuthentication",ehrUsername,appname,"",token,ehrPassword,"","","","","",null);
      return response.readEntity(String.class);

    }

    private Response doRequest(String action, String userid, String appname,String patientid, String token, String param1, String param2,String param3, String param4, String param5, String param6,byte[] data){
        JsonObject jo = new JsonObject( );
        jo.addProperty("Action", action);
        jo.addProperty("Appname", appname);
        jo.addProperty("AppUserID", userid);
        jo.addProperty("PatientID", patientid);
        jo.addProperty("Token", token);
        jo.addProperty("Parameter1", param1);
        jo.addProperty("Parameter2", param2);
        jo.addProperty("Parameter3", param3);
        jo.addProperty("Parameter4", param4);
        jo.addProperty("Parameter5", param5);
        jo.addProperty("Parameter6", param6);
        jo.addProperty("Data", gson.toJson(data));
        try {
            Response response = target.path("/Unity/UnityService.svc/json/MagicJson").request().post(Entity.entity(jo.toString(), MediaType.APPLICATION_JSON));
            //Response response = target.request().post(Entity.entity(jo.toString(), MediaType.APPLICATION_JSON));
            return response;
        }catch (Exception e){
            return null;  // make it a null or error response
        }
    }

    public String getProvider(String token,String providerID,String textProviderID){
        Response response = this.doRequest("GetProvider",ehrUsername,appname,"",token,providerID,textProviderID,"","","","",null);
        return response.readEntity(String.class);

    }
    public String getProvider(String token,String providerID) {
        Response response = this.doRequest("GetProvider", ehrUsername, appname, "", token, providerID, "", "", "", "", "", null);
        return response.readEntity(String.class);
    }


    public Response test(String token){
        JsonObject jo = new JsonObject( );
        jo.addProperty("Action", "GetUserAuthentication");
        jo.addProperty("AppUserID", "terry");
        jo.addProperty("Appname", "AugmentHealthTechnology.AugmentAPI.TestApp");
        jo.addProperty("Token", token);
        jo.addProperty("PatientID", "");
        jo.addProperty("Parameter1", "");
        jo.addProperty("Parameter2", "");
        jo.addProperty("Parameter3", "");
        jo.addProperty("Parameter4", "");
        jo.addProperty("Parameter5", "");
        jo.addProperty("Parameter6", "");
        jo.addProperty("Data", "");
        try {
            Response response = target.path("/Unity/UnityService.svc/json/MagicJson").request().post(Entity.entity(jo.toString(), MediaType.APPLICATION_JSON));
           // Response response = target.request().post(Entity.entity(jo.toString(), MediaType.APPLICATION_JSON));
            return response;
        }catch (Exception e){
            return null;  // make it a null or error response
        }
    }

}
