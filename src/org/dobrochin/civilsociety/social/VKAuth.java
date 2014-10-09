package org.dobrochin.civilsociety.social;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class VKAuth extends CurrentAuth {
	public VKAuth(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	private static final String WRAPPER = "response";
	private static final String FIRST_NAME = "first_name";
	private static final String LAST_NAME = "last_name";
	private static final String BIRTH_DATE = "bdate";
	private static final String COUNTRY = "country";
	private static final String CITY = "city";
	
	private static final int app_id = 4568700;
	private static final String api_version = "5.25";
	private static final String auth_redirect = "https://oauth.vk.com/blank.html";
	
	public static final String VK_AUTH_URL = "https://oauth.vk.com/authorize?client_id=$app_id&"+
			"scope=notify&"+"" +
			"redirect_uri=$auth_redirect &" +
			"display=mobile&"+ 
			"v=$api_ver&"+
			"response_type=token";
	public static final String VK_GET_PROFILE_INFO = "https://api.vk.com/method/account.getProfileInfo?&access_token=";
	@Override
	public String getName(JSONObject data) {
		String fullName = null;
		
		try {
			JSONObject profile = data.getJSONObject(WRAPPER);
			fullName = profile.getString(FIRST_NAME).concat(" ").concat(profile.getString(LAST_NAME));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fullName;
	}
	@Override
	protected String getAuthURL() {
		String url = VK_AUTH_URL;
		url = url.replace("$app_id", String.valueOf(app_id))
				.replace("$api_ver", api_version)
				.replace("$auth_redirect", auth_redirect);
		return url;
	}
	@Override
	protected String getRedirectURL() {
		return auth_redirect;
	}
	@Override
	protected String getAuthToken(String authResponse) {
		return getURLQueryParameter(authResponse, "access_token");
	}
	@Override
	protected String getProfileRequest(String token) {
		// TODO Auto-generated method stub
		return VK_GET_PROFILE_INFO.concat(token);
	}

}
