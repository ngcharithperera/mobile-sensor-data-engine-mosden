package au.csiro.gsnlite.windowing;

import au.csiro.gsnlite.beans.StreamElement;
import au.csiro.gsnlite.beans.StreamSource;



public interface SlidingHandler {
	
	public void addStreamSource(StreamSource streamSource);
	
	public void removeStreamSource(StreamSource streamSource);

	public boolean dataAvailable(StreamElement streamElement);

	public boolean isInterestedIn(StreamSource streamSource);

	public long getOldestTimestamp();
	
	public void dispose();

}
