package au.csiro.gsnlite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Properties;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import android.os.Environment;
import au.csiro.gsnlite.beans.ContainerConfig;
import au.csiro.gsnlite.beans.Mappings;
import au.csiro.gsnlite.beans.StorageConfig;
import au.csiro.gsnlite.http.ControllerServlet;
import au.csiro.gsnlite.http.HelloServlet;
import au.csiro.gsnlite.http.rest.LocalDeliveryWrapper;
import au.csiro.gsnlite.http.rest.PushDelivery;
import au.csiro.gsnlite.http.rest.RestDelivery;
import au.csiro.gsnlite.http.rest.RestStreamHanlder;
import au.csiro.gsnlite.storage.DBConnectionInfo;
import au.csiro.gsnlite.storage.StorageManager;
import au.csiro.gsnlite.storage.StorageManagerFactory;
import au.csiro.gsnlite.utils.Logger;
import au.csiro.gsnlite.utils.SQLValidator;
import au.csiro.gsnlite.utils.ValidityTools;
import au.csiro.gsnlite.vsensor.SQLValidatorIntegration;
import au.csiro.gsnlite.vsensor.VSensorConfig;
import au.csiro.gsnlite.vsensor.VSensorLoader;
import au.csiro.gsnlite.wrappers.WrappersUtil;


/**
 * Web URLs :
 * GSN: http://localhost:22001/gsn
 */
public class Main {

	private static Main singleton ;
	private static int gsnControllerPort;	
    private static StorageManager mainStorage = null;
    private static StorageManager windowStorage = null;
    private static StorageManager validationStorage = null;

	private GSNController controlSocket;
    private static final int DEFAULT_JETTY_SERVLETS = 100;
    public static final int DEFAULT_MAX_DB_CONNECTIONS = 8;

	//public static final String     DEFAULT_GSN_LOG4J_PROPERTIES     = "conf/log4j.properties";

		
	private static transient Logger logger             = Logger.getInstance();
	private static String TAG = "Main.class";
	public static final String     DEFAULT_GSN_CONF_FILE            = "conf/gsn.xml";
	public static String     DEFAULT_VIRTUAL_SENSOR_DIRECTORY = "conf/virtual-sensors";	
	public static final String     DEFAULT_WEB_APP_PATH             = "conf/webapp";
	

	/**
	 * Mapping between the wrapper name (used in addressing of stream source)
	 * into the class implementing DataSource.
	 */
	private static  Properties wrappers ;

	private  ContainerConfig containerConfig;
	
	
//	public static void StartGSN ( String port)  {
//	
//		Main.gsnControllerPort = Integer.parseInt(port);
//		
//		//the update splash will have to be modified to sow all outputs to android screen
//		updateSplashIfNeeded(new String[] {"GSN is trying to start.","All GSN logs are available at: logs/gsn.log"});
//		try {
//			Main.getInstance();
//		}catch (Exception e) {
//			updateSplashIfNeeded(new String[] {"Starting GSN failed! Look at logs/gsn.log for more information."});
//			try {
//				Thread.sleep(4000);
//			} catch (InterruptedException e1) {
//				e1.printStackTrace();
//			}
//		}
		//closeSplashIfneeded();
//	}
//	
	public static Main getInstance(String port) {		
		if (singleton==null)
			Main.gsnControllerPort = Integer.parseInt(port);
			updateSplashIfNeeded(new String[] {"GSN is trying to start.","All GSN logs are available at: logs/gsn.log"});
			try {
				singleton=new Main();
			} catch (Exception e) {
				logger.error (TAG,e.getMessage(),e);
				throw new RuntimeException(e);
			}
			return singleton;
	}



