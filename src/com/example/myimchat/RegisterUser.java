/**
* Created By: Godfrey Oguike Copyright 2014
* 
*this class is used to register a new user 
*adding an extra row to the database with all the user details
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
import android.app.ProgressDialog;
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
import android.widget.Toast;

public class RegisterUser extends Activity{

	public static ProgressDialog pDialog;
	
    JSONParser jsonParser = new JSONParser();
	EditText userName, password, email;
	Button signUp;
	String cUserName, cPassword, cEmail, ip;
	String port;
	CheckBox showPass;
	
	private static String url = "http://"+Connect.aIp+"/myIMChat/create_user.php";
	private static final String TAG_SUCCESS = "success";
	public static Handler updateConversationHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regiter_user);
		
		userName = (EditText) findViewById(R.id.etuserName);
		password = (EditText) findViewById(R.id.etPassword);
		email = (EditText) findViewById(R.id.etEmail);
		signUp = (Button) findViewById(R.id.bSignUp);
		showPass = (CheckBox) findViewById(R.id.showPasswordR);
		
		updateConversationHandler = new Handler();
		
		getLocalIpAddress();
		generatePort();
		
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
		
		signUp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder dAlert  = new AlertDialog.Builder(RegisterUser.this);
				if (userName.length() > 0 && password.length() > 0 && email.length() > 0){
					new RegisterUsers().execute();
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
	}
	
	// register the user (add to the DB)
	class RegisterUsers extends AsyncTask<String, String, String> {
		 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RegisterUser.this);
            pDialog.setMessage("Creating user..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            
        }
       
        @Override
		protected String doInBackground(String... args) {
        	cUserName = userName.getText().toString();
        	cPassword = password.getText().toString();
        	cEmail = email.getText().toString();
 
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user_name", cUserName));
            params.add(new BasicNameValuePair("password", cPassword));
            params.add(new BasicNameValuePair("email", cEmail));
            params.add(new BasicNameValuePair("ip", ip));
            params.add(new BasicNameValuePair("port", port));
            
            JSONObject json = jsonParser.makeHttpRequest(url,
                    "POST", params);
 
            // check log cat fro response
            Log.d("RegisterUser", json.toString());
           
            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // successfully created product
                    Intent i = new Intent(RegisterUser.this, Logi.class);
                    startActivity(i);

                    String Smessage = "User successfully created";
                    updateConversationHandler.post(new updateUIThread(Smessage));
 
                    // closing this screen
                    finish();
                } else {
                    // failed to register user.
                	String Emessage = "This user name already exists";
                	updateConversationHandler.post(new updateUIThread(Emessage));
                	
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
		protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }
    }
	
	// get device IP address
	public void getLocalIpAddress()
	{
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
	                   ip = inetAddress.getHostAddress().toString();
	                   
	                }
	            }
	        }
	    } 
	    catch (Exception e)
	    {
	       e.printStackTrace();
	    }
	}
	
	// generate an port number
	public void generatePort(){
		int x = (int)(Math.random() * 9999)+1000;
		port = x+"";
	}
	
	// UI thread to show error messages
	public class updateUIThread implements Runnable {
		public String msg;

		public updateUIThread(String str) {
			msg = str;
		}

		@Override
		public void run() {
			//pDialog.dismiss();
			Toast t = Toast.makeText(RegisterUser.this, msg, Toast.LENGTH_LONG);
			t.show();
			//Log.v("Server", "printing to screen.....");
			
		}
	}
}
