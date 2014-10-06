package org.dobrochin.civilsociety.social;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.dobrochin.civilsociety.BaseActivity;
import org.dobrochin.civilsociety.requests.RequestService;
import org.dobrochin.civilsociety.requests.models.PostTwitterData;
import org.dobrochin.civilsociety.requests.models.RequestWrapper;
import org.dobrochin.civilsociety.views.DialogWebView.AuthFinishListener;
import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class TwitterAuth extends CurrentAuth {
	private static final String POST_REQUEST = "POST";
	private static final String POST_REQUEST_TOKEN_URL = "https://api.twitter.com/oauth/request_token";
	
	//как-то шифровать это дело надо, а то ж декомпилируется легко
	private static final String CONSUMER_KEY="AXuC4A6NnMJne6OJLjvEzinvZ";
	private static final String CONSUMER_SECRET = "7ef7EPeEPbT81nCtjJOXbF4y8SuGWAeRaRG7vEGHFjps0vonWD";
	
	private SecureRandom random = new SecureRandom();
	@Override
	public String getName(JSONObject data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getProfileRequest(String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getAuthURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getRedirectURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getAuthToken(String authResponse) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void showAuthDialog(BaseActivity activity,
			AuthFinishListener listener) {
		PostTwitterData requestPairs = getRequestPostPairs();
		requestPairs.setOauth_callback("oob");
		String signature = createRequestSignature(requestPairs, POST_REQUEST_TOKEN_URL, POST_REQUEST, "");
		Log.i("wtf", signature);
		requestPairs.setOauth_signature(signature);
		Intent intent = new Intent();
		intent.putExtra(RequestService.REQUEST_TYPE, RequestService.REQUEST_GET_REQUEST_TOKEN);
		intent.putExtra(RequestService.SOCIAL_NETWORK_URL, POST_REQUEST_TOKEN_URL);
		intent.putExtra(RequestService.REQUEST_JSON_POST, new GsonBuilder().disableHtmlEscaping().create().toJson(requestPairs));
		activity.sendRequest(intent);
	}
	public PostTwitterData getRequestPostPairs()
	{
		PostTwitterData post = new PostTwitterData(getNextRequestId(), CONSUMER_KEY);
		return post;
	}
	private String getNextRequestId()
	{
		return new BigInteger(130, random).toString(32);
	}
	private String createRequestSignature(PostTwitterData post, String url, String httpMethod, String tokenSecret)
	{
		Gson gson = new Gson();
		
		//sort keys alphabetically
		String postStringJson = gson.toJson(post);
		Type stringStringMap = new TypeToken<TreeMap<String, String>>(){}.getType();
		TreeMap<String, String> tmPost = gson.fromJson(postStringJson, stringStringMap);
		
		String parameters = "";
		for(Entry<String, String> entry: tmPost.entrySet())
		{
			if(entry.getValue() != null && !entry.getValue().equals(""))
			{
				if(!parameters.equals("")) parameters = parameters.concat("&");
				String encodedKey = entry.getKey();
				String encodedValue = entry.getValue();
				try {
					encodedKey = URLEncoder.encode(encodedKey, "utf-8");
					encodedValue = URLEncoder.encode(encodedValue, "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				parameters = parameters.concat(encodedKey).concat("=").concat(encodedValue);
			}
		}
		
		String encodedUrl = url;
		String encodedParameters = parameters;
		try {
			encodedUrl = URLEncoder.encode(url, "utf-8");
			encodedParameters = URLEncoder.encode(parameters, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String signatureBaseString = httpMethod.toUpperCase().concat("&")
				.concat(encodedUrl).concat("&").concat(encodedParameters);
		String consumerSecret = CONSUMER_SECRET;
		
		try {
			consumerSecret = URLEncoder.encode(consumerSecret, "utf-8");
			tokenSecret = URLEncoder.encode(tokenSecret, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String signingKey = consumerSecret.concat("&").concat(tokenSecret);
		Log.i("wtf", signingKey);
		String signature = null;
		try {
			signature = RequestWrapper.sha1(signatureBaseString, signingKey);
			
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return signature;
	}
	
	
}
