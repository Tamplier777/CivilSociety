package org.dobrochin.civilsociety.social;

import org.json.JSONObject;

import android.content.Context;

public interface CurrentParser {
	public String getName(JSONObject data);
	public String getAuthURL();
	public String getRedirectURL();
	public String getAuthToken(String authResponse);
}
