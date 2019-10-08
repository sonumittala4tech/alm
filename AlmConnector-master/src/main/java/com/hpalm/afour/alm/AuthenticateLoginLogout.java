package com.hpalm.afour.alm;

import com.hpalm.afour.infrastructure.*;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * This example shows how to login/logout/authenticate to the server with REST.
 * note that this is a  rather "thin" layer over {@link RestConnector} because
 * these operations are *almost* HTML standards.
 */
public class AuthenticateLoginLogout {

    public static void main(String[] args) throws Exception {
    }

    private RestConnector con;

    public AuthenticateLoginLogout() {
        con = RestConnector.getInstance();
    }

    /**
     * @param username
     * @param password
     * @return true if authenticated at the end of this method.
     * @throws Exception
     *
     * convenience method used by other examples to do their login
     */
    public boolean login(String username, String password) throws Exception {

        String authenticationPoint = this.isAuthenticated();
        if (authenticationPoint != null) {
            return this.login(authenticationPoint, username, password);
        }
        return true;
    }

    /**
     * @param loginUrl
     *            to authenticate at
     * @param username
     * @param password
     * @return true on operation success, false otherwise
     * @throws Exception
     *
     * Logging in to our system is standard http login (basic authentication),
     * where one must store the returned cookies for further use.
     */
    public boolean login(String loginUrl, String username, String password)
            throws Exception {

        //create a string that lookes like:
        // "Basic ((username:password)<as bytes>)<64encoded>"
        byte[] credBytes = (username + ":" + password).getBytes();
        String credEncodedString = "Basic " + Base64Encoder.encode(credBytes);

        Map<String, String> map = new HashMap<String, String>();
        map.put("Authorization", credEncodedString);

        Response response = con.httpGet(loginUrl, null, map);

        boolean ret = response.getStatusCode() == HttpURLConnection.HTTP_OK;

        return ret;
    }

    /**
     * @return true if logout successful
     * @throws Exception
     *             close session on server and clean session cookies on client
     */
    public boolean logout() throws Exception {

        //note the get operation logs us out by setting authentication cookies to:
        // LWSSO_COOKIE_KEY="" via server response header Set-Cookie
        Response response =
                con.httpGet(con.buildUrl("authentication-point/logout"),
                        null, null);

        return (response.getStatusCode() == HttpURLConnection.HTTP_OK);

    }

    /**
     * @return null if authenticated.<br>
     *         a url to authenticate against if not authenticated.
     * @throws Exception
     */
    public String isAuthenticated() throws Exception {

        String isAuthenticateUrl = con.buildUrl("rest/is-authenticated");
        String ret;

        Response response = con.httpGet(isAuthenticateUrl, null, null);
        int responseCode = response.getStatusCode();

        //if already authenticated
        if (responseCode == HttpURLConnection.HTTP_OK) {

            ret = null;
        }

        //if not authenticated - get the address where to authenticate
        // via WWW-Authenticate
        else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {

            Iterable<String> authenticationHeader =
                    response.getResponseHeaders().get("WWW-Authenticate");

            String newUrl =
                    authenticationHeader.iterator().next().split("=")[1];
            newUrl = newUrl.replace("\"", "");
            newUrl += "/authenticate";
            ret = newUrl;
        }

        //Not ok, not unauthorized. An error, such as 404, or 500
        else {

            throw response.getFailure();
        }

        return ret;
    }

}
