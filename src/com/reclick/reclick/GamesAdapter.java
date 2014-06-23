package com.reclick.reclick;

import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import unite.Client;
import unite.OnResponseListener;
import unite.Response;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.reclick.framework.Prefs;
import com.reclick.request.Urls;

public class GamesAdapter extends BaseAdapter {
	
	private final String TAG = this.getClass().getSimpleName();
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
	public long getItemId(int position) { // returns the game id of the game in the given position.
		try {
			return Long.parseLong(((JSONObject)games.get(position)).getString("gameId"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private class ViewHolder {
		String gameId;
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
			JSONObject currentGame = (JSONObject)games.get(position);
			holder.gameName.setText(currentGame.getString("name"));
			holder.gameDescription.setText(currentGame.getString("description"));
			holder.numOfPlayers.setText(currentGame.getString("numOfPlayers"));
			holder.gameId = currentGame.getString("id");
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
			if (isCurrUserGames) { // in case user want's to enter to one of his games.
				context.startActivity(new Intent(context, GameActivity.class));
			} else { // in case user want's to join to one of the open games list.
				String gameId = ((ViewHolder) ((View) view.getParent()).getTag()).gameId;
				new Client()
					.post(Urls.addPlayerToGame(context, gameId, Prefs.getUsername(context)))
					.setHeader(HTTP.CONTENT_TYPE, context.getString(R.string.application_json))
					.send(onResponseListener);
			}
		}
	};
	
	private OnResponseListener onResponseListener = new OnResponseListener() {
		
		@Override
		public void onResponseReceived(Response response) {
			if (response.getStatusCode() != HttpStatus.SC_OK) {
				Log.e(TAG, response.getBody());
				return;
			}
			context.startActivity(new Intent(context, GameActivity.class));
		}
	};
}