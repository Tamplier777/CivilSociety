package org.dobrochin.civilsociety.social;

import org.dobrochin.civilsociety.BaseActivity;
import org.dobrochin.civilsociety.views.DialogWebView;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

public class SocialNetworkDataParser {
	
	//Социальные сети:
	public enum SOCIAL_NETWORKS_LIST {VK, FACEBOOK, GOOGLEP, TWITTER, LINKEDLN, MAILRU, YANDEX, ODNOKLASSNIKI};
	
	private JSONObject profileData;
	private CurrentAuth currentParser;
	private SOCIAL_NETWORKS_LIST currentNetwork;
	public SocialNetworkDataParser(SOCIAL_NETWORKS_LIST network)
	{
		currentNetwork = network;
		switch(network)
		{
			case VK:
				currentParser = new VKAuth();
				break;
			case FACEBOOK:
				currentParser = new FacebookAuth();
				break;
			case GOOGLEP:
				currentParser = new GooglePAuth();
				break;
			case TWITTER:
				currentParser = new TwitterAuth();
				break;
			default:
				break;
		}
	}
	public void showAuthDialog(BaseActivity activity, DialogWebView.AuthFinishListener listener)
	{
		currentParser.showAuthDialog(activity, listener);
	}
	public void sendGetProfileRequest(BaseActivity activity, String authData)
	{
		currentParser.sendGetProfileRequest(activity, authData);
	}
	public void setSNResponse(String response) throws JSONException
	{
		profileData = new JSONObject(response);
	}
	public String getName()
	{
		return currentParser.getName(profileData);
	}
}
