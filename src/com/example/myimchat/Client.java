/**
* Created By: Godfrey Oguike Copyright 2014
* 
* this class is the Client class which consists of a socket client
* Which connects to a specified server.
**/

package com.example.myimchat;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Client extends Activity implements OnClickListener{
	

	static int CP; // Connecting to Port: EditText value
	static String CIP; // Connecting to IP: EditText value
	public static String cToIP; 
	public static String cToP;
	//public static DBAdapter db;
	public static Context context;
	public static InetAddress IP;
	public static String[] IP2;
	public static EditText Imessage;
	public static TextView myIp, myPort, Dmessages, ConnectIP, ConnectPORT, displayConnectingTo;
	public static Handler updateConversationHandler;
	
	String timeStamp; // Saves current time and date here after method execution
	//Server server;
	Button send, clear;
	Socket socket;
	Thread toSend, toListen;
	PrintWriter sendMessage;
	AlertDialog.Builder dAlert;
	Message serverMessage;
	Thread clientThread;
	String Emessage = "This user is not be online"; // Error message (user not online);
	InputMethodManager imm;
	
	public static ListView list; 
	ArrayAdapter<String> adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		
		Imessage = (EditText) findViewById(R.id.etMessage); // Message field
		clear = (Button) findViewById(R.id.bClear);
		send = (Button)findViewById(R.id.bSend1);
		myIp = (TextView)findViewById(R.id.tvIP2); // Displays my devices IP
		myPort = (TextView) findViewById(R.id.tvPORT2); // Display my devices PORT
		ConnectIP = (TextView) findViewById(R.id.etIP); // TextBox who to connect to IP
		ConnectPORT = (TextView) findViewById(R.id.etPORT); // TextBox who to connect  to PORT
		list = (ListView) findViewById(R.id.listView1); // ListView for the chat messages
		displayConnectingTo = (TextView) findViewById(R.id.ConnectedTo); // Display friend who chatting to
		
		getExtras(); 
		updateConversationHandler = new Handler();
		context = getApplicationContext();
		getLocalIpAddress();
		
		ConnectIP.setText(cToIP);
		ConnectPORT.setText(cToP);
		myPort.setText(""+Server.SERVERPORT);
		
		displayConnectingTo.setText("Chatting To: "+FriendsList.theFriend); //Shows the user you are connecting to.
		
		dAlert  = new AlertDialog.Builder(Client.this);
		
		clientThread = new Thread(new ClientThread());
		
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
		send.setOnClickListener(this);
		
		// On press button will clear the chat list.
		clear.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Logi.db.deleteAll();
				getStuff();
			}
		});
		getStuff();
	}
	
	// Set the IP and PORT in the text field.
	public void initilize(){
			CIP = ConnectIP.getText().toString(); //Gets IP from ConnectIP text
			CP = Integer.parseInt(ConnectPORT.getText().toString());
		}
	
	// Get the IP address of the device.
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
	                   myIp.setText(inetAddress.getHostAddress().toString());
	                   
	                }
	            }
	        }
	    } 
	    catch (Exception e)
	    {
	       e.printStackTrace();
	    }
	}
	
	///////////////////////////////////////////CLIENT/////////////////////////////////////
	
	@Override
	public void onClick(View view) {
		initilize();
		getDate();
		imm.hideSoftInputFromWindow(Imessage.getWindowToken(), 0);
		if (clientThread.getState() == Thread.State.NEW){
			
			clientThread.start();
			Log.v("Client","CLIENT THREAD STARTED........");
		}
		else{
			Log.v("Client","BUTTON CLICKED.............");
		
		try {
			String str = Logi.parsedUser+" Says:                "+Imessage.getText().toString()+"                 Sent At: "+timeStamp;
			
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
					true);
			out.println(str);
			
			try{
				Logi.db.insertMessage(str, Logi.parsedUser, FriendsList.theFriend);
				
				getStuff();
				Imessage.setText("");
				Log.v("Client","DID INSERT!!!!!");
				}
				catch(Exception e){
					Log.v("Client","DIDNT INSERT......");
				}

		} catch (UnknownHostException e) {
			e.printStackTrace();
			Log.e("CLIENT","SOME ERROR 1 ");
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("CLIENT","SOME ERROR 2 ");
		} 
		catch (Exception e) {
			e.printStackTrace();
			Log.v("CLIENT","SOME ERROR 3 ");
			
        	updateConversationHandler.post(new updateUIThread(Emessage));
			}
		}
	}

	class ClientThread implements Runnable {

		@Override
		public void run() {
			
			try {
				InetAddress serverAddr = InetAddress.getByName(CIP);
				socket = new Socket(serverAddr, CP);
				
				Log.v("CLIENT","CAN CONNECT ");
			} 
			catch (UnknownHostException e1)
			{
				e1.printStackTrace();
				Log.e("CLIENT","CANT CONNECT USING THIS IP...");
				updateConversationHandler.post(new updateUIThread(Emessage));
			}
			catch (IOException e1) 
			{
				e1.printStackTrace();
				Log.e("CLIENT","CANT CONNECT USING THIS PORT....");
			}
		}
	}
	
	// return a cursor object from from the database and use it to populate the list.
	public static void getStuff(){
		
        Cursor c = Logi.db.getAllRows(); // returns a cursor object
        
        String[] fromFieldNames = new String[]{DBAdapter.KEY_MESSAGE};
        int[] toViewIDs = new int[]{R.id.itemMessage};
        
        SimpleCursorAdapter myCursorAdapter = 
				new SimpleCursorAdapter(
						context,		// Context
						R.layout.item_layout,	// Row layout template
						c,					// cursor (set of DB records to map)
						fromFieldNames,			// DB Column names
						toViewIDs);
        
        list.setAdapter(myCursorAdapter);
	}
	
	// get the current date and time.
	public void getDate(){
		timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
	}
	
	// Gets the parsed data from the Connect activity
	public void getExtras(){
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		cToIP = extras.getString("IP");
		cToP = extras.getString("PORT");
	}
	
	public class updateUIThread implements Runnable {
		public String msg;

		public updateUIThread(String str) {
			msg = str;
		}

		@Override
		public void run() {
			
			Toast t = Toast.makeText(Client.this, msg, Toast.LENGTH_LONG);
			t.show();
		}
	}
}
