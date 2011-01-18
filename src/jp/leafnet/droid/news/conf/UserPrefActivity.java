package jp.leafnet.droid.news.conf;

import jp.leafnet.droid.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class UserPrefActivity extends PreferenceActivity {

	public void onCreate(Bundle savedInstanceState) {
		this.setTheme(android.R.style.Theme_Black);
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.layout.userprefs);
	}
}
