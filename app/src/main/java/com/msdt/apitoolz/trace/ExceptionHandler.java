/*
Copyright (c) 2009 nullwire aps

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

Contributors: 
Mads Kristiansen, mads.kristiansen@nullwire.com
Glen Humphrey
Evan Charlton
Peter Hewitt
*/

package com.msdt.apitoolz.trace;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.msdt.apitoolz.clients.NetworkEngine;
import com.msdt.apitoolz.models.ErrorTrace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExceptionHandler {
	
	public static String TAG = "ExceptionsHandler";
	
	private static String[] stackTraceFileList = null;
	
	/**
	 * Register handler for unhandled exceptions.
	 * @param context
	 */
	public static boolean register(Context context) {
		// Get information about the Package
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo pi;
			// Version
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			G.APP_VERSION = pi.versionName;
			// Package name
			G.APP_PACKAGE = pi.packageName;
			// Files dir for storing the stack traces
			G.FILES_PATH = context.getFilesDir().getAbsolutePath();
			// Device model
            G.PHONE_MODEL = android.os.Build.MODEL;
            // Android version
            G.ANDROID_VERSION = android.os.Build.VERSION.RELEASE;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		boolean stackTracesFound = false;
		// We'll return true if any stack traces were found
		if ( searchForStackTraces().length > 0 ) {
			stackTracesFound = true;
		}
		
		new Thread() {
			@Override
			public void run() {
				// First of all transmit any stack traces that may be lying around
				submitStackTraces();
				UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
				if (currentHandler != null) {
					Log.d(TAG, "current handler class="+currentHandler.getClass().getName());
				}	
				// don't register again if already registered
				if (!(currentHandler instanceof DefaultExceptionHandler)) {
					// Register default exceptions handler
					Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(currentHandler));
				}
			}
       	}.start();
		
		return stackTracesFound;
	}
	
	/**
	 * Search for stack trace files.
	 * @return
	 */
	private static String[] searchForStackTraces() {
		if ( stackTraceFileList != null ) {
			return stackTraceFileList;
		}
		File dir = new File(G.FILES_PATH + "/");
		// Try to create the files folder if it doesn't exist
		dir.mkdir();
		// Filter for ".stacktrace" files
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".stacktrace"); 
			} 
		}; 
		return (stackTraceFileList = dir.list(filter));	
	}
	
	/**
	 * Look into the files folder to see if there are any "*.stacktrace" files.
	 * If any are present, submit them to the trace server.
	 */
	public static void submitStackTraces() {
		try {
			String[] list = searchForStackTraces();
			if ( list != null && list.length > 0 ) {
				for (int i=0; i < list.length; i++) {
					String filePath = G.FILES_PATH+"/"+list[i];
					String version = list[i].split("-")[0];
					Log.d(TAG, "Stacktrace in file '"+filePath+"' belongs to version " + version);
					// Read contents of stacktrace
					StringBuilder contents = new StringBuilder();
					BufferedReader input =  new BufferedReader(new FileReader(filePath));
					String line = null;
					String androidVersion = null;
	                String phoneModel = null;
	                while (( line = input.readLine()) != null){
                        if (androidVersion == null) {
                            androidVersion = line;
                            continue;
                        }
                        else if (phoneModel == null) {
                            phoneModel = line;
                            continue;
                        }
                        contents.append(line);
			            contents.append(System.getProperty("line.separator"));
			        }
			        input.close();
			        String stacktrace;
			        stacktrace = contents.toString();
			        Log.e(TAG, "Transmitting stack trace: " + stacktrace);
					ErrorTrace er = new ErrorTrace();
					er.setPackageName(G.APP_PACKAGE);
					er.setAppVersion(version);
					er.setPhoneModel(phoneModel);
					er.setAndroidVersion(androidVersion);
					er.setStacktrace(stacktrace);
					NetworkEngine.getInstance().postError(er).enqueue(new Callback<ErrorTrace>() {
						@Override
						public void onResponse(Call<ErrorTrace> call, Response<ErrorTrace> response) {

						}

						@Override
						public void onFailure(Call<ErrorTrace> call, Throwable t) {

						}
					});
				}
			}
		} catch( Exception e ) {
			e.printStackTrace();
		} finally {
			try {
				String[] list = searchForStackTraces();
				for ( int i = 0; i < list.length; i ++ ) {
					File file = new File(G.FILES_PATH+"/"+list[i]);
					file.delete();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
