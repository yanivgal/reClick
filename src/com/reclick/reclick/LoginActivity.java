package com.reclick.reclick;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import com.reclick.framework.App;
import com.reclick.framework.Prefs;
import com.reclick.request.Request;
import com.reclick.request.Request.RequestObject;
import com.reclick.request.Request.RequestType;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {
	
	private final String TAG = this.getClass().getSimpleName();
	
	private EditText usernameInput;
	private EditText passwordInput;
	private TextView nicknameLabel;
	private EditText nicknameInput;
	private Button loginBtn;
	private Button signUpBtn;
	private TextView loginLink;
	private TextView signUpLink;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		usernameInput = (EditText) findViewById(R.id.login_username_input);
		passwordInput = (EditText) findViewById(R.id.login_password_input);
		nicknameLabel = (TextView) findViewById(R.id.login_nickname_label);
		nicknameInput = (EditText) findViewById(R.id.login_nickname_input);
		loginBtn = (Button) findViewById(R.id.login_btn);
		signUpBtn = (Button) findViewById(R.id.signup_btn);
		loginLink = (TextView) findViewById(R.id.login_here_link);
		signUpLink = (TextView) findViewById(R.id.sign_up_here_link);
	}
	
	public void login(View v) {
		App.hideSoftKeyboard(this);
		
		String username = usernameInput.getText().toString();
		String password = passwordInput.getText().toString();
		
		if (username.isEmpty() || password.isEmpty()) {
			App.showToast(this, "Please fill both fields");
			return;
		}
		
		RequestObject ro = new RequestObject("http://192.168.1.10/reclick/login/", RequestType.POST);
		ro.addParameter("username", username);
		ro.addParameter("password", App.md5(password));
		ro.addParameter("gcmRegId", Prefs.getGcmRegId(this));
		
		try {
			JSONObject jsonResponse = new Request(ro).execute().get();
			if (jsonResponse.getString("status").equals("success")) {
				
				Prefs.setUsername(this, jsonResponse.getString("username"));
				Prefs.setNickname(this, jsonResponse.getString("nickname"));
				
				
				Intent intent = new Intent(
						this, com.reclick.reclick.MainActivity.class);
				startActivity(intent);
				
				App.showToast(this, jsonResponse.getString("message"));
			}
			App.showToast(this, jsonResponse.getString("message"));
		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage());
		} catch (ExecutionException e) {
			Log.e(TAG, e.getMessage());
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
		}
	}
	
	public void signup(View v) {
		App.hideSoftKeyboard(this);
	}
	
	public void loginLink(View v) {
		nicknameLabel.setVisibility(View.GONE);
		nicknameInput.setVisibility(View.GONE);
		signUpBtn.setVisibility(View.GONE);
		loginLink.setVisibility(View.GONE);
		
		loginBtn.setVisibility(View.VISIBLE);
		signUpLink.setVisibility(View.VISIBLE);
	}
	
	public void signUpLink(View v) {
		loginBtn.setVisibility(View.GONE);
		signUpLink.setVisibility(View.GONE);
		
		nicknameLabel.setVisibility(View.VISIBLE);
		nicknameInput.setVisibility(View.VISIBLE);
		signUpBtn.setVisibility(View.VISIBLE);
		loginLink.setVisibility(View.VISIBLE);
	}
}
