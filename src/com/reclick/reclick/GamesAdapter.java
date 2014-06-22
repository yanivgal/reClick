package com.reclick.reclick;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import unite.Client;

import com.reclick.framework.App;
import com.reclick.framework.Prefs;
import com.reclick.request.Urls;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GamesAdapter extends BaseAdapter {
	
	private Context context;
	private JSONArray games;
	private boolean isCurrUserGames;
	
	public GamesAdapter(Context context, JSONArray games, boolean isCurrUserGames) {
		super();
		this.context = context;
		this.games = games;
		this.isCurrUserGames = isCurrUserGames;
	}
	
	@Override
	public int getCount() {
		return games.length();
	}

	@Override
	public Object getItem(int position) {
		try {
			return games.get(position);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private class ViewHolder {
		TextView gameName;
		TextView gameDescription;
		TextView numOfPlayers;
		Button joinOrEnter;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.main_games_row, null);
			holder = new ViewHolder();
			holder.gameName = (TextView) convertView.findViewById(R.id.main_activity_games_row_game_name);
			holder.gameDescription = (TextView) convertView.findViewById(R.id.main_activity_games_row_game_description);
			holder.numOfPlayers = (TextView) convertView.findViewById(R.id.main_activity_games_row_game_num_of_players);
			holder.joinOrEnter = (Button) convertView.findViewById(R.id.main_activity_join_or_enter_game_button);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		try {
			holder.gameName.setText(((JSONObject)games.get(position)).getString("name"));
			holder.gameDescription.setText(((JSONObject)games.get(position)).getString("description"));
			holder.numOfPlayers.setText(((JSONObject)games.get(position)).getString("numOfPlayers"));
			if (isCurrUserGames) {
				holder.joinOrEnter.setText("Enter");
			}
			holder.joinOrEnter.setOnClickListener(onClickListener);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		return convertView;
	}
	
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			App.showToast(context, isCurrUserGames ? "Enter" : "Join");
////			new Client()
////				.get(Urls.getOpenGames(this))
////				.setHeader(HTTP.CONTENT_TYPE, "application/json")
////				.setOnResponseListener(onCurrUserGamesResponseListener)
////				.send();
//			new Client()
//				.post(Urls.addPlayerToGame(context))
//				.setHeader(HTTP.CONTENT_TYPE, "application/json")
//				.addParam("gameId", )
//				.addParam("username", Prefs.getUsername(context))
		}
	};
}