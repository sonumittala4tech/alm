package com.hpalm.afour.test;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by rajan.b on 3/8/2017.
 */
public class GenerateTestNGxmlAndTestSet {

    HashMap<String, String> config = null;

    public void GenerateTestNGxmlAndTestSet() throws Exception {
        boolean flag = false;
        // Check if HP-ALM logging is enabled
        if (config.get("almUpdateReport").equalsIgnoreCase("TRUE")) {
            if (config.get("isTestSetFolder").equalsIgnoreCase("TRUE")) {
                flag = true;
            }
            // Create new com.hpalm.afour.test run
            CreateTestSet crTestSet = new CreateTestSet();
            System.out.println("\n\n\n");
            System.out.println("Flag value for isTestSetFolder: " + flag);
            System.out.println("Path: " + System.getProperty("user.dir").toString().replaceAll("//lib",""));
            System.out.println("\n\n\n");
            crTestSet.createTestSet(config.get("almURL"), config.get("almDomain"), config.get("almProject"), config.get("almUsername"), config.get("almPassword"),
                    config.get("testSetFolderName"), getTestSetName(config.get("buildNumber")), config.get("almOwner"), config.get("referanceTestSetID"),
                    flag, System.getProperty("user.dir").toString().replaceAll("//lib",""),config.get("Browser"));
        } else {
            System.out.println("HP-ALM reporting disabled");
        }

    }

    public String getTestSetName(String buildNumber) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss a");
        Date date = new Date();
        String testSetName;
        if(this.config.containsKey("Execution_Platform")) {
            testSetName = dateFormat.format(date) + " - " + this.config.get("deviceName") + " - " + this.config.get("platformVersion") + " - " + buildNumber;
        } else {
            testSetName = dateFormat.format(date) + " - " + buildNumber;
        }
        this.writePropertiesFile("testSetName", testSetName);
        return testSetName;
    }

    public HashMap<String, String> getConfigData() throws Exception {
        if (config == null) {
            HashMap<String, String> hashMap = null;
            hashMap = propertiesLoader(System.getProperty("user.dir").toString().replaceAll("//lib","") + "/config.properties");
            config = hashMap;
            return hashMap;
        } else {
            return config;
        }
    }

    public HashMap<String, String> propertiesLoader(String filePath) throws Exception {
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
     * Method to append data into config properties files
     * @param key: Key name
     * @param data: Value for the key
     * @return
     */
    public void writePropertiesFile(String key, String data) {
        FileOutputStream fileOut = null;
        FileInputStream fileIn = null;

        try {
            Properties configProperty = new Properties();
            File file = new File("config.properties");
            fileIn = new FileInputStream(file);
            configProperty.load(fileIn);
            configProperty.setProperty(key, data);
            fileOut = new FileOutputStream(file);
            configProperty.store(fileOut, (String)null);
        } catch (Exception var15) {
            ;
        } finally {
            try {
                fileOut.close();
            } catch (IOException var14) {
                ;
            }

        }

    }
}



