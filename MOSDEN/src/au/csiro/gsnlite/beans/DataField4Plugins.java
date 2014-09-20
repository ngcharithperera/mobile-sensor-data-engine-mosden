package au.csiro.gsnlite.beans;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;
import au.csiro.gsnlite.GSNRuntimeException;

public final class DataField4Plugins implements Serializable, Parcelable{
  
   private static final long serialVersionUID = -8841539191525018987L;
   
   private String            description      = "Not Provided";
   
   private String            name;
   
   private byte               dataTypeID       = -1;
   
   private String            type;
   
   private DataField4Plugins ( ) {}
   
   public DataField4Plugins ( final String fieldName , final String type , final String description ) throws GSNRuntimeException {
      this.name = fieldName;
      this.type = type;
      this.dataTypeID = DataTypes.convertTypeNameToGSNTypeID( type );
      this.description = description;
   }

   /*
   * Use this constructor only with types which require precision parameter (char, varchar, blob, binary)
   * */
   public DataField4Plugins ( final String fieldName , final String type, final int precision , final String description ) throws GSNRuntimeException {
      this.name = fieldName;
      this.type = type +"("+precision+")";
      this.dataTypeID = DataTypes.convertTypeNameToGSNTypeID( this.type );
      this.description = description;
   }
   
   public DataField4Plugins ( final String name , final String type ) {
      this.name = name;
      this.type = type;
      this.dataTypeID = DataTypes.convertTypeNameToGSNTypeID( type );
   }
 
   public DataField4Plugins(String colName,byte dataTypeID) {
     this.name=colName;
     this.dataTypeID = dataTypeID;
     this.type = DataTypes.TYPE_NAMES[this.dataTypeID];
  }

  public String getDescription ( ) {
      return this.description;
   }
   transient boolean fieldNameConvertedToLowerCase = false;
   public String getName ( ) {
      if (fieldNameConvertedToLowerCase==false) {
         fieldNameConvertedToLowerCase=true;
         this.name=name.toLowerCase( );
      }
      return this.name;
   }
   
   public boolean equals ( final Object o ) {
      if ( this == o ) return true;
      if ( !( o instanceof DataField4Plugins ) ) return false;
      
      final DataField4Plugins dataField = ( DataField4Plugins ) o;
      if ( this.name != null ? !this.name.equals( dataField.name ) : dataField.name != null ) return false;
      return true;
   }
   
   /**
    * @return Returns the dataTypeID.
    */
   public byte getDataTypeID ( ) {
      if ( this.dataTypeID == -1 ) this.dataTypeID = DataTypes.convertTypeNameToGSNTypeID( this.type );
      return this.dataTypeID;
   }
   
   public int hashCode ( ) {
      return ( this.name != null ? this.name.hashCode( ) : 0 );
   }
   
   public String toString ( ) {
      final StringBuilder result = new StringBuilder( );
      result.append( "[Field-Name:" ).append( this.name ).append( ", Type:" ).append( DataTypes.TYPE_NAMES[ this.getDataTypeID( ) ] ).append( "[" + this.type + "]" ).append( ", Decription:" )
            .append( this.description ).append( "]" );
      return result.toString( );
   }
   
   /**
    * @return Returns the type. This method is just used in the web interface
    * for detection the output of binary fields.
    */
   public String getType ( ) {
      return this.type;
   }

@Override
public int describeContents() {
	// TODO Auto-generated method stub
	return 0;
}

@Override
public void writeToParcel(Parcel dest, int flags) {
	dest.writeString(name);
	dest.writeString(description);
	dest.writeString(type);
	dest.writeByte(dataTypeID);
	
}
public static final Parcelable.Creator<DataField4Plugins> CREATOR = new
Parcelable.Creator<DataField4Plugins>() {
    public DataField4Plugins createFromParcel(Parcel in) {
        return new DataField4Plugins(in);
    }

    public DataField4Plugins[] newArray(int size) {
        return new DataField4Plugins[size];
    }
};
private DataField4Plugins(Parcel in) {
    readFromParcel(in);
}

private void readFromParcel(Parcel in) {
	name=in.readString();
	description=in.readString();
	type=in.readString();
	dataTypeID=in.readByte();
}
}
