package au.csiro.gsnlite.exceptions;

public class OperationNotSupportedException extends Exception{
	   public OperationNotSupportedException ( ) {
		      super( );
		   }
		   
		   public OperationNotSupportedException ( String message ) {
		      super( message );
		   }
		   
		   public OperationNotSupportedException ( String message , Throwable cause ) {
		      super( message , cause );
		   }
		   
		   public OperationNotSupportedException ( Throwable cause ) {
		      super( cause );
		   }
}
