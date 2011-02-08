package jp.leafnet.droid.regist;

import java.util.Map;

public interface Register extends Runnable {

	public abstract void run();

	public abstract Map<String, String> getResultMap();

}