package com.augmentht;

/**
 * Created by dkochar on 5/24/2017.
 */

import com.augmentht.dao.SprJDao;
import com.augmentht.tablehandler.careOpportunityReceive;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import com.augmentht.tablehandler.patienttable;
import com.augmentht.tablehandler.addresstable;
import com.augmentht.tablehandler.providerTable;
import com.augmentht.tablehandler.codatasendtable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Workflow1 {

    Querier ehr = new Querier();
    careOpportunityReceive cotable;
    patienttable ptable;
    addresstable atable;
    providerTable proTable;
    codatasendtable codatasendtable;

    JsonParser parser = new JsonParser();
    DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    Workflow1() {
        SprJDao dao = new SprJDao();
        cotable = new careOpportunityReceive(dao);
        ptable = new patienttable(dao);
        atable = new addresstable(dao);
        proTable = new providerTable(dao);
        codatasendtable = new codatasendtable(dao);


    }

    public static void main(String... args) {

        Workflow1 wf = new Workflow1();
        wf.go();
    }

    public String go() {
        StringBuffer chatter = new StringBuffer(5000);
        // Read care opportunity table
        SqlRowSet incoming = cotable.getUnprocessedCO();

        //get token for operation
        chatter.append("Fetching token for Pro Latest...");
        String token = ehr.getToken();
        chatter.append("Fetched token :"+token);
        chatter.append("Authenticating user...");
        ehr.userAuth(token);
        chatter.append("Authenticated.");
//loop here for each row in sql
        while (incoming.next()) {

            //Get a patient
            String PatientID = incoming.getString("COPatientId");
            //Check if Id is EHR's id
            if (true) {
                String incjson = ehr.getPatientbyID(token, PatientID);
                incjson = incjson.substring(1, incjson.length() - 1);//dont need the outer square brackets, is there a better way to do this...


                //Parse Data
                JsonObject pJson = parser.parse(incjson).getAsJsonObject();

                JsonObject patient = pJson.get("getpatientinfo").getAsJsonArray().get(0).getAsJsonObject();
                String patientID = patient.get("ID").getAsString();
                String PatientFirstName = patient.get("Firstname").getAsString();
                String PatientMiddleName = patient.get("middlename").getAsString();
                String PatientLastName = patient.get("LastName").getAsString();
                String PatientSuffix = patient.get("Title").getAsString();
                //What is patientSuffix for
                //How is this different from id
                String COPatientId = PatientID;//patient.get("mrn").getAsString();
                String EHRPatientId = PatientID;

                //EMPI ???
                String PatientEMPI = patient.get("mrn").getAsString();
                String PatientSSN = patient.get("ssn").getAsString().replaceAll("-", "");//removes the dashes
                String CreatedDate = sdf.format(new Date());
                int CreatedBy = 47; // admin code ?
                String ModifiedDate = CreatedDate;
                int ModifiedBy = CreatedBy;
                // so now can save to patient table
                chatter.append("Saving patient "+PatientFirstName+PatientLastName+" to table...");
                ptable.execute("insert into ocean.patient values('" + PatientID + "','" + PatientFirstName + "','" + PatientMiddleName + "','" + PatientLastName + "','" + PatientSuffix + "','" + COPatientId + "','" + EHRPatientId + "','" + PatientEMPI + "','" + PatientSSN + "','" + CreatedDate + "','" + CreatedBy + "','" + ModifiedDate + "','" + ModifiedBy + "')");
                chatter.append("Patient record stored");
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
                //now to parse address  . getPatient returns partial address, there is another call GetPatientFull, maybe do that ?

                int AddressId = 47;
                String Address1 = patient.get("Addressline1").getAsString();
                String Address2 = patient.get("AddressLine2").getAsString();
                String Address3 = "placeholder";
                String AddressName = "";
                String City = patient.get("City").getAsString();
                String State = patient.get("State").getAsString();
                String ZipCode = patient.get("ZipCode").getAsString();
                String AddressType = "9";
                String Verified = "0";
                CreatedDate = sdf.format(new Date());
                CreatedBy = 47;
                ModifiedDate = CreatedDate;
                ModifiedBy = CreatedBy;
                chatter.append("Saving patient address to table..");
                //Now save to addresstable.
                atable.execute("insert into ocean.address values ('" + "47" + "','" + Address1 + "','" + Address2 + "','" + Address3 + "','" + AddressName + "','" + City + "','" + State + "','" + ZipCode + "','" + AddressType + "','" + Verified + "','" + CreatedDate + "','" + CreatedBy + "','" + ModifiedDate + "','" + ModifiedBy + "')");
                chatter.append("Patient address stored.");
            /*
            Outogoing data:AddressId, Address1, Address2, Address3, AddressName, City, State, ZipCode, AddressType, Verified, CreatedDate, CreatedBy, ModifiedDate, ModifiedBy*/


                //Load patient data into patient and address table


            } else {
                chatter.append("probably have to search and stuff");
            }


            //Get Provider Info

            String ProviderID = incoming.getString("COProviderId");
            String proData = ehr.getProvider(token, ProviderID);
            proData = proData.substring(1, proData.length() - 1);//dont want the square brackets around it


            //Parse Data
            JsonObject proJson = parser.parse(proData).getAsJsonObject();
            JsonObject provider = proJson.get("getproviderinfo").getAsJsonArray().get(0).getAsJsonObject();

            String ProviderId = ProviderID;
            String ProviderFirstName = provider.get("FirstName").getAsString();
            String ProviderMiddleName = provider.get("MiddleName").getAsString();
            String ProviderLastName = provider.get("LastName").getAsString();
            String ClientID = incoming.getString("ClientId");//provider.get("personid").getAsString();
            String ProviderNPI = provider.get("NPI").getAsString();
            String CreatedDate = sdf.format(new Date());
            int CreatedBy = 42;
            String ModifiedDate = CreatedDate;
            int ModifiedBy = CreatedBy;
            chatter.append("Saving provider to table...");
            proTable.execute("insert into ocean.provider values ('" + ProviderId + "','" + ProviderFirstName + "','" + ProviderMiddleName + "','" + ProviderLastName + "','" + ClientID + "','" + ProviderNPI + "','" + CreatedDate + "','" + CreatedBy + "','" + ModifiedDate + "','" + ModifiedBy + "');");
//Load provider data into provider and adress table
            chatter.append("Provider "+ProviderFirstName+ProviderLastName+" saved.");
            /*                       Typical response:

            {
  "getproviderinfo": [
    {
      "SuffixName": "",
      "LastName": "Manager",
      "AddressLine1": "5501 Dillard Drive",
      "ZipCode": "27609",
      "Fax": "",
      "EntryMnemonic": "",
      "TitleName": "",
      "ProviderKeyEXT": "",
      "Specialty": "Undefined",
      "PrefixName": "",
      "FirstName": "System",
      "DEANumber": "",
      "ProviderIsInactiveFLAG": "",
      "ExpirationDT": "",
      "NPI": "",
      "City": "Cary",
      "Phone": "",
      "AddressLine2": "",
      "MiddleName": "",
      "personid": "SM1",
      "State": "NC",
      "EntryCode": "1"
    }
  ]

            ProviderId, ProviderFirstName, ProviderMiddleName, ProviderLastName, ClientId, ProviderNPI, CreatedDate, CreatedBy, ModifiedDate, ModifiedBy


             */

            //do same for provider address


            //do remap


            //save to data send queue

/*Columns of sendqueue table
CareOpportunityDataSendQueueId, ClientId, COPatientId, COProviderId, EHRPatientId, EHRProviderId, CareOpportunityDescription, COAttachmentTypeID, COAttachmentDescription, COAttachment, Parameter1, Parameter2, Parameter3, Parameter4, Parameter5, Parameter6, Parameter7, Parameter8, CreatedDate, CreatedBy, ModifiedDate, ModifiedBy
 */
            chatter.append("Saving to Datasendtable...");
            codatasendtable.execute("insert into ocean.careopportunitydatasendqueue values('" + "1" + "','" + ClientID + "','" + PatientID + "','" + ClientID + "','" + PatientID + "','" + ClientID + "','" + incoming.getString("CareOpportunityDescription") + "','" + incoming.getString("COAttachmentTypeID") + "','" + incoming.getString("COAttachmentDescription") + "','" + incoming.getString("COAttachment") + "','" + incoming.getString("Parameter1") + "','" + incoming.getString("Parameter2") + "','" + incoming.getString("Parameter3") + "','" + incoming.getString("Parameter4") + "','" + incoming.getString("Parameter5") + "','" + incoming.getString("Parameter6") + "','" + incoming.getString("Parameter7") + "','" + incoming.getString("Parameter8") + "','" + CreatedDate + "','" + CreatedBy + "','" + ModifiedDate + "','" + ModifiedBy + "')");
            chatter.append("Saved to datasendtable successfully.");

            //Savetask
/*
Expectation:
UserID,AppName,PatientID,Token,
Param 1= Tasktype(from Referral,review,Send Chart)
Param 2 = TargetUser
Param 3 = WorkObjectID = Optional, if document included, supply doc iD
pARAM 4 = Info(can set subject if subject not defind)

Param 5 = (Optional) Info = XML extra info<taskdata>
<taskpriority>Routine</taskpriority>
<subject>A subject</subject>
<messagetype>Send Chart</messagetype>
</taskdata>
 Param 6 = (Optional) Format of the next binary data(text/pdf)

return 2 object json -status(always 0) TaskID

 */
            chatter.append("Sending task to EHR...");
            String actionReport = ehr.saveTask(token, PatientID, incoming.getString("Parameter1"), ClientID, incoming.getString("Parameter2"), incoming.getString("CareOpportunityDescription"), incoming.getString("Parameter3"), incoming.getString("COAttachmentTypeID"), incoming.getString("COAttachment").getBytes());
            chatter.append("Task report successfully sent.");
            //chatter.append(actionReport);
            actionReport = actionReport.substring(1, actionReport.length() - 1);
            System.out.println(actionReport);
            JsonObject taskReturn = parser.parse(actionReport).getAsJsonObject();
            JsonObject taskret = taskReturn.get("savetaskinfo").getAsJsonArray().get(0).getAsJsonObject();
            String taskID = taskret.get("TaskId").getAsString();
            chatter.append("Task was stored with TaskId:" + taskID);
            String task = ehr.getTaskbyID(token, taskID);
            chatter.append("Retrieved task was :" + task);


            //Load CO data after remapping parameters into //remap from ehrparametermapping CODatasendqueue table


            //remap to api required format,Hit ehr api, send the CO data with attachments


        }
    return chatter.toString();
    }
}
