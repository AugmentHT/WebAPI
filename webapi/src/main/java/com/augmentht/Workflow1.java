package com.augmentht;

/**
 * Created by dkochar on 5/24/2017.
 */

import com.augmentht.dao.SprJDao;
import com.augmentht.tablehandler.careOpportunityReceive;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import com.augmentht.tablehandler.patienttable;

public class Workflow1 {

    Querier ehr = new Querier();
    careOpportunityReceive cotable;
    patienttable ptable ;
    JsonParser parser = new JsonParser();
    Workflow1(){
        careOpportunityReceive cotable = new careOpportunityReceive(new SprJDao());



}
public static void main(String... args) {

Workflow1 wf  = new Workflow1();
wf.go();
}

public void go(){
    // Read care opportunity table
    SqlRowSet incoming =  cotable.getUnprocessedCO();

    //get token for operation
    String token = ehr.getToken();
    ehr.userAuth(token);
//loop here for each row in sql
    while(incoming.next()) {

        //Get a patient
        String PatientID = incoming.getString("COPatientId");
        //Check if Id is EHR's id
        if(true){
           String incjson = ehr.getPatientbyID(token,PatientID);

           //Parse Data
            JsonElement pJson = parser.parse(incjson);
           // pJson.getAsJsonArray("getpatientinfo");
            //Get all fields and send to
            /*Incoming -"Nickname": "","Language": "English","HomeEmail": "test@email.com","trainingPat": "N",
      "activePat": "Y","PhysLastName": "Manning","PhoneNumber": "9195557855","age": "41y","middlename": "S",
      "mrn": "3230000","nextappointment": "No next appointment has been scheduled","Occupation": "",
      "CellPhone": "9191340980","Email": "test@email.com","LastName": "Aaron","WorkPhone": "",
      "MostRecentWeight": "220 lb (as of 06/18/2013)","gender": "M","ID": "54520","WorkEmail": "test2@email.com",
      "Race": "Black or African American","ssn": "000-07-2756","dateofbirth": "23-Aug-1975","AddressLine2": "",
      "ZipCode": "27511","Title": "","State": "NC   ","PhysPhone": "8888287788889","Firstname": "Bill",
      "MaritalStatus": "","Ethnicity": "Not Hispanic or Latino","Addressline1": "9825 Tilden St ",
      "PhysFirstName": "Terry","PrimaryInsurance": "No primary insurance","lastappointment": "Last Appointment: 26-May-2016 9:00 am with Manning, Terry A MD",
      "base64image": "","timeofbirth": "","PhysUserName": "TERRY","City": "Raleigh","Employer": "",
      "HomePhone": "9195557855"



Destination format - PatientId, PatientFirstName, PatientMiddleName, PatientLastName, PatientSuffix, COPatientId, EHRPatientId, PatientEMPI, PatientSSN, CreatedDate, CreatedBy, ModifiedDate, ModifiedBy
            */



           //Load patient data into patient and address table






        }else{
            System.out.println("probably have to search and stuff");
        }






        //Get Provider Info


        //Load provider data into provider and adress table


        //Load CO data after remapping parameters into //remap from ehrparametermapping CODatasendqueue table


        //remap to api required format,Hit ehr api, send the CO data with attachments

    }
}
}
