package au.csiro.gsnlite;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.os.AsyncTask;


public class GSNStop extends AsyncTask<Integer, Void, Void>{
  
//  public static void main(String[] args) {
//    stopGSN(Integer.parseInt(args[0]));
//  }
  public static void stopGSN(int gsnControllerPort){

  }

@Override
protected Void doInBackground(Integer... params) {
    try {   	
//      Socket socket = new Socket(InetAddress.getLocalHost().getLocalHost(), gsn.GSNController.GSN_CONTROL_PORT);
    System.out.println("About to Stop GSN");
      Socket socket = new Socket("127.0.0.1", params[0]);
      PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
      writer.println(GSNController.GSN_CONTROL_SHUTDOWN);
      writer.flush();
      System.out.println("[Done]");
      
    }catch (Exception e) {
      System.out.println("[Failed: "+e.getMessage()+ "]");
    }
	return null;
}
}
