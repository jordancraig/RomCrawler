package com.t3hh4xx0r.poc;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.widget.TextView;

public class POCActivity extends Activity {
	TextView mURLText;
	TextView mZipText;
	String mLinkURL;
	String mURLURL;
	static String finalName;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mURLText = (TextView) findViewById(R.id.url);
        mZipText = (TextView) findViewById(R.id.zip);
        
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
	    			String strClean = strMessy.replaceAll("\\<.*?>","");

	    			getFinalName(strClean);
    		
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
	    	mHandler.sendEmptyMessage(1);
	    	}
	    }).start();
    }
    	
    public void getLinks() throws IOException {
		new Thread(new Runnable() {
            public void run() {
            	mURLURL = "http://rootzwiki.com/topic/12083-unsecured-stock-bootimg402/";
            	Document doc = null;
				try {
					doc = Jsoup.connect(mURLURL).get();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		        Elements links = doc.select("a[href]");

            	for (Element link : links) {
            		if (link.attr("abs:href").contains(finalName)){
            			mLinkURL = finalName + " " + " " + link.attr("abs:href");
            		}
            		//String strClean = zip.absUrl("href"); //foo bar blah.zip	
            		//getFinalName(strClean);
            		//String[] linkUrlsFull = linkUrlFull.split(".zip");
            		//String linkUrl = linkUrlsFull[0]; //ends string before .zip -- foo bar blah
            		//String[] parts = linkUrl.split(" ");
            		//String lastWord = parts[parts.length - 1]; //cuts all but last word -- blah
            		//mLinkURL = lastWord + ".zip"; //adds back .zip extension -- blah.zip
            		Log.d("POC", "\"" + mLinkURL + "\"");
            	} mHandler.sendEmptyMessage(0);
            } 
		}).start();
    }
    
    public void getFinalName(String strClean) {
    	//should loop through until string no longer contains any .zip files.
    	//while (strClean.contains(".zip")){
    		String trimmed = strClean.substring(0, strClean.lastIndexOf(".zip"));
    		String[] parts = trimmed.split("\n");
    		String lastWord = parts[parts.length - 1] + ".zip";
    		finalName = lastWord;
    		//Log.d("POC", finalName);
    		//strClean = new String(strClean.replace(finalName, ""));
    	//}
    }
    
	final Handler mHandler = new Handler(){ 
        public void handleMessage (Message  msg) {
        	switch (msg.what) {
        	case 0:
        		mURLText.setText(mURLURL);
        		mZipText.setText(mLinkURL);
        		break;
        	case 1:
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