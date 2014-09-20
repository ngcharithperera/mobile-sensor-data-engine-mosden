package au.csiro.gsnlite.storage;



import java.util.Enumeration;

import au.csiro.gsnlite.beans.StreamElement;

public interface DataEnumeratorIF extends Enumeration<StreamElement> {

    public boolean hasMoreElements() ;

    public StreamElement nextElement() throws RuntimeException ;

    public void close() ;

}
