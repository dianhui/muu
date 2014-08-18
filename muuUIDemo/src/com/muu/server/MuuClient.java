package com.muu.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;

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
	private static final String sRandomListPath = "/cartoon/random";
	private static final String sTopListPath = "/cartoon/top";
	private static final String sNewListPath = "/cartoon/hot";
	private static final String sTopicListPath = "/cartoons/topic";
	private static final String sChapterInfoPath = "/cartoon/chapters";
	private static final String sCommentsPath = "/cartoon/comments";
	private static final String sSearchCartoonPath = "/cartoons/name";
	private static final String sChapterImageInfoPath = "/cartoon/chapter/images";
	private static final String sActivitiesPath = "/activities";
	
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
	 **/
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
	 **/
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
	
	/**
	 * get cartoons name of which contains the search string
	 * 
	 * @param
	 * 	- searchStr: String
	 * 	- idx: int, page index
	 * 	- count: int, cartoon count
	 **/
	public JSONArray getCartoonsBySearchStr(String searchStr, int idx, int count) {
		JSONArray json = null;
		try {
			ClientResponse resp = mHttpClient.handle(HttpMethod.GET, String
					.format("%s/%d/%d?name=%s", sSearchCartoonPath, idx, count,
							URLEncoder.encode(searchStr, "UTF-8")));
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
	
	public JSONObject getCartoonsRandom(int count) {
		return null;
	}
	
	public JSONObject getCartoonById(int id) {
		return null;
	}
	
	/**
	 * get newest comments of given cartoon id.
	 * 
	 * @param
	 *	- cartoonId: int
	 *	- count: int, count of comments 
	 **/
	public JSONArray getCommentsByCartoonId(int cartoonId, int idx, int count) {
		JSONArray json = null;
		try {
			ClientResponse resp = mHttpClient.handle(HttpMethod.GET, String
					.format("%s/%d/%d/%d", sCommentsPath, cartoonId, idx,
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
	
	/**
	 * get chapter info by given cartoon id.
	 * 
	 * @param
	 * 	- cartoonId: id of cartoon.
	 * 	- idx: page index.
	 * 	- count: count of required cartoon.
	 **/
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
	
	/**
	 * get all image info of given chapter id.
	 * 
	 * @param
	 * 	- chapterId: id of chapter.
	 * 	- idx: page index.
	 * 	- count: count of required image info.
	 * 
	 * */
	public JSONArray getChapterImageInfo(int chapterId, int idx, int count) {
		JSONArray json = null;
		try {
			ClientResponse resp = mHttpClient.handle(HttpMethod.GET, String
					.format("%s/%d/%d/%d", sChapterImageInfoPath, chapterId, idx,
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
	
	public WeakReference<Bitmap> getBitmapByUrl(String url) {
		Bitmap bitmap = null;
		try {
			ClientResponse resp = mHttpClient.handle(HttpMethod.GET, url);
			byte[] image = null;
			image = resp.getResponseEntity();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            // TODO: some image can not be decode.
            bitmap = BitmapFactory.decodeByteArray(image, 0, image.length, options);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new WeakReference<Bitmap>(bitmap);
	}
	
	public JSONArray getActivityEventInfos() {
		JSONArray json = null;
		try {
			ClientResponse resp = mHttpClient.handle(HttpMethod.GET, sActivitiesPath);
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
	
	public void downloadActivityCoverByUrl(String title, String url) {
		WeakReference<Bitmap> bmpRef = new TempDataLoader().getActivityCover(title);
		Bitmap bmp = null;
		if (bmpRef != null) {
			bmp = bmpRef.get();
		}
		
		if (bmp != null) {
			bmp.recycle();
			bmp = null;
			return;
		}
		
		try {
			
			bmpRef = getBitmapByUrl(url);
			if (bmpRef != null) {
				bmp = bmpRef.get();
			}
			if (bmp == null) {
				return;
			}

            String path = PropertyMgr.getInstance().getActivityCoverPath();
            File file = new File(path);
            if (!file.exists()) file.mkdirs();
            
            OutputStream fOut = null;
            String fileSuffix = FileFormatUtil.getFileSuffixByUrl(url);
			file = new File(path, title + fileSuffix);
            fOut = new FileOutputStream(file);

            if (fileSuffix.equals(FileFormatUtil.JPG_POSTFIX)) {
            	bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
			} else {
				bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
			}
            
            fOut.flush();
            fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bmp == null) {
				return;
			}
			
			bmp.recycle();
			bmp = null;
		}
		return;
	}
	
	public void downloadCoverByUrl(int id, String url) {
		Bitmap bitmap = null;
		WeakReference<Bitmap> bmpRef = new TempDataLoader().getCartoonCover(id);
		if (bmpRef != null) {
			bitmap = bmpRef.get();
		}
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
			return;
		}
		
		try {
			bmpRef = getBitmapByUrl(url);
			if (bmpRef != null) {
				bitmap = bmpRef.get();
			}
			
			if (bitmap == null) {
				return;
			}

            String path = PropertyMgr.getInstance().getCoverPath(id);
            File file = new File(path);
            if (!file.exists()) file.mkdirs();
            
            OutputStream fOut = null;
            String fileSuffix = FileFormatUtil.getFileSuffixByUrl(url);
			file = new File(path, "cover" + fileSuffix);
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
	
	public void downloadCartoonImage(int cartoonId, int imgId, String url) {
		Bitmap bitmap = null;
		WeakReference<Bitmap> bmpRef = new TempDataLoader().getImage(cartoonId, imgId);
		if (bmpRef != null) {
			bitmap = bmpRef.get();
		}
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
			return;
		}
		
		try {
			bmpRef = getBitmapByUrl(url);
			if (bmpRef != null) {
				bitmap = bmpRef.get();
			}
			
            if (bitmap == null) {
				return;
			}

            String path = PropertyMgr.getInstance().getCartoonPath(cartoonId);
            File file = new File(path);
            if (!file.exists()) file.mkdirs();
            
            OutputStream fOut = null;
            String fileSuffix = FileFormatUtil.getFileSuffixByUrl(url);
			file = new File(path, imgId + fileSuffix);
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