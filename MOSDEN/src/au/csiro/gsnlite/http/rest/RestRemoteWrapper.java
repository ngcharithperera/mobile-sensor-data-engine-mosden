package au.csiro.gsnlite.http.rest;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.KeyStore;
import java.sql.SQLException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import au.csiro.gsnlite.Main;
import au.csiro.gsnlite.beans.ContainerConfig;
import au.csiro.gsnlite.beans.DataField;
import au.csiro.gsnlite.beans.StreamElement;
import au.csiro.gsnlite.utils.Logger;
import au.csiro.gsnlite.wrappers.AbstractWrapper;

import com.thoughtworks.xstream.XStream;

public class RestRemoteWrapper extends AbstractWrapper {

    private final XStream XSTREAM = StreamElement4Rest.getXstream();

    private String TAG = "RestRemoteWrapper.class";
    private final transient Logger logger = Logger.getInstance();

    private DataField[] structure = null;

    private DefaultHttpClient httpclient;

    private long lastReceivedTimestamp = -1;

    private ObjectInputStream inputStream;

    private HttpResponse response;

    private HttpParams getHttpClientParams(int timeout) {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
        HttpProtocolParams.setUseExpectContinue(params, true);
        HttpConnectionParams.setTcpNoDelay(params, false);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        HttpConnectionParams.setStaleCheckingEnabled(params, true);
        HttpConnectionParams.setConnectionTimeout(params, 30 * 1000);    // Set the connection time to 30s
        HttpConnectionParams.setSoTimeout(params, timeout);
        HttpProtocolParams.setUserAgent(params, "GSN-HTTP-CLIENT");
        return params;
    }

    public DataField[] getOutputFormat() {
        return structure;
    }

    private RemoteWrapperParamParser initParams;

    public String getWrapperName() {
        return "Rest Remote Wrapper";
    }

    public boolean initialize() {
        try {
            initParams = new RemoteWrapperParamParser(getActiveAddressBean(), false);
            httpclient = new DefaultHttpClient(getHttpClientParams(initParams.getTimeout()));
            // Init the http client
            if (initParams.isSSLRequired()) {
                KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(new FileInputStream(new File("conf/servertestkeystore")), Main.getContainerConfig().getSSLKeyStorePassword().toCharArray());
                SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
                socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                int sslPort = Main.getContainerConfig().getSSLPort() > 0 ? Main.getContainerConfig().getSSLPort() : ContainerConfig.DEFAULT_SSL_PORT;
                Scheme sch = new Scheme("https", socketFactory, sslPort);
                httpclient.getConnectionManager().getSchemeRegistry().register(sch);
            }
            Scheme plainsch = new Scheme("http", PlainSocketFactory.getSocketFactory(), Main.getContainerConfig().getContainerPort());
            httpclient.getConnectionManager().getSchemeRegistry().register(plainsch);
            //
            lastReceivedTimestamp = initParams.getStartTime();
            structure = connectToRemote();
        } catch (Exception e) {
            logger.error(TAG, e.getMessage(), e);
            return false;
        }
        return true;
    }


