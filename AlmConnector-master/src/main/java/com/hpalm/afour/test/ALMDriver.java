package com.hpalm.afour.test;

/**
 * Created by Rajan Bansod on 2/17/2017.
 */
public class ALMDriver {

    public static void main(String[] args) throws Exception {
        ALMDriver almDriver = new ALMDriver();
        System.out.println("Jar called");
        if (args.length > 0) {

            System.out.println("Arguments passed: " + args.length );

            for (int iCount = 0; iCount < args.length; iCount++) {
                System.out.println(args[iCount]);
            }
            GenerateTestNGxmlAndTestSet triggerExecution = new GenerateTestNGxmlAndTestSet();
            triggerExecution.getConfigData();
            triggerExecution.GenerateTestNGxmlAndTestSet();
        } else {
            System.out.println("No arguments passed");
        }
   //     almDriver.createNewTestSet("http://hq-qcapp-prd:8080/qcbin","ISI","CLMS_Automation_Mobile_2018","dsahu","Welcome12345",
   //             "Automation_Result","Test Set Daily Run Today","dsahu","2",true,"Q:\\Users\\prathameshs\\IntuSurg\\IntutiveSurgical","CHROME,FIREFOX,IE");

//        for(int i=0;i<=5;i++)
//        {
//            almDriver.createNewRun("http://hq-qcapp-prd:8080/qcbin","ISI","CLMS_Automation_Mobile_2018","dsahu","Welcome12345","dsahu",
//                    "Run_2-23_21-10-57","302","WIN7-XDT-PVD74","Windows 7","Not Completed");
//            almDriver.createNewRun("http://hq-qcapp-prd:8080/qcbin","ISI","CLMS_Automation_Mobile_2018","dsahu","Welcome12345","dsahu",
//                    "Run_2-23_21-10-57","318","WIN7-XDT-PVD74","Windows 7","Completed");
//
//        }


//        almDriver.createNewRun("http://hq-qcapp-prd:8080/qcbin","ISI","Salesforce_Release_2017","prathameshs","Welcome2","aniketd",
//                "Run_2-23_21-10-51","Release testing 08 -511 ISI Admin is able to manage Sales Account Team record","WIN7-XDT-PVD74","Windows 7","Not Completed");
//        almDriver.updateEntityResult("http://hq-qcapp-prd:8080/qcbin","ISI","Salesforce_Release_2017","prathameshs","Welcome2",
//                "run","name","Run_2-23_21-10-51","name","Step 2",
//                "status","Passed","Updated bu automation");
//        almDriver.updateEntityResult("http://hq-qcapp-prd:8080/qcbin","ISI","Salesforce_Release_2017","prathameshs","Welcome2",
//                "run","name","Run_2-23_21-10-51","name","Step 3",
//                "status","Failed","Updated by automation");
//        almDriver.attachScreenshot("http://hq-qcapp-prd:8080/qcbin","ISI","Salesforce_Release_2017","prathameshs","Welcome2",
//                "run","name","Run_2-23_21-10-51","name","Step 3",
//                "Screenshot.png","Q:\\Setup_Intu_a4\\workspace\\gitRepo\\IntuSurgALM\\carrom.png","Test Description");
//        almDriver.createNewDefect("http://hq-qcapp-prd:8080/qcbin","ISI","Salesforce_Release_2017","prathameshs","Welcome2",
//                "aniketd","P3-Low","Test Defect Run_2-15_22-53-42","01 Spring Release Practice Testing 2017",
//                "02-103 Rev.02 Schedule and Complete X-box sales activities relae to the opportunity",
//                "Run_2-15_22-53-41","Step 2");
//        almDriver.attachScreenshot("http://hq-qcapp-prd:8080/qcbin","ISI","Salesforce_Release_2017","prathameshs","Welcome2",
//                "defect","name","Test Defect Run_2-15_22-53-42","","",
//                "Screenshot.png","Q:\\Setup_Intu_a4\\workspace\\gitRepo\\IntuSurgALM\\carrom.png","Test Description");
//        almDriver.createNewTestSet("http://hq-qcapp-prd:8080/qcbin","ISI","Salesforce_Release_2017","prathameshs","Welcome2",
//                "AutomationTest","Test Set Daily Run 69","aniketd","80",
//                false,"Q:\\Users\\prathameshs\\IntuSurg\\IntutiveSurgical","CHROME,FIREFOX,IE");
//        almDriver.createNewTestSet("http://hq-qcapp-prd:8080/qcbin","ISI","Salesforce_Release_2017","prathameshs","Welcome2",
//                "AutomationTest","Test Set Daily Run 70","aniketd","29",
//                true,"Q:\\Users\\prathameshs\\IntuSurg\\IntutiveSurgical","CHROME,FIREFOX,IE");
    }

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
    public void attachScreenshot(final String serverUrl, final String domain,
                                 final String project, String username, String password, String entityType, String entityKeyToSearch,
                                 String entityValueToSearch, String runStepKeyToSearch, String runStepValueToSearch,
                                 String multipartFileName, String multipartFilPath, String multipartFileDescription) throws Exception {
        AttachScreenshotToEntity attScreenshot = new AttachScreenshotToEntity();
        attScreenshot.attachScreenshotToEntity(
                serverUrl, domain, project, username, password, entityType, entityKeyToSearch,
                entityValueToSearch, runStepKeyToSearch, runStepValueToSearch, multipartFileName,
                multipartFilPath, multipartFileDescription);
    }

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
    public String createNewTestSet(final String serverUrl, final String domain,
                                   final String project, String username, String password, String testSetFolderName, String testSetName,
                                   String owner, String referanceTestSetID, boolean isTestFolder, String testngFilePath, String browser) throws Exception {
        CreateTestSet createTestSet = new CreateTestSet();
        return createTestSet.createTestSet(serverUrl, domain,
                project, username, password, testSetFolderName, testSetName, owner, referanceTestSetID, isTestFolder, testngFilePath, browser);
    }

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
     * @param testName      name of com.hpalm.afour.test case
     * @param runName       name of run
     * @param stepName      name of step
     * @throws Exception
     */
    public String createNewDefect(final String serverUrl, final String domain,
                                  final String project, String username, String password, String owner, String priority,
                                  String name, String userTempSeven, String testName, String runName, String stepName) throws Exception {
        CreateDefect cd = new CreateDefect();
        return cd.createDefect(serverUrl, domain,
                project, username, password, owner, priority, name, userTempSeven, testName, runName, stepName);
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
     * @param testID  description of defect
     * @param host      Name of testing machine e.g. WIN7-XDT-PVD74
     * @param osName    OS of testing machine e.g. Windows 7
     * @param status    Passed, Failed, Not Started, In Progress, No Run
     * @throws Exception
     */
    public String createNewRun(final String serverUrl, final String domain,
                               final String project, String username, String password, String owner, String name,
                               String testID, String host, String osName, String status) throws Exception {
        CreateRun createRun = new CreateRun();
        return createRun.createRun(serverUrl, domain, project, username, password, owner, name,
                testID, host, osName, status);
    }

    /**
     * Method to update defect with any single field.
     *
     * @param serverUrl                      https://hq-qcapp-prd:8080
     * @param domain                         ISI
     * @param project                        ISI_AutomatedTest_Repository
     * @param username                       rajanb
     * @param password                       abc@123
     * @param defectKeyToSearch              name, id etc.
     * @param defectValueToSearch            defect name etc
     * @param updatedField                   Field to update
     * @param updatedFieldInitialUpdateValue Field value to update
     * @throws Exception
     */
    public void updateDefect(final String serverUrl, final String domain,
                             final String project, String username, String password, String defectKeyToSearch, String defectValueToSearch,
                             String updatedField, String updatedFieldInitialUpdateValue) throws Exception {
        UpdateDefect upDefect = new UpdateDefect();
        upDefect.updateDefect(serverUrl, domain,
                project, username, password, defectKeyToSearch, defectValueToSearch, updatedField, updatedFieldInitialUpdateValue);
    }

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
    public void updateEntityResult(final String serverUrl, final String domain,
                                   final String project, String username, String password, String entityType, String entityKeyToSearch,
                                   String entityValueToSearch, String runStepKeyToSearch, String runStepValueToSearch, String updatedField,
                                   String updatedFieldInitialUpdateValue, String actualValue) throws Exception {
        UpdateEntityResult upEntityResult = new UpdateEntityResult();
        upEntityResult.updateEntityResult(serverUrl, domain,
                project, username, password, entityType, entityKeyToSearch,
                entityValueToSearch, runStepKeyToSearch, runStepValueToSearch, updatedField,
                updatedFieldInitialUpdateValue, actualValue);
    }

    /**
     * Method to update Test Case with any single field.
     *
     * @param serverUrl                      https://hq-qcapp-prd:8080
     * @param domain                         ISI
     * @param project                        ISI_AutomatedTest_Repository
     * @param username                       rajanb
     * @param password                       abc@123
     * @param testSetFolderName              AutomationTest
     * @param testSetName                    2017-05-10 03-42-02 PM - Daily Test
     * @param testId                         Test Case ID  visible in Test lan
     * @param updatedField                   Field to update
     * @param updatedFieldInitialUpdateValue Field value to update
     * @throws Exception
     */
    public void updateTestCaseInstance(final String serverUrl, final String domain,
                                          final String project, String username, String password, String testSetFolderName, String testSetName,String testId,String updatedField, String updatedFieldInitialUpdateValue)
            throws Exception {
        UpdateTestCaseInstance updateInstance = new UpdateTestCaseInstance();
        updateInstance.updateTestCaseInstance(serverUrl, domain,
                project, username, password, testSetFolderName, testSetName,testId ,updatedField,
                updatedFieldInitialUpdateValue);
    }

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
    public void attachFile(final String serverUrl, final String domain,
                                 final String project, String username, String password, String entityType, String entityKeyToSearch,
                                 String entityValueToSearch, String runStepKeyToSearch, String runStepValueToSearch,
                                 String multipartFileName, String multipartFilPath, String multipartFileDescription) throws Exception {
        AttachFileToEntity attachFile = new AttachFileToEntity();
        attachFile.attachFileToEntity(
                serverUrl, domain, project, username, password, entityType, entityKeyToSearch,
                entityValueToSearch, runStepKeyToSearch, runStepValueToSearch, multipartFileName,
                multipartFilPath, multipartFileDescription);
    }

}
