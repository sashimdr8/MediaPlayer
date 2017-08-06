package com.mediaplayer;

//import java.util.ArrayList;
//import java.util.HashMap;

//import android.content.Context;
import android.database.Cursor;
//import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter{

	private Cursor c;
	private LayoutInflater inflater;

	public  CustomAdapter(MainActivity mactivity, Cursor data) {
		c = data;
		inflater = LayoutInflater.from(mactivity);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return c.getCount();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	class ViewHolder{

		TextView songname ;
		TextView artistname; 
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ViewHolder holder;
		c.moveToPosition(position);
		
		if(convertView==null){
		
				holder  = new ViewHolder();
				convertView = inflater.inflate(R.layout.listrow, null);
				holder.songname = (TextView)convertView.findViewById(R.id.songname);
				holder.artistname = (TextView)convertView.findViewById(R.id.artistname);
				convertView.setTag(R.string.holderobj,holder);
		}
		else{
				holder = (ViewHolder)convertView.getTag(R.string.holderobj);
		}
		
		holder.songname.setText(c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
		holder.artistname.setText(c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
		return convertView;

	}
	
	
	

}
