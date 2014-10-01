package org.dobrochin.civilsociety.social;

import org.json.JSONObject;

import android.content.Context;

public interface CurrentAuth {
	public String getName(JSONObject data);
	public String getProfileRequest(String token);
	public String getAuthURL();
	public String getRedirectURL();
	public String getAuthToken(String authResponse);
}
