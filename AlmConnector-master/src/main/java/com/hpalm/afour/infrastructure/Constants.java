package com.hpalm.afour.infrastructure;

/**
*
* These constants are used throughout the code to set the
* server to work with.
* To execute this code, change these settings to fit
* those of your server.
*/
public class Constants {
private Constants() {}
   public static final String HOST = "hq-qcapp-prd";
   public static final String PORT = "8080";

   public static final String USERNAME = "prathameshs";
   public static final String PASSWORD = "Welcome2";

   public static final String DOMAIN = "ISI";
   public static final String PROJECT = "ISI_AutomatedTest_Repository";


  /**
    * Supports running tests correctly on both versioned
    * and non-versioned projects.
    * @return true if entities of entityType support versioning
    */
   public static boolean isVersioned(String entityType,
       final String domain, final String project)
       throws Exception {

       RestConnector con = RestConnector.getInstance();
       String descriptorUrl =
           con.buildUrl("rest/domains/"
                + domain
                + "/projects/"
                + project
                + "/customization/entities/"
                + entityType);

       String descriptorXml =
          con.httpGet(descriptorUrl, null, null).toString();
       EntityDescriptor descriptor =
               EntityMarshallingUtils.marshal
                   (EntityDescriptor.class, descriptorXml);

       boolean isVersioned = descriptor.getSupportsVC().getValue();

       return isVersioned;
   }

   public static String generateFieldXml(String field, String value) {
       return "<Field Name=\"" + field
          + "\"><Value>" + value
          + "</Value></Field>";
   }

   /**
    * This string used to create new "requirement" type entities.
    */
   public static final String entityToPostName = "Automation"
       + Double.toHexString(Math.random());
   public static final String entityToPostFieldName =
       "detection-version";
   public static final String entityToPostFieldValue = "1";
    //public static final String entityToPostFormat = entityXML;

    public static final String entityToPostFormat =
            "<Entity Type=\"defect\">"
                +"<Fields>"
                    +"<Field Name=\"detected-by\">"
                    +"<Value>prathameshs</Value>"
                    +"</Field>"
                +"<Field Name=\"detection-version\">" +
                    "  <Value>"+entityToPostFieldValue+"</Value>" +
                    "  </Field>"
                +"<Field Name=\"owner\">" +
                    "  <Value>aniketd</Value>" +
                    "  </Field>"
                +"<Field Name=\"creation-time\">"
                    +"<Value>2017-02-10</Value>"
                    +"</Field>"
                +"<Field Name=\"severity\">"
                    +"<Value>2-Medium</Value>"
                    +"</Field>"
                +"<Field Name=\"priority\">" +
                    "<Value>P3-Low</Value>" +
                    "</Field>"
                +"<Field Name=\"description\">" +
                    "            <Value>Test Description Car does not start on cold mornings</Value>" +
                    "        </Field>"
                    +"<Field Name=\"user-template-01\">" +
                    "  <Value>01-21 ISU</Value>" +
                    "  </Field>"
                    +"<Field Name=\"user-template-02\">" +
                    "  <Value>User Training</Value>" +
                    "  </Field>"
                    +"<Field Name=\"user-template-07\">" +
                    "  <Value>Test Cycle 1</Value>" +
                    "  </Field>"
                    +"<Field Name=\"name\">"
                    +"<Value>Returned value not does not match value in database.</Value>"
                    +"</Field>"
                +"</Fields>"
                +"</Entity>";

   public static final String entityToPostXml =
       String.format(
               entityToPostFormat,
               "name",
               entityToPostName,
               entityToPostFieldName,
               entityToPostFieldValue);

   public static final CharSequence entityToPostFieldXml =
       generateFieldXml(Constants.entityToPostFieldName,
       Constants.entityToPostFieldValue);

}
