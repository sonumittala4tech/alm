package com.hpalm.afour.alm;
import com.hpalm.afour.infrastructure.*;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * This example shows multiple ways to create an entity,
 * and how to remove entities.
 *
 */
public class CreateDeleteEntity {

    private RestConnector con;

    public CreateDeleteEntity() {
        con = RestConnector.getInstance();
    }

    /**
     * @param collectionUrl
     *            the url of the collection of the relevant entity type.
     * @param postedEntityXml
     *            the xml describing an instance of said entity type.
     * @return the url of the newly created entity.
     */
    public String createEntity(String collectionUrl, String postedEntityXml)
            throws Exception {

        Map<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put("Content-Type", "application/xml");
        requestHeaders.put("Accept", "application/xml");

        // As can be seen in the implementation below, creating an entity
        //is simply posting its xml into the correct collection.
        Response response = con.httpPost(collectionUrl,
                postedEntityXml.getBytes(), requestHeaders);

        Exception failure = response.getFailure();
        if (failure != null) {
            throw failure;
        }

        /*
         Note that we also get the xml of the newly created entity.
         at the same time we get the url where it was created in a
         location response header.
        */
        String entityUrl =
                response.getResponseHeaders().get("Location").iterator().next();
        return entityUrl;
    }

    /**
     * @param entityUrl
     *            the url of the entity that we wish to remove
     * @return xml of deleted entity
     */
    public String deleteEntity(String entityUrl) throws Exception {

        // As we can see from the implementation below, to delete an entity
        // is simply to use HTTP delete on its url.
        Map<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put("Accept", "application/xml");

        Response serverResponse = con.httpDelete(entityUrl, requestHeaders);
        if (serverResponse.getStatusCode() != HttpURLConnection.HTTP_OK) {
            throw new Exception(serverResponse.toString());
        }

        return serverResponse.toString();
    }

}
