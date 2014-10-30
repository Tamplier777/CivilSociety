package org.dobrochin.civilsociety.social;

import org.dobrochin.civilsociety.requests.models.RequestWrapper;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class OdnoklassnikiAuth extends CurrentAuth {
	
	private static final String DIALOG_URL = "http://www.odnoklassniki.ru/oauth/authorize?" +
			"client_id=$clientId&scope=&" +
			"response_type=token&" +
			"redirect_uri=$redirectUri&" +
			"layout=m";
	private static final String app_id = "1106965760";
	private static final String application_public_key = "CBAGPLPCEBABABABA";
	private static final String redirect_url = "okauth://ok" + app_id;
	private static final String url_get_profile = "http://api.odnoklassniki.ru/fb.do?application_key=$publicKey&" +
			"method=users.getCurrentUser&" +
			"access_token=$access_token&" +
			"format=json&" +
			"sig=$signature";
	
	private String sessionSecretKey;
	private String signatre = "application_key="+application_public_key+"format=jsonmethod=users.getCurrentUser";
	public OdnoklassnikiAuth(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String getName(JSONObject data) {
		// TODO Auto-generated method stub
		String name = "";
		try {
			name = data.getString("name");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}

	@Override
	protected String getProfileRequest(String token) {
		// TODO Auto-generated method stub
		String md5Signature = RequestWrapper.md5(signatre.concat(sessionSecretKey));
		return url_get_profile.replace("$publicKey", application_public_key).replace("$access_token", token).replace("$signature", md5Signature);
	}

	@Override
	protected String getAuthURL() {
		// TODO Auto-generated method stub
		return DIALOG_URL.replace("$clientId", app_id).replace("$redirectUri", redirect_url);
	}

	@Override
	protected String getRedirectURL() {
		// TODO Auto-generated method stub
		return redirect_url;
	}

	@Override
	protected String getAuthToken(String authResponse) {
		// TODO Auto-generated method stub
		sessionSecretKey = getURLQueryParameter(authResponse, "session_secret_key");
		return getURLQueryParameter(authResponse, "access_token");
	}

}