    private Main() throws Exception {

		ValidityTools.checkAccessibilityOfFiles (WrappersUtil.DEFAULT_WRAPPER_PROPERTIES_FILE , DEFAULT_GSN_CONF_FILE );
    	ValidityTools.checkAccessibilityOfDirs ( DEFAULT_VIRTUAL_SENSOR_DIRECTORY );
		////PropertyConfigurator.configure ( Main.DEFAULT_GSN_LOG4J_PROPERTIES );
    	
    	//  initializeConfiguration();
    	
		try {
			controlSocket = new GSNController(null, Main.gsnControllerPort);
			containerConfig = loadContainerConfiguration();
			//updateSplashIfNeeded(new String[] {"GSN is starting at port:"+containerConfig.getContainerPort(),"All GSN logs are available at: logs/gsn.log"});
			System.out.println("Global Sensor Networks (GSN) is Starting on port "+containerConfig.getContainerPort()+"...");
            System.out.println("The logs of GSN server are available in logs/gsn.log file.");
			System.out.println("To Stop GSN execute the gsn-stop script.");
			//code to register the new GSN instance with the global transaction manager						
		} catch ( FileNotFoundException e ) {
			logger.error (TAG, new StringBuilder ( ).append ( "The the configuration file : conf/gsn.xml").append ( " doesn't exist." ).toString ( ) );
			logger.error (TAG, e.getMessage ( ) );
			logger.error (TAG, "Check the path of the configuration file and try again." );
			if ( logger.isDebugEnabled ( ) ) logger.debug (TAG, e.getMessage ( ) , e );
			throw new Exception(e);
		}
        int maxDBConnections = System.getProperty("maxDBConnections") == null ? DEFAULT_MAX_DB_CONNECTIONS : Integer.parseInt(System.getProperty("maxDBConnections"));
        int maxSlidingDBConnections = System.getProperty("maxSlidingDBConnections") == null ? DEFAULT_MAX_DB_CONNECTIONS : Integer.parseInt(System.getProperty("maxSlidingDBConnections"));
        int maxServlets = System.getProperty("maxServlets") == null ? DEFAULT_JETTY_SERVLETS : Integer.parseInt(System.getProperty("maxServlets"));

        // Init the AC db connection.
//        if(Main.getContainerConfig().isAcEnabled()==true)
//        {
//            ConnectToDB.init ( containerConfig.getStorage().getJdbcDriver() , containerConfig.getStorage().getJdbcUsername ( ) , containerConfig.getStorage().getJdbcPassword ( ) , containerConfig.getStorage().getJdbcURL ( ) );
//        }

        mainStorage = StorageManagerFactory.getInstance(containerConfig.getStorage().getJdbcDriver ( ) , containerConfig.getStorage().getJdbcUsername ( ) , containerConfig.getStorage().getJdbcPassword ( ) , containerConfig.getStorage().getJdbcURL ( ) , maxDBConnections);
        //
        StorageConfig sc = containerConfig.getSliding() != null ? containerConfig.getSliding().getStorage() : containerConfig.getStorage() ;
        windowStorage = StorageManagerFactory.getInstance(sc.getJdbcDriver ( ) , sc.getJdbcUsername ( ) , sc.getJdbcPassword ( ) , sc.getJdbcURL ( ), maxSlidingDBConnections);
        //
        validationStorage = StorageManagerFactory.getInstance("org.h2.Driver", "sa", "", "jdbc:h2:/data/data/au.csiro.gsnlite/database/validator;FILE_LOCK=FS;PAGE_SIZE=1024;CACHE_SIZE=8192", Main.DEFAULT_MAX_DB_CONNECTIONS);

        if ( logger.isInfoEnabled ( ) ) 
        	logger.info (TAG, "The Container Configuration file loaded successfully." );

        //Ver1 of GSN-Lite is without a HTTP Server.
        //We want to load a VSensor and create some data in the database
        
		try {
			logger.debug (TAG,"Starting the http-server @ port: "+containerConfig.getContainerPort()+" (maxDBConnections: "+maxDBConnections+", maxSlidingDBConnections: " + maxSlidingDBConnections + ", maxServlets:"+maxServlets+")"+" ...");
            Server jettyServer = getJettyServer(getContainerConfig().getContainerPort(),  maxServlets);
			jettyServer.start ( );
			logger.debug (TAG,"http-server running @ port: "+containerConfig.getContainerPort());
		} catch ( Exception e ) {
			throw new Exception("Start of the HTTP server failed. The HTTP protocol is used in most of the communications: "+ e.getMessage(),e);
		}
		

		
		VSensorLoader vsloader = VSensorLoader.getInstance ( DEFAULT_VIRTUAL_SENSOR_DIRECTORY );
		controlSocket.setLoader(vsloader);

//		String msrIntegration = "gsn.msr.sensormap.SensorMapIntegration";
//		try {
//			vsloader.addVSensorStateChangeListener((VSensorStateChangeListener) Class.forName(msrIntegration).newInstance());
//		}catch (Exception e) {
//			logger.warn("MSR Sensor Map integration is disabled.");
//		}

		vsloader.addVSensorStateChangeListener(new SQLValidatorIntegration(SQLValidator.getInstance()));
		vsloader.addVSensorStateChangeListener(DataDistributer.getInstance(LocalDeliveryWrapper.class));
		vsloader.addVSensorStateChangeListener(DataDistributer.getInstance(PushDelivery.class));
		vsloader.addVSensorStateChangeListener(DataDistributer.getInstance(RestDelivery.class));

		ContainerImpl.getInstance().addVSensorDataListener(DataDistributer.getInstance(LocalDeliveryWrapper.class));
		ContainerImpl.getInstance().addVSensorDataListener(DataDistributer.getInstance(PushDelivery.class));
		ContainerImpl.getInstance().addVSensorDataListener(DataDistributer.getInstance(RestDelivery.class));
		
		vsloader.startLoading();


	}

