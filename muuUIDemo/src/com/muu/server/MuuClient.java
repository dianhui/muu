package com.muu.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.muu.net.ClientResponse;
import com.muu.net.HttpMethod;
import com.muu.util.FileFormatUtil;
import com.muu.util.PropertyMgr;
import com.muu.util.TempDataLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MuuClient {
	private static final String sRandomListPath = "/cartoon/top";
	private static final String sTopListPath = "/cartoon/top";
	private static final String sNewListPath = "/cartoon/hot";
	private static final String sTopicListPath = "/cartoons/topic";
	private static final String sChapterInfoPath = "/cartoon/chapters";
	
	public static enum ListType {
		RANDOM("random", sRandomListPath),
		TOP("top", sTopListPath),
		NEW("new", sNewListPath);
		
		public String name, path;
		ListType(String name, String path) {
			this.name = name;
			this.path = path;
		}
	};
	
	private CommHttpClient mHttpClient = null;
	public MuuClient() {
		mHttpClient = new CommHttpClient();
	}
	
	/**
	 * Get cartoon list by given #ListType.
	 * @param
	 * 	-type: given list type.
	 * 	-idx: page index.
	 *  -count: wanted cartoon count.
	 *  
	 *  @return
	 *  	- JASONArray of cartoon list by fixed format, eg.
	 *  	[{"author":"author of cartoon","categoryCode":"006","chapterCount":239,"cover":"url of cover","createdTime":"2014-06-04 16:50:17.0",
	 * 		"id":12918,"introduction":"introduction of cartoon","name":"name of cartoon","progress":"false","topicCode":"11",
	 * 		"updatedTime":"2014-07-05 17:34:58.0"}, ...]
	 * 
	 * */
	public JSONArray getCartoonsListByType(ListType type, int idx, int count) {
		JSONArray json = null;
		try {
			ClientResponse resp = mHttpClient.handle(HttpMethod.GET, type.path
					+ "/" + idx + "/" + count);
			byte[] entity = resp.getResponseEntity();
			
			String jsonStr = new String(entity);
			json = new JSONArray(jsonStr);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * get cartoon list by given topic code string
	 * 
	 * @param
	 * 	-topicCode: given topic code string.
	 * 	-idx: page index.
	 *  -count: wanted cartoon count.
	 * */
	public JSONArray getCartoonsByTopic(String topicCode, int idx, int count) {
		JSONArray json = null;
		try {
			ClientResponse resp = mHttpClient.handle(HttpMethod.GET, String
					.format("%s/%s/%d/%d", sTopicListPath, topicCode, idx,
							count));
			byte[] entity = resp.getResponseEntity();
			
			String jsonStr = new String(entity);
			json = new JSONArray(jsonStr);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public JSONObject getCaroonsBySearchStr() {
		return null;
	}
	
	public JSONObject getCartoonsRandom(int count) {
		return null;
	}
	
	public JSONObject getCartoonById(int id) {
		return null;
	}
	
	public JSONObject getCommentsByCartoonId(int cartoonId, int count) {
		return null;
	}
	
	/**
	 * get chapter info by given cartoon id.
	 * 
	 * @param
	 * 	- cartoonId: id of cartoon.
	 * 	- idx: page index.
	 * 	- count: count of required cartoon.
	 * */
	public JSONArray getChapterInfoByCartoonId(int cartoonId, int idx, int count) {
		JSONArray json = null;
		try {
			ClientResponse resp = mHttpClient.handle(HttpMethod.GET, String
					.format("%s/%d/%d/%d", sChapterInfoPath, cartoonId, idx,
							count));
			byte[] entity = resp.getResponseEntity();
			
			String jsonStr = new String(entity);
			json = new JSONArray(jsonStr);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public Bitmap getBitmapByIdx(int cartoonId, int chapterIdx, int pageIdx) {
		return null;
	}
	
	public void downloadCoverByUrl(int id, String url) {
		Bitmap bitmap = new TempDataLoader().getCartoonCover(id);
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
			return;
		}
		
		try {
			ClientResponse resp = mHttpClient.handle(HttpMethod.GET, url);
			byte[] image = null;
			image = resp.getResponseEntity();
            BitmapFactory.Options options = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeByteArray(image, 0, image.length, options);

            String path = PropertyMgr.getInstance().getCachePath() + "cover/";
            File file = new File(path);
            if (!file.exists()) file.mkdirs();
            
            OutputStream fOut = null;
            String fileSuffix = FileFormatUtil.getFileSuffixByUrl(url);
			file = new File(path, id + fileSuffix);
            fOut = new FileOutputStream(file);

            if (fileSuffix.equals(FileFormatUtil.JPG_POSTFIX)) {
            	bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
			} else {
				bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
			}
            
            fOut.flush();
            fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bitmap == null) {
				return;
			}
			
			bitmap.recycle();
			bitmap = null;
		}
		return;
	}
}