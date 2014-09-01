/**
* Created By: Godfrey Oguike Copyright 2014
* 
*the server class when instantiated creates a server 
*Which listens to incoming user messages and connection
**/

package com.example.myimchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.os.Handler;
import android.util.Log;

public class Server{
	Thread serverThread;
	ServerSocket serverSocket;
	
	public static String regX; // After regular expression only the sender name is saved
	public static int SERVERPORT;
	public static Handler updateConversationHandler;
	public static int gPort;
	
	public void Server(){
		SERVERPORT = Logi.gPort;
		updateConversationHandler = new Handler();
		serverThread = new Thread(new ServerThread());
		serverThread.start();
	}

	// thread to accept incoming user connections
	class ServerThread implements Runnable {

		@Override
		public void run() {
			Socket socket = null;
			
			try {
				serverSocket = new ServerSocket(SERVERPORT);
				
				Log.v("Server", "SERVER SOCKET IS SET!!!!");
				Log.v("Server", "Socket is set to "+Logi.generatePort);
				Log.v("Server", "Ip is set to "+Logi.generateIp);
			} catch (IOException e) {
				e.printStackTrace();
				Log.v("Server", "SERVER SOCKET NOT SET.....");
			}
			
			while (!Thread.currentThread().isInterrupted()) {

				try {
					socket = serverSocket.accept();
					CommunicationThread commThread = new CommunicationThread(socket);
					new Thread(commThread).start();
					
					Log.v("Server", "client connected......");
					} catch (Exception e) {		
				}
			}
		}
	}

	// thread to read the data
	class CommunicationThread implements Runnable {

		private Socket clientSocket;
		private BufferedReader input;

		public CommunicationThread(Socket clientSocket) {

			this.clientSocket = clientSocket;

			try {

				this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
				Log.v("Server", "getting input.....");
			} catch (IOException e) {
				e.printStackTrace();
				Log.v("Server", "failed to read......");
			}
		}

		@Override
		public void run() {

			while (!Thread.currentThread().isInterrupted()) {
				try {

					String read = input.readLine();
					if(read != null){	
						
						updateConversationHandler.post(new updateUIThread(read));
						Logi.notification(regX, read);
					}
					
				} catch (IOException e) {
					e.printStackTrace();
					Log.v("Server", "client DISconnected...... in CT");
				}
			}
		}
	}

	// Updater thread to do extra stuff like:
	// Regular expressions
	// save incoming messages
	public class updateUIThread implements Runnable {
		public String msg;

		public updateUIThread(String str) {
			msg = str;
			if(msg != null){
			regexChecker("\\w*\\s(Says)",msg);
			regexChecker2("\\w*\\s", regX);
			}
		}

		@Override
		public void run() {
			
				if (regX.equals(FriendsList.theFriend)){
						
				Logi.db.insertMessage(msg, Logi.parsedUser, FriendsList.theFriend);
				Client.getStuff();
				Log.v("Server", "printing to screen.....");
					}
					
				else{
					Logi.db.insertMessage(msg, Logi.parsedUser, regX);
				}		
			}
		}
	
	
	public void regexChecker(String theRegex, String str2Check){
		Pattern checkRegex = Pattern.compile(theRegex);
		Matcher regexMatcher = checkRegex.matcher( str2Check );
		
		while ( regexMatcher.find() ){
			
			regX = regexMatcher.group().trim();	
		}
	}
	
	public void regexChecker2(String theRegex, String str2Check){
		Pattern checkRegex = Pattern.compile(theRegex);
		Matcher regexMatcher = checkRegex.matcher( str2Check );
		
		while ( regexMatcher.find() ){
			
			regX = regexMatcher.group().trim();	
		}
	}
}



