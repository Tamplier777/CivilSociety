package org.dobrochin.civilsociety.social;

import org.dobrochin.civilsociety.BaseActivity;
import org.dobrochin.civilsociety.requests.RequestService;
import org.dobrochin.civilsociety.views.DialogWebView;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;

public abstract class CurrentAuth {
	public abstract String getName(JSONObject data);
	protected abstract String getProfileRequest(String token);
	protected abstract String getAuthURL();
	protected abstract String getRedirectURL();
	protected abstract String getAuthToken(String authResponse);
	public void showAuthDialog(BaseActivity activity, DialogWebView.AuthFinishListener listener)
	{
		DialogWebView dwv = new DialogWebView(activity, getRedirectURL(), listener);
		dwv.setURL(getAuthURL());
		dwv.show();
	}
	public void sendGetProfileRequest(BaseActivity activity, String authData)
	{
		Intent intent = new Intent();
		intent.putExtra(RequestService.REQUEST_TYPE, RequestService.REQUEST_GET_SOCIAL_PROFILE);
		String getProfileRequest = getProfileRequest(getAuthToken(authData));
		intent.putExtra(RequestService.SOCIAL_NETWORK_GET_PROFILE_REQUEST, getProfileRequest);
		activity.sendRequest(intent);
	}
}
