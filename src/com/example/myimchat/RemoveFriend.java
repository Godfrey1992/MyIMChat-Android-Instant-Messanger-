/**
* Created By: Godfrey Oguike Copyright 2014
* 
* the remove friends class makes a call to the remove_frined.php script
* which will delete the friend from the users friends list
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

public class RemoveFriend extends Activity{

	Button rFriend;
	EditText fName;
	JSONParser jsonParser = new JSONParser();
	
	public static ProgressDialog pDialog;
	public static Handler updateConversationHandler;
	private static final String TAG_SUCCESS = "success";
	private static String url = "http://"+Connect.aIp+"/myIMChat/remove_Friend.php";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remove_friend);
		
		rFriend = (Button) findViewById(R.id.bRemoveFriend);
		fName = (EditText) findViewById(R.id.etFriendName1);
		
		updateConversationHandler = new Handler();
		
		rFriend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new RemoveFriends().execute();
				
			}
		});
	}

	// send friends details for them to be removed
	class RemoveFriends extends AsyncTask<String, String, String> {
		 
        // Display a dialog to inform the user their request is being processed.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RemoveFriend.this);
            pDialog.setMessage("Deleting Friend..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
       
        @Override
		protected String doInBackground(String... args) {
        	String friendToRemove = fName.getText().toString(); // Convert text box entry to string and save.
        	
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("the_friend", friendToRemove));
            params.add(new BasicNameValuePair("the_user", Logi.parsedUser));
            
            // parse the URL to the JSON object
            //(for the PHP script), dispatch method(POST or GET), and the parameters(the values to be parsed).
            JSONObject json = jsonParser.makeHttpRequest(url,
                    "POST", params);
 
            // check log cat from response(FOR DEBUGGIGN)
            Log.d("Remove Friend", json.toString());
            
            // Check the returned success tag.
            try {
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // user has successfully been added to friend list
                    Intent i = new Intent(RemoveFriend.this, FriendsList.class);
                    startActivity(i); //switch back to FriendsList activity
                    
                 // Send error message to be displayed as toast (user has been deleted).
                    String sMessage = "Friend has successfully been deleted";
                	updateConversationHandler.post(new updateUIThread(sMessage));
                    
                    finish(); // closing this screen
                } else if(success == 2){
                	// Send error message to be displayed as toast (failed to add this user as a friend).
                	String eMessage2 = "This user is not in your friends list";
                	updateConversationHandler.post(new updateUIThread(eMessage2));
                	
                }else {
                	// Send error message to be displayed as toast.
                	String eMessage = "This user does not exist";
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
			Toast t = Toast.makeText(RemoveFriend.this, msg, Toast.LENGTH_LONG);
			t.show();
		}
	}
}
