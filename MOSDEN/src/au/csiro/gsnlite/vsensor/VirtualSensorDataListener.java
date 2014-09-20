package au.csiro.gsnlite.vsensor;

import au.csiro.gsnlite.beans.StreamElement;

public interface VirtualSensorDataListener {
	public void consume(StreamElement se,VSensorConfig config);
}
