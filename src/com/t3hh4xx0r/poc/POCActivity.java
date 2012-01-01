package com.t3hh4xx0r.poc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class POCActivity extends ListActivity  {
	TextView mURLText;
	TextView mZipText;
	String mLinkURL;
	String mURLURL;
	static String finalName;
    private ArrayAdapter<String> linkArrayAdapter;
    Elements links;
    String strClean;
    Document doc;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ArrayList<String> linkArray = new ArrayList<String>();
        linkArrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item,
            R.id.itemName, linkArray);
        setListAdapter(linkArrayAdapter);

        try {
		getNames();
        } catch (Exception e) {
        	e.printStackTrace();
        }

    }

	public void getNames() throws IOException {
	    new Thread(new Runnable() {
	    	public void run() {
	    		try {
	    			URL url = new URL("http://rootzwiki.com/topic/12083-unsecured-stock-bootimg402/");
	    			URLConnection con = url.openConnection();
	    			Pattern p = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");
	    			Matcher m = p.matcher(con.getContentType());

	    			String charset = m.matches() ? m.group(1) : "ISO-8859-1";
	    			Reader r = new InputStreamReader(con.getInputStream(), charset);
	    			StringBuilder buf = new StringBuilder();
	    			while (true) {
	    				int ch = r.read();
	    				if (ch < 0)
	    					break;
	    				buf.append((char) ch);
	    			}
	    			String strMessy = buf.toString();
	    			strClean = strMessy.replaceAll("\\<.*?>","");
		        	POCActivity.this.runOnUiThread(new Runnable() {
	        		@Override
	        			public void run() {
	        				getFinalName(strClean);
	        			}
		        	});			    			
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
	    	}
	    }).start();
    }
    	
	public void getLinks() throws IOException {
		new Thread(new Runnable() {
	           public void run() {
	            	mURLURL = "http://rootzwiki.com/topic/12083-unsecured-stock-bootimg402/";
					try {
						doc = Jsoup.connect(mURLURL).get();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        	POCActivity.this.runOnUiThread(new Runnable() {
		        		@Override
		        		public void run() {
		        			Elements links = doc.select("a[href]");
		        			for (int i=0; i<linkArrayAdapter.getCount(); i++) {
		        				finalName = linkArrayAdapter.getItem(i);
		        				for (Element link : links) {
		        					if (link.attr("abs:href").contains(finalName)){
		        						linkArrayAdapter.remove(finalName);
		        						linkArrayAdapter.add(link.attr("abs:href"));
		        					}
		        				}
		        			}
			        	}
				    });
            } 
		}).start();
    }
    
    public void getFinalName(String strClean) {
        DBAdapter db = new DBAdapter(this); 

    	//should loop through until string no longer contains any .zip files.
    	while (strClean.contains(".zip")){
    		String trimmed = strClean.substring(0, strClean.lastIndexOf(".zip"));
    		String[] parts = trimmed.split("\n");
    		String lastWord = parts[parts.length - 1] + ".zip";
    		finalName = lastWord;
    		strClean = new String(strClean.replace(finalName, ""));
			linkArrayAdapter.add(finalName);
    	}
    	mHandler.sendEmptyMessage(1);
    }
    
	final Handler mHandler = new Handler(){ 
        public void handleMessage (Message  msg) {
        	switch (msg.what) {
        	case 0:
        		//mURLText.setText(mURLURL);
        		//mZipText.setText(mLinkURL);
        		break;
        	case 1:
        		try {
        			Log.d("POC", "HANDLER MESSAGE RECEIVED!");
        			getLinks();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		break;
        	}
        } 
	}; 
}