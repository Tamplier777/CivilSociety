package org.dobrochin.civilsociety.social;

import org.dobrochin.civilsociety.R;
import org.dobrochin.civilsociety.requests.URL;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class VKParser implements CurrentParser {
	private static final String WRAPPER = "response";
	private static final String FIRST_NAME = "first_name";
	private static final String LAST_NAME = "last_name";
	private static final String BIRTH_DATE = "bdate";
	private static final String COUNTRY = "country";
	private static final String CITY = "city";
	
	private static final int app_id = 4568700;
	private static final String api_version = "5.25";
	private static final String auth_redirect = "https://oauth.vk.com/blank.html";
	@Override
	public String getName(JSONObject data) {
		String fullName = null;
		
		try {
			JSONObject profile = data.getJSONObject(WRAPPER);
			fullName = profile.getString(FIRST_NAME) + profile.getString(LAST_NAME);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fullName;
	}
	@Override
	public String getAuthURL() {
		String url = URL.VK_AUTH_URL;
		url = url.replace("$app_id", String.valueOf(app_id))
				.replace("$api_ver", api_version)
				.replace("$auth_redirect", auth_redirect);
		return url;
	}
	@Override
	public String getRedirectURL() {
		return auth_redirect;
	}
	@Override
	public String getAuthToken(String authResponse) {
		String authPart = authResponse.split("#")[1];
		String authParams[] = authPart.split("&");
		for(int i=0; i < authParams.length; i++)
		{
			String keyValuePairs[] = authParams[i].split("=");
			if(keyValuePairs[0].equals("access_token")) return keyValuePairs[1];
		}
		return null;
	}

}
