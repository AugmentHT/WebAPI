package com.augmentht;


import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 * Created by dkochar on 5/16/2017.
 */
public class GetTests {
    Credentials cred = new Credentials();
    ClientConfig config = new ClientConfig();
    Client client = ClientBuilder.newClient(config);
    //WebTarget target = client.target();

}
