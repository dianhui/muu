package com.muu.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Top2CartoonInfo extends CartoonInfo {
	public String recommendCover;
	
	public Top2CartoonInfo(JSONObject cartoonInfo) {
		try {
			id = cartoonInfo.getInt("id");
			updateDate = cartoonInfo.getString("updatedTime");
			isComplete = cartoonInfo.getBoolean("progress") ? 1 : 0;
			name = cartoonInfo.getString("name").replaceAll("\\\\", "");
			author = cartoonInfo.getString("author").replaceAll("\\\\", "");
			abst = cartoonInfo.getString("introduction").replaceAll("\\\\", "");
			topicCode = cartoonInfo.getString("topicCode").replaceAll("\\\\", "");
			coverUrl = cartoonInfo.getString("cover").replaceAll("\\\\", "");
			chapterCount = cartoonInfo.getInt("chapterCount");
			recommendCover = cartoonInfo.getString("recommendCover").replaceAll("\\\\", "");
			size = 0;
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}