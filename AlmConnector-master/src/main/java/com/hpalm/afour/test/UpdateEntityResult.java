package com.hpalm.afour.test;

import com.hpalm.afour.alm.AuthenticateLoginLogout;
import com.hpalm.afour.alm.CommonFunctionLib;
import com.hpalm.afour.alm.UpdateEntity;
import com.hpalm.afour.infrastructure.*;

import java.util.HashMap;
import java.util.Map;

/**
 * This example shows how to change data on already existing entities.
 * <p>
 * Created by Rajan Bansod on 2/14/2017.
 */
public class UpdateEntityResult {

    /**
     * Method to update Entity like com.hpalm.afour.test step, defect, com.hpalm.afour.test run etc with given field.
     *
     * @param serverUrl                      https://hq-qcapp-prd:8080
     * @param domain                         ISI
     * @param project                        ISI_AutomatedTest_Repository
     * @param username                       rajanb
     * @param password                       abc@123
     * @param entityType                     Entity like com.hpalm.afour.test step, defect, com.hpalm.afour.test run etc
     * @param entityKeyToSearch              name or status or owner etc
     * @param entityValueToSearch            testName or passed or rajanb etc
     * @param runStepKeyToSearch             null or name or any step field etc
     * @param runStepValueToSearch           null or Step 1 etc
     * @param updatedField                   Field to update
     * @param updatedFieldInitialUpdateValue Field value to update
     * @param actualValue                    'actual' field value to update
     * @throws Exception
     */
    protected String updateEntityResult(final String serverUrl, final String domain,
                                      final String project, String username, String password, String entityType, String entityKeyToSearch, String entityValueToSearch, String runStepKeyToSearch, String runStepValueToSearch, String updatedField, String updatedFieldInitialUpdateValue, String actualValue)
            throws Exception {

        if (updatedFieldInitialUpdateValue.equalsIgnoreCase("Passed")) {
            updatedFieldInitialUpdateValue = "Completed";
        } else if (updatedFieldInitialUpdateValue.equalsIgnoreCase("Failed")) {
            updatedFieldInitialUpdateValue = "Not Completed";
        }

        RestConnector con =
                RestConnector.getInstance().init(
                        new HashMap<String, String>(),
                        serverUrl,
                        domain,
                        project);

        AuthenticateLoginLogout login =
                new AuthenticateLoginLogout();
        UpdateEntity example = new UpdateEntity();
        CommonFunctionLib commLib = new CommonFunctionLib();
        String newEntityToUpdateUrl = "";
        String returnID = "";
        try {
            // We use the example code of how to login to handle our login
            // in this example.
            boolean loginResponse = login.login(username, password);
            Assert.assertTrue("login failed", loginResponse);

            // Added the following method to the class RestConnector.java.
            // Once called the method login() from the class RestConnector, call this method getQcSession().
            con.getQCSession();

            Map<String, String> requestHeaders = new HashMap<String, String>();
            requestHeaders.put("Accept", "application/xml");

            String requirementsUrl =
                    con.buildEntityCollectionUrl(entityType);

            // Get entity id to build the url
            if (!entityKeyToSearch.equalsIgnoreCase("id")) {
                String id = commLib.getEntityID(entityType, entityKeyToSearch, entityValueToSearch, con, requestHeaders);
                newEntityToUpdateUrl = requirementsUrl + "/" + id;
                returnID = id;
            } else {
                newEntityToUpdateUrl = requirementsUrl + "/" + entityValueToSearch;
            }

            String runStepsToUpdateUrl = "";
            String updatedEntityXml = "";
            String runStepsUrl = newEntityToUpdateUrl + "/run-steps";

            if ((runStepKeyToSearch != null) && !runStepKeyToSearch.isEmpty()) {

                String stepRunUrl =
                        con.buildEntityCollectionUrl("run-step");

                // Get entity id to build the url
                runStepsToUpdateUrl = newEntityToUpdateUrl + "/run-steps";
                if (!entityKeyToSearch.equalsIgnoreCase("id")) {
                    String id = commLib.getEntityIDByURL(runStepsToUpdateUrl, runStepKeyToSearch, runStepValueToSearch, con, requestHeaders);
                    runStepsToUpdateUrl = stepRunUrl + "/" + id;
                    returnID = id;
                } else {
                    runStepsToUpdateUrl = stepRunUrl + "/" + runStepValueToSearch;
                }

                updatedEntityXml =
                        commLib.generateSingleFieldUpdateXml("run-step", updatedField,
                                updatedFieldInitialUpdateValue, actualValue);
            } else {
                updatedEntityXml =
                        commLib.generateSingleFieldUpdateXml(entityType, updatedField,
                                updatedFieldInitialUpdateValue, actualValue);
            }

            //checkout (or lock) the entity - depending on versioning support.
            boolean isVersioned = Constants.isVersioned(entityType,
                    domain, project);
            String preModificationXml = null;
            if (isVersioned) {

                // Note that we selected an entity that supports versioning
                // on a project that supports versioning. Would fail otherwise.
                String firstCheckoutComment = "check out comment1";
                preModificationXml = example.checkout(newEntityToUpdateUrl,
                        firstCheckoutComment, -1);
                Assert.assertTrue(
                        "checkout comment missing",
                        preModificationXml.contains(Constants.generateFieldXml(
                                "vc-checkout-comments",
                                firstCheckoutComment)));
            } else {

                preModificationXml = example.lock(newEntityToUpdateUrl);
            }

            Assert.assertTrue(
                    "posted field value not found",
                    preModificationXml.contains(updatedField));

            if ((runStepKeyToSearch != null) && !runStepKeyToSearch.isEmpty()) {
                //update the entity
                String put = example.update(runStepsToUpdateUrl,
                        updatedEntityXml).toString();
                Assert.assertTrue("posted field value not found",
                        put.contains(updatedFieldInitialUpdateValue));

                if (updatedFieldInitialUpdateValue.equalsIgnoreCase("Not Completed")) {

                    updatedEntityXml =
                            commLib.generateSingleFieldUpdateXml(entityType, updatedField,
                                    updatedFieldInitialUpdateValue, actualValue);

                    //update the entity
                    put = example.update(newEntityToUpdateUrl,
                            updatedEntityXml).toString();
                    Assert.assertTrue("posted field value not found",
                            put.contains(updatedFieldInitialUpdateValue));

                    updatedEntityXml =
                            commLib.generateSingleFieldUpdateXml("test", "exec-status",
                                    "Completed", actualValue);
                    String testID = commLib.getValueFromXMLUsingFieldName(put, "test-id");

                    String testUrl =
                            con.buildEntityCollectionUrl("test");

                    String testToUpdateUrl = testUrl + "/" + testID;

                    //update the entity
                    put = example.update(testToUpdateUrl,
                            updatedEntityXml).toString();

                } else {
                    //update the entity
                    Response serverResponse =
                            con.httpGet(runStepsUrl, null, requestHeaders);
                    String serverResponseString = serverResponse.toString();
                    if (!serverResponseString.contains("<Field Name=\"status\"><Value>Blocked</Value></Field></Fields>")
                            || !serverResponseString.contains("<Field Name=\"status\"><Value>No Run</Value></Field></Fields>")
                            || !serverResponseString.contains("<Field Name=\"status\"><Value>Not Completed</Value></Field></Fields>")
                            || serverResponseString.contains("<Field Name=\"status\"><Value>Completed</Value></Field></Fields>")) {

                        updatedEntityXml =
                                commLib.generateSingleFieldUpdateXml("test", "exec-status",
                                        "Passed", actualValue);
                        String testID = commLib.getValueFromXMLUsingFieldName(serverResponseString, "test-id");

                        String testUrl =
                                con.buildEntityCollectionUrl("test");

                        String testToUpdateUrl = testUrl + "/" + testID;

                        //update the entity
                        put = example.update(testToUpdateUrl,
                                updatedEntityXml).toString();
                        updatedEntityXml =
                                commLib.generateSingleFieldUpdateXml(entityType, updatedField,
                                        updatedFieldInitialUpdateValue, actualValue);

                        //update the entity
                        put = example.update(newEntityToUpdateUrl,
                                updatedEntityXml).toString();
                    }

                    if (serverResponseString.contains("<Field Name=\"status\"><Value></Value></Field></Fields>")
                            || serverResponseString.contains("<Field Name=\"status\"><Value>No Run</Value></Field></Fields>")) {

                        updatedEntityXml =
                                commLib.generateSingleFieldUpdateXml("test", "exec-status",
                                        "In Progress", actualValue);
                        String testID = commLib.getValueFromXMLUsingFieldName(serverResponseString, "test-id");

                        String testUrl =
                                con.buildEntityCollectionUrl("test");

                        String testToUpdateUrl = testUrl + "/" + testID;

                        //update the entity
                        put = example.update(testToUpdateUrl,
                                updatedEntityXml).toString();
                        Assert.assertTrue("posted field value not found",
                                put.contains("In Progress"));

                        updatedEntityXml =
                                commLib.generateSingleFieldUpdateXml("run", "status",
                                        "In Progress", actualValue);

                        //update the entity
                        put = example.update(newEntityToUpdateUrl,
                                updatedEntityXml).toString();
                        Assert.assertTrue("posted field value not found",
                                put.contains("In Progress"));
                    }
                }
            } else {

                //update the entity
                String put = example.update(newEntityToUpdateUrl,
                        updatedEntityXml).toString();
                Assert.assertTrue("posted field value not found",
                        put.contains(updatedFieldInitialUpdateValue));
            }

            //checkin (or unlock) the entity - depending on versioning support.
            try {
                if (isVersioned) {
                    String firstCheckinComment = "check in comment1";
                    boolean checkin = example.checkin(newEntityToUpdateUrl,
                            firstCheckinComment, false);
                    Assert.assertTrue("checkin failed", checkin);
                } else {

                    boolean unlock = example.unlock(newEntityToUpdateUrl);
                    Assert.assertTrue("unlock failed", unlock);
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        try {
            boolean unlock = example.unlock(newEntityToUpdateUrl);
            Assert.assertTrue("unlock failed", unlock);
        } catch (Exception e) {
            //e.printStackTrace();
        }

        // Logout the connection
        login.logout();
        return returnID;
    }

    private RestConnector con;

    /**
     * @param
     */
    public UpdateEntityResult() {
        con = RestConnector.getInstance();
    }


}
