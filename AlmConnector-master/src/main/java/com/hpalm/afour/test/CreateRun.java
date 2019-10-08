package com.hpalm.afour.test;

import com.hpalm.afour.alm.AuthenticateLoginLogout;
import com.hpalm.afour.alm.CommonFunctionLib;
import com.hpalm.afour.alm.CreateDeleteEntity;
import com.hpalm.afour.infrastructure.Assert;
import com.hpalm.afour.infrastructure.RestConnector;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Rajan Bansod on 2/14/2017.
 */
public class CreateRun {
    HashMap<String, String> config = null;


    public CreateRun() throws  Exception{
        this.config = this.propertiesLoader(System.getProperty("user.dir").toString().replaceAll("lib","") +File.separator+ "config.properties");
    }


    private HashMap<String, String> propertiesLoader(String filePath) throws Exception {
        System.out.println("Property file path: " + filePath);
        HashMap<String, String> HMap = new HashMap<String, String>();
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream(filePath);
            prop.load(input);
            Enumeration<?> e = prop.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                String value = prop.getProperty(key);
                HMap.put(key, value);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return HMap;

    }
    /**
     * Method to create com.hpalm.afour.test run
     *
     * @param serverUrl https://hq-qcapp-prd:8080
     * @param domain    ISI
     * @param project   ISI_AutomatedTest_Repository
     * @param username  rajanb
     * @param password  abc@123
     * @param owner     owner of the defect e.g. rajanb
     * @param name      name of run e.g. Run_2-20_17-35-51
     * @param testCaseID    test ID of test case
     * @param host      Name of testing machine e.g. WIN7-XDT-PVD74
     * @param osName    OS of testing machine e.g. Windows 7
     * @param status    Passed, Failed, Not Started, In Progress, No Run
     * @throws Exception
     */
    public String createRun(final String serverUrl, final String domain,
                          final String project, String username, String password, String owner, String name, String testCaseID, String host, String osName, String status)
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

//        String testID = commLib.getEntityID("test", "id", testCaseID, con, requestHeaders);

        String testCycleID = null;
        String newCreatedResourceUrl=null;
        String testSetID=null;
        String testSetFolderID=null;
        String runEntityUrl = con.buildEntityCollectionUrl("run");
        if ((config.get("isTestSetFolder").equalsIgnoreCase("TRUE"))&& config.get("isTestSet").equalsIgnoreCase("TRUE")) {
                testSetFolderID = commLib.getEntityID("test-set-folder", "name", config.get("testSetFolderName"), con, requestHeaders);
                testSetID=commLib.getEntityIDWithMultiValueSearch("test-set","parent-id","name",testSetFolderID,config.get("testSetName"),con,requestHeaders);
                testCycleID =commLib.getEntityIDWithMultiValueSearch("test-instance","cycle-id","test-id",testSetID,testCaseID,con,requestHeaders);
            newCreatedResourceUrl =
                    writeExample.createEntity(runEntityUrl,
                            commLib.generateFieldXmlForRunWithCycleID(owner, name, status, testCaseID, "60", host, osName, testCycleID,testSetID));
        }
        else
        {
            try {
                testCycleID = commLib.getEntityIDWithNoCheck("test-instance", "test-id", testCaseID, con, requestHeaders);
                Thread.sleep(2000);
            }catch(Exception e){
                e.printStackTrace();
            }
            // Use the writing example to generate an entity so that we can read it.
            // Go over this code to learn how to create new entities.
               newCreatedResourceUrl =
                    writeExample.createEntity(runEntityUrl,
                            commLib.generateFieldXmlForRun(owner, name, status, testCaseID, "60", host, osName, testCycleID));
        }
        String runID = commLib.getIDFromURL(newCreatedResourceUrl);
        login.logout();
        System.out.println("Run ID: " + runID);
        return runID;
    }
}
