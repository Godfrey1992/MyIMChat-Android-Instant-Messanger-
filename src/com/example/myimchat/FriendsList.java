/**
* Created By: Godfrey Oguike Copyright 2014
* 
*the friends list gets the list of friends for a particular user from the MySQL database 
*and populates a list with the returned data.
**/

package com.example.myimchat;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class FriendsList extends Activity{
	

	ListView Flist;
	Button addF, removeF;
	TextView LoggedInUserName;
	List<String> list = new ArrayList<String>();
	String [] friends;
	String userName = Logi.parsedUser; //used to get the friend list for the specific user;
	List<NameValuePair> params;
	JSONObject json2;
	JSONParser jsonParser2 = new JSONParser();
	
	public static String theFriend;
	private static String url = "http://"+Connect.aIp+"/myIMChat/friends_list.php";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends_list);
		
		Flist = (ListView) findViewById(R.id.Flist);
		addF = (Button) findViewById(R.id.bAddFriend);
		removeF = (Button) findViewById(R.id.bRemoveFriend);
		LoggedInUserName = (TextView) findViewById(R.id.tvUserName);
		
		LoggedInUserName.setText("User Logged in:    "+userName);
		
		removeF.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), RemoveFriend.class);
                startActivity(i);
			}
		});
		
		addF.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), AddFriend.class);
                startActivity(i);
				
			}
		});
		new getFriends().execute();
	}

	// get the friends list
	class getFriends extends AsyncTask<String, String, String> {

	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	        }
	        
	        @Override
			protected void onPostExecute(String result)
	        {
	        	display();
	        }
	        
	        @Override
			protected String doInBackground(String... args) {
	        	try{
	            // Building Parameters
	            params = new ArrayList<NameValuePair>();
	            params.add(new BasicNameValuePair("username", userName));
	            
	            Log.v("LOGi","DID BUILD ARRAY!!!!!");
	        	}
	        	catch(Exception e){
	        		Log.v("LOGi","DIDNT BUILD....");
	        	}
	            
	        	try{
	           
	             json2 = jsonParser2.makeHttpRequest(url,
	                    "POST", params);
	             Log.v("FRINEDS","DID SEND!!!!!");
	        	}
	        	catch(Exception e){
	        		Log.v("LOGi","DIDNT SEND......");
	        	}
	 
	            // check log cat fro response
	        	try{
	            Log.d("FRIENDS_LIST", json2.toString());
	            Log.v("FRINEDS","DID PRINT!!!!!!");
	        	}
	        	catch(Exception e){
	        		Log.v("FRINEDS","DIDNT PRINT......");
	        	}
	        	
	        	try {
	        		
	        		Log.v("FRIENDS_LIST",JSONParser.json);

	        		   JSONArray jArray = new JSONArray(JSONParser.json);
	        		   
	        		   for(int i=0; i<jArray.length();i++){
	        			   JSONObject json = jArray.getJSONObject(i);
	        			   list.add(json.getString("friend_with"));
	        		   }
	       
	        	   } catch (Exception e) {
	        		// TODO: handle exception
	        		   Log.e("log_tag", "Error Parsing Data "+e.toString());
	        	   }
	            return null;
	        	}
			}
	
		// display friends in the list view 
		public void display(){

			friends = list.toArray(new String[list.size()]);
			ArrayAdapter arrayAdapter = new ArrayAdapter(this,R.layout.friends_list_item1, R.id.textView1123, friends);
	        Flist.setAdapter(arrayAdapter);
	        
	        Flist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View viewClicked, int postion, long id) {
					theFriend = list.get(postion);

					Intent i = new Intent(FriendsList.this, Connect.class);
	                startActivity(i);
				}
			});
		}
	}

