package org.dobrochin.civilsociety.social;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class FacebookAuth extends CurrentAuth {
	public FacebookAuth(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private static final String RESPONSE_FIELD_FIRST_NAME = "first_name";
	private static final String RESPONSE_FIELD_GENDER = "gender";
	private static final String RESPONSE_FIELD_LAST_NAME = "last_name";
	private static final String RESPONSE_FIELD_FULL_NAME = "name";
	private static final String RESPONSE_FIELD_LOCATION = "location";
	private static final String RESPONSE_FIELD_HOMETOWN = "hometown";
	private static final String RESPONSE_FIELD_BIRTHDAY = "birthday";
	private static final String FACEBOOK_AUTH_URL = "https://www.facebook.com/dialog/oauth?" +
			"client_id=$client_id&" +
			"redirect_uri=$redirect_url&" +
			"display=touch&" +
			"response_type=token&" +
			"scope=public_profile,user_birthday,user_location,user_hometown";
	private static final String redirectedUrl = "https://www.facebook.com/connect/login_success.html";
	private static final String getProfileUrl = "https://graph.facebook.com/v2.1/me?access_token=";
	private static final String app_id = "680852062011488";
	@Override
	public String getName(JSONObject data) {
		String fullName = null;
		
		try {
			fullName = data.getString(RESPONSE_FIELD_FULL_NAME);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fullName;
	}

	@Override
	protected String getAuthURL() {
		// TODO Auto-generated method stub
		String url = FACEBOOK_AUTH_URL.replace("$client_id", app_id).replace("$redirect_url", redirectedUrl);
		return url;
	}

	@Override
	protected String getRedirectURL() {
		// TODO Auto-generated method stub
		return redirectedUrl;
	}

	@Override
	protected String getAuthToken(String authResponse) {
		return getURLQueryParameter(authResponse, "access_token");
	}

	@Override
	protected String getProfileRequest(String token) {
		// TODO Auto-generated method stub
		return getProfileUrl.concat(token);
	}

}
