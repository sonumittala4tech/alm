package com.hpalm.afour.alm;

import com.hpalm.afour.infrastructure.Assert;
import com.hpalm.afour.infrastructure.Constants;
import com.hpalm.afour.infrastructure.Response;
import com.hpalm.afour.infrastructure.RestConnector;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rajan Bansod on 2/15/2017.
 */
public class CommonFunctionLib {

 public static  HashMap<String,String > config= null;

    /**
     * Method to get the ID from provided xml
     *
     * @param listFromCollectionAsXml Collection As Xml in string
     * @return
     */
    public String getIDFromXML(String listFromCollectionAsXml) {
        Pattern p = Pattern.compile("<Field Name=\"id\"><Value>(.+?)</Value></Field>");
        Matcher m = p.matcher(listFromCollectionAsXml);
        m.find();
        return m.group(1);
    }

    /**
     * Method to get the ID from provided xml
     *
     * @param listFromCollectionAsXml Collection As Xml in string
     * @return
     */
    public List<String> getValuesFromXMLUsingFieldName(String listFromCollectionAsXml, String fieldName) {
        fieldName = fieldName.replace("-", "\\-");
        listFromCollectionAsXml= listFromCollectionAsXml.replaceAll("(\\r\\n|\\n|\\r)", "");
        Pattern pattern = Pattern.compile("<Field Name=\"" + fieldName + "\"><Value>(.+?)</Value></Field>");
        List<String> list = new ArrayList<String>();
        Matcher m = pattern.matcher(listFromCollectionAsXml);
        while (m.find()) {
            list.add(m.group(1));
        }
        return list;
    }

    public String getIDFromURL(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    /**
     * Method to get the tag value from provided xml
     *
     * @param listFromCollectionAsXml Collection As Xml in string
     * @param fieldName               tag name
     * @return
     */
    public String getValueFromXMLUsingFieldName(String listFromCollectionAsXml, String fieldName) {
        fieldName = fieldName.replace("-", "\\-");
//        Pattern p = Pattern.compile("<Field Name=\"" + fieldName + "\"><Value>\\s(.*)</Value></Field>", Pattern.DOTALL);
        Pattern p = Pattern.compile("<Field Name=\"" + fieldName + "\"><Value>(.+?)</Value></Field>", Pattern.MULTILINE);
        Matcher m = p.matcher(listFromCollectionAsXml);
        m.find();
        return m.group(1);
    }

    /**
     * Method to get the tag value from provided xml
     *
     * @param listFromCollectionAsXml Collection As Xml in string
     * @param fieldName               tag name
     * @return
     */
    public String getValueFromXMLUsingFieldNameMultiline(String listFromCollectionAsXml, String fieldName) {
        fieldName = fieldName.replace("-", "\\-");
        listFromCollectionAsXml= listFromCollectionAsXml.replaceAll("(\\r\\n|\\n|\\r)", "");
        Pattern p = Pattern.compile("<Field Name=\"" + fieldName + "\"><Value>(.+?)</Value></Field>");
        Matcher m = p.matcher(listFromCollectionAsXml);
        m.find();
        return m.group(1);
    }

    /**
     * @param field the field name to update
     * @param value the new value to use
     * @return an xml that can be used to update an entity's single
     * given field to given value
     */
    public static String generateSingleFieldUpdateXmlForDefect(String field,
                                                               String value) {
        return "<Entity Type=\"defect\"><Fields>"
                + Constants.generateFieldXml(field, value)
                + "</Fields></Entity>";
    }

    /**
     * @param field the field name to update
     * @param value the new value to use
     * @return an xml that can be used to update an entity's single
     * given field to given value
     */
    public static String generateSingleFieldUpdateXml(String entityType, String field,
                                                      String value,
                                                      String actualValue) {
        if (entityType.equalsIgnoreCase("run-step")) {
            SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("PST"));
            if (null==config)
            {
                config=propertiesLoader(System.getProperty("user.dir")+File.separator+"config.properties");
            }

            return "<Entity Type=\"" + entityType + "\"><Fields>"
                    + Constants.generateFieldXml(field, value)
                    + Constants.generateFieldXml("actual", actualValue)
                    + Constants.generateFieldXml("execution-time", sdf.format(new Date()))
                    +Constants.generateFieldXml("user-template-04", config.get("almUsername"))
                    + "</Fields></Entity>";
        } else {
            return "<Entity Type=\"" + entityType + "\"><Fields>"
                    + Constants.generateFieldXml(field, value)
                    + "</Fields></Entity>";
        }
    }


