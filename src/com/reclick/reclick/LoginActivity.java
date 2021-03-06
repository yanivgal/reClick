package com.reclick.reclick;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import unite.Client;
import unite.OnResponseListener;
import unite.Response;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.reclick.framework.App;
import com.reclick.framework.Prefs;
import com.reclick.request.Urls;

public class LoginActivity extends Activity implements OnResponseListener {

	private final String TAG = this.getClass().getSimpleName();

	private LinearLayout nicknameContainer;
	private EditText usernameInput;
	private EditText passwordInput;
	private EditText nicknameInput;
	private Button loginBtn;
	private Button signUpBtn;
	private TextView loginText;
	private TextView loginLink;
	private TextView signUpText;
	private TextView signUpLink;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		((LinearLayout) findViewById(R.id.login_activity_main_content))
				.setOnTouchListener(onTouchListener);

		nicknameContainer = (LinearLayout) findViewById(R.id.login_activity_nickname_container);

		usernameInput = (EditText) findViewById(R.id.login_activity_username_input);
		passwordInput = (EditText) findViewById(R.id.login_activity_password_input);
		nicknameInput = (EditText) findViewById(R.id.login_activity_nickname_input);

		loginBtn = (Button) findViewById(R.id.login_activity_login_btn);
		signUpBtn = (Button) findViewById(R.id.login_activity_signup_btn);

		loginText = (TextView) findViewById(R.id.login_activity_login_here_text);
		loginLink = (TextView) findViewById(R.id.login_activity_login_here_link);

		signUpText = (TextView) findViewById(R.id.login_activity_sign_up_here_text);
		signUpLink = (TextView) findViewById(R.id.login_activity_sign_up_here_link);
	}

	public void login(View v) {
		App.hideSoftKeyboard(this);

		String username = usernameInput.getText().toString();
		String password = passwordInput.getText().toString();

		if (username.isEmpty() || password.isEmpty()) {
			App.showToast(this,
					getString(R.string.login_activity_fill_all_fields_login));
			return;
		}

		sendSessionRequest(Urls.login(this),
				loginParamsToList(username, password, Prefs.getGcmRegId(this)));
	}

	public void signup(View v) {
		App.hideSoftKeyboard(this);

		String username = usernameInput.getText().toString();
		String password = passwordInput.getText().toString();
		String nickname = nicknameInput.getText().toString();

		if (username.isEmpty() || password.isEmpty() || nickname.isEmpty()) {
			App.showToast(this,
					getString(R.string.login_activity_fill_all_fields_sign_up));
			return;
		}

		sendSessionRequest(
				Urls.signup(this),
				signUpParamsToList(username, password, nickname,
						Prefs.getGcmRegId(this)));
	}

	public void loginLink(View v) {
		nicknameContainer.setVisibility(View.GONE);
		signUpBtn.setVisibility(View.GONE);
		loginText.setVisibility(View.GONE);
		loginLink.setVisibility(View.GONE);

		loginBtn.setVisibility(View.VISIBLE);
		signUpText.setVisibility(View.VISIBLE);
		signUpLink.setVisibility(View.VISIBLE);
	}

	public void signUpLink(View v) {
		loginBtn.setVisibility(View.GONE);
		signUpText.setVisibility(View.GONE);
		signUpLink.setVisibility(View.GONE);

		nicknameContainer.setVisibility(View.VISIBLE);
		signUpBtn.setVisibility(View.VISIBLE);
		loginText.setVisibility(View.VISIBLE);
		loginLink.setVisibility(View.VISIBLE);
	}

	/**
	 * 
	 */
	private List<NameValuePair> loginParamsToList(String username,
			String password, String gcmRegId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", App.md5(password)));
		params.add(new BasicNameValuePair("gcmRegId", gcmRegId));
		return params;
	}

	/**
	 * 
	 */
	private List<NameValuePair> signUpParamsToList(String username,
			String password, String nickname, String gcmRegId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", App.md5(password)));
		params.add(new BasicNameValuePair("nickname", nickname));
		params.add(new BasicNameValuePair("gcmRegId", gcmRegId));
		return params;
	}

	private void sendSessionRequest(String url, List<NameValuePair> params) {
		new Client()
			.post(url)
			.setHeader(HTTP.CONTENT_TYPE,
					getString(R.string.application_json)).setParams(params)
			.send(this);
	}

	@Override
	public void onResponseReceived(Response response) {
		if (response.getStatusCode() != HttpStatus.SC_OK) {
			Log.e(TAG, response.getErrorMsg());
			return;
		}
		try {
			JSONObject jsonResponse = response.getJsonBody();
			if (jsonResponse.getString("status").equals("success")) {
				Prefs.setUsername(LoginActivity.this, jsonResponse
						.getJSONObject("data").getString("username"));
				Prefs.setNickname(LoginActivity.this, jsonResponse
						.getJSONObject("data").getString("nickname"));
				startActivity(new Intent(LoginActivity.this, MainActivity.class));
				finish();
			} else {
				App.showToast(LoginActivity.this, jsonResponse.getString("message"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private OnTouchListener onTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			App.hideSoftKeyboard(LoginActivity.this);
			return true;
		}
	};
}