package com.t3hh4xx0r.romcrawler;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ListActivity {
    ArrayList<String> threadArray;
    ArrayList<String> titleArray;
    private ArrayAdapter<String> titleArrayAdapter;
    String message;
    String threadTitle = null;


	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        
        threadArray = new ArrayList<String>();
        titleArray = new ArrayList<String>();
        titleArrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item,
            R.id.itemName, titleArray);

        getDevice();
        if(Constants.DEVICE != null){
        	try {
        		getThreads();
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
        }
    }
    
    public void getDevice() {
    	Constants.DEVICE = android.os.Build.DEVICE.toUpperCase();
    	try {
    		DeviceType type = Enum.valueOf(DeviceType.class, Constants.DEVICE);
    		Constants.FORUM = type.getForumUrl();
    		message = Constants.FORUM;
    	} catch (IllegalArgumentException e) {
    		message = "Device not found/supported!";
    	}
    	makeToast(message);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	super.onListItemClick(l, v, position, id);
        Constants.THREADURL = threadArray.get(position);
        threadTitle = titleArray.get(position);
    	Log.d("POC", "clicked: " + threadArray.get(position));
        Intent intent = new Intent(MainActivity.this, ThreadActivity.class);
        Bundle b = new Bundle();
        b.putString("title", threadTitle);
        intent.putExtras(b);
        startActivity(intent);    	
    }
    
	public void getThreads() throws IOException {
		Thread getThreadsThread = new Thread() {
			public void run() {
					Document doc = null;
					try {
						doc = Jsoup.connect(Constants.FORUM).get();
					} catch (IOException e) {
						e.printStackTrace();
					}
					Elements threads = doc.select(".topic_title");
		       		for (Element thread : threads) {
		       			String threadStr = thread.attr("abs:href");
		       			threadTitle = thread.text();
		       			String endTag = "/page__view__getnewpost";
						threadStr = new String(threadStr.replace(endTag, ""));
						titleArray.add(threadTitle);
		       			threadArray.add(threadStr);
		       		}
            } 
		};
		getThreadsThread.start();
		try {
			getThreadsThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	mHandler.sendEmptyMessage(0);
    }
	
	final Handler mHandler = new Handler(){ 
        public void handleMessage (Message  msg) {
        	switch (msg.what) {
        	case 0:
	        	MainActivity.this.runOnUiThread(new Runnable() {
	        		@Override
	        		public void run() {
	        			setListAdapter(titleArrayAdapter);
	        		}
	        	});
        		break;
        	}
        } 
	}; 

	public void makeToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
}
