package au.csiro.gsnlite.http.rest;
 
 
 
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
 
import au.csiro.gsnlite.beans.DataField;
import au.csiro.gsnlite.beans.StreamElement;
 
import com.thoughtworks.xstream.XStream;
 
public class StreamElement4Rest {

	private List<Field4Rest> fields = new ArrayList<Field4Rest>();

	public StreamElement4Rest() {}
	
	public StreamElement4Rest(StreamElement se) {
        System.out.println("Working....");
		// this.timestamp=new Date(se.getTimeStamp()).toString();

		// prem
		this.timestamp = se.getTimeStamp();

		for (int i = 0; i < se.getFieldNames().length; i++) {
			fields.add(new Field4Rest(se.getFieldNames()[i],
					se.getFieldTypes()[i], se.getData()[i]));
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("StreamElement4Rest: (timestamp:");
		// sb.append(timestamp.toString());
		sb.append(timestamp);
		sb.append("Fields =>{ ");
		for (Field4Rest field : fields)
			sb.append(field.toString()).append(", ");
		sb.append("})");
		return sb.toString();
	}

	// private Date timestamp;

	// charith
	// private String timestamp;

	// prem modifications
	private long timestamp;

	public StreamElement toStreamElement() {
		String[] names = new String[fields.size()];
		Serializable[] values = new Serializable[fields.size()];
		Byte[] types = new Byte[fields.size()];
		int idx = 0;
		for (Field4Rest field : fields) {
			names[idx] = field.getName();
			values[idx] = field.getValue();
			types[idx] = field.getType();
			idx++;
		}

		// return new StreamElement(names,types,values, getTimestamp());
		return new StreamElement(names, types, values, timestamp);

	}

	public static XStream getXstream() {
		XStream xstream = new XStream();
		xstream.alias("stream-element", StreamElement4Rest.class);
		xstream.alias("field", Field4Rest.class);
		// charith
		xstream.useAttributeFor(StreamElement4Rest.class, "timestamp");
		xstream.addImplicitCollection(StreamElement4Rest.class, "fields");
		xstream.registerConverter(new Field4RestConverter());
		xstream.alias("strcture", DataField.class);

		return xstream;
	}

	public static XStream getXstream4Structure() {
		XStream xstream = new XStream();
		 xstream.alias("stream-element", StreamElement4Rest.class);
		 xstream.alias("field", Field4Rest.class);
		 xstream.useAttributeFor(StreamElement4Rest.class,"timestamp");
		 xstream.addImplicitCollection(StreamElement4Rest.class, "fields");
		 xstream.registerConverter(new Field4RestConverter());
		return xstream;
	}

	// public long getTimestamp() {
	// //charith
	// //System.out.println("do");
	// //System.out.println("do");
	//
	// Date date;
	// try {
	// // String stringdate = timeStamp;
	//
	//
	// date = new
	// SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z").parse(timestamp);
	// } catch (Exception e) {
	// date = new Date();
	//
	// //e.printStackTrace();
	// }
	// return date.getTime();
	//
	// //
	// //return timestamp;
	// }

	public long getTimestamp() {
		return timestamp;
	}
}