package org.dobrochin.civilsociety.social;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class YandexAuth extends CurrentAuth {
	private static final String DIALOG_URL = "https://oauth.yandex.ru/authorize?response_type=token&" +
			"client_id=$app_id&" +
			"display=popup";
	private static final String app_id = "d5c71e9907b1472ca32816ef8e19d960";
	private static final String REDIRECT_URL = "androidapp://token";
	private static final String GET_PROFILE_URL = "https://login.yandex.ru/info?format=json&oauth_token=$token";
	
	public YandexAuth(Context context)
	{
		super(context);
	}

	@Override
	public String getName(JSONObject data) {
		String name = "";
		try {
			name = data.getString("real_name");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}

	@Override
	protected String getProfileRequest(String token) {
		// TODO Auto-generated method stub
		return GET_PROFILE_URL.replace("$token", token);
	}

	@Override
	protected String getAuthURL() {
		// TODO Auto-generated method stub
		return DIALOG_URL.replace("$app_id", app_id);
	}

	@Override
	protected String getRedirectURL() {
		// TODO Auto-generated method stub
		return REDIRECT_URL;
	}

	@Override
	protected String getAuthToken(String authResponse) {
		// TODO Auto-generated method stub
		return getURLQueryParameter(authResponse, "access_token");
	}

}
