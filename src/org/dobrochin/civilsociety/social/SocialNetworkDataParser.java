package org.dobrochin.civilsociety.social;

import org.dobrochin.civilsociety.BaseActivity;
import org.dobrochin.civilsociety.BaseActivity.OnRequestFailedListener;
import org.dobrochin.civilsociety.requests.RequestService;
import org.dobrochin.civilsociety.views.DialogWebView;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class SocialNetworkDataParser implements OnRequestFailedListener{
	
	//Социальные сети:
	public enum SOCIAL_NETWORKS_LIST {VK, FACEBOOK, GOOGLEP, TWITTER, LINKEDIN, MAILRU, YANDEX, ODNOKLASSNIKI};
	
	private JSONObject profileData;
	private CurrentAuth currentParser;
	private SOCIAL_NETWORKS_LIST currentNetwork;
	public SocialNetworkDataParser(Context context, SOCIAL_NETWORKS_LIST network)
	{
		currentNetwork = network;
		switch(network)
		{
			case VK:
				currentParser = new VKAuth(context);
				break;
			case FACEBOOK:
				currentParser = new FacebookAuth(context);
				break;
			case GOOGLEP:
				currentParser = new GooglePAuth(context);
				break;
			case TWITTER:
				currentParser = new TwitterAuth(context);
				break;
			case ODNOKLASSNIKI:
				currentParser = new OdnoklassnikiAuth(context);
				break;
			case MAILRU:
				currentParser = new MailRuAuth(context);
				break;
			case YANDEX:
				currentParser = new YandexAuth(context);
				break;
			case LINKEDIN:
				currentParser = new LinkedInAuth(context);
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
		response = response.replace("[", "");
		response = response.replace("]", "");
		profileData = new JSONObject(response);
	}
	public String getName()
	{
		return currentParser.getName(profileData);
	}
	public void setAdditionalParameters(String params)
	{
		currentParser.setAdditionalParameters(params);
	}
	@Override
	public void onRequestFailed(int requestType) {
		// TODO Auto-generated method stub
		if(requestType == RequestService.REQUEST_GET_ADITIONAL_SN_INFORMATION) currentParser.hideWaitDialog();
	}
}
