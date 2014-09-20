package au.csiro.gsnlite.storage;

import au.csiro.gsnlite.storage.db.H2StorageManager;
import au.csiro.gsnlite.utils.Logger;


public class StorageManagerFactory {

        
    private static transient Logger logger             = Logger.getInstance();
	private static String TAG = "StorageManagerFactory.class";

    /**
     * @param driver
     * @param username
     * @param password
     * @param databaseURL
     * @param maxDBConnections
     * @return A new instance of {@link gsn.storage.StorageManager} that is described by its parameters, or null
     *         if the driver can't be found.
     */
    public static StorageManager getInstance(String driver, String username, String password, String databaseURL, int maxDBConnections) {
        //
        StorageManager storageManager = null;
        // Select the correct implementation
        if ("org.h2.Driver".equalsIgnoreCase(driver)) {
            storageManager = new H2StorageManager();
        }         
        
		else {
			logger.error (TAG,new StringBuilder().append("The GSN doesn't support the database driver : ").append(driver).toString());
			logger.error (TAG,new StringBuilder().append("Please check the storage elements in the configuration files.").toString());
		}
        // Initialise the storage manager
        if (storageManager != null) {
            storageManager.init(driver, username, password, databaseURL, maxDBConnections);    
        }
        //
        return storageManager;
    }
    
}
