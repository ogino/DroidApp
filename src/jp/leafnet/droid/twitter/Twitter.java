package jp.leafnet.droid.twitter;

import java.net.URLEncoder;

import jp.leafnet.droid.twitter.bitly.Bitly;
import jp.leafnet.droid.util.StringUtil;

public class Twitter {

	private String title;
	private String url;
	private String hashTag;
	private Bitly bitLy;

	private static final String URL_FORMAT = "https://mobile.twitter.com/login?redirect_after_login=%s";
	private static final String STATUS_FORMAT = "/home?status=%s";
	private static final String TWEER_FORMAT = "%s %s %s";

	public Twitter(final String title, final String url, final String hashTag) {
		this.title = title;
		this.url = url;
		this.hashTag = hashTag;
		this.bitLy = new Bitly();
	}

	public String createTweetUrl() {
		String url = this.bitLy.createUrl(this.url);
		if (!StringUtil.isEmpty(url)) this.url = url;
		String tweet = URLEncoder.encode(String.format(TWEER_FORMAT, this.title, this.url, this.hashTag));
		String status = URLEncoder.encode(String.format(STATUS_FORMAT, tweet));
		return String.format(URL_FORMAT, status);
	}
}
