package org.dobrochin.civilsociety.social;

import org.dobrochin.civilsociety.BaseActivity;
import org.dobrochin.civilsociety.requests.RequestService;
import org.dobrochin.civilsociety.views.DialogWebView;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

public abstract class CurrentAuth {
	private ProgressDialog mConnectionProgressDialog;
	public CurrentAuth(Context context)
	{
		mConnectionProgressDialog = new ProgressDialog(context);
        mConnectionProgressDialog.setMessage("Пробуем подключиться...");
	}
	protected void showWaitDialog()
	{
		mConnectionProgressDialog.show();
	}
	protected void hideWaitDialog()
	{
		mConnectionProgressDialog.dismiss();
	}
	public abstract String getName(JSONObject data);
	protected abstract String getProfileRequest(String token);
	protected abstract String getAuthURL();
	protected abstract String getRedirectURL();
	protected abstract String getAuthToken(String authResponse);
	protected boolean isNeedFakeUserAgent(){return false;}
	public void setAdditionalParameters(String params)
	{
		//do something if it necessary in extended classes
	}
	public void showAuthDialog(BaseActivity activity, DialogWebView.AuthFinishListener listener)
	{
		DialogWebView dwv = new DialogWebView(activity, getRedirectURL(), listener);
		dwv.setURL(getAuthURL(), isNeedFakeUserAgent());
		dwv.show();
	}
	public void sendGetProfileRequest(BaseActivity activity, String authData)
	{
		sendGetProfileRequest(activity, authData, null);
	}
	public void sendGetProfileRequest(BaseActivity activity, String authData, String postJson)
	{
		Intent intent = new Intent();
		intent.putExtra(RequestService.REQUEST_TYPE, RequestService.REQUEST_GET_SOCIAL_PROFILE);
		String getProfileRequest = getProfileRequest(getAuthToken(authData));
		intent.putExtra(RequestService.SOCIAL_NETWORK_URL, getProfileRequest);
		intent.putExtra(RequestService.REQUEST_JSON_POST, postJson);
		activity.sendRequest(intent);
	}
	public String getURLQueryParameter(String query, String parameterName)
	{
		return getURLQueryParameter(query, "#", parameterName);
	}
	public String getURLQueryParameter(String query, String queryDivider, String parameterName)
	{
		String authPart = query.split(queryDivider)[1];
		String authParams[] = authPart.split("&");
		for(int i=0; i < authParams.length; i++)
		{
			String keyValuePairs[] = authParams[i].split("=");
			if(keyValuePairs[0].equals(parameterName)) return keyValuePairs[1];
		}
		return null;
	}
}
