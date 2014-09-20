package au.csiro.gsnlite.http.rest;



import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.io.WriterOutputStream;

import au.csiro.gsnlite.beans.DataField;
import au.csiro.gsnlite.beans.StreamElement;
import au.csiro.gsnlite.utils.Logger;

import com.thoughtworks.xstream.XStream;

public class RestDelivery implements DeliverySystem {

    private Continuation continuation;
    private ObjectOutputStream objectStream;

    private static final StreamElement keepAliveMsg = new StreamElement(new DataField[]{new DataField("keepalive", "string")}, new Serializable[]{"keep-alive message"}, Long.MIN_VALUE);

    public RestDelivery(Continuation connection) throws IOException {
        this.continuation = connection;
        XStream dataStream = StreamElement4Rest.getXstream();
        objectStream = dataStream.createObjectOutputStream((new WriterOutputStream(continuation.getServletResponse().getWriter())));
    }

    private String TAG = "RestDelivery.class";
    private static transient Logger logger = Logger.getInstance();

    public void writeStructure(DataField[] fields) throws IOException {
        objectStream.writeObject(fields);
        objectStream.flush();
        continuation.getServletResponse().flushBuffer();
    }

    public synchronized boolean writeStreamElement(StreamElement se) {
        try {
            objectStream.writeObject(new StreamElement4Rest(se));
            objectStream.flush();
            continuation.resume();
            return ((LinkedBlockingQueue<Boolean>) continuation.getAttribute("status")).take();
        } catch (Exception e) {
            logger.debug(TAG, e.getMessage(), e);
            return false;
        }
    }

    public boolean writeKeepAliveStreamElement() {
        logger.debug(TAG,"Sending the keepalive message.");
        return writeStreamElement(keepAliveMsg);
    }

    public void close() {
        try {
            if (objectStream != null){
                objectStream.close();
                continuation.complete();
            }
        } catch (Exception e) {
            logger.debug(TAG,e.getMessage(), e);
        }
    }

    public boolean isClosed() {
        try {
            return continuation.getServletResponse().getWriter().checkError();
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }

    }
}
