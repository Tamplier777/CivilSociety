package org.dobrochin.civilsociety.social;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.dobrochin.civilsociety.BaseActivity;
import org.dobrochin.civilsociety.requests.RequestService;
import org.dobrochin.civilsociety.requests.models.PostTwitterData;
import org.dobrochin.civilsociety.requests.models.RequestWrapper;
import org.dobrochin.civilsociety.views.DialogWebView.AuthFinishListener;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class TwitterAuth extends CurrentAuth {
	public TwitterAuth(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	private static final String POST_REQUEST = "POST";
	private static final String GET_REQUEST = "GET";
	private static final String POST_REQUEST_TOKEN_URL = "https://api.twitter.com/oauth/request_token";
	private static final String AUTH_URL = "https://api.twitter.com/oauth/authorize?&oauth_token=$request_token";
	private static final String GET_TOKEN_URL = "https://api.twitter.com/oauth/access_token";
	private static final String GET_PROFILE_URL = "https://api.twitter.com/1.1/account/verify_credentials.json";
	
	private static final String REDIRECTED_URL = "http://apwsllc.com/prototypes/1/";
	private static final String URL_ENCODING = "utf-8";
	
	//как-то шифровать это дело надо, а то ж декомпилируется легко
	private static final String CONSUMER_KEY="AXuC4A6NnMJne6OJLjvEzinvZ";
	private static final String CONSUMER_SECRET = "7ef7EPeEPbT81nCtjJOXbF4y8SuGWAeRaRG7vEGHFjps0vonWD";
	
	private String oauth_request_token_secret;
	private String oauth_request_token;
	
	private String oauth_access_token_secret;
	private String oauth_access_token;
	
	private BaseActivity activity;
	AuthFinishListener listener;
	private SecureRandom random = new SecureRandom();
	@Override
	public String getName(JSONObject data) {
		// TODO Auto-generated method stub
		String name = "";
		try {
			name = data.getString("name");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}

	@Override
	protected String getProfileRequest(String token) {
		// TODO Auto-generated method stub
		return GET_PROFILE_URL.concat(token);
	}

	@Override
	protected String getAuthURL() {
		// TODO Auto-generated method stub
		return AUTH_URL.replace("$request_token", oauth_request_token);
	}

	@Override
	protected String getRedirectURL() {
		// TODO Auto-generated method stub
		return REDIRECTED_URL;
	}

	@Override
	protected String getAuthToken(String paramsJson) {
		// TODO Auto-generated method stub	
		String urlParams = "?".concat(transformJsonToUrlParams(paramsJson));
		return urlParams;
	}
	@Override
	public void setAdditionalParameters(String params) {
		// TODO Auto-generated method stub
		hideWaitDialog();
		if(oauth_request_token == null)
		{
			JSONObject obj;
			try {
				obj = new JSONObject(params);
				oauth_request_token_secret = obj.getString("oauth_token_secret");
				oauth_request_token = obj.getString("oauth_token");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//real show auth dialog
			super.showAuthDialog(activity, listener);
		}
		else if(oauth_access_token == null)
		{
			JSONObject obj;
			try {
				obj = new JSONObject(params);
				oauth_access_token_secret = obj.getString("oauth_token_secret");
				oauth_access_token = obj.getString("oauth_token");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//need to send get profile request
			PostTwitterData post = getRequestPostPairs();
			post.setOauth_token(oauth_access_token);
			String signature = createRequestSignature(post, GET_PROFILE_URL, GET_REQUEST, oauth_access_token_secret);
			post.setOauth_signature(signature);
			super.sendGetProfileRequest(activity, new GsonBuilder().disableHtmlEscaping().create().toJson(post));
		}
	}
	@Override
	public void showAuthDialog(BaseActivity activity,
			AuthFinishListener listener) {
		this.activity = activity;
		this.listener = listener;
		oauth_request_token = null;
		oauth_request_token_secret = null;
		oauth_access_token = null;
		oauth_access_token_secret = null;
		//actually at this class showAuthDialog - only preparing to show auth dialog
		showWaitDialog();
		PostTwitterData requestPairs = getRequestPostPairs();
		requestPairs.setOauth_callback(REDIRECTED_URL);
		String signature = createRequestSignature(requestPairs, POST_REQUEST_TOKEN_URL, POST_REQUEST, "");
		requestPairs.setOauth_signature(signature);
		
		sendAdditionalDataRequest(activity, POST_REQUEST_TOKEN_URL, requestPairs);
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
		String parameters = transformJsonToUrlParams(postStringJson);
		
		String encodedUrl = url;
		String encodedParameters = parameters;
		try {
			encodedUrl = URLEncoder.encode(url, URL_ENCODING);
			encodedParameters = URLEncoder.encode(parameters, URL_ENCODING);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String signatureBaseString = httpMethod.toUpperCase().concat("&")
				.concat(encodedUrl).concat("&").concat(encodedParameters);
		String consumerSecret = CONSUMER_SECRET;
		
		try {
			consumerSecret = URLEncoder.encode(consumerSecret, URL_ENCODING);
			tokenSecret = URLEncoder.encode(tokenSecret, URL_ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String signingKey = consumerSecret.concat("&").concat(tokenSecret);
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
	
	@Override
	public void sendGetProfileRequest(BaseActivity activity, String authData) {
		
			//first step - get access token
			showWaitDialog();
			PostTwitterData requestPairs = getRequestPostPairs();
			String verifier = getURLQueryParameter(authData, "\\?", "oauth_verifier");
			requestPairs.setOauth_verifier(verifier);
			requestPairs.setOauth_token(oauth_request_token);
			String signature = createRequestSignature(requestPairs, GET_TOKEN_URL, POST_REQUEST, oauth_request_token_secret);
			requestPairs.setOauth_signature(signature);
			
			sendAdditionalDataRequest(activity, GET_TOKEN_URL, requestPairs);
	}
	private void sendAdditionalDataRequest(BaseActivity activity, String url, PostTwitterData pairs)
	{
		Intent intent = new Intent();
		intent.putExtra(RequestService.REQUEST_TYPE, RequestService.REQUEST_GET_ADITIONAL_SN_INFORMATION);
		intent.putExtra(RequestService.SOCIAL_NETWORK_URL, url);
		intent.putExtra(RequestService.REQUEST_JSON_POST, new GsonBuilder().disableHtmlEscaping().create().toJson(pairs));
		activity.sendRequest(intent);
	}
	private String transformJsonToUrlParams(String json)
	{
		Type stringStringMap = new TypeToken<TreeMap<String, String>>(){}.getType();
		TreeMap<String, String> tmPost = new Gson().fromJson(json, stringStringMap);
		String parameters = "";
		for(Entry<String, String> entry: tmPost.entrySet())
		{
			if(entry.getValue() != null && !entry.getValue().equals(""))
			{
				if(!parameters.equals("")) parameters = parameters.concat("&");
				String encodedKey = entry.getKey();
				String encodedValue = entry.getValue();
				try {
					encodedKey = URLEncoder.encode(encodedKey, URL_ENCODING);
					encodedValue = URLEncoder.encode(encodedValue, URL_ENCODING);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				parameters = parameters.concat(encodedKey).concat("=").concat(encodedValue);
			}
		}
		return parameters;
	}
	@Override
	protected boolean isNeedFakeUserAgent() { //this method only required for stupid twitter. twitter cant work with android firefox. but can with random string... kill them all
		// TODO Auto-generated method stub
		return true;
	}
}
