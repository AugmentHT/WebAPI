package com.augmentht;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.awt.*;

/**Exposes our Api endpoints
 *
 * Created by dkochar on 5/20/2017.
 */
@Path("/test")
public class endpoints {


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(){
        return "Hello this is jersey speaking";
    }



}