    public DataField[] connectToRemote() throws IOException, ClassNotFoundException {
        // Create the GET request
        HttpGet httpget = new HttpGet(initParams.getRemoteContactPointEncoded(lastReceivedTimestamp));
        // Create local execution context
        HttpContext localContext = new BasicHttpContext();
        //
        structure = null;
        int tries = 0;
        AuthState authState = null;
        //
        if (inputStream != null) {
            try {
                if(response != null && response.getEntity() != null) {
                        response.getEntity().consumeContent();
                }
                inputStream.close();
                inputStream = null;
            }
            catch (Exception e) {
                logger.debug(TAG,e.getMessage(), e);
            }
        }
        //
        while (tries < 2) {
            tries++;
            try {
                // Execute the GET request
                response = httpclient.execute(httpget, localContext);
                //
                int sc = response.getStatusLine().getStatusCode();
                //
                if (sc == HttpStatus.SC_OK) {
                    logger.debug(TAG,new StringBuilder().append("Wants to consume the structure packet from ").append(initParams.getRemoteContactPoint()).toString());
                    inputStream = XSTREAM.createObjectInputStream(response.getEntity().getContent());
                    structure = (DataField[]) inputStream.readObject();
                    logger.warn(TAG,"Connection established for: " + initParams.getRemoteContactPoint());
                    break;
                } else {
                    if (sc == HttpStatus.SC_UNAUTHORIZED)
                        authState = (AuthState) localContext.getAttribute(ClientContext.TARGET_AUTH_STATE); // Target host authentication required
                    else if (sc == HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED)
                        authState = (AuthState) localContext.getAttribute(ClientContext.PROXY_AUTH_STATE); // Proxy authentication required
                    else {
                        logger.error(TAG,new StringBuilder()
                                .append("Unexpected GET status code returned: ")
                                .append(sc)
                                .append("\nreason: ")
                                .append(response.getStatusLine().getReasonPhrase()).toString());
                    }
                    if (authState != null) {
                        if (initParams.getUsername() == null || (tries > 1 && initParams.getUsername() != null)) {
                            logger.error(TAG,"A valid username/password required to connect to the remote host: " + initParams.getRemoteContactPoint());
                        } else {

                            AuthScope authScope = authState.getAuthScope();
                            logger.warn(TAG,new StringBuilder().append("Setting Credentials for host: ").append(authScope.getHost()).append(":").append(authScope.getPort()).toString());
                            Credentials creds = new UsernamePasswordCredentials(initParams.getUsername(), initParams.getPassword());
                            httpclient.getCredentialsProvider().setCredentials(authScope, creds);
                        }
                    }
                }
            }
            catch (RuntimeException ex) {
                // In case of an unexpected exception you may want to abort
                // the HTTP request in order to shut down the underlying
                // connection and release it back to the connection manager.
                logger.warn(TAG,"Aborting the HTTP GET request.");
                httpget.abort();
                throw ex;
            }
            finally {
                if (structure == null) {
                    if(response != null && response.getEntity() != null) {
                        response.getEntity().consumeContent();
                    }
                }
            }
        }

        if (structure == null)
            throw new RuntimeException("Cannot connect to the remote host: " + initParams.getRemoteContactPoint());

        return structure;
    }

    public void dispose() {
        try {
            httpclient.getConnectionManager().shutdown(); //This closes the connection already in use by the response
        } catch (Exception e) {
            logger.debug(TAG, e.getMessage(), e);
        }
    }

    public void run() {
        StreamElement4Rest se = null;
        while (isActive()) {
            try {
                while (isActive() && (se = (StreamElement4Rest) inputStream.readObject()) != null) {
                    StreamElement streamElement = se.toStreamElement();
                    if ( ! (streamElement.getFieldNames().length == 1 && streamElement.getFieldNames()[0].equals("keepalive"))) {
                        boolean status = manualDataInsertion(streamElement);
                        if (!status && inputStream != null) {
                            response.getEntity().consumeContent();
                            inputStream.close();
                            inputStream = null;
                        }
                    }
                    else
                        logger.debug(TAG,"Received a keep alive message.");
                }
            }
            catch (Exception e) {
                logger.warn(TAG,"Connection to the remote host: " + initParams.getRemoteContactPoint() + " is lost, trying to reconnect in 3 seconds...");
                try {
                    if (isActive()) {
                        Thread.sleep(3000);
                        connectToRemote();
                    }
                } catch (Exception err) {
                    logger.debug(TAG,err.getMessage(), err);
                }
            }
        }
    }

    public boolean manualDataInsertion(StreamElement se) {
        try {
            // If the stream element is out of order, we accept the stream element and wait for the next (update the last received time and return true)
            if (isOutOfOrder(se)) {
                lastReceivedTimestamp = se.getTimeStamp();
                return true;
            }
            // Otherwise, we first try to insert the stream element.
            // If the stream element was inserted succesfully, we wait for the next,
            // otherwise, we return false.
            boolean status=false;
			try {
				status = postStreamElement(se);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            if (status)
                lastReceivedTimestamp = se.getTimeStamp();
            return status;
        }
        catch (SQLException e) {
            logger.warn(TAG,e.getMessage(), e);
            return false;
        }
    }
}