    /**
     * Method to generate Field Xml for Defect
     *
     * @param userName      detected-by e.g. rajanb
     * @param owner         owner of defect e.g. rajanb
     * @param priority      P1-High or P2-Medium or P3-Low
     * @param name          name of defect
     * @param description   description of defect
     * @param userTempOne   e.g. 01-21 ISU
     * @param userTempTwo   e.g. User Training
     * @param userTempSeven e.g. Test Cycle 1
     * @return
     */
    public static String generateFieldXmlForDefect(String subject, String userName, String owner, String priority, String name, String description, String userTempOne, String userTempTwo, String userTempSeven) {
        return "<Entity Type=\"defect\">"
                + "<Fields>"
                + Constants.generateFieldXml("subject", subject)
                + Constants.generateFieldXml("detected-by", userName)
                + Constants.generateFieldXml("owner", owner)
                + Constants.generateFieldXml("creation-time", getDateTime("yyyy-MM-dd"))
                + Constants.generateFieldXml("priority", priority)
                + Constants.generateFieldXml("name", name)
                + Constants.generateFieldXml("user-template-01", userTempOne)
                + Constants.generateFieldXml("user-template-02", userTempTwo)
                + Constants.generateFieldXml("user-template-07", userTempSeven)
                + Constants.generateFieldXml("description", description)
                + "</Fields>"
                + "</Entity>";
    }


    public static String generateFieldXmlForDefectLink(String runStepNo, String defectID) {
        return "<Entity Type=\"defect-link\">"
                + "<Fields>"
                + Constants.generateFieldXml("first-endpoint-id", defectID)
                + Constants.generateFieldXml("second-endpoint-id", runStepNo)
                + Constants.generateFieldXml("second-endpoint-type", "run-step")
                + "</Fields>"
                + "</Entity>";
    }

    /**
     * Method to generate Field Xml for Run
     *
     * @param owner      owner of the defect e.g. rajanb
     * @param name       name of run
     * @param status     Passed, Failed, Not Started, In Progress, No Run
     * @param testID     com.hpalm.afour.test case id
     * @param duration   execution time
     * @param host       Name of testing machine e.g. WIN7-XDT-PVD74
     * @param osName     OS of testing machine e.g. Windows 7
     * @param testcyclID Instance ID of com.hpalm.afour.test case
     * @return
     */
    public static String generateFieldXmlForRun(String owner, String name, String status, String testID, String duration, String host, String osName, String testcyclID) {
        return "<Entity Type=\"run\">"
                + "<Fields>"
                + Constants.generateFieldXml("test-id", testID)
                + Constants.generateFieldXml("duration", duration)
                + Constants.generateFieldXml("subtype-id", "hp.qc.run.MANUAL")
                + Constants.generateFieldXml("host", host)
                + Constants.generateFieldXml("owner", owner)
                + Constants.generateFieldXml("os-name", osName)
                + Constants.generateFieldXml("name", name)
                + Constants.generateFieldXml("testcycl-id", testcyclID)
                + Constants.generateFieldXml("status", status)
                + "</Fields>"
                + "</Entity>";
    }

