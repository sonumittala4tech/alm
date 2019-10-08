package com.hpalm.afour.test;

import com.hpalm.afour.alm.AuthenticateLoginLogout;
import com.hpalm.afour.alm.CommonFunctionLib;
import com.hpalm.afour.alm.UpdateEntity;
import com.hpalm.afour.infrastructure.*;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Rajan Bansod on 2/15/2017.
 */
public class AttachScreenshotToEntity {

    /**
     * Method to attach Screenshot to Entity like com.hpalm.afour.test step, defect, com.hpalm.afour.test run etc.
     *
     * @param serverUrl                https://hq-qcapp-prd:8080
     * @param domain                   ISI
     * @param project                  ISI_AutomatedTest_Repository
     * @param username                 rajanb
     * @param password                 abc@123
     * @param entityType               run-step, com.hpalm.afour.test, run, defect etc
     * @param entityKeyToSearch        name or status or owner etc
     * @param entityValueToSearch      testName or passed or rajanb etc
     * @param runStepKeyToSearch       null or name or any step field etc
     * @param runStepValueToSearch     null or Step 1 etc
     * @param multipartFileName        "screenshot.png" fileName to display in ALM
     * @param multipartFilPath         filepath
     * @param multipartFileDescription description of attachment
     * @throws Exception
     */
    protected void attachScreenshotToEntity(final String serverUrl, final String domain,
                                            final String project, String username, String password, String entityType, String entityKeyToSearch,
                                            String entityValueToSearch, String runStepKeyToSearch, String runStepValueToSearch, String multipartFileName,
                                            String multipartFilPath, String multipartFileDescription)
            throws Exception {

        RestConnector con =
                RestConnector.getInstance().init(
                        new HashMap<String, String>(),
                        serverUrl,
                        domain,
                        project);

        /*
          We use the login example to handle our login for this example.
          You can view that code to learn more on authentication.

          We use the write example to create an entity for us to attach files
          to. To learn more on creating entities view that code.

          We use the update example to lock the entity to which we want
          to attach files. This is an "updating" operation.
          View that code to learn more on updating.
        */
        AuthenticateLoginLogout login =
                new AuthenticateLoginLogout();
        UpdateEntity updater = new UpdateEntity();
        AttachScreenshotToEntity example = new AttachScreenshotToEntity();
        CommonFunctionLib commLib = new CommonFunctionLib();
        boolean isVersioned = false;
        String createdEntityUrl = null;

        boolean loginResponse = login.login(username, password);
        Assert.assertTrue("login failed", loginResponse);

        // Added the following method to the class RestConnector.java.
        // Once called the method login() from the class RestConnector, call this method getQcSession().
        con.getQCSession();


        final String requirementsUrl =
                con.buildEntityCollectionUrl(entityType);
        Map<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put("Accept", "application/xml");

        try {

            if (!entityKeyToSearch.equalsIgnoreCase("id")) {
                String id = commLib.getEntityID(entityType, entityKeyToSearch, entityValueToSearch, con, requestHeaders);
                createdEntityUrl = requirementsUrl + "/" + id;
            } else {
                createdEntityUrl = requirementsUrl + "/" + entityValueToSearch;
            }

            // Add attachment to run step if runStepKeyToSearch value is not null else attach screenshot to given entity like defect , com.hpalm.afour.test run, com.hpalm.afour.test case
            if ((runStepKeyToSearch != null) && !runStepKeyToSearch.isEmpty()) {

                createdEntityUrl = createdEntityUrl + "/run-steps";
                String stepRunUrl =
                        con.buildEntityCollectionUrl("run-step");

                if (!runStepKeyToSearch.equalsIgnoreCase("id")) {
                    String id = commLib.getEntityIDByURL(createdEntityUrl, runStepKeyToSearch, runStepValueToSearch, con, requestHeaders);
                    createdEntityUrl = stepRunUrl + "/" + id;
                } else {
                    createdEntityUrl = stepRunUrl + "/" + runStepValueToSearch;
                }
            }

            // Before editing an entity, lock it is versioning is not enabled,
            // or check it out, if versioning is enabled.
            isVersioned = Constants.isVersioned(entityType,
                    domain, project);
            String preModificationXml = null;
            if (isVersioned) {

                // Note that we selected an entity that supports versioning
                // on a project that supports versioning. Would fail otherwise.
                String firstCheckoutComment = "check out comment1";
                preModificationXml =
                        updater.checkout(createdEntityUrl, firstCheckoutComment, -1);
                Assert.assertTrue(
                        "checkout comment missing",
                        preModificationXml.contains(Constants.generateFieldXml(
                                "vc-checkout-comments",
                                firstCheckoutComment)));
            } else {

                preModificationXml = updater.lock(createdEntityUrl);
            }

            String newMultiPartAttachmentUrl =
                    example.attachWithMultipart(
                            createdEntityUrl,
                            Files.readAllBytes(Paths.get(multipartFilPath)),
                            "image/png",
                            multipartFileName,
                            multipartFileDescription);
            try {
                // Changes aren't visible to other users until we check them
                //  in if versioned
                if (isVersioned) {
                    String firstCheckinComment = "check in comment1";
                    boolean checkin =
                            updater.checkin(createdEntityUrl, firstCheckinComment, false);
                    Assert.assertTrue("checkin failed", checkin);
                } else {

                    boolean unlock = updater.unlock(createdEntityUrl);
                    Assert.assertTrue("unlock failed", unlock);
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //checkin
            if (isVersioned) {
                final String firstCheckinComment = "check in comment1";
                boolean checkin =
                        updater.checkin(createdEntityUrl, firstCheckinComment, false);
                Assert.assertTrue("checkin failed", checkin);
            } else {
                boolean unlock = updater.unlock(createdEntityUrl);
                Assert.assertTrue("unlock failed", unlock);
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
        login.logout();
    }

    RestConnector con;

    /**
     * AttachScreenshotToEntity
     */
    public AttachScreenshotToEntity() {
        con = RestConnector.getInstance();
    }

    /**
     * @param entityUrl of the entity whose attachments we want to get
     * @return an xml with metadata on all attachmens of the entity
     * @throws Exception
     */
    private String readAttachments(String entityUrl) throws Exception {

        Map<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put("Accept", "application/xml");

        Response readResponse =
                con.httpGet(entityUrl + "/attachments", null, requestHeaders);

        if (readResponse.getStatusCode() != HttpURLConnection.HTTP_OK) {
            throw new Exception(readResponse.toString());
        }

        return readResponse.toString();
    }

    /**
     * @param entityUrl   url of entity to attach the file to
     * @param fileData    content of file
     * @param contentType of the file - txt/html or xml, or octetstream etc..
     * @param filename    to use on serverside
     * @return
     */
    private String attachWithMultipart(
            String entityUrl,
            byte[] fileData,
            String contentType,
            String filename,
            String description) throws Exception {
        /*

headers:
Content-Type: multipart/form-data; boundary=<boundary>

//template for file mime part:
--<boundary>\r\n
Content-Disposition: form-data; name="<fieldName>"; filename="<filename>"\r\n
Content-Type: <mime-type>\r\n
\r\n
<file-data>\r\n
<boundary>--

//template for post parameter mime part, such as description and/or filename:
--<boundary>\r\n
    Content-Disposition: form-data; name="<fieldName>"\r\n
    \r\n
    <value>\r\n
<boundary>--

//end of parts:
--<boundary>--

we need 3 parts:
filename(template for parameter), description(template for parameter),
and file data(template for file).

         */

        // This can be pretty much any string.
        // It's used to mark the different mime parts
        String boundary = "exampleboundary";

        //template to use when sending field data (assuming none-binary data)
        String fieldTemplate =
                "--%1$s\r\n"
                        + "Content-Disposition: form-data; name=\"%2$s\" \r\n\r\n"
                        + "%3$s"
                        + "\r\n";

        // Template to use when sending file data.
        // Binary data still needs to be suffixed.
        String fileDataPrefixTemplate =
                "--%1$s\r\n"
                        + "Content-Disposition: form-data; name=\"%2$s\"; filename=\"%3$s\"\r\n"
                        + "Content-Type: %4$s\r\n\r\n";

        String filenameData = String.format(fieldTemplate, boundary,
                "filename", filename);
        String descriptionData = String.format(fieldTemplate, boundary,
                "description", description);
        String fileDataSuffix = "\r\n--" + boundary + "--";
        String fileDataPrefix =
                String.format(fileDataPrefixTemplate, boundary, "file",
                        filename, contentType);

        // Note the order - extremely important:
        // Filename and description before file data.
        // Name of file in file part and filename part value MUST MATCH.
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bytes.write(filenameData.getBytes());
        bytes.write(descriptionData.getBytes());
        bytes.write(fileDataPrefix.getBytes());
        bytes.write(fileData);
        bytes.write(fileDataSuffix.getBytes());
        bytes.close();

        Map<String, String> requestHeaders = new HashMap<String, String>();

        requestHeaders.put("Content-Type", "multipart/form-data; boundary="
                + boundary);

        Response response =
                con.httpPost(entityUrl + "/attachments", bytes.toByteArray(),
                        requestHeaders);

        if (response.getStatusCode() != HttpURLConnection.HTTP_CREATED) {
            throw new Exception(response.toString());
        }

        return response.getResponseHeaders().get("Location").iterator().next();
    }
}
