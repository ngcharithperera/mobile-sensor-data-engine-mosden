package au.csiro.gsnlite.vsensor;



public interface VSensorStateChangeListener {
	public boolean vsLoading(VSensorConfig config);
	
	public boolean vsUnLoading(VSensorConfig config);
	
	public void release() throws Exception;
}
