package com.muu.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityEventInfo {
	public String imgUrl;
	public String activityUrl;
	public String activityName;
	
	public ActivityEventInfo(JSONObject activityInfo) {
		try {
			imgUrl = activityInfo.getString("img");
			activityUrl = activityInfo.getString("link");
			activityName = activityInfo.getString("title");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
