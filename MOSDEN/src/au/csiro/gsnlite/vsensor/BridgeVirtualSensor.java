package au.csiro.gsnlite.vsensor;

import java.util.TreeMap;

import au.csiro.gsnlite.beans.StreamElement;
import au.csiro.gsnlite.utils.Logger;

public class BridgeVirtualSensor extends AbstractVirtualSensor {

    
    private static final transient Logger logger           = Logger.getInstance();
	private String TAG = "BridgeVirtualSensor.class";
    
    private boolean allow_nulls = true; // by default allow nulls

    public boolean initialize() {
        VSensorConfig vsensor = getVirtualSensorConfiguration();
        TreeMap<String, String> params = vsensor.getMainClassInitialParams();

        String allow_nulls_str = params.get("allow-nulls");
        if (allow_nulls_str != null)
            allow_nulls = allow_nulls_str.equalsIgnoreCase("true");

        return true;
    }

    public void dataAvailable(String inputStreamName, StreamElement data) {
    	System.out.println("MobileServer,"+this.getVirtualSensorConfiguration().getName()+","+ System.currentTimeMillis());

    	if (allow_nulls)
            dataProduced(data);
        else {
            if (!areAllFieldsNull(data))
                dataProduced(data);
            else {
                if (logger.isDebugEnabled())
                    logger.debug (TAG,"Nulls received for timestamp (" + data.getTimeStamp() + "), discarded");
            }
        }
        if (logger.isDebugEnabled()) logger.debug (TAG,"Data received under the name: " + inputStreamName);
    }

    public boolean areAllFieldsNull(StreamElement data) {
        boolean allFieldsNull = false;
        for (int i = 0; i < data.getData().length; i++)
            if (data.getData()[i] == null) {
                allFieldsNull = true;
                break;
            }

        return allFieldsNull;
    }

    public void dispose() {

    }

}
