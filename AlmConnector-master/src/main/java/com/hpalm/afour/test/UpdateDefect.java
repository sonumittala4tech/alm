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
public class UpdateDefect {

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
    protected void updateDefect(final String serverUrl, final String domain,
                                final String project, String username, String password, String defectKeyToSearch, String defectValueToSearch, String updatedField, String updatedFieldInitialUpdateValue)
            throws Exception {

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

            String exampleEntityType = "defect";
            String requirementsUrl =
                    con.buildEntityCollectionUrl(exampleEntityType);

            if (!defectKeyToSearch.equalsIgnoreCase("id")) {
                String id = commLib.getEntityID(exampleEntityType, defectKeyToSearch, defectValueToSearch, con, requestHeaders);
                newEntityToUpdateUrl = requirementsUrl + "/" + id;
            } else {
                newEntityToUpdateUrl = requirementsUrl + "/" + defectValueToSearch;
            }

            //create xml that when posted modifies the entity
            String updatedEntityXml =
                    commLib.generateSingleFieldUpdateXmlForDefect(updatedField,
                            updatedFieldInitialUpdateValue);

            //checkout (or lock) the entity - depending on versioning support.
            boolean isVersioned = Constants.isVersioned(exampleEntityType,
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

            //update the entity
            String put = example.update(newEntityToUpdateUrl,
                    updatedEntityXml).toString();
            Assert.assertTrue("posted field value not found",
                    put.contains(updatedFieldInitialUpdateValue));

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
            e.printStackTrace();

        }
        try {
            boolean unlock = example.unlock(newEntityToUpdateUrl);
            Assert.assertTrue("unlock failed", unlock);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        //logout and close connections
        login.logout();

    }

    private RestConnector con;

    /**
     * @param
     */
    public UpdateDefect() {
        con = RestConnector.getInstance();
    }


}
