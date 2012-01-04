package com.t3hh4xx0r.romcrawler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
    ArrayList<String> threadArray;
    ArrayList<String> authorArray;
    String message;
    String threadTitle = null;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        
        final ListView lv1 = (ListView) findViewById(R.id.listView);
        threadArray = new ArrayList<String>();
        authorArray = new ArrayList<String>();

        getDevice();
        
        lv1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
             Object o = lv1.getItemAtPosition(position);
             TitleResults fullObject = (TitleResults)o;
             threadTitle = fullObject.getItemName();
             Constants.THREADURL = threadArray.get(position);
             Intent intent = new Intent(MainActivity.this, ThreadActivity.class);
             Bundle b = new Bundle();
             b.putString("title", threadTitle);
             intent.putExtras(b);
             startActivity(intent);    	
            }  
           });
        
        if(Constants.DEVICE != null){
            ArrayList<TitleResults> titleArray = getTitles();
            lv1.setAdapter(new TitleAdapter(this, titleArray)); 
            }
        Log.d("POC", "AUTHORARRAY SIZE: " + authorArray.size() + " THREADARRAY SIZE: " + threadArray.size());
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
    
    private ArrayList<TitleResults> getTitles(){
        final ArrayList<TitleResults> results = new ArrayList<TitleResults>();

        Thread getTitlesThread = new Thread() {
			public void run() {
		        TitleResults titleArray =  new TitleResults();
				StringBuilder whole = new StringBuilder();

				try {
					URL url = new URL(
							Constants.FORUM);
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
					try {
						BufferedReader in = new BufferedReader(
							new InputStreamReader(new BufferedInputStream(urlConnection.getInputStream())));
						String inputLine;
						while ((inputLine = in.readLine()) != null)
							whole.append(inputLine);
						in.close();
					} catch (IOException e) {
						Log.e("POC", e.getMessage());
					} finally {
						urlConnection.disconnect();
					}
				} catch (Exception e) {
					Log.e("POC", e.getMessage());
				}
				Document doc = Parser.parse(whole.toString(), Constants.FORUM);
				Elements threads = doc.select(".topic_title");
		       	Elements authors = doc.select("a[hovercard-ref]");
	      		for (Element author : authors) {
	       			authorArray.add(author.text());
	      		}
	      		cleanAuthors();

	       		for (Element thread : threads) {
	       			titleArray =  new TitleResults();

	       			titleArray.setAuthorDate(authorArray.get(0));
	       			authorArray.remove(0);
	       			
	       			//Thread title
	       			threadTitle = thread.text();
	       			titleArray.setItemName(threadTitle);
		       		
	       			//Thread link
	       			String threadStr = thread.attr("abs:href");
	       			String endTag = "/page__view__getnewpost"; //trim link
	       			threadStr = new String(threadStr.replace(endTag, ""));
	       			threadArray.add(threadStr);
					results.add(titleArray);
	       		}
           } 
		};
		getTitlesThread.start();
		try {
			getTitlesThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return results;
    }

    public void cleanAuthors() {
        ArrayList<String> tmpArray = new ArrayList<String>();
		for (int i=2; i<authorArray.size(); i++) {
	        tmpArray.add(authorArray.get(i));
		}
		authorArray = new ArrayList<String>();
		for (int i=0; i<tmpArray.size(); i += 2) {
			authorArray.add(tmpArray.get(i));
		}
    }
    
	public void makeToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
}
