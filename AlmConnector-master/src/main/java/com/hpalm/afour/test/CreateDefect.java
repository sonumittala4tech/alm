package com.hpalm.afour.test;

import com.hpalm.afour.alm.AuthenticateLoginLogout;
import com.hpalm.afour.alm.CommonFunctionLib;
import com.hpalm.afour.alm.CreateDeleteEntity;
import com.hpalm.afour.infrastructure.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rajan Bansod on 2/14/2017.
 */
public class CreateDefect {

    /**
     * Method to create defect from com.hpalm.afour.test step
     *
     * @param serverUrl     https://hq-qcapp-prd:8080
     * @param domain        ISI
     * @param project       ISI_AutomatedTest_Repository
     * @param username      rajanb
     * @param password      abc@123
     * @param owner         owner of the defect e.g. rajanb
     * @param priority      P1-High or P2-Medium or P3-Low
     * @param name          name of defect
     * @param userTempSeven Folder Name like '01 Spring Release Practice Testing 2017'
     * @param testID      id of com.hpalm.afour.test case
     * @param runName       name of run
     * @param stepName      name of step
     * @throws Exception
     */
    protected String createDefect(final String serverUrl, final String domain,
                             final String project, String username, String password, String owner, String priority, String name, String userTempSeven, String testID, String runName, String stepName)
            throws Exception {

        RestConnector con =
                RestConnector.getInstance().init(
                        new HashMap<String, String>(),
                        serverUrl,
                        domain,
                        project);

        // Use the login example code to login for this com.hpalm.afour.test.
        // Go over this code to learn how to authenticate/login/logout
        AuthenticateLoginLogout login =
                new AuthenticateLoginLogout();
        CommonFunctionLib commLib = new CommonFunctionLib();

        // Use the writing example to generate an entity so that we
        // can read it.
        // Go over this code to learn how to create new entities.
        CreateDeleteEntity writeExample = new CreateDeleteEntity();

        boolean loginState = login.login(username, password);
        Assert.assertTrue("login failed.", loginState);

        // Added the following method to the class RestConnector.java.
        // Once called the method login() from the class RestConnector, call this method getQcSession().
        con.getQCSession();

        Map<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put("Accept", "application/xml");

        // Get com.hpalm.afour.test details
        String stepUrl = con.buildEntityCollectionUrl("test");
        //String testID = commLib.getEntityID("test", "name", testName, con, requestHeaders);
        stepUrl = stepUrl  + "/" + testID;
        Response serverResponse =
                con.httpGet(stepUrl, null, requestHeaders);
        String serverResponseString = serverResponse.toString();

        String testSet = commLib.getValueFromXMLUsingFieldName(serverResponseString,"user-template-22");
        String subject = commLib.getValueFromXMLUsingFieldName(serverResponseString,"parent-id");
        String testNameXML = commLib.getValueFromXMLUsingFieldName(serverResponseString,"name");

        // Get run details
        String runUrl = con.buildEntityCollectionUrl("run");
        String runID = commLib.getEntityID("run", "name", runName, con, requestHeaders);
        runUrl = runUrl + "/" + runID;

        String runStepsUrl = runUrl + "/run-steps";
        String runStepID = commLib.getEntityIDByURL(runStepsUrl, "name", stepName, con, requestHeaders);
        runStepsUrl = runStepsUrl + "/" + runStepID;

        Response serverResponseForRun =
                con.httpGet(runStepsUrl, null, requestHeaders);
        String serverResponseForRunString = serverResponseForRun.toString();

        String stepDescription = commLib.getValueFromXMLUsingFieldNameMultiline(
                serverResponseForRunString,"description");
        String stepExpected = commLib.getValueFromXMLUsingFieldNameMultiline(serverResponseForRunString, "expected");
        String stepActual = commLib.getValueFromXMLUsingFieldNameMultiline(serverResponseForRunString,"actual");
        stepDescription = stepDescription.replaceAll("&lt;/body&gt;&lt;/html&gt;|&lt;html&gt;&lt;body&gt;&lt;br /&gt;","");
        stepDescription = stepDescription.replaceAll("&amp;quot;",":");
        stepExpected = stepExpected.replaceAll("&lt;/body&gt;&lt;/html&gt;|&lt;html&gt;&lt;body&gt;&lt;br /&gt;","");
        stepExpected = stepExpected.replaceAll("&amp;quot;",":");
        stepActual = stepActual.replaceAll("</Value></Field><Field Name=\"step-order\"><Value>(.+?)","");
        stepActual = stepActual.replaceAll("&lt;/body&gt;&lt;/html&gt;|&lt;html&gt;&lt;body&gt;&lt;br /&gt;","");
        stepActual = stepActual.replaceAll("&amp;quot;",":");

        // Generate defect description string
        String defectDescription = commLib.generateDefectDescription(testSet, testNameXML, runName, stepName, stepDescription, stepExpected, stepActual, runStepID);
        defectDescription = defectDescription.replaceAll("<"," &lt; ");
        defectDescription = defectDescription.replaceAll(">"," &gt; ");
        defectDescription = defectDescription.replaceAll(":"," &amp;quot; ");

        // Use the writing example to generate an entity so that we can read it.
        // Go over this code to learn how to create new entities.
        String defectUrl = con.buildEntityCollectionUrl("defect");

        String newCreatedResourceUrl =
                writeExample.createEntity(defectUrl,
                        commLib.generateFieldXmlForDefect(subject, username, owner, priority, name, defectDescription, testSet, "Bug", userTempSeven));

        String defectID = commLib.getIDFromURL(newCreatedResourceUrl);
        String defectLinkUrl = con.buildEntityCollectionUrl("defect-link");

        String newCreatedDefectLinkUrl =
                writeExample.createEntity(defectLinkUrl,
                        commLib.generateFieldXmlForDefectLink(runStepID,defectID));

        login.logout();

        return defectID;
    }
}
