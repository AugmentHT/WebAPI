package com.augmentht;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.awt.*;

/**Exposes our Api endpoints
 *
 * Created by dkochar on 5/20/2017.
 */
@Path("/")
public class endpoints {


    @Path("/workflowdemo")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String executeWorkflow(){
        return new Workflow1().go();
    }

    @Path("/test")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String formtest(@FormParam("proID") String providerID){return "received-"+providerID;}
}
