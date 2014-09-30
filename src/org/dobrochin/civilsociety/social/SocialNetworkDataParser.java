package org.dobrochin.civilsociety.social;

import org.dobrochin.civilsociety.requests.URL;
import org.dobrochin.civilsociety.requests.URL.SOCIAL_NETWORKS_LIST;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class SocialNetworkDataParser {
	private JSONObject profileData;
	private URL.SOCIAL_NETWORKS_LIST networkName;
	private CurrentParser currentParser;
	public SocialNetworkDataParser(URL.SOCIAL_NETWORKS_LIST network)
	{
		networkName = network;
		switch(network)
		{
			case VK:
				currentParser = new VKParser();
				break;
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
