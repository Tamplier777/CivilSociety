package org.dobrochin.civilsociety;

import org.dobrochin.civilsociety.requests.RequestService;
import org.dobrochin.civilsociety.requests.URL;
import org.dobrochin.civilsociety.social.GooglePAuth;
import org.dobrochin.civilsociety.social.SocialNetworkDataParser;
import org.dobrochin.civilsociety.social.VKAuth;
import org.dobrochin.civilsociety.views.DialogWebView;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.AccountPicker;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends BaseActivity implements DialogWebView.AuthFinishListener, View.OnClickListener{
	private static final String CACHE_LOGIN = "login";
	private static final String CACHE_PASSWORD = "password";		
	public static TextView res;
	private EditText login;
	private EditText password;
	private Button vk_auth;
	private Button facebook_auth;
	private com.google.android.gms.common.SignInButton google_auth;
	private SocialNetworkDataParser socParser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
	}
	
	private void init()
	{
		res = (TextView)findViewById(R.id.result);
		login = (EditText)findViewById(R.id.username);
		password = (EditText)findViewById(R.id.password);
		vk_auth = (Button)findViewById(R.id.vk_auth);
		facebook_auth = (Button)findViewById(R.id.facebook_auth);
		google_auth = (com.google.android.gms.common.SignInButton)findViewById(R.id.google_auth);
		google_auth.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onReceiveResponse(Intent intent) {
		int requestType = intent.getIntExtra(RequestService.REQUEST_TYPE, -1);
		switch(requestType)
		{
			case RequestService.REQUEST_GET_NEWS:
				res.setText("" + intent.getStringExtra(RequestService.RESPONSE));
				break;
			case RequestService.REQUEST_GET_SOCIAL_PROFILE:
				try {
					socParser.setSNResponse(intent.getStringExtra(RequestService.RESPONSE));
					login.setText(socParser.getName());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Intent intent = new Intent();
		intent.putExtra(RequestService.REQUEST_TYPE, RequestService.REQUEST_GET_NEWS);
		sendRequest(intent);
		sendLogToServer(1, 1, 1);
	}

	@Override
	protected void cacheLoaded(String cache) {
		// TODO Auto-generated method stub
		try {
			JSONObject cacheJson = new JSONObject(cache);
			String loginStr = cacheJson.getString(CACHE_LOGIN);
			String passwdString = cacheJson.getString(CACHE_PASSWORD);
			
			login.setText(loginStr);
			password.setText(passwdString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected String getCacheToSave() {
		// TODO Auto-generated method stub
		JSONObject cacheJson = new JSONObject();
		try {
			cacheJson.put(CACHE_LOGIN, login.getText().toString());
			cacheJson.put(CACHE_PASSWORD, password.getText().toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cacheJson.toString();
	}

	@Override
	public void onAuthFinish(String authData) {
		// TODO Auto-generated method stub
		socParser.sendGetProfileRequest(this, authData);
	}

	@Override
	public void onClick(View view) {
		SocialNetworkDataParser.SOCIAL_NETWORKS_LIST selectedSocial = SocialNetworkDataParser.SOCIAL_NETWORKS_LIST.VK;
		if(view.equals(vk_auth)) selectedSocial = SocialNetworkDataParser.SOCIAL_NETWORKS_LIST.VK;
		else if(view.equals(facebook_auth)) selectedSocial = SocialNetworkDataParser.SOCIAL_NETWORKS_LIST.FACEBOOK;
		else if(view.equals(google_auth)) selectedSocial = SocialNetworkDataParser.SOCIAL_NETWORKS_LIST.GOOGLEP; 		
		
		socParser = new SocialNetworkDataParser(selectedSocial);
		socParser.showAuthDialog(this, this);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == GooglePAuth.REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK)
		{
			socParser.showAuthDialog(this, this);
		}
	}
}
