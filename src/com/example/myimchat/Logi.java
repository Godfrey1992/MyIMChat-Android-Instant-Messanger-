/**
* Created By: Godfrey Oguike Copyright 2014
* 
* The class below validates user details in the database 
* compared to what they have entered. if user details are correct
* login is successful if not users can register or try again
**/

package com.example.myimchat;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Logi extends Activity{
		
    JSONParser jsonParser = new JSONParser();
    String errorMessage;
	TextView eMessage;
	EditText userName, password, email;
	Button logIn, bReg, testClient;
	String cUserName, cPassword, cEmail, ip;
	String port;
	CheckBox showPass;
	List<NameValuePair> params;
	JSONObject json;
	AlertDialog.Builder dAlert;
	
	public static DBAdapter db;
	public static int gPort;
	public static String generatePort;
	public static String generateIp;
	public static Handler updateConversationHandler;
	private static String url = "http://"+Connect.aIp+"/myIMChat/user_login.php";
	private static final String TAG_SUCCESS = "success";
	public static String parsedUser;
	public static Context context;
	public static ProgressDialog pDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logi);
		
		// initialize variable for interface values
		userName = (EditText) findViewById(R.id.EnterUserName);
		password = (EditText) findViewById(R.id.EnterPassword);
		logIn = (Button) findViewById(R.id.ButtonSend);
		bReg = (Button) findViewById(R.id.ButtonRegister);
		showPass = (CheckBox) findViewById(R.id.showPassword);
		context = getApplicationContext();
		
		// Listener for checkBox to show password
		showPass.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(showPass.isChecked()){
					password.setInputType(InputType.TYPE_CLASS_TEXT);
				}else{
					password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				}
				
			}
		});
		
		updateConversationHandler = new Handler();
		genIpPort(); // generate a new port number
		
		bReg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Logi.this, RegisterUser.class);
                startActivity(i);
			}
		});

		logIn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// make sure all fields are filled out 
				dAlert  = new AlertDialog.Builder(Logi.this);
				if (userName.length() > 0 && password.length() > 0 ){
					
						new Login().execute();
				}
				else{
					dAlert.setMessage("Please fill out all field");
					dAlert.setTitle("Error Message");
					dAlert.setPositiveButton("OK", null);
					dAlert.setCancelable(true);
					dAlert.create().show();
				}
			}
		});
		
		startS(); // start the server
		db = new DBAdapter(this);
		db.open(); // open the SQLite databse
	}
	
	class Login extends AsyncTask<String, String, String> {
		
		// declare progress dialog
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Logi.this);
            pDialog.setMessage("Loging In");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();    
        }
        
        
        @Override
		protected String doInBackground(String... args) {
        	
        	// try to get user input from interface fields
        	try{
        	cUserName = userName.getText().toString();
        	cPassword = password.getText().toString();
        	parsedUser = cUserName;  //The user name will be parsed to next activity
        	Log.v("LOGi","DID CONVERT!!!!!"); // debugging
        	}
        	catch(Exception e){
        		Log.v("LOGi","DIDNT CONVERT...."); // debugging
        	}
        	
        	try{
            // Building Parameters
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", cUserName));
            params.add(new BasicNameValuePair("password", cPassword));
            params.add(new BasicNameValuePair("ip", generateIp));
            params.add(new BasicNameValuePair("port", generatePort));
            Log.v("LOGi","DID BUILD ARRAY!!!!!"); // debugging
        	}
        	catch(Exception e){
        		Log.v("LOGi","DIDNT BUILD...."); // debugging
        	}
            
        	// invoke the makehttpResquest method in the jsonPaser class
        	try{
             json = jsonParser.makeHttpRequest(url,
                    "POST", params);
             Log.v("LOGi","DID SEND!!!!!"); // debugging
             
        	}
        	catch(Exception e){
        		Log.v("LOGi","DIDNT SEND......"); // debugging
        	}
        	// try convert JSON object to string
        	try{	
	            Log.d("Logi", json.toString());
	            errorMessage = json.toString(); 
	            Log.v("LOGi","DID PRINT!!!!!!"); // debugging
        	}
	        	catch(Exception e){
	        		Log.v("LOGi","DIDNT PRINT......"); // debugging
        	}
        	// if user has successfully 
            try {
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {

                	// go to the next activity
                    Intent i = new Intent(Logi.this, FriendsList.class);
                    startActivity(i);
                    
                } else { 
                	// invoke error message to be displayed
                	updateConversationHandler.post(new updateUIThread(errorMessage));
                }
            } catch (JSONException e) {
            	Log.v("LOGi","DIDNT WORK....."); // debugging
                e.printStackTrace();
            }
            return null;
        }
 
        @Override
		protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }
    }
	
	// get the IP address of the device
	public void genIpPort(){
		try 
	    {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
	        {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
	            {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) 
	                {
	                   generateIp = inetAddress.getHostAddress().toString(); // save to variable
	                   //generateIp = "192.168.1.69";
	                   gPort = (int)(Math.random() * 9999); // generate port number
	           		   generatePort = ""+gPort; // Convert to string
	           		   Log.v("LOGi","Ip and Port generated");	           		 
	                }
	            }
	        }
	    } 
	    catch (Exception e)
	    {
	       e.printStackTrace();
	    }
	}
	
	// Method updates UI displaying error messages
	public class updateUIThread implements Runnable {
		public String msg;

		// message is error message is parsed as a parameter
		public updateUIThread(String str) {
			msg = str;
		}

		@Override
		public void run() {
			// display error message in the form of a toast
			Toast t = Toast.makeText(Logi.this, "User name or password incorrect", Toast.LENGTH_LONG);
			t.show();
		}
	}
	
	// create a new instance of the server class and start the server
	public void startS(){
		// try start the server
		try{
		Server server = new Server();
		server.Server();
		Log.v("Login","SERVER DID START...."); // debugging
		}
		catch(Exception e){
			Log.v("Login","Server didnt start =("); // debugging
		}
	}
	
	// create a notification manager to display notifications when a message is received
	public static void notification(String from, String message){
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher, "New Message", System.currentTimeMillis());
		Intent i = new Intent(context, ConnFromNotification.class); // call activity on press
		notification.flags |= Notification.FLAG_AUTO_CANCEL; // cancel notification on click
		notification.defaults |= Notification.DEFAULT_ALL; // set notification alert to vibrate and sound
		
		PendingIntent activity = PendingIntent.getActivity(context, 0, i, 0);
		
		notification.setLatestEventInfo(context, "New Message From: "+from, message, activity);
		
		nm.notify(9, notification);
	}
}
