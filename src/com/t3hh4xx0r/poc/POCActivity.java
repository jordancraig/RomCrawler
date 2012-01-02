package com.t3hh4xx0r.poc;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class POCActivity extends ListActivity  {
	TextView mURLText;
	TextView mZipText;
	String mLinkURL;
	String mURLURL ="http://rootzwiki.com/topic/12083-unsecured-stock-bootimg402/page__fromsearch__1";
	//String mURLURL = "http://rootzwiki.com/topic/12451-rom-android-open-kang-project-toro-build-2012-dec-31/";
	static String finalName;
    private ArrayAdapter<String> linkArrayAdapter;
    ArrayList<String> linkArray;
    Elements links;
    String strClean;
    Document doc;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        linkArray = new ArrayList<String>();
        linkArrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item,
            R.id.itemName, linkArray);
        
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
	    			URL url = new URL(mURLURL);
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
		        				for (Element link : links) {
				        			for (int i=0; i<linkArrayAdapter.getCount(); i++) {
				        				finalName = linkArrayAdapter.getItem(i);
				       					if (link.attr("abs:href").contains(finalName)) {
				       						linkArrayAdapter.remove(finalName);
				       						linkArrayAdapter.add(link.attr("abs:href"));
				       					}
				       				}
		        					if (link.attr("abs:href").contains(".zip")) {	
		       							linkArrayAdapter.add(link.attr("abs:href"));
		       						}
		       					}
		        			setList();
			        	}
				    });
            } 
		}).start();
    }
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setList() {
		HashSet hashSet = new HashSet(linkArray);
 	    linkArray = new ArrayList(hashSet);
		for (Iterator c = linkArray.iterator(); c.hasNext();) {
			Log.d("POC", (String)c.next());
		}
	    linkArrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item,
             R.id.itemName, linkArray);
        setListAdapter(linkArrayAdapter);
	}
	
    public void getFinalName(String strClean) {
    	//should loop through until string no longer contains any .zip files.
    	while (strClean.contains(".zip")){
    		String trimmed = strClean.substring(0, strClean.lastIndexOf(".zip"));
    		String[] parts = trimmed.split("\n");
    		String lastWord = parts[parts.length - 1] + ".zip";
    		finalName = lastWord;
    		strClean = new String(strClean.replace(finalName, ""));
			linkArrayAdapter.add(finalName);
    	}
    	mHandler.sendEmptyMessage(0);
    }
    
	final Handler mHandler = new Handler(){ 
        public void handleMessage (Message  msg) {
        	switch (msg.what) {
        	case 0:
           		try {
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