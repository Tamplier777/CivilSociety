package org.dobrochin.civilsociety.social;

import org.json.JSONObject;

public class FacebookAuth implements CurrentAuth {
	private static final String FACEBOOK_AUTH_URL = "https://www.facebook.com/dialog/oauth?" +
			"client_id=$client_id&" +
			"redirect_uri=$redirect_url&" +
			"response_type=token";
	private static final String redirectedUrl = "https://www.facebook.com/connect/login_success.html";
	private static final String app_id = "680852062011488";
	@Override
	public String getName(JSONObject data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAuthURL() {
		// TODO Auto-generated method stub
		String url = FACEBOOK_AUTH_URL.replace("$client_id", app_id).replace("$redirect_url", redirectedUrl);
		return url;
	}

	@Override
	public String getRedirectURL() {
		// TODO Auto-generated method stub
		return redirectedUrl;
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
