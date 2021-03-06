package main.java.org.baderlab.csapps.socialnetwork.twitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import main.java.org.baderlab.csapps.socialnetwork.Cytoscape;
  
/**
 * Methods and fields for manipulating Twitter data
 * @author Victor Kofia
 */
public class Twitter {
	private Map<String, String> oauthMap = null;
	final private String BASE_URL = "https://api.twitter.com/1.1/";
	final private String USER_AGENT = "Mozilla/5.0";

	private String createOAuthNonce() {
		SecureRandom random = new SecureRandom();
		String oAuthNonce = new BigInteger(130, random).toString(32);
		return oAuthNonce;
	}
	
	private String createOAuthSignature() {
		String oAuthSignature = "";
		return oAuthSignature;
	}
	
	private String createOAuthTimeStamp() {
		long oauthTimeStamp = System.currentTimeMillis() / 1000L;
		return Long.toString(oauthTimeStamp);
	}
	
	private String getOAuthToken() {
		String oAuthToken = "";
		return oAuthToken;
	}
	
	private Map<String, String> getOAuthMap() {
		if (this.oauthMap == null) {
			this.constructOAuthMap(this.createOAuthNonce(), 
								   this.createOAuthSignature(),
								   this.createOAuthTimeStamp(),
								   this.getOAuthToken());
		}
		return this.oauthMap;
	}
	
	private void setOAuthMap(Map<String, String> oauthMap) {
		this.oauthMap = oauthMap;
	}
 
	private String BuildOAuthHeader() {
		String header = "OAuth ";
		for (Entry<String, String> entry : this.getOAuthMap().entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			try {
				header += URLEncoder.encode(key, "UTF-8") + "\\\"" 
			                                              + URLEncoder.encode(value, "UTF-8") 
			                                              + "\\\",";
			} catch (UnsupportedEncodingException e) {
				//??
			}
		}
		// Remove last comma from string (cosmetic purposes)
		return header.substring(0, header.length() - 1);
	}
	
	private void constructOAuthMap(String oauthNonce, 
			                       String oauthSignature, 
			                       String oauthTimeStamp, 
			                       String oauthToken) {
		this.oauthMap = new HashMap<String, String>();
		oauthMap.put("oauth_consumer_key", "YMrFw7eXnL2GiNusZ4Pj3Q");
		oauthMap.put("oauth_nonce", oauthNonce);
		oauthMap.put("oauth_signature", oauthSignature);
		oauthMap.put("oauth_signature_method", "HMAC-SHA1");
		oauthMap.put("oauth_timestamp", oauthTimeStamp);
		oauthMap.put("oauth_token", oauthToken);
		oauthMap.put("oauth_version", "1.0");
	}
	
	private HttpURLConnection createRequest(String resourceURL, 
											String httpMethod) {
		
		try {
			URL obj = new URL(this.BASE_URL + resourceURL + ".json");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod(httpMethod);
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("X-HostCommonName", "api.twitter.com");
			con.setRequestProperty("Authorization", this.BuildOAuthHeader());
			con.setRequestProperty("Host", "api.twitter.com");
			con.setRequestProperty("X-Target-URI", "https://api.twitter.com");
			con.setRequestProperty("Connection", "Keep Alive");
			return con;
		} catch (MalformedURLException e) {
			Cytoscape.notifyUser("Twitter! DERP!!");
		} catch (IOException e) {
			Cytoscape.notifyUser("Twitter! DERP!!");
		}
		return null;
		
	}
 
	private void sendRequest(String resourceURL, String httpMethod) throws Exception {

		HttpURLConnection con = this.createRequest(resourceURL, httpMethod);
		int responseCode = con.getResponseCode();
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		// print result
		System.out.println(response.toString());
		
	}

	public static void main(String[] args) throws Exception {
		
		Twitter http = new Twitter();
		System.out.println("Testing 1 - Send a request");
		http.sendRequest("followers/ids", "GET");
		
	}
 
}