    public Server getJettyServer(int port, int maxThreads) throws IOException {
		
    	
    	Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);               
 
        context.addServlet(new ServletHolder(HelloServlet.class),"/*");
        context.addServlet(new ServletHolder(new ControllerServlet()),"/gsn/*");
        context.addServlet(new ServletHolder(new RestStreamHanlder()),"/streaming/*");
   	
    	

    	//server.setHandler(new HelloHandler());

  //--------------------------------------------------------------------------------------------------
    	
    	//Prem Try
    	
//        Server server = new Server();
//		HandlerCollection handlers = new HandlerCollection();
//        ContextHandlerCollection contexts = new ContextHandlerCollection();
//        server.setThreadPool(new QueuedThreadPool(maxThreads));

//--------------------------------------------------------------------------------------------------
//        SslSocketConnector sslSocketConnector = null;
//        if (sslPort > 0) {
//            System.out.println("SSL is Starting on port "+sslPort+"...");
//			sslSocketConnector = new SslSocketConnector();
//            sslSocketConnector.setPort(getContainerConfig().getSSLPort());
//            sslSocketConnector.setKeystore("conf/servertestkeystore");
//            sslSocketConnector.setPassword(getContainerConfig().getSSLKeyPassword());
//            sslSocketConnector.setKeyPassword(getContainerConfig().getSSLKeyStorePassword());
//            sslSocketConnector.setTruststore("conf/servertestkeystore");
//            sslSocketConnector.setTrustPassword(getContainerConfig().getSSLKeyStorePassword());
//        }
//        else if (getContainerConfig().isAcEnabled())
//            logger.error (TAG,"SSL MUST be configured in the gsn.xml file when Access Control is enabled !");
//      
//--------------------------------------------------------------------------------------------------        
//Prem Try        
//        AbstractConnector connector=new SelectChannelConnector (); // before was connector//new SocketConnector ();//using basic connector for windows bug; Fast option=>SelectChannelConnector
//        connector.setPort ( port );
//        connector.setMaxIdleTime(30000);
//        connector.setAcceptors(2);
//        server.setConnectors ( new Connector [ ] { connector } );
        
//--------------------------------------------------------------------------------------------------
        //        connector.setConfidentialPort(sslPort);

		//if (sslSocketConnector==null)
			
		//else
			//server.setConnectors ( new Connector [ ] { connector,sslSocketConnector } );
//--------------------------------------------------------------------------------------------------
      //Prem Try
//        File sdcard = Environment.getExternalStorageDirectory();
//		WebAppContext webAppContext = new WebAppContext(contexts, sdcard.getAbsolutePath() + "/" + DEFAULT_WEB_APP_PATH ,"/");
//
//		handlers.setHandlers(new Handler[]{contexts,new DefaultHandler()});
//		server.setHandler(handlers);
//--------------------------------------------------------------------------------------------------
		//Properties usernames = new Properties();
		//usernames.load(new FileReader("conf/realm.properties"));
//		if (!usernames.isEmpty()){
//			HashLoginService loginService = new HashLoginService();
//			loginService.setName("GSNRealm");
//			loginService.setConfig("conf/realm.properties");
//			loginService.setRefreshInterval(10000); //re-reads the file every 10 seconds.
//
//			Constraint constraint = new Constraint();
//			constraint.setName("GSN User");
//			constraint.setRoles(new String[]{"gsnuser"});
//			constraint.setAuthenticate(true);
//
//			ConstraintMapping cm = new ConstraintMapping();
//			cm.setConstraint(constraint);
//			cm.setPathSpec("/*");
//			cm.setMethod("GET");
//
//			ConstraintMapping cm2 = new ConstraintMapping();
//			cm2.setConstraint(constraint);
//			cm2.setPathSpec("/*");
//			cm2.setMethod("POST");
//
//			ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
//			securityHandler.setLoginService(loginService);
//			securityHandler.setConstraintMappings(new ConstraintMapping[]{cm, cm2});
//			securityHandler.setAuthenticator(new BasicAuthenticator());
//			webAppContext.setSecurityHandler(securityHandler);
//		}
// --------------------------------------------------------------------------------------------------
		//Prem Try
//		server.setSendServerVersion(true);
//		server.setStopAtShutdown ( true );
//		server.setSendServerVersion ( false );
//		server.setSessionIdManager(new HashSessionIdManager(new Random()));
		
//--------------------------------------------------------------------------------------------------
		
		//Prem Test Code
		
		
		
		
		
		return server;
		
	}
	private static void updateSplashIfNeeded(String message[]) {
		
		logger.debug (TAG,message.toString() + "\n");
	}

    public static StorageManager getValidationStorage() {
        return validationStorage;
    }

	public static ContainerConfig loadContainerConfiguration() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException, CertificateException, SecurityException, SignatureException, IOException{
		ValidityTools.checkAccessibilityOfFiles ( WrappersUtil.DEFAULT_WRAPPER_PROPERTIES_FILE , Main.DEFAULT_GSN_CONF_FILE );
		ValidityTools.checkAccessibilityOfDirs ( Main.DEFAULT_VIRTUAL_SENSOR_DIRECTORY );
		////PropertyConfigurator.configure ( Main.DEFAULT_GSN_LOG4J_PROPERTIES );
		ContainerConfig toReturn = null;
		try {
			toReturn = loadContainerConfig (DEFAULT_GSN_CONF_FILE );
			wrappers = WrappersUtil.loadWrappers(new HashMap<String, Class<?>>());
			if ( logger.isInfoEnabled ( ) ) logger.info (TAG, new StringBuilder ( ).append ( "Loading wrappers.properties at : " ).append ( WrappersUtil.DEFAULT_WRAPPER_PROPERTIES_FILE ).toString ( ) );
			if ( logger.isInfoEnabled ( ) ) logger.info (TAG, "Wrappers initialization ..." );		
		} catch ( FileNotFoundException e ) {
			logger.error (TAG, new StringBuilder ( ).append ( "The the configuration file : conf/gsn.xml").append ( " doesn't exist." ).toString ( ) );
			logger.error (TAG, e.getMessage ( ) );
			logger.error (TAG, "Check the path of the configuration file and try again." );
			if ( logger.isDebugEnabled ( ) ) logger.debug (TAG, e.getMessage ( ) , e );
			System.exit ( 1 );
		} catch ( ClassNotFoundException e ) {
			logger.error (TAG, "The file wrapper.properties refers to one or more classes which don't exist in the classpath");
			logger.error (TAG, e.getMessage ( ),e );
			System.exit ( 1 );
		} catch (JiBXException e){
			logger.error (TAG, "The configuration properties file cannot be loaded. Please ensure BIND operation was performed");
			logger.error(TAG, e.getMessage());
			e.printStackTrace();
			System.exit ( 1 );
		}
		finally {
			return toReturn;
		}
	}

	/**
	 * This method is called by Rails's Application.rb file.
	 */
	public static ContainerConfig loadContainerConfig (String gsnXMLpath) throws JiBXException, FileNotFoundException, NoSuchAlgorithmException, NoSuchProviderException, IOException, KeyStoreException, CertificateException, SecurityException, SignatureException, InvalidKeyException, ClassNotFoundException {
		File sdcard = Environment.getExternalStorageDirectory();
		
		if (!new File(sdcard, gsnXMLpath).isFile()) {
			logger.error (TAG,"Couldn't find the gsn.xml file @: "+(new File(gsnXMLpath).getAbsolutePath()));
			System.exit(1);
		}

		//Prem - OLD Code - Commented to replace the JiBX Parser and Remove Simple Parser
//		Serializer serializer = new Persister();
//		File source = new File(sdcard, gsnXMLpath);
//		
////		IBindingFactory bfact = BindingDirectory.getFactory ( ContainerConfig.class );
////		IUnmarshallingContext uctx = bfact.createUnmarshallingContext ( );
//		
//		ContainerConfig conf = null;
//		try {
//			conf = serializer.read(ContainerConfig.class, source);
//			//Class.forName(conf.getStorage().getJdbcDriver());
//			conf.setContainerConfigurationFileName (  gsnXMLpath );
//		} catch (Exception e) {
//			logger.error (TAG,e.getMessage());
//		} 
//				
//		//( ContainerConfig ) uctx.unmarshalDocument ( new FileInputStream ( new File ( gsnXMLpath ) ) , null );
//		
//		return conf;
		
		IBindingFactory bfact = BindingDirectory.getFactory ( ContainerConfig.class );
		IUnmarshallingContext uctx = bfact.createUnmarshallingContext ( );
		ContainerConfig conf = ( ContainerConfig ) uctx.unmarshalDocument ( new FileInputStream ( new File (sdcard, gsnXMLpath ) ) , null );
		Class.forName(conf.getStorage().getJdbcDriver());
		conf.setContainerConfigurationFileName (  gsnXMLpath );
		return conf;
	}

	//FIXME: COPIED_FOR_SAFE_STOAGE
	public static Properties getWrappers()  {
		if (singleton==null )
			return WrappersUtil.loadWrappers(new HashMap<String, Class<?>>());
		return singleton.wrappers;
	}
    
	//FIXME: COPIED_FOR_SAFE_STOAGE
	public  static Class < ? > getWrapperClass ( String id ) {
		try {
			String className =  getWrappers().getProperty(id);
			if (className ==null) {
				logger.error (TAG,"The requested wrapper: "+id+" doesn't exist in the wrappers.properties file.");
				return null;
			}

			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			logger.error (TAG,e.getMessage(),e);
		}
		return null;
	}

	/**
	 * Get's the GSN configuration without starting GSN.
	 * @return
	 * @throws Exception
	 */
	public static ContainerConfig getContainerConfig() {
		if (singleton == null)
			try {
				return loadContainerConfig(DEFAULT_GSN_CONF_FILE);
			} catch (Exception e) {
				return null;
			}
			else
				return singleton.containerConfig;
	}

//	public Server getJettyServer(int port, int sslPort, int maxThreads) throws IOException {
//		
//        Server server = new Server();
//		HandlerCollection handlers = new HandlerCollection();
//        ContextHandlerCollection contexts = new ContextHandlerCollection();
//        server.setThreadPool(new QueuedThreadPool(maxThreads));
//
//        SslSocketConnector sslSocketConnector = null;
//        if (sslPort > 0) {
//            System.out.println("SSL is Starting on port "+sslPort+"...");
//			sslSocketConnector = new SslSocketConnector();
//            sslSocketConnector.setPort(getContainerConfig().getSSLPort());
//            sslSocketConnector.setKeystore("conf/servertestkeystore");
//            sslSocketConnector.setPassword(getContainerConfig().getSSLKeyPassword());
//            sslSocketConnector.setKeyPassword(getContainerConfig().getSSLKeyStorePassword());
//            sslSocketConnector.setTruststore("conf/servertestkeystore");
//            sslSocketConnector.setTrustPassword(getContainerConfig().getSSLKeyStorePassword());
//        }
//        else if (getContainerConfig().isAcEnabled())
//            logger.error (TAG,"SSL MUST be configured in the gsn.xml file when Access Control is enabled !");
//        
//        AbstractConnector connector=new SelectChannelConnector (); // before was connector//new SocketConnector ();//using basic connector for windows bug; Fast option=>SelectChannelConnector
//        connector.setPort ( port );
//        connector.setMaxIdleTime(30000);
//        connector.setAcceptors(2);
//        connector.setConfidentialPort(sslPort);
//
//		if (sslSocketConnector==null)
//			server.setConnectors ( new Connector [ ] { connector } );
//		else
//			server.setConnectors ( new Connector [ ] { connector,sslSocketConnector } );
//
//		WebAppContext webAppContext = new WebAppContext(contexts, DEFAULT_WEB_APP_PATH ,"/");
//
//		handlers.setHandlers(new Handler[]{contexts,new DefaultHandler()});
//		server.setHandler(handlers);
//
//		Properties usernames = new Properties();
//		usernames.load(new FileReader("conf/realm.properties"));
//		if (!usernames.isEmpty()){
//			HashLoginService loginService = new HashLoginService();
//			loginService.setName("GSNRealm");
//			loginService.setConfig("conf/realm.properties");
//			loginService.setRefreshInterval(10000); //re-reads the file every 10 seconds.
//
//			Constraint constraint = new Constraint();
//			constraint.setName("GSN User");
//			constraint.setRoles(new String[]{"gsnuser"});
//			constraint.setAuthenticate(true);
//
//			ConstraintMapping cm = new ConstraintMapping();
//			cm.setConstraint(constraint);
//			cm.setPathSpec("/*");
//			cm.setMethod("GET");
//
//			ConstraintMapping cm2 = new ConstraintMapping();
//			cm2.setConstraint(constraint);
//			cm2.setPathSpec("/*");
//			cm2.setMethod("POST");
//
//			ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
//			securityHandler.setLoginService(loginService);
//			securityHandler.setConstraintMappings(new ConstraintMapping[]{cm, cm2});
//			securityHandler.setAuthenticator(new BasicAuthenticator());
//			webAppContext.setSecurityHandler(securityHandler);
//		}
//
//		server.setSendServerVersion(true);
//		server.setStopAtShutdown ( true );
//		server.setSendServerVersion ( false );
//		server.setSessionIdManager(new HashSessionIdManager(new Random()));

//		return server;
//	}
//
//    public static StorageManager getValidationStorage() {
//        return validationStorage;
//    }

    private static HashMap<Integer, StorageManager> storages = new HashMap<Integer, StorageManager>();
    private static HashMap<VSensorConfig, StorageManager> storagesConfigs = new HashMap<VSensorConfig, StorageManager>();
    public static StorageManager getStorage(VSensorConfig config) {
        StringBuilder sb = new StringBuilder("get storage for: ").append(config == null ? null : config.getName()).append(" -> use ");
        StorageManager sm = storagesConfigs.get(config == null ? null : config);
        if  (sm != null)
            return sm;

        DBConnectionInfo dci = null;
        if (config == null || config.getStorage() == null || !config.getStorage().isDefined()) {
            // Use the default storage
        //    sb.append("(default) config: ").append(config);
            sm = mainStorage;
        } else {
        //    sb.append("(specific) ");
            // Use the virtual sensor specific storage
            if (config.getStorage().isIdentifierDefined()) {
                //TODO get the connection info with the identifier.
                throw new IllegalArgumentException("Identifiers for storage is not supported yet.");
            } else {
                dci = new DBConnectionInfo(config.getStorage().getJdbcDriver(),
                        config.getStorage().getJdbcURL(),
                        config.getStorage().getJdbcUsername(),
                        config.getStorage().getJdbcPassword());
            }
            sm = storages.get(dci.hashCode());
            if (sm == null) {
                sm = StorageManagerFactory.getInstance(config.getStorage().getJdbcDriver(), config.getStorage().getJdbcUsername(), config.getStorage().getJdbcPassword(), config.getStorage().getJdbcURL(), DEFAULT_MAX_DB_CONNECTIONS);
                storages.put(dci.hashCode(), sm);
                storagesConfigs.put(config, sm);
            }
        }
        sb.append("storage: ").append(sm.toString()).append(" dci: ").append(dci).append(" code: ").append(dci == null ? null : dci.hashCode());
        if (logger.isDebugEnabled())
        	logger.warn(TAG,sb.toString());
        return sm;

    }

    public static StorageManager getStorage(String vsName) {
        return getStorage(Mappings.getVSensorConfig(vsName));
    }

    public static StorageManager getDefaultStorage() {
        return getStorage((VSensorConfig)null);
    }

    public static StorageManager getWindowStorage() {
        return windowStorage;
    }
}
