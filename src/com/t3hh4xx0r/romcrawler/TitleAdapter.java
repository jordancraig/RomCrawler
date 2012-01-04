package com.t3hh4xx0r.romcrawler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TitleAdapter extends BaseAdapter {
	 private static ArrayList<TitleResults> threadArrayList;
	 
	 private LayoutInflater mInflater;

	 public TitleAdapter(Context context, ArrayList<TitleResults> results) {
	  threadArrayList = results;
	  mInflater = LayoutInflater.from(context);
	 }

	 public int getCount() {
	  return threadArrayList.size();
	 }

	 public Object getItem(int position) {
	  return threadArrayList.get(position);
	 }

	 public long getItemId(int position) {
	  return position;
	 }

	 public View getView(int position, View convertView, ViewGroup parent) {
	  ViewHolder holder;
	  if (convertView == null) {
	   convertView = mInflater.inflate(R.layout.list_item, null);
	   holder = new ViewHolder();
	   holder.authorDate = (TextView) convertView.findViewById(R.id.authorDate);
	   holder.itemName = (TextView) convertView.findViewById(R.id.itemName);

	   convertView.setTag(holder);
	  } else {
	   holder = (ViewHolder) convertView.getTag();
	  }
	  
	  holder.itemName.setText(threadArrayList.get(position).getItemName());
	  holder.authorDate.setText(threadArrayList.get(position).getAuthorDate());

	  return convertView;
	 }

	 static class ViewHolder {
	  TextView itemName;
	  TextView authorDate;
	 }
	}