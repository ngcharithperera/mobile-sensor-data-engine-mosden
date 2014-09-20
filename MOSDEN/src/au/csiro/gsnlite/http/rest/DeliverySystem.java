package au.csiro.gsnlite.http.rest;



import java.io.IOException;

import au.csiro.gsnlite.beans.DataField;
import au.csiro.gsnlite.beans.StreamElement;

public interface DeliverySystem {

	public abstract void writeStructure(DataField[] fields) throws IOException;

	public abstract boolean writeStreamElement(StreamElement se);

    public abstract boolean writeKeepAliveStreamElement();

	public abstract void close();

	public abstract boolean isClosed();

}