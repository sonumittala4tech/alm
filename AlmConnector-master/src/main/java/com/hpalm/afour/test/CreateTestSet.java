package com.hpalm.afour.test;

import com.hpalm.afour.alm.AuthenticateLoginLogout;
import com.hpalm.afour.alm.CommonFunctionLib;
import com.hpalm.afour.alm.CreateDeleteEntity;
import com.hpalm.afour.infrastructure.Assert;
import com.hpalm.afour.infrastructure.RestConnector;

import java.io.*;
import java.util.*;
import java.io.File;

/**
 * Created by Rajan Bansod on 2/14/2017.
 */
public class CreateTestSet {
    /**
     * Method to create new Test Set from given reference com.hpalm.afour.test set and associate com.hpalm.afour.test cases to it
     *
     * @param serverUrl          https://hq-qcapp-prd:8080
     * @param domain             ISI
     * @param project            ISI_AutomatedTest_Repository
     * @param username           rajanb
     * @param password           abc@123
     * @param testSetFolderName  Name of com.hpalm.afour.test set folder under which new com.hpalm.afour.test set will get create
     * @param testSetName        Name of new Test Set
     * @param owner              rbansod
     * @param referanceTestSetID Test Set id/ Test Set Folder id from which new com.hpalm.afour.test set will generate
     * @param isTestFolder       flag to check if its folder(true - for com.hpalm.afour.test set folder else false)
     * @param testngFilePath     Path to create testng.xml file e.g. "Q:\\Setup_Intu_a4\\workspace\\ALM\\IntutiveSurgical"
     * @return
     * @throws Exception
     */
    public String createTestSet(final String serverUrl, final String domain,
                                final String project, String username, String password,
                                String testSetFolderName, String testSetName, String owner, String referanceTestSetID, boolean isTestFolder, String testngFilePath, String browsers)
            throws Exception {

        RestConnector con =
                RestConnector.getInstance().init(
                        new HashMap<String, String>(),
                        serverUrl,
                        domain,
                        project);

        String[] browser = browsers.split(",");

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

        String testSetFolderID = commLib.getEntityID("test-set-folder", "name", testSetFolderName, con, requestHeaders);

        String runEntityUrl = con.buildEntityCollectionUrl("test-set");
        String testUrl = con.buildEntityCollectionUrl("test");

        // Use the writing example to generate an entity so that we can read it.
        // Go over this code to learn how to create new entities.
        String newCreatedResourceUrl =
                writeExample.createEntity(runEntityUrl,
                        commLib.generateFieldXmlForTestSet(testSetName, testSetFolderID));

        String testSetID = commLib.getIDFromURL(newCreatedResourceUrl);

        // Generate testng XML file
        List<String> testIDs = null;
//        List<String> testCaseNames = null;
        List<String> testSetIDs = null;
        List<String> testSetNames = null;
        List<String> testSetFolderNames = null;
        List<String> classNames = new ArrayList<String>();
        String packageName=null;
        String testFolderPath=null;
        if(project.contains("CRM_Automation"))
        {
            packageName="com.vanilla.mobile";
            testFolderPath="\\src\\test\\java\\com\\vanilla\\mobile\\";

        }else if(project.contains("Salesforce")){
            packageName="com.vanilla.mobile";
            testFolderPath="\\src\\test\\java\\com\\vanilla\\mobile\\";
        }
        else if(project.contains("CLMS")){
            packageName="com.vanilla.lms";
            testFolderPath=File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"com"+File.separator+"vanilla"+File.separator+"lms"+File.separator+"testcases"+File.separator+"clms";
        }
        else if(project.contains("SNOP")){
            packageName="com.vanilla.snop";
            testFolderPath=File.separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"com"+File.separator+"vanilla"+File.separator+"snop"+File.separator+"testcases";
        }
        else
        {
            packageName="com.vanilla.intusurg.master";
            testFolderPath="\\src\\test\\java\\com\\vanilla\\intusurg\\master\\";
        }
//        writer.write("<!DOCTYPE suite SYSTEM \"http://testng.org/testng-1.0.dtd\" >\n");
//        //writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
//        writer.write("<suite name=\"Test Suite: " + testSetFolderName + "parallel=\"classes\" thread-count=\"10\" >\n");
//        writer.write("  <test name=\"" + testSetName + "\" >\n");
//        writer.write("      <parameter name=\"myBrowser\" value=\""+browser[0]+"\"/>\n");
//        writer.write("      <classes>\n");

        if (!isTestFolder) {
            // Create Test Instance and Associate tests to com.hpalm.afour.test set
            String testInstanceUrl = con.buildEntityCollectionUrl("test-instance");
            testIDs = commLib.getEntitiesIDByURL(testInstanceUrl, "cycle-id", referanceTestSetID, "test-id", con, requestHeaders);

            testSetNames = commLib.getEntitiesIDByURL(runEntityUrl, "id", referanceTestSetID, "name", con, requestHeaders);

            // Print the name from the list....
//                for (String testID : testIDs) {
//                    String newCreatedTestInstanceUrl =
//                            writeExample.createEntity(testInstanceUrl,
//                                    commLib.generateFieldXmlForTestInstance(testID, testSetID, owner));
//                    //System.out.println(newCreatedTestInstanceUrl);
//                    writer.write("          <class name=\"" + testID + "\"/>\n");
//                }
            String testcaseLocation = testngFilePath + testFolderPath;
            System.out.println("Checking Testcases availability in "+testcaseLocation);
            for (int cnt = 0; cnt < testIDs.size(); cnt++) {
                String newCreatedTestInstanceUrl =
                        writeExample.createEntity(testInstanceUrl,
                                commLib.generateFieldXmlForTestInstance(testIDs.get(cnt), testSetID, owner));
//                System.out.println(newCreatedTestInstanceUrl);
                String updatedTestCaseName = commLib.checkIfFileExist(testcaseLocation , testIDs.get(cnt));
                if (updatedTestCaseName.equalsIgnoreCase("NoMatchFound")) {
                    System.out.println("No automated test case associated  with ID :: "+ testIDs.get(cnt));
                } else {
                    classNames.add(updatedTestCaseName);
                }
//                if (new File(testngFilePath + "\\src\\test\\java\\com\\vanilla\\intusurg\\master\\" + "PKG_" + testSetNames.get(0).replaceAll(" ", "_") + "\\TC_" + testIDs.get(cnt) + ".java").exists()) {
////                    writer.write("          <class name=\"" + packageName + "." + "PKG_" + testSetNames.get(0).replaceAll(" ", "_") + ".TC_" + testIDs.get(cnt) + "\"/>\n");
//                    classNames.add("          <class name=\"" + packageName + "." + "PKG_" + testSetNames.get(0).replaceAll(" ", "_") + ".TC_" + testIDs.get(cnt) + "\"/>\n");
//                } else {
//                    System.out.println("No automated test case associated  with " + "PKG_" + testSetNames.get(0).replaceAll(" ", "_") + ".TC_" + testIDs.get(cnt));
//                }

            }

            // Generate
        } else {
            String testSetUrl = con.buildEntityCollectionUrl("test-set");
            testSetIDs = commLib.getEntitiesIDByURL(testSetUrl, "parent-id", referanceTestSetID, "id", con, requestHeaders);
            String testSetFolderUrl = con.buildEntityCollectionUrl("test-set-folder");
            testSetFolderNames = commLib.getEntitiesIDByURL(testSetFolderUrl, "id", referanceTestSetID, "name", con, requestHeaders);
//                // Print the name from the list....
//                for (String searchedTestSetID : testSetIDs) {
//                    //System.out.println("searchedTestSetID: " + searchedTestSetID);
//                    String testInstanceUrl = con.buildEntityCollectionUrl("test-instance");
//                    testIDs = commLib.getEntitiesIDByURL(testInstanceUrl, "cycle-id", searchedTestSetID, "test-id", con, requestHeaders);
//
//                    // Print the name from the list....
//                    for (String testID : testIDs) {
//                        String newCreatedTestInstanceUrl =
//                                writeExample.createEntity(testInstanceUrl,
//                                        commLib.generateFieldXmlForTestInstance(testID, testSetID, owner));
//                        //System.out.println("testID: " + newCreatedTestInstanceUrl);
//                        writer.write("          <class name=\"" + testID + "\"/>\n");
//                    }
//                }
            String testcaseLocation = testngFilePath + testFolderPath;
            System.out.println("Checking Testcases availability in "+testcaseLocation);
            for (int cnt = 0; cnt < testSetIDs.size(); cnt++) {
                String testInstanceUrl = con.buildEntityCollectionUrl("test-instance");
                testIDs = commLib.getEntitiesIDByURL(testInstanceUrl, "cycle-id", testSetIDs.get(cnt), "test-id", con, requestHeaders);
                testSetNames = commLib.getEntitiesIDByURL(runEntityUrl, "id", testSetIDs.get(cnt), "name", con, requestHeaders);
                for (int cntInner = 0; cntInner < testIDs.size(); cntInner++) {
                    String newCreatedTestInstanceUrl =
                            writeExample.createEntity(testInstanceUrl,
                                    commLib.generateFieldXmlForTestInstance(testIDs.get(cntInner), testSetID, owner));
                    //System.out.println(newCreatedTestInstanceUrl);

                    String updatedTestCaseName = commLib.checkIfFileExist(testcaseLocation, testIDs.get(cntInner));
                    if (updatedTestCaseName.equalsIgnoreCase("NoMatchFound")) {
                        System.out.println("No automated test case associated  with ID :: "+ testIDs.get(cnt));
                    } else {
                        classNames.add(updatedTestCaseName);
                    }


//                    System.out.println(testngFilePath + "\\src\\test\\java\\com\\vanilla\\intusurg\\master\\" + "PKG_" + testSetNames.get(0).replaceAll(" ", "_") + "\\TC_" + testIDs.get(cntInner) + ".java");
//                    if (new File(testngFilePath + "\\src\\test\\java\\com\\vanilla\\intusurg\\master\\" + "PKG_" + testSetNames.get(0).replaceAll(" ", "_") + "\\TC_" + testIDs.get(cntInner) + ".java").exists()) {
////                        writer.write("          <class name=\"" + packageName + "." + "PKG_" + testSetNames.get(0).replaceAll(" ", "_") + ".TC_" + testIDs.get(cntInner) + "\"/>\n");
//                        classNames.add("          <class name=\"" + packageName + "." + "PKG_" + testSetNames.get(0).replaceAll(" ", "_") + ".TC_" + testIDs.get(cntInner) + "\"/>\n");
//                    } else {
//                        System.out.println("No automated test case associated  with " + "PKG_" + testSetNames.get(0).replaceAll(" ", "_") + ".TC_" + testIDs.get(cntInner));
//                    }
                }

            }
        }
        //Writer writer = null;
        Properties prop = new Properties();
        OutputStream output = null;
        try {
            output = new FileOutputStream(testngFilePath+"\\Testsuites\\testcases.properties");
            String tcList = "";
            for(String a : classNames){
                if(a!=classNames.get(0)) {
                    tcList = tcList + "," + a;
                }else{
                    tcList = a;
                }
            }
            // set the properties value
            prop.setProperty("testcases", tcList);
            // save properties to project root folder
            prop.store(output, null);
            /*writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(testngFilePath + "/testng.xml"), "utf-8"));
            writer.write("<!DOCTYPE suite SYSTEM \"http://testng.org/testng-1.0.dtd\" >\n");
            writer.write("<suite name=\"Test Suite: " + testSetFolderName + "\"  parallel=\"classes\" thread-count=\"10\" >\n");

            for (String className : classNames) {
                for (String browserName : browser) {
                    writer.write("  <test name=\"" +  className.substring(className.lastIndexOf('.')+1).split("\"")[0] + " : " + browserName + "\" >\n");
                    writer.write("      <parameter name=\"myBrowser\" value=\"" + browserName + "\"/>\n");
                    writer.write("      <classes>\n");
                    writer.write(className);
                    writer.write("      </classes>\n");
                    writer.write("  </test>\n");
                }
            }*/

//            for (String browserName : browser) {
//                writer.write("  <test name=\"" + testSetName + " : " + browserName + "\" >\n");
//                writer.write("      <parameter name=\"myBrowser\" value=\"" + browserName + "\"/>\n");
//                writer.write("      <classes>\n");
//                for (String className : classNames) {
//                    writer.write(className);
//                }
//                writer.write("      </classes>\n");
//                writer.write("  </test>\n");
//            }

            //writer.write("</suite>\n");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(output!=null) {
                try {
                    //writer.close();
                    output.close();
                } catch (Exception ex) {/*ignore*/}
            }
        }
        login.logout();

        return "";
    }
}
