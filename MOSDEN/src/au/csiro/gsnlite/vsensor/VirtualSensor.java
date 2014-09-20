package au.csiro.gsnlite.vsensor;

import java.io.File;
import java.sql.SQLException;

import au.csiro.gsnlite.Main;
import au.csiro.gsnlite.beans.InputStream;
import au.csiro.gsnlite.beans.StreamSource;
import au.csiro.gsnlite.exceptions.VirtualSensorInitializationFailedException;
import au.csiro.gsnlite.utils.Logger;

public class VirtualSensor {

    //private static final transient Logger logger = new Logger(".class");
    
    private static Logger logger = Logger.getInstance();
    private static String TAG = "VirtualSensor.class";

    private static final int GARBAGE_COLLECTOR_INTERVAL = 2;

    private String processingClassName;

    private AbstractVirtualSensor virtualSensor = null;

    private VSensorConfig config = null;

    private long lastModified = -1;

    private int noOfCallsToReturnVS = 0;

    public VirtualSensor(VSensorConfig config) {
        this.config = config;
        this.lastModified = new File(config.getFileName()).lastModified();
    }

    public synchronized AbstractVirtualSensor borrowVS() throws VirtualSensorInitializationFailedException {
        if (virtualSensor == null) {
            try {
                virtualSensor = (AbstractVirtualSensor) Class.forName(config.getProcessingClass()).newInstance();
                virtualSensor.setVirtualSensorConfiguration(config);
            } catch (Exception e) {
                throw new VirtualSensorInitializationFailedException(e.getMessage(), e);
            }
            if (virtualSensor.initialize() == false) {
                virtualSensor = null;
                throw new VirtualSensorInitializationFailedException();
            }
            if (logger.isDebugEnabled())
                logger.debug (TAG,new StringBuilder().append("Created a new instance for VS ").append(config.getName()).toString());
        }
        return virtualSensor;
    }

    /**
     * The method ignores the call if the input is null
     *
     * @param o
     */
    public synchronized void returnVS(AbstractVirtualSensor o) throws Exception {
        if (o == null) return;
        if (++noOfCallsToReturnVS % GARBAGE_COLLECTOR_INTERVAL == 0)
            DoUselessDataRemoval();
    }

    public synchronized void closePool() {
        if (virtualSensor != null) {
            virtualSensor.dispose();
            if (logger.isDebugEnabled())
                logger.debug (TAG,new StringBuilder().append("VS ").append(config.getName()).append(" is now released.").toString());
        } else if (logger.isDebugEnabled())
            logger.debug (TAG,new StringBuilder().append("VS ").append(config.getName()).append(" was already released.").toString());
    }

    public void start() throws VirtualSensorInitializationFailedException {
        for (InputStream inputStream : config.getInputStreams()) {
            for (StreamSource streamSource : inputStream.getSources()) {
                streamSource.getWrapper().start();
            }
        }
        borrowVS();
    }

    /**
     * @return the config
     */
    public VSensorConfig getConfig() {
        return config;
    }

    /**
     * @return the lastModified
     */
    public long getLastModified() {
        return lastModified;
    }

    public void dispose() {
    }

    // apply the storage size parameter to the virtual sensor table
    public void DoUselessDataRemoval() {
        if (config.getParsedStorageSize() == VSensorConfig.STORAGE_SIZE_NOT_SET) return;
        StringBuilder query;

        if (config.isStorageCountBased()) {
            query = Main.getStorage(config.getName()).getStatementRemoveUselessDataCountBased(config.getName(), config.getParsedStorageSize());
        }
        else {
            query = Main.getStorage(config.getName()).getStatementRemoveUselessDataTimeBased(config.getName(), config.getParsedStorageSize());
        }

        int effected = 0;
        try {
            if (logger.isDebugEnabled())
                logger.debug (TAG,new StringBuilder().append("Enforcing the limit size on the VS table by : ").append(query).toString());
            effected = Main.getStorage(config.getName()).executeUpdate(query);
        } catch (SQLException e) {
            logger.error (TAG,"Error in executing: " + query);
            logger.error (TAG,e.getMessage());
        }
        catch (Exception e){
        	logger.error (TAG,e.getMessage());
        }
        if (logger.isDebugEnabled())
            logger.debug (TAG,new StringBuilder().append(effected).append(" old rows dropped from ").append(config.getName()).toString());
    }
}

