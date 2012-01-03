package com.t3hh4xx0r.poc;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.commonsware.cwac.merge.MergeAdapter;

import android.app.ListActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class POCActivity extends ListActivity  {
	String[] mURLURLS;
	private MergeAdapter adapter=null;
	private ArrayAdapter<String> arrayAdapter=null;
    ArrayList<String> linkArray;
    Elements links;
    Thread getNameThread;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);

        getNameThread = new Thread();
        linkArray = new ArrayList<String>();
        mURLURLS = new String[] {"http://forum.xda-developers.com/showthread.php?t=1022282", "http://rootzwiki.com/topic/12251-romlte-rootzboat-403-v61/", "http://rootzwiki.com/topic/12601-romaosp403cdma-gummynex-038-010112/", "http://rootzwiki.com/topic/12083-unsecured-stock-bootimg402/", "http://rootzwiki.com/topic/12451-rom-android-open-kang-project-toro-build-2012-dec-31/"};	        

        adapter = new MergeAdapter();

		for (int i=0; i<mURLURLS.length; i++) {
	        try {
				getNames(mURLURLS[i]);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		createUI();
    }

    private ArrayAdapter<String> buildList(ArrayList<String> linkArray) {
		for (Iterator<String> c = linkArray.iterator(); c.hasNext();) {
			Log.d("POC", (String)c.next());
		}
        return(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, linkArray));
      }
    
	public void getNames(final String mURLURLS) throws IOException, InterruptedException {
		Thread getNameThread = new Thread() {
			public void run() {
				try {
			        linkArray = new ArrayList<String>();
					URL url = new URL(mURLURLS);
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
					String strClean = new String (strMessy.replaceAll("\\<.*?>",""));
					while (strClean.contains(".zip")){
						String trimmed = strClean.substring(0, strClean.lastIndexOf(".zip"));
						String[] parts = trimmed.split("\n");
						String lastWord = parts[parts.length - 1] + ".zip";
						strClean = new String(strClean.replace(lastWord, ""));
						linkArray.add(lastWord);
					}
					Document doc = Jsoup.connect(mURLURLS).get();
        			Elements links = doc.select("a[href]");
    				for (Element link : links) {
    					for (Iterator<String> c = linkArray.iterator(); c.hasNext();) {
    						String newName = c.next();
	       					if (link.attr("abs:href").contains(newName)) {		        						
	       						c.remove();
	       						//addList.add(link.attr("abs:href").toString());
	       					}
    					}
    					if (link.attr("abs:href").contains(".zip")) {	
   							linkArray.add(link.attr("abs:href"));
   						}
   					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		getNameThread.start();
		getNameThread.join();
		setUI(mURLURLS);
	} 	
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setUI(String mURLURLS) {
		HashSet hashSet = new HashSet(linkArray);
 	    linkArray = new ArrayList(hashSet);
        arrayAdapter = buildList(linkArray);
        adapter.addView(buildHeader(mURLURLS));
        adapter.addAdapter(arrayAdapter);
	}
	
	public void createUI() {
        setListAdapter(adapter);
	}
    
    private View buildHeader(String mURLURLS) {
    	TextView header = new TextView(this);
    	header.setText(mURLURLS);
    	header.setBackgroundResource(R.drawable.dialog_full_holo_dark);
    	header.setTextColor(getResources().getColor(R.color.ics));
    	return header;
      }
}