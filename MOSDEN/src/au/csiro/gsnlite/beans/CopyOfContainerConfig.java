package au.csiro.gsnlite.beans;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
//import org.jibx.runtime.BindingDirectory;
//import org.jibx.runtime.IBindingFactory;
//import org.jibx.runtime.IUnmarshallingContext;
//import org.jibx.runtime.JiBXException;

import au.csiro.gsnlite.utils.KeyValueImp;
import au.csiro.gsnlite.utils.ValidityTools;

public class CopyOfContainerConfig {

	public static final String            NOT_PROVIDED                     = "Not Provided";

	public static final int               DEFAULT_GSN_PORT                 = 22001;

    public static final int               DEFAULT_SSL_PORT                 = 8443;

	protected String                      name;

	public static final String            FIELD_NAME_webName               = "webName";

	protected String                      author;

	public static final String            FIELD_NAME_webAuthor             = "webAuthor";

	protected String                      description;

	public static final String            FIELD_NAME_webDescription        = "webDescription";

	protected String                      email;

	public static final String            FIELD_NAME_webEmail              = "webEmail";

	protected int                         port                    = DEFAULT_GSN_PORT;

	protected String                      containerFileName;

	protected int                         storagePoolSize    =-1              ;

	private int                           sslPort                           = DEFAULT_SSL_PORT;

    private boolean                       acEnabled                     = false;

	private String sslKeyStorePassword;

	private String sslKeyPassword;

    private StorageConfig storage;

    private SlidingConfig sliding;

    public boolean isAcEnabled() {
        return acEnabled;
    }

    public StorageConfig getStorage() {
        return storage;
    }

    public SlidingConfig getSliding() {
        return sliding;
    }

    public String getContainerFileName ( ) {
		return this.containerFileName;
	}

	public void setContainerConfigurationFileName ( final String containerFileName ) {
		this.containerFileName = containerFileName;
	}

	/**
	 * @return Returns the author.
	 */
	public String getWebAuthor ( ) {
		if ( this.author == null || this.author.trim( ).equals( "" ) )
			this.author = NOT_PROVIDED;
		else
			this.author = this.author.trim( );
		return this.author;

	}

	/**
	 * @return Returns the containerPort.
	 */
	public int getContainerPort ( ) {
		return this.port;
	}

	public static final String FIELD_NAME_gsnPortNo = "containerPort";

	public void setContainerPort ( int newValue ) {
		int oldValue = this.port;
		this.port = newValue;
	}

	/**
	 * @return Returns the webDescription.
	 */
	public String getWebDescription ( ) {
		if ( this.description == null || this.description.trim( ).equals( "" ) ) this.description = NOT_PROVIDED;
		return this.description.trim( );
	}

	/**
	 * @return Returns the webEmail.
	 */
	public String getWebEmail ( ) {
		if ( this.email == null ) this.email = NOT_PROVIDED;
		return this.email;
	}

	/**
	 * @return Returns the name.
	 */
	public String getWebName ( ) {
		if ( this.name == null || this.name.trim( ).equals( "" ) ) this.name = NOT_PROVIDED;
		this.name = this.name.trim( );
		return this.name;
	}

	public void setWebEmail ( String newValue ) {
		String oldValue = this.email;
		this.email = newValue;
	}

	public void setWebAuthor ( String newValue ) {
		String oldValue = this.author;
		this.author = newValue;
	}

	public void setWebName ( String newValue ) {
		String oldValue = this.name;
		this.name = newValue;
	}

	/**
	 * @return Returns the registryBootstrapAddr.
	 */
	private boolean isRegistryBootStrapAddrInitialized = false;



	/**
	 * @return Returns the storagePoolSize.
	 */
	public int getStoragePoolSize ( ) {
		return this.storagePoolSize;
	}

	public String toString ( ) {
		final StringBuilder builder = new StringBuilder( );
		builder.append( this.getClass( ).getName( ) ).append( " class [" ).append( "name=" ).append( this.name ).append( "," );
		return builder.append( "]" ).toString( );
	}

	/****************************************************************************
	 * UTILITY METHODS, Used by the GUI mainly.
	 ***************************************************************************/

    private static final String   DEFAULT_SSL_KEYSTORE_PWD = "changeit";

    private static final String   DEFAULT_SSL_KEY_PWD = "changeit";

	private String                 directoryLoggingLevel;

