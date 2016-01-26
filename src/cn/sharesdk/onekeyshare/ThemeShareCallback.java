package cn.sharesdk.onekeyshare;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;

public interface ThemeShareCallback {
	public void doShare(HashMap<Platform, HashMap<String, Object>> shareData);
}
