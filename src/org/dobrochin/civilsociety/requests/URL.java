package org.dobrochin.civilsociety.requests;

public class URL {
	public static final String BASE_URL = "http://apwsllc.com/prototypes/1/";
	
	//Социальные сети:
	public enum SOCIAL_NETWORKS_LIST {VK, FACEBOOK, GOOGLEP, TWITTER, LINKEDLN, MAILRU, YANDEX, ODNOKLASSNIKI};
	
	public static final String VK_AUTH_URL = "https://oauth.vk.com/authorize?client_id=$app_id&"+
			"scope=notify&"+"" +
			"redirect_uri=$auth_redirect &" +
			"display=mobile&"+ 
			"v=$api_ver&"+
			"response_type=token";
	public static final String VK_GET_PROFILE_INFO = "https://api.vk.com/method/account.getProfileInfo?&access_token=$token";
	
	
	public static final String FACEBOOK_AUTH_URL = "";
	public static final String GOOGLEP_AUTH_URL = "";
	public static final String TWITTER_AUTH_URL = "";
	public static final String LINKEDLN_AUTH_URL = "";
	public static final String MAILRU_AUTH_URL = "";
	public static final String YANDEX_AUTH_URL = "";
	public static final String ODNOKLASSNIKI_AUTH_URL = "";
}
