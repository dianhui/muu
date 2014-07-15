package com.muu.data;

import org.json.JSONException;
import org.json.JSONObject;

public class ImageInfo {
	public int id;
	public String imgUrl;
	
	public ImageInfo(JSONObject jsonImgInfo) {
		try {
			id = jsonImgInfo.getInt("id");
			imgUrl = jsonImgInfo.getString("url").replaceAll("\\\\", "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
		return String.format("%d|%s", id, imgUrl);
	}
}
