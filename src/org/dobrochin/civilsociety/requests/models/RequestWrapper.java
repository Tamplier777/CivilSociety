package org.dobrochin.civilsociety.requests.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
}
