package com.hpalm.afour.alm;

import com.hpalm.afour.infrastructure.*;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * This example shows how to change data on already existing entities.
 *
 */

public class UpdateEntity {

    private RestConnector con;

    /**
     * @param
     */
    public UpdateEntity() {
        con = RestConnector.getInstance();
    }

    /**
     * @param entityUrl
     *            of the entity to checkout
     * @param comment
     *            to keep on the server side of why you checked this entity out
     * @param version
     *            to checkout or -1 if you want the latest
     * @return a string description of the checked out entity
     * @throws Exception
     */
    public String checkout(String entityUrl, String comment, int version)
            throws Exception {

        String commentXmlBit =
                ((comment != null) && !comment.isEmpty()
                        ? "<Comment>" + comment + "</Comment>"
                        : "");

        String versionXmlBit =
                (version >= 0 ? "<Version>" + version + "</Version>" : "");

        String xmlData = commentXmlBit + versionXmlBit;

        String xml =
                xmlData.isEmpty() ? "" : "<CheckOutParameters>"
                        + xmlData + "</CheckOutParameters>";

        Map<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put("Content-Type", "application/xml");
        requestHeaders.put("Accept", "application/xml");

        Response response =
                con.httpPost(entityUrl + "/versions/check-out", xml.getBytes(), requestHeaders);

        if (response.getStatusCode() != HttpURLConnection.HTTP_OK) {
            throw new Exception(response.toString());
        }

        return response.toString();
    }

    /**
     * @param entityUrl
     *            to checkin
     * @param comment
     *            this will override any comment you made in the checkout
     * @param overrideLastVersion
     *            this will override last version
     * @return true if operation is successful
     * @throws Exception
     */
    public boolean checkin(String entityUrl, String comment,
                           boolean overrideLastVersion) throws Exception {

        final String commentXmlBit =
                ((comment != null) && !comment.isEmpty()
                        ? "<Comment>" + comment + "</Comment>"
                        : "");

        final String overrideLastVersionBit =
                overrideLastVersion == true ?
                        "<OverrideLastVersion>true</OverrideLastVersion>" : "" ;

        final String xmlData = commentXmlBit + overrideLastVersionBit;

        final String xml =
                xmlData.isEmpty() ? "" : "<CheckInParameters>" + xmlData + "</CheckInParameters>";

        final Map<String, String> requestHeaders =
                new HashMap<String, String>();
        requestHeaders.put("Content-Type", "application/xml");

        //just execute a post operation on the checkin resource of your entity
        Response response =
                con.httpPost(entityUrl + "/versions/check-in", xml.getBytes(),
                        requestHeaders);

        boolean ret = response.getStatusCode() == HttpURLConnection.HTTP_OK;

        return ret;
    }

    /**
     * @param entityUrl
     *            to lock
     * @return the locked entity xml
     * @throws Exception
     */
    public String lock(String entityUrl) throws Exception {

        Map<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put("Accept", "application/xml");

        Response lockResponse = con.httpPost(entityUrl + "/lock", null,
                requestHeaders);
        if (lockResponse.getStatusCode() != HttpURLConnection.HTTP_OK) {
         //   throw new Exception(lockResponse.toString());
        }
        return lockResponse.toString();
    }

    /**
     * @param entityUrl
     *            to unlock
     * @return
     * @throws Exception
     */
    public boolean unlock(String entityUrl) throws Exception {

        return con.httpDelete(entityUrl + "/lock", null).getStatusCode() == HttpURLConnection.HTTP_OK;
    }

    /**
     * @param field
     *            the field name to update
     * @param value
     *            the new value to use
     * @return an xml that can be used to update an entity's single
     *          given field to given value
     */
    private static String generateSingleFieldUpdateXml(String field,
                                                       String value) {
        return "<Entity Type=\"defect\"><Fields>"
                + Constants.generateFieldXml(field, value)
                + "</Fields></Entity>";
    }

    /**
     * @param entityUrl
     *            to update
     * @param updatedEntityXml
     *            New entity descripion. Only lists updated fields.
     *            Unmentioned fields will not change.
     *
     * @return xml description of the entity on the serverside, after update.
     * @throws Exception
     */
    public Response update(String entityUrl, String updatedEntityXml)
            throws Exception {

        Map<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put("Content-Type", "application/xml");
        requestHeaders.put("Accept", "application/xml");

        Response putResponse =
                con.httpPut(entityUrl, updatedEntityXml.getBytes(), requestHeaders);

        if (putResponse.getStatusCode() != HttpURLConnection.HTTP_OK) {
            throw new Exception(putResponse.toString());
        }

        return putResponse;
    }

}
