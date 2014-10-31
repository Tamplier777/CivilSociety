package org.dobrochin.civilsociety.social;

import java.util.Arrays;

import org.dobrochin.civilsociety.requests.models.RequestWrapper;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class MailRuAuth extends CurrentAuth {
	public static final String DIALOG_URL = "https://connect.mail.ru/oauth/authorize?client_id=$app_id&" +
			"response_type=token&" +
			"display=mobile&" +
			"redirect_uri=$redirect_url";
	public static final String REDIRECT_URL = "http://connect.mail.ru/oauth/success.html";
	public static final String app_id = "726368";
	public static final String get_profile_url = "http://www.appsmail.ru/platform/api?method=users.getInfo&" +
			"app_id=$app_id&" +
			"session_key=$token&" +
			"secure=0";
	public static final String PRIVATE_KEY = "880cf774ef9e46160e0759196dc257d0";

	public String userid;
	public MailRuAuth(Context context)
	{
		super(context);
	}
	@Override
	public String getName(JSONObject data) {
		// TODO Auto-generated method stub
		String name = "";
		try {
			name = data.getString("first_name").concat(" ").concat(data.getString("last_name"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}

	@Override
	protected String getProfileRequest(String token) {
		String url = get_profile_url.replace("$app_id", app_id).replace("$token", token);
		String signature = getSignature(url, token);
		// TODO Auto-generated method stub
		return url.concat("&sig=").concat(signature);
	}

	@Override
	protected String getAuthURL() {
		// TODO Auto-generated method stub
		return DIALOG_URL.replace("$app_id", app_id).replace("$redirect_url", REDIRECT_URL);
	}

	@Override
	protected String getRedirectURL() {
		// TODO Auto-generated method stub
		return REDIRECT_URL;
	}

	@Override
	protected String getAuthToken(String authResponse) {
		// TODO Auto-generated method stub
		userid = getURLQueryParameter(authResponse,"x_mailru_vid");
		return getURLQueryParameter(authResponse, "access_token");
	}
	
	private String getSignature(String url, String token)
	{
		String urlParts[] = url.split("\\?");
		String params[] = urlParts[1].split("&");
		Arrays.sort(params);
		String signature = "";
		for(int i=0; i < params.length; i++) signature +=params[i];
		
		signature = RequestWrapper.md5(userid + signature + PRIVATE_KEY);
		return signature;
	}
}
