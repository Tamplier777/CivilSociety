package org.dobrochin.civilsociety.requests.models;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;

import com.google.gson.Gson;

public class RequestWrapper {
	public static String wrapRequest(String taskName, Object data, boolean dataInArray) throws JSONException
	{
		if(dataInArray) data = new Object[]{data};
		JSONObject logJson = new JSONObject();
		if(dataInArray) logJson.put(taskName, new JSONArray(new Gson().toJson(data)));
		else logJson.put(taskName, new JSONObject(new Gson().toJson(data)));
		return logJson.toString();
	}
	public static final String md5(final String s) {
	    try {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest
	                .getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();
	 
	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i = 0; i < messageDigest.length; i++) {
	            String h = Integer.toHexString(0xFF & messageDigest[i]);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        return hexString.toString();
	 
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return "";
	}
	public static String sha1(String msg, String keyString) throws 
	UnsupportedEncodingException, NoSuchAlgorithmException, 
	InvalidKeyException
	{
		SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(key);
			
		byte[] bytes = mac.doFinal(msg.getBytes("UTF-8"));
		
		return new String( Base64.encodeToString(bytes, Base64.NO_WRAP) );
	}
}
