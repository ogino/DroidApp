package jp.leafnet.droid.twitter.bitly;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

public class Bitly {

	private Logger logger = Logger.getLogger(Bitly.class.getPackage().getName());
	private String login;
	private String apiKey;

	public Bitly() {
		this.logger.setLevel(Level.WARNING);
		this.login = "leafnet";
		this.apiKey = "R_07bf560c9a5790daa4ee6f7da2a1d442";
	}

	public Bitly(final String login, final String apiKey) {
		this.logger.setLevel(Level.WARNING);
		this.login = login;
		this.apiKey = apiKey;
	}

	public String createUrl(final String url) {
		String shortUrl = null;
		JSONObject rootJSON = this.createJSONObject(url);
		if (rootJSON == null) return null;
		try {
			JSONObject dataJSON = rootJSON.getJSONObject("data");
			shortUrl = dataJSON.getString("url");
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getLocalizedMessage());
		}
		return shortUrl;
	}

	private JSONObject createJSONObject(final String url) {
		JSONObject json = null;
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(createShotenUrl(url));
		try {
			HttpResponse response = client.execute(get);
			String jsonText = createJSONText(response.getEntity().getContent());
			json = new JSONObject(jsonText);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getLocalizedMessage());
		}
		return json;
	}
	
	private String createJSONText(InputStream content) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		StringBuffer buffer = new StringBuffer();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
				buffer.append("\n");
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getLocalizedMessage());
		}
		return buffer.toString();
	}

	private static final String SHOTEN_FORMAT = "http://api.bit.ly/v3/shorten?login=%s&apiKey=%s&longUrl=%s&format=json";

	private String createShotenUrl(final String longUrl) {
		String url = null;
		try {
			String encURL = URLEncoder.encode(longUrl, "UTF-8");
			url = String.format(SHOTEN_FORMAT, this.login, this.apiKey, encURL);
		} catch (UnsupportedEncodingException e) {
			logger.log(Level.SEVERE, e.getLocalizedMessage());
		}
		return url;
	}
}
