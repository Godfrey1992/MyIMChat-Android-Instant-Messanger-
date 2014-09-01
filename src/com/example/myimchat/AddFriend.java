/**
* Created By: Godfrey Oguike Copyright 2014
* 
* The class below gets user parameters and posts the parameters to 
* the add_friend.php script.
* it can also process returned data
**/

package com.example.myimchat;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddFriend extends Activity{
	
	EditText friendName;
	Button addFriendButton;
	String friendToAdd, errorMessage;
	JSONParser jsonParser = new JSONParser();
	
	public static ProgressDialog pDialog;
	public static Handler updateConversationHandler;
	private static final String TAG_SUCCESS = "success";
	private static String url = "http://"+Connect.aIp+"/myIMChat/add_friend.php";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_friend);
		
		friendName = (EditText)findViewById(R.id.etFriendName);
		addFriendButton = (Button)findViewById(R.id.bAddFriend);
		
		updateConversationHandler = new Handler(); // Create instance of Handler for UI updates.
		
		// Listener for the add friends button.
		addFriendButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new AddFriends().execute();
			}
		});
	}
	
	class AddFriends extends AsyncTask<String, String, String> {
		 
        // Display a dialog to inform the user their request id being processed.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddFriend.this);
            pDialog.setMessage("Adding Friend..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
       
        @Override
		protected String doInBackground(String... args) {
        	friendToAdd = friendName.getText().toString(); // Convert text box entry to string and save.
        	
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("the_friend", friendToAdd));
            params.add(new BasicNameValuePair("the_user", Logi.parsedUser));
            
            // parse the URL to the JSON object
            //(for the PHP script), dispatch method(POST or GET), and the parameters(the values to be parsed).
            JSONObject json = jsonParser.makeHttpRequest(url,
                    "POST", params);
 
            // check log cat from response(FOR DEBUGGIGN)
            Log.d("Add Friend", json.toString());
            
            // Check the returned success tag.
            try {
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // user has successfully been added to friend list
                    Intent i = new Intent(AddFriend.this, FriendsList.class);
                    startActivity(i); //switch back to FriendsList activity
                    
                 // Send error message to be displayed as toast (user has been added as a friend).
                    String sMessage = "Friend has successfully added";
                	updateConversationHandler.post(new updateUIThread(sMessage));
                    
                    finish(); // closing this screen
                    
                } else if(success == 2){
                	// Send error message to be displayed as toast (failed to add this user as a friend).
                	String eMessage2 = "This user is already a friend";
                	updateConversationHandler.post(new updateUIThread(eMessage2));
                	
                }else {
                	// Send error message to be displayed as toast (failed to add this user as a friend).
                	String eMessage = "This user is not registered";
                	updateConversationHandler.post(new updateUIThread(eMessage));
                	
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        } 
        @Override
		protected void onPostExecute(String file_url) {
            // Dismiss the dialog once done
            pDialog.dismiss();
        }
	}
	
	// Thread Used to update the UI. to display error messages eg. Toast, Dialogs.
	public class updateUIThread implements Runnable {
		public String msg;

		public updateUIThread(String str) {
			msg = str;
		}

		@Override
		public void run() {
			Toast t = Toast.makeText(AddFriend.this, msg, Toast.LENGTH_LONG);
			t.show();
		}
	}
}
