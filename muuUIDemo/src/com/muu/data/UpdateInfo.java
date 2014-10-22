package com.muu.data;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateInfo {
	public Boolean hasUpdate;
	public String updatePath;
	
	public UpdateInfo() {}
	
	public UpdateInfo(JSONObject json) {
		try {
			hasUpdate = json.getInt("code") == 0 ? true : false;
			updatePath = json.getString("url").replaceAll("\\\\", "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
