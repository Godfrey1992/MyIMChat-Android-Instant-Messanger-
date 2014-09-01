/**
* Created By: Godfrey Oguike Copyright 2014
* 
* this class is used to process notification requests
**/

package com.example.myimchat;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.myimchat.Client;
import com.example.myimchat.FriendsList;
import com.example.myimchat.JSONParser;
import com.example.myimchat.R;


public class ConnFromNotification extends Activity{
	public static ProgressDialog pDialog;
	
	//public final static String aIp = "192.168.0.3"; //The IP address for all classes using connection.
	
	public static String thePort;
	public static String theIp;
	
	//public final static String PREFS_NAME = "myPrefsFile";
	private static String url = "http://"+Connect.aIp+"/myIMChat/get_friend.php";
	ArrayList<NameValuePair> params;
	JSONObject json3;
	JSONParser jsonParser3 = new JSONParser();
	
	public static String regString;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connecting);
		
		get();
	}

	public void get(){
		new getFriendDestails().execute();
	}
	
	// Class returns the details of a specific user selected in the friends list.
	// For the FriendsList class.
	class getFriendDestails extends AsyncTask<String, String, String> {
		 
	       
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ConnFromNotification.this);
            pDialog.setMessage("Connecting");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            
        }
        
        @Override
		protected String doInBackground(String... args) {

			try{
            // Building Parameters
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("friend", Server.regX));
            FriendsList.theFriend = Server.regX;
            //Log.v("LOGi","DID BUILD ARRAY!!!!!");
        	}
        	catch(Exception e){
        		//Log.v("LOGi","DIDNT BUILD....");
        	}
            
        	try{
            

             json3 = jsonParser3.makeHttpRequest(url,
                    "POST", params);
             //Log.v("FRINEDS","DID SEND!!!!!");
        	}
        	catch(Exception e){
        		//Log.v("LOGi","DIDNT SEND......");
        	}
 
            // check log cat fro response
        	try{
            Log.d("Connect", json3.toString());
            regString = json3.toString();
            
            convert();
            parseData();
        	}
        	catch(Exception e){
        		Log.v("Tools","DIDNT PRINT......");
        	}
        	
            return null;
        	}
		}
	
	public void convert(){
		regexChecker("\\d{4}",regString);
        regexChecker2("\\d{3}\\.\\d{3}\\.\\d{1,3}\\.\\d{1,3}",regString);
	}
	
	public void regexChecker(String theRegex, String str2Check){
		Pattern checkRegex = Pattern.compile(theRegex);
		Matcher regexMatcher = checkRegex.matcher( str2Check );
		
		while ( regexMatcher.find() ){
			//Log.v("Tools",regexMatcher.group().trim());
			thePort = regexMatcher.group().trim();	
			Log.v("Connect",thePort);
		}
	}
	
	public void regexChecker2(String theRegex, String str2Check){
		Pattern checkRegex = Pattern.compile(theRegex);
		Matcher regexMatcher = checkRegex.matcher( str2Check );
		
		while ( regexMatcher.find() ){
			//Log.v("Tools",regexMatcher.group().trim());
			theIp = regexMatcher.group().trim();
			Log.v("Connect",theIp);
			
		}
	}
	
	public void parseData(){
		Intent intent = new Intent(ConnFromNotification.this, Client.class);
		Bundle extras = new Bundle();
		extras.putString("IP", theIp);
		extras.putString("PORT", thePort);
		intent.putExtras(extras);
		
        startActivity(intent);
        Log.v("Connect","parsed!!!");
        finish();
	}
}
