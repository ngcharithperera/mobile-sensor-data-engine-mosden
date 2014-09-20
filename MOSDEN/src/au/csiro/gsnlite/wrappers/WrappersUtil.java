package au.csiro.gsnlite.wrappers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import android.os.Environment;
import au.csiro.gsnlite.utils.Logger;
public class WrappersUtil {
  
  //public static transient Logger logger= new Logger( ".class ");
  
  private static Logger logger = Logger.getInstance();
  private static String TAG = "WrappersUtil.class";
  
  public static final String     DEFAULT_WRAPPER_PROPERTIES_FILE  = "conf/wrappers.properties";
  public static Properties loadWrappers(HashMap<String, Class<?>> wrappers, String location) {
    
	File sdcard = Environment.getExternalStorageDirectory();
	location = sdcard.getAbsolutePath() + "/" + location;
	
	Properties config = new Properties ();
    try {// Trying to load the wrapper specified in the configuration file of the container. 
      config.load(new FileReader( location ));
    } catch ( IOException e ) {
      logger.error (TAG, "The wrappers configuration file's syntax is not compatible." );
      logger.error (TAG, new StringBuilder ( ).append ( "Check the :" ).append ( location ).append ( " file and make sure it's syntactically correct." ).toString ( ) );
      logger.error (TAG, "Sample wrappers extention properties file is provided in GSN distribution." );
      logger.error (TAG, e.getMessage ( ) , e );
      System.exit ( 1 );
    }  
   // TODO: Checking for duplicates in the wrappers file.
    return config;
  }  
  public static Properties loadWrappers(HashMap<String, Class<?>> wrappers){
    return loadWrappers(wrappers,DEFAULT_WRAPPER_PROPERTIES_FILE);
  }
}