    /**
     * Method to generate Field Xml for Run
     *
     * @param owner      owner of the defect e.g. rajanb
     * @param name       name of run
     * @param status     Passed, Failed, Not Started, In Progress, No Run
     * @param testID     com.hpalm.afour.test case id
     * @param duration   execution time
     * @param host       Name of testing machine e.g. WIN7-XDT-PVD74
     * @param osName     OS of testing machine e.g. Windows 7
     * @param testcyclID Instance ID of com.hpalm.afour.test case
     * @return
     */
    public static String generateFieldXmlForRunWithCycleID(String owner, String name, String status, String testID, String duration, String host, String osName, String testcyclID,String testsetID) {
        return "<Entity Type=\"run\">"
                + "<Fields>"
                + Constants.generateFieldXml("test-id", testID)
                + Constants.generateFieldXml("duration", duration)
                + Constants.generateFieldXml("subtype-id", "hp.qc.run.MANUAL")
                + Constants.generateFieldXml("host", host)
                + Constants.generateFieldXml("owner", owner)
                + Constants.generateFieldXml("os-name", osName)
                + Constants.generateFieldXml("name", name)
                + Constants.generateFieldXml("testcycl-id", testcyclID)
                + Constants.generateFieldXml("status", status)
                + Constants.generateFieldXml("cycle-id", testsetID)
                + "</Fields>"
                + "</Entity>";
    }


    public static String generateFieldXmlForTestSet(String name, String testSetFolderID) {
        return "<Entity Type=\"test-set\">"
                + "<Fields>"
                + Constants.generateFieldXml("name", name)
                + Constants.generateFieldXml("parent-id", testSetFolderID)
                + Constants.generateFieldXml("status", "Open")
                + Constants.generateFieldXml("subtype-id", "hp.qc.test-set.default")
                + "</Fields>"
                + "</Entity>";
    }


    public static String generateFieldXmlForTestInstance(String testID, String cycleID, String owner) {
        return "<Entity Type=\"test-instance\">"
                + "<Fields>"
                + Constants.generateFieldXml("test-id", testID)
                + Constants.generateFieldXml("cycle-id", cycleID)
                + Constants.generateFieldXml("status", "No Run")
                + Constants.generateFieldXml("subtype-id", "hp.qc.test-instance.MANUAL")
                + Constants.generateFieldXml("owner", owner)
                + "</Fields>"
                + "</Entity>";
    }

    public static String generateDefectDescription(String testSet,String testName,String runName,String stepName, String stepDescription, String stepExpected ,String stepActual, String runStepNo) {
        return "&lt;html&gt;&lt;body&gt; &lt;div align=\"left\"&gt; &lt;b&gt;&lt;font color=\"#000080\"&gt;Test Set: &lt;/font&gt;&lt;/b&gt;"
                + testSet
                + "&lt;br /&gt; &lt;b&gt;&lt;font color=\"#000080\"&gt;Test: &lt;/font&gt;&lt;/b&gt;"
                + testName
                + "&lt;br /&gt; &lt;b&gt;&lt;font color=\"#000080\"&gt;Run: &lt;/font&gt;&lt;/b&gt;"
                + runName
                + "&lt;br /&gt; &lt;br /&gt; &lt;b&gt;&lt;font color=\"#000080\"&gt;Test Parameters: &lt;/font&gt;&lt;/b&gt; &lt;br /&gt; &lt;br /&gt; &lt;b&gt;&lt;font color=\"#000080\"&gt;Step: &lt;/font&gt;&lt;/b&gt;"
                + stepName
                + "&lt;br /&gt; &lt;br /&gt; &lt;b&gt;&lt;font color=\"#000080\"&gt;Description: &lt;/font&gt;&lt;/b&gt; &lt;br /&gt;"
                + stepDescription
                + "&lt;br /&gt; &lt;br /&gt; &lt;b&gt;&lt;font color=\"#000080\"&gt;Expected: &lt;/font&gt;&lt;/b&gt; &lt;br /&gt;"
                + stepExpected
                + "&lt;br /&gt; &lt;br /&gt; &lt;b&gt;&lt;font color=\"#000080\"&gt;Actual: &lt;/font&gt;&lt;/b&gt; &lt;br /&gt; &lt;div align=\"left\" style=\"min-height:9pt\"&gt; &lt;font face=\"Arial\"&gt;&lt;span style=\"font-size:8pt\"&gt;"
                + stepActual
                + "&lt;/span&gt;&lt;/font&gt; &lt;/div&gt; &lt;br /&gt; &lt;b&gt;&lt;font color=\"#000080\"&gt;Run Step["
                + runStepNo
                + "] : &lt;/font&gt;&lt;/b&gt;"
                + stepName
                + "&lt;br /&gt; &lt;/div&gt; &lt;/body&gt;&lt;/html&gt;"
                ;
    }

