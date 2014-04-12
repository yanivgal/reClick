package com.reclick.reclick;

import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import com.reclick.framework.App;
import com.reclick.request.Request;
import com.reclick.request.Request.RequestObject;
import com.reclick.request.Request.RequestType;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {
	
	private final String TAG = this.getClass().getSimpleName();
	
	private EditText usernameInput;
	private EditText passwordInput;
	private EditText nicknameInput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		usernameInput = (EditText) findViewById(R.id.login_username_input);
		passwordInput = (EditText) findViewById(R.id.login_password_input);
	}
	
	public void login(View v) {
		String username = usernameInput.getText().toString();
		String password = passwordInput.getText().toString();
		
		if (username.isEmpty() || password.isEmpty()) {
			App.showToast(this, "Please fill both fields");
			return;
		}
		
		RequestObject ro = new RequestObject("http://192.168.1.10/reclick/login/", RequestType.POST);
		ro.addParameter("username", username);
		ro.addParameter("password", App.md5(password));
		
		JSONObject jsonResponse;
		try {
			jsonResponse = new Request(ro).execute().get();
		} catch (InterruptedException e) {

		} catch (ExecutionException e) {

		}
	}
}