	public static final String     FIELD_NAME_directoryLoggingLevel   = "directoryLoggingLevel";

	private long                   maxDirectoryLogSizeInMB;

	public static final String     FIELD_NAME_maxDirectoryLogSizeInMB = "maxDirectoryLogSizeInMB";

	private String                 gsnLoggingLevel;

	public static final String     FIELD_NAME_gsnLoggingLevel         = "gsnLoggingLevel";

	private long                   maxGSNLogSizeInMB;

	public static final String     FIELD_NAME_maxGSNLogSizeInMB       = "maxGSNLogSizeInMB";

	public static final String     FIELD_NAME_directoryPortNo         = "directoryPortNo";

	public static final int        DEFAULT_DIRECTORY_PORT             = 1882;

	private String                 directoryLogFileName;

	public static final String     FIELD_NAME_directoryLogFileName    = "directoryLogFileName";

	private String                 gsnLogFileName;

	public static final String     FIELD_NAME_gsnLogFileName          = "gsnLogFileName";

	private String                 gsnLog4jFile;

	private String                 gsnConfigurationFileName;

	private Properties             gsnLog4JProperties;

	public static final String     FIELD_NAME_directoryServiceHost    = "directoryServiceHost";

	public static final String [ ] LOGGING_LEVELS                     = { "DEBUG" , "INFO" , "WARN" , "ERROR" };

	public static final String [ ] JDBC_SYSTEMS                       = { "H2 in Memory" , "H2 in File" , "MySql", "SQL Server" };

	public static final String [ ] JDBC_URLS                          = new String [ ] { "jdbc:h2:mem:." , "jdbc:h2:file:/path/to/file" , "jdbc:mysql://localhost:3306/gsn", "jdbc:jtds:sqlserver://localhost/gsn" };

	public static final String [ ] JDBC_DRIVERS                       = new String [ ] { "org.h2.Driver" , "org.h2.Driver" , "com.mysql.jdbc.Driver", "net.sourceforge.jtds.jdbc.Driver" };

	public static final String [ ] JDBC_URLS_PREFIX                   = new String [ ] { "jdbc:h2:mem:" , "jdbc:h2:file:" , "jdbc:mysql:", "jdbc:jtds:sqlserver:" };

	public static final String     DEFAULT_LOGGING_LEVEL              = CopyOfContainerConfig.LOGGING_LEVELS[ 3 ];

	private String                 databaseSystem;

	public static final String     FIELD_NAME_databaseSystem          = "databaseSystem";

	/**
	 * One Megabyte;
	 */
	public static final long       DEFAULT_GSN_LOG_SIZE               = 1 * 1024 * 1024;

	private boolean                isdatabaseSystemInitialzied        = false;

	public void setDirectoryLoggingLevel ( String newValue ) {
		String oldValue = this.directoryLoggingLevel;
		this.directoryLoggingLevel = newValue;
	}

	public String getDirectoryLoggingLevel ( ) {
		return this.directoryLoggingLevel;
	}

	public void setMaxDirectoryLogSizeInMB ( long newValue ) {
		long oldValue = this.maxDirectoryLogSizeInMB;
		this.maxDirectoryLogSizeInMB = newValue;
	}

	public long getMaxDirectoryLogSizeInMB ( ) {
		return this.maxDirectoryLogSizeInMB;
	}

	public void setGsnLoggingLevel ( String newValue ) {
		String oldValue = this.gsnLoggingLevel;
		this.gsnLoggingLevel = newValue;
	}

	public String getGsnLoggingLevel ( ) {
		return this.gsnLoggingLevel;
	}

	public void setMaxGSNLogSizeInMB ( long newValue ) {
		long oldValue = this.maxGSNLogSizeInMB;
		this.maxGSNLogSizeInMB = newValue;
	}

	public long getMaxGSNLogSizeInMB ( ) {
		return this.maxGSNLogSizeInMB;
	}

	public void setDirectoryLogFileName ( String newValue ) {
		String oldValue = this.directoryLogFileName;
		this.directoryLogFileName = newValue;
	}

	public String getDirectoryLogFileName ( ) {
		return this.directoryLogFileName;
	}

	public void setGsnLogFileName ( String newValue ) {
		String oldValue = this.gsnLogFileName;
		this.gsnLogFileName = newValue;
	}

	public String getGsnLogFileName ( ) {
		return this.gsnLogFileName;
	}



    /*
	static {
		int i = 0;
		NETWORK_ADDRESSES = new String [ ValidityTools.NETWORK_LOCAL_ADDRESS.size( ) ];
		for ( String address : ValidityTools.NETWORK_LOCAL_ADDRESS )
			NETWORK_ADDRESSES[ i++ ] = address + ":" + DEFAULT_DIRECTORY_PORT;
	}
	*/

	private static String extractLoggingLevel ( String property , String [ ] setOfPossibleValues , String defaultValue ) {
		String toReturn = defaultValue;
		if ( property == null ) return toReturn;
		StringTokenizer st = new StringTokenizer( property , "," );
		if ( st == null || st.countTokens( ) == 0 ) return toReturn;
		String inputLogLevel = st.nextToken( );
		if ( inputLogLevel == null )
			return toReturn;
		else
			inputLogLevel = inputLogLevel.toUpperCase( ).trim( );
		for ( String level : setOfPossibleValues )
			if ( level.equals( inputLogLevel ) ) {
				toReturn = level;
				break;
			}
		return toReturn;
	}

//	public static ContainerConfig getConfigurationFromFile ( String containerConfigurationFileName , String gsnLog4jFile , String dirLog4jFile ) throws JiBXException , FileNotFoundException {
//		IBindingFactory bfact = BindingDirectory.getFactory( ContainerConfig.class );
//		IUnmarshallingContext uctx = bfact.createUnmarshallingContext( );
//		ContainerConfig toReturn = ( ContainerConfig ) uctx.unmarshalDocument( new FileInputStream( containerConfigurationFileName ) , null );
//
//		Properties gsnLog4j = new Properties( );
//		try {
//			gsnLog4j.load( new FileInputStream( gsnLog4jFile ) );
//		} catch ( IOException e ) {
//			System.out.println( "Can't read the log4j files, please check the 2nd and 3rd parameters and try again." );
//			e.printStackTrace( );
//			System.exit( 1 );
//		}
//		toReturn.initLog4JProperties( gsnLog4j  );
//		toReturn.setSourceFiles( containerConfigurationFileName , gsnLog4jFile , dirLog4jFile );
//		return toReturn;
//	}

	private void initLog4JProperties ( Properties gsnLog4j  ) {
		this.gsnLog4JProperties = gsnLog4j;
		setGsnLoggingLevel( extractLoggingLevel( gsnLog4j.getProperty( "log4j.rootLogger" ) , CopyOfContainerConfig.LOGGING_LEVELS , DEFAULT_LOGGING_LEVEL ) );
		//setMaxGSNLogSizeInMB( OptionConverter.toFileSize( gsnLog4j.getProperty( "log4j.appender.file.MaxFileSize" ) , ContainerConfig.DEFAULT_GSN_LOG_SIZE ) / ( 1024 * 1024 ) );
	}

	private void setSourceFiles ( String gsnConfigurationFileName , String gsnLog4jFile , String dirLog4jFile ) {
		this.gsnConfigurationFileName = gsnConfigurationFileName;
		this.gsnLog4jFile = gsnLog4jFile;
	}

	public void setdatabaseSystem ( String newValue ) {
		isdatabaseSystemInitialzied = true;
		String oldValue = this.databaseSystem;
		databaseSystem = newValue;
        storage = new StorageConfig();
        storage.setJdbcDriver(convertToDriver( newValue ));
        if ( newValue == JDBC_SYSTEMS[ 0 ] ) {
			storage.setJdbcPassword("");
            storage.setJdbcUsername("sa");
            storage.setJdbcURL(JDBC_URLS[ 0 ]);
		} else if ( newValue == JDBC_SYSTEMS[ 1 ] ) {
			storage.setJdbcPassword("");
            storage.setJdbcUsername("sa");
            storage.setJdbcURL(JDBC_URLS[ 1 ]);
		} else if ( newValue == JDBC_SYSTEMS[ 2 ] ) {
			storage.setJdbcURL(JDBC_URLS[ 2 ]);
		} else if ( newValue == JDBC_SYSTEMS[ 3 ] ) {
			storage.setJdbcURL(JDBC_URLS[ 3 ]);
		}
	}

	public String getdatabaseSystem ( ) {
		if ( isdatabaseSystemInitialzied == false ) {
			isdatabaseSystemInitialzied = true;

			for ( int i = 0 ; i < JDBC_URLS_PREFIX.length ; i++ )
				if ( storage.getJdbcURL().toLowerCase( ).trim( ).startsWith( JDBC_URLS_PREFIX[ i ] ) ) {
					setdatabaseSystem( JDBC_SYSTEMS[ i ] );
					break;
				}
		}
		return this.databaseSystem;
	}

	private String convertToDriver ( String dbSys ) {
		for ( int i = 0 ; i < JDBC_SYSTEMS.length ; i++ )
			if ( JDBC_SYSTEMS[ i ].equals( dbSys ) ) return JDBC_DRIVERS[ i ];
		return "";
	}

	public void writeConfigurations ( ) throws FileNotFoundException , IOException {
		gsnLog4JProperties.put( "log4j.rootLogger" , getGsnLoggingLevel( ) + ",file" );
		gsnLog4JProperties.put( "log4j.appender.file.MaxFileSize" , getMaxGSNLogSizeInMB( ) + "MB" );
		StringTemplateGroup templateGroup = new StringTemplateGroup( "gsn" );
		StringTemplate st = templateGroup.getInstanceOf( "gsn/gui/templates/templateConf" );
		st.setAttribute( "name" , getWebName( ) );
		st.setAttribute( "author" , getWebAuthor( ) );
		st.setAttribute( "description" , getWebDescription( ) );
		st.setAttribute( "email" , getWebEmail( ) );
		st.setAttribute( "db_user" , storage.getJdbcUsername( ) );
		st.setAttribute( "db_password" , storage.getJdbcPassword( ) );
		st.setAttribute( "db_driver" , storage.getJdbcDriver( ) );
		st.setAttribute( "db_url" , storage.getJdbcURL( ) );
		st.setAttribute( "gsn_port" , getContainerPort( ) );

		gsnLog4JProperties.store( new FileOutputStream( gsnLog4jFile ) , "" );
		FileWriter writer = new FileWriter( gsnConfigurationFileName );
		writer.write( st.toString( ) );
		writer.close( );

	}

	public static String extractDirectoryServiceHost ( String rawValue ) {
		return ValidityTools.getHostName( rawValue );
	}

	public static CopyOfContainerConfig getDefaultConfiguration ( ) {
		CopyOfContainerConfig bean = new CopyOfContainerConfig( );
		bean.setContainerPort( CopyOfContainerConfig.DEFAULT_GSN_PORT );
		bean.storage = new StorageConfig();
        bean.storage.setJdbcDriver( CopyOfContainerConfig.JDBC_SYSTEMS[ 0 ] );
		bean.storage.setJdbcPassword( "" );
		bean.storage.setJdbcURL( "sa" );
		bean.storage.setJdbcURL( CopyOfContainerConfig.JDBC_URLS[ 0 ] );
		bean.setWebName( "NoName." );
		bean.setWebAuthor( "Author not specified." );
		bean.setWebEmail( "Email not specified." );
		bean.setDirectoryLogFileName( "gsn-dir.log" );
		bean.setDirectoryLoggingLevel( LOGGING_LEVELS[ 3 ] );
		bean.setGsnLogFileName( "gsn.log" );
		bean.setGsnLoggingLevel( LOGGING_LEVELS[ 3 ] );
		bean.setMaxDirectoryLogSizeInMB( 1 );
		bean.setMaxGSNLogSizeInMB( 10 );
		return bean;
	}

	public int getSSLPort(){
		return sslPort;
	}
	public String getSSLKeyStorePassword(){
		return sslKeyStorePassword == null ? DEFAULT_SSL_KEYSTORE_PWD : sslKeyStorePassword;
	}
	public String getSSLKeyPassword(){
		return sslKeyPassword == null ? DEFAULT_SSL_KEY_PWD : sslKeyPassword;
	}
	
	/**
	 * MSR MAP PART.
	 */
	private ArrayList<KeyValueImp> msrMap ;
	private HashMap<String, String> msrMapCached ;
	public HashMap<String, String> getMsrMap() {
		if (msrMapCached==null) {
			msrMapCached = new HashMap<String, String>();
			if (msrMap==null)
				return msrMapCached;
			for (KeyValueImp kv : msrMap)
				msrMapCached.put(kv.getKey().toLowerCase().trim(), kv.getValue());
		}
		return msrMapCached;
	}

	private String timeFormat = "";

	public String getTimeFormat() {
		return timeFormat;
	}
	
}