    /**
     * Method to get the date time stamp in provided format
     *
     * @param pattern date time stamp format
     * @return
     */
    public static String getDateTime(String pattern) {
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * Method to get ID of given entity using entity type like com.hpalm.afour.test step, defect, com.hpalm.afour.test run etc
     *
     * @param entityType          Entity like com.hpalm.afour.test step, defect, com.hpalm.afour.test run etc
     * @param entityKeyToSearch   name or status or owner etc
     * @param entityValueToSearch testName or passed or rajanb etc
     * @param conn                connection string
     * @param requestHeaders      request header format
     * @return
     */
    public static String getEntityID(String entityType, String entityKeyToSearch, String entityValueToSearch, RestConnector conn, Map<String, String> requestHeaders) {
        try {
            String requirementsUrl =
                    conn.buildEntityCollectionUrl(entityType);

            //query a collection of entities:
            StringBuilder b = new StringBuilder();
            //The query - where field name has a  value that starts with the
            // name of the requirement we posted
            b.append("query={" + URLEncoder.encode(entityKeyToSearch, "UTF-8") + "['" + URLEncoder.encode(entityValueToSearch, "UTF-8") + "']}");
            //The fields to display: id, name
            b.append("&fields=id,name");
            //determine the sorting order - descending by id (highest id first)
            b.append("&order-by={id[DESC]}");
            //display 10 results
            b.append("&page-size=10");
            //counting from the 1st result, including
            b.append("&start-index=1");

            // Get ID of entity
            Response serverResponse =
                    conn.httpGet(requirementsUrl, b.toString(), requestHeaders);
            Assert.assertEquals(
                    "failed obtaining response for requirements collection "
                            + requirementsUrl,
                    HttpURLConnection.HTTP_OK,
                    serverResponse.getStatusCode());

            String listFromCollectionAsXml = serverResponse.toString();

            Assert.assertTrue(
                    "didn't find exactly one match, though we posted exactly one entity with '"
                            + entityValueToSearch
                            + "' name. Either entity not present OR more than one entity present in ALM",
                    listFromCollectionAsXml.contains("<Entities TotalResults=\"1\">"));

            // Extract the ID from xml response
            String id = new CommonFunctionLib().getIDFromXML(listFromCollectionAsXml);
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	 /**
     * Method to get ID of given entity using entity type like com.hpalm.afour.test step, defect, com.hpalm.afour.test run etc
     *
     * @param entityType          Entity like com.hpalm.afour.test step, defect, com.hpalm.afour.test run etc
     * @param entityKeyToSearch1   name or status or owner etc
	 * @param entityKeyToSearch2   name or status or owner etc
     * @param entityValueToSearch1 testName or passed or rajanb etc
	 * @param entityValueToSearch2 testName or passed or rajanb etc
     * @param conn                connection string
     * @param requestHeaders      request header format
     * @return
     */	
	public static String getEntityIDWithMultiValueSearch(String entityType, String entityKeyToSearch1,String entityKeyToSearch2, String entityValueToSearch1,String entityValueToSearch2, RestConnector conn, Map<String, String> requestHeaders) {
        try {
            String requirementsUrl =
                    conn.buildEntityCollectionUrl(entityType);

            //query a collection of entities:
            StringBuilder b = new StringBuilder();
            //The query - where field name has a  value that starts with the
            // name of the requirement we posted
            b.append("query={" + URLEncoder.encode(entityKeyToSearch1, "UTF-8") + "['" + URLEncoder.encode(entityValueToSearch1, "UTF-8") + "'];");
            b.append(URLEncoder.encode(entityKeyToSearch2,"UTF-8")+"['"+URLEncoder.encode(entityValueToSearch2,"UTF-8")+"']}");
            //The fields to display: id, name
            b.append("&fields=id,name");
            //determine the sorting order - descending by id (highest id first)
            b.append("&order-by={id[DESC]}");
            //display 10 results
            b.append("&page-size=10");
            //counting from the 1st result, including
            b.append("&start-index=1");

            // Get ID of entity
            Response serverResponse =
                    conn.httpGet(requirementsUrl, b.toString(), requestHeaders);
            Assert.assertEquals(
                    "failed obtaining response for requirements collection "
                            + requirementsUrl,
                    HttpURLConnection.HTTP_OK,
                    serverResponse.getStatusCode());

            String listFromCollectionAsXml = serverResponse.toString();

            Assert.assertTrue(
                    "didn't find exactly one match, though we posted exactly one entity with '"
                            + entityValueToSearch1 +entityKeyToSearch2
                            + "' name. Either entity not present OR more than one entity present in ALM",
                    listFromCollectionAsXml.contains("<Entities TotalResults=\"1\">"));

            // Extract the ID from xml response
            String id = new CommonFunctionLib().getIDFromXML(listFromCollectionAsXml);
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method to get ID of given entity using entity type like com.hpalm.afour.test step, defect, com.hpalm.afour.test run etc
     *
     * @param entityType          Entity like com.hpalm.afour.test step, defect, com.hpalm.afour.test run etc
     * @param entityKeyToSearch   name or status or owner etc
     * @param entityValueToSearch testName or passed or rajanb etc
     * @param conn                connection string
     * @param requestHeaders      request header format
     * @return
     */
    public static String getEntityIDWithNoCheck(String entityType, String entityKeyToSearch, String entityValueToSearch, RestConnector conn, Map<String, String> requestHeaders) {
        try {
            String requirementsUrl =
                    conn.buildEntityCollectionUrl(entityType);

            //query a collection of entities:
            StringBuilder b = new StringBuilder();
            //The query - where field name has a  value that starts with the
            // name of the requirement we posted
            b.append("query={" + URLEncoder.encode(entityKeyToSearch, "UTF-8") + "['" + URLEncoder.encode(entityValueToSearch, "UTF-8") + "']}");
            //The fields to display: id, name
            b.append("&fields=id,name");
            //determine the sorting order - descending by id (highest id first)
            b.append("&order-by={id[DESC]}");
            //display 10 results
            b.append("&page-size=10");
            //counting from the 1st result, including
            b.append("&start-index=1");

            // Get ID of entity
            Response serverResponse =
                    conn.httpGet(requirementsUrl, b.toString(), requestHeaders);
            System.out.println(serverResponse.toString());
            Assert.assertEquals(
                    "failed obtaining response for requirements collection "
                            + requirementsUrl,
                    HttpURLConnection.HTTP_OK,
                    serverResponse.getStatusCode());

            String listFromCollectionAsXml = serverResponse.toString();

            // Extract the ID from xml response
            String id = new CommonFunctionLib().getIDFromXML(listFromCollectionAsXml);
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method to get ID of given entity using entity url
     *
     * @param createdEntityUrl    Entity URL
     * @param entityKeyToSearch   name or status or owner etc
     * @param entityValueToSearch testName or passed or rajanb etc
     * @param conn                connection string
     * @param requestHeaders      request header format
     * @return
     */
    public static String getEntityIDByURL(String createdEntityUrl, String entityKeyToSearch, String entityValueToSearch, RestConnector conn, Map<String, String> requestHeaders) {
        try {
            //query a collection of entities:
            StringBuilder b = new StringBuilder();
            //The query - where field name has a  value that starts with the
            // name of the requirement we posted
            b.append("query={" + URLEncoder.encode(entityKeyToSearch, "UTF-8") + "['" + URLEncoder.encode(entityValueToSearch, "UTF-8") + "']}");
            //The fields to display: id, name
            b.append("&fields=id,name");
            //determine the sorting order - descending by id (highest id first)
            b.append("&order-by={id[DESC]}");
            //display 10 results
            b.append("&page-size=10");
            //counting from the 1st result, including
            b.append("&start-index=1");

            // Get ID of entity
            Response serverResponse =
                    conn.httpGet(createdEntityUrl, b.toString(), requestHeaders);
            Assert.assertEquals(
                    "failed obtaining response for requirements collection "
                            + createdEntityUrl,
                    HttpURLConnection.HTTP_OK,
                    serverResponse.getStatusCode());

            String listFromCollectionAsXml = serverResponse.toString();
            Assert.assertTrue(
                    "didn't find exactly one match, though we posted exactly one entity with '"
                            + entityValueToSearch
                            + "' name. Either entity not present OR more than one entity present in ALM",
                    listFromCollectionAsXml.contains("<Entities TotalResults=\"1\">"));

            // Extract the ID from xml response
            String id = new CommonFunctionLib().getIDFromXML(listFromCollectionAsXml);
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Method to get ID of given entity using entity url
     *
     * @param createdEntityUrl    Entity URL
     * @param entityKeyToSearch   name or status or owner etc
     * @param entityValueToSearch testName or passed or rajanb etc
     * @param conn                connection string
     * @param requestHeaders      request header format
     * @return
     */
    public static List<String> getEntitiesIDByURL(String createdEntityUrl, String entityKeyToSearch, String entityValueToSearch, String fieldValueToExtract, RestConnector conn, Map<String, String> requestHeaders) {
        try {
            //query a collection of entities:
            StringBuilder b = new StringBuilder();
            //The query - where field name has a  value that starts with the
            // name of the requirement we posted
            b.append("query={" + URLEncoder.encode(entityKeyToSearch, "UTF-8") + "['" + URLEncoder.encode(entityValueToSearch, "UTF-8") + "']}");

            // Get ID of entity
            Response serverResponse =
                    conn.httpGet(createdEntityUrl, b.toString(), requestHeaders);
            Assert.assertEquals(
                    "failed obtaining response for requirements collection "
                            + createdEntityUrl,
                    HttpURLConnection.HTTP_OK,
                    serverResponse.getStatusCode());

            String listFromCollectionAsXml = serverResponse.toString();

            // Extract the ID from xml response
            List<String> testIDs = new CommonFunctionLib().getValuesFromXMLUsingFieldName(listFromCollectionAsXml,fieldValueToExtract);
            return testIDs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String checkIfFileExist(String packageName, String testCaseID){
//        String testCaseID = "123";
        String temp = "NoMatchFound";
        String testCaseName  = "(.+?)TC_(.+?)_"+testCaseID+".java";
        File directory = new File(packageName);
        File[] files = directory.listFiles();
        Pattern p = Pattern.compile(testCaseName);

        if (files != null && files.length > 0) {
            for(File file:files){
                try {
                    Matcher m = p.matcher(file.toString());
                    m.find();
                    temp = "TC_"+m.group(2)+"_"+testCaseID;
                    return temp;
                }catch(Exception e){}
            }
        }else {
            return temp;
        }
        return temp;
    }
//    public static String checkIfFileExist(String packageName, String pattern){
////        String dirPath =  "D:\\Transform\\IntSurg\\Part 2\\IntutiveSurgical\\src\\test\\java\\com\\vanilla\\intusurg\\master" + "\\" + packageName;
//        File directory = new File(packageName);
//
//        File[] files = directory.listFiles();
//
////        String pattern = "TC_(.+?).java";
//        System.out.println("\nFiles that match regular expression: " + pattern);
//        FileFilter filter = new RegexFileFilter(pattern);
//        files = directory.listFiles(filter);
//
//        if (files != null && files.length > 0) {
//            return files[0].getName().replaceAll("\\.java","");
//        }else {
//            return "NoMatchFound";
//        }
//    }
    /**
     * Method to get all element info from a property file.
     *
     * @param filePath - from which file info needs to be fetched
     * @return - HashMap<String,String>
     */
    public static HashMap<String, String> propertiesLoader(String filePath)  {
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

        } catch (Exception ex) {
            ex.printStackTrace();

        }

        return HMap;

    }

}
