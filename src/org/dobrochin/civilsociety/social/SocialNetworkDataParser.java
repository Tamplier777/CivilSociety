package org.dobrochin.civilsociety.social;

import org.dobrochin.civilsociety.requests.URL;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class SocialNetworkDataParser {
	
	//Социальные сети:
	public enum SOCIAL_NETWORKS_LIST {VK, FACEBOOK, GOOGLEP, TWITTER, LINKEDLN, MAILRU, YANDEX, ODNOKLASSNIKI};
	
	private JSONObject profileData;
	private SOCIAL_NETWORKS_LIST networkName;
	private CurrentAuth currentParser;
	public SocialNetworkDataParser(SOCIAL_NETWORKS_LIST network)
	{
		networkName = network;
		switch(network)
		{
			case VK:
				currentParser = new VKAuth();
				break;
			case FACEBOOK:
				currentParser = new FacebookAuth();
			default:
				break;
		}
	}
	public void setSNResponse(String response) throws JSONException
	{
		profileData = new JSONObject(response);
	}
	public String getName()
	{
		return currentParser.getName(profileData);
	}
	public String getAuthUrl(Context context)
	{
		return currentParser.getAuthURL();
	}
	public String getAuthRedirectUrl()
	{
		return currentParser.getRedirectURL();
	}
	public String getAuthToken(String authResponse)
	{
		return currentParser.getAuthToken(authResponse);
	}
}
