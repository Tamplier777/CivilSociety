package org.dobrochin.civilsociety.social;

import org.dobrochin.civilsociety.BaseActivity;
import org.dobrochin.civilsociety.requests.RequestService;
import org.dobrochin.civilsociety.requests.models.PostTwitterData;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.GsonBuilder;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LinkedInAuth extends CurrentAuth {
	private static final String DIALOG_URL = "https://www.linkedin.com/uas/oauth2/authorization?response_type=code&" +
			"client_id=$api_key&" +
			"scope=r_fullprofile&" +
			"state=STATE&" +
			"redirect_uri=$redirect_url";
	private static final String  GET_ACCESS_TOKEN_URL = "https://www.linkedin.com/uas/oauth2/accessToken?" +
			"grant_type=authorization_code&" +
			"code=$auth_code&" +
			"redirect_uri=$redirect_url&" +
			"client_id=$api_key&" +
			"client_secret=$secret_key&" +
			"format=json";
	
	private static final String GET_PRIFILE_URL = "https://api.linkedin.com/v1/people/~?oauth2_access_token=$token&format=json";

	private static final String REDIRECT_URL = "http://localhost/";
	private static final String API_KEY = "77ll4zbb3h5xfm";
	private static final String SECRET_KEY = "Gj4layJBZJCLig3p";
	private String access_token;
	private BaseActivity activity;
	public LinkedInAuth(Context context)
	{
		super(context);
	}
	@Override
	public String getName(JSONObject data) {
		// TODO Auto-generated method stub
		String name = "";
		try {
			name = data.getString("firstName").concat(" ").concat(data.getString("lastName"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		hideWaitDialog();
		return name;
	}

	@Override
	protected String getProfileRequest(String token) {
		// TODO Auto-generated method stub
		return GET_PRIFILE_URL.replace("$token", token);
	}

	@Override
	protected String getAuthURL() {
		// TODO Auto-generated method stub
		return DIALOG_URL.replace("$redirect_url", REDIRECT_URL).replace("$api_key", API_KEY);
	}

	@Override
	protected String getRedirectURL() {
		// TODO Auto-generated method stub
		return REDIRECT_URL;
	}

	@Override
	protected String getAuthToken(String authResponse) {
		// TODO Auto-generated method stub
		return authResponse;
	}
	
	@Override
	public void sendGetProfileRequest(BaseActivity activity, String authData) {
		//not so fast, we have just code, no token... stupid... whether there is a real standard without developer inventions?
		showWaitDialog();
		this.activity = activity;
		String code = getURLQueryParameter(authData, "\\?", "code");
		if(code != null)
		{
			String url = GET_ACCESS_TOKEN_URL.replace("$auth_code", code).replace("$redirect_url", REDIRECT_URL).
					replace("$api_key", API_KEY).replace("$secret_key", SECRET_KEY);
			Intent intent = new Intent();
			intent.putExtra(RequestService.REQUEST_TYPE, RequestService.REQUEST_GET_ADITIONAL_SN_INFORMATION);
			intent.putExtra(RequestService.SOCIAL_NETWORK_URL, url);
			activity.sendRequest(intent);
		} else
		{
			hideWaitDialog();
			activity.showAlertDialog("Не удалось авторизоваться");
		}
	}
	@Override
	public void setAdditionalParameters(String params) {
		try {
			access_token = new JSONObject(params).getString("access_token");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(access_token != null) super.sendGetProfileRequest(activity, access_token);
	}
}
