package com.reclick.reclick;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RivalsAdapter extends BaseAdapter {

	private Context context;
	private JSONArray players;
	
	public RivalsAdapter(Context context, JSONArray players) {
		super();
		this.context = context;
		this.players = players;
	}
	
	@Override
	public int getCount() {
		return players.length();
	}

	@Override
	public Object getItem(int position) {
		try {
			return players.get(position);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		try {
			return Long.parseLong(((JSONObject)players.get(position)).getString("id"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private class ViewHolder {
		TextView nickname;
		TextView location;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.game_rivals_info_row, null);
			holder = new ViewHolder();
			holder.nickname = (TextView) convertView.findViewById(R.id.game_activity_rivals_row_rival_nickname);
			holder.location = (TextView) convertView.findViewById(R.id.game_activity_rivals_row_rival_location);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		try {
			JSONObject currentRival = (JSONObject) players.get(position);
			holder.nickname.setText(currentRival.getString("nickname"));
			holder.location.setText(currentRival.getString("location"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return convertView;
	}
}