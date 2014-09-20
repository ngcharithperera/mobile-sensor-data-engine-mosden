package au.csiro.gsnlite;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import au.csiro.gsnlite.utils.Logger;

public class GSNLiteMainActivity extends Activity {

		
	private static Button startButton;
	private static Button stopButton;
	private static TextView gsnLog;
	private static EditText gsnport;
	private static Logger logger;
	
	private static Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gsnmain);
		
		handler = new Handler() {
			  public void handleMessage(Message msg) {
				  TextView myTextView = 
	                      (TextView)findViewById(R.id.gsnlog);
				  myTextView.append(msg.getData().getString("message") + "\n");
			     }
			 };
		
		 au.csiro.gsnlite.utils.ApplicationObject.setGsnLiteMainActivity(this);
		 startButton = (Button)findViewById(R.id.startGSNButton);
	     stopButton = (Button)findViewById(R.id.stopGSNButton);
	     gsnLog = (TextView)findViewById(R.id.gsnlog);
	     gsnport = (EditText)findViewById(R.id.editTextPort);
	     
		logger = Logger.getInstance();
		logger.setUilistener(handler);
	     
	     startButton.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Thread mainThread = new Thread(){
						public void run(){
							Main.getInstance(gsnport.getText().toString());
						}
						
					};
					mainThread.start();
				}
	     });
	     
	     stopButton.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stu
					gsnport.append("/n Stopping GSN Android");
					new GSNStop().execute(Integer.parseInt(gsnport.getText().toString()));
				}
	     });
	}
	
	
}
