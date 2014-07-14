package com.muu.server;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.muu.data.CartoonInfo;
import com.muu.data.ChapterInfo;
import com.muu.data.Comment;
import com.muu.server.MuuClient.ListType;
import com.muu.util.TempDataLoader;


public class MuuServerWrapper {
	private Context mCtx = null;
	private TempDataLoader mTmpDataLoader = null;
	private MuuClient mMuuClient = null;
	
	public MuuServerWrapper(Context ctx) {
		mCtx = ctx.getApplicationContext();
		mTmpDataLoader = new TempDataLoader();
		mMuuClient = new MuuClient();
	}
	
	/**
	 * get cartoon list by given list type.
	 * 
	 * @param
	 * 	- type: #{@link ListType}
	 * 	- idx: page index.
	 * 	- count: cartoon count.
	 * 
	 * @return
	 * 	- cartoon list of given type.
	 **/
	public ArrayList<CartoonInfo> getCartoonListByType(ListType type, int idx, int count) {
		JSONArray jsonArray = mMuuClient.getCartoonsListByType(type, idx, count);
		if (jsonArray == null) return null;
		
		ArrayList<CartoonInfo> cartoonList = new ArrayList<CartoonInfo>();
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				JSONObject cartoonInfo = jsonArray.getJSONObject(i);
				CartoonInfo cartoon = new CartoonInfo(cartoonInfo);
				cartoonList.add(cartoon);
				mMuuClient.downloadCoverByUrl(cartoon.id, cartoon.coverUrl);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		mTmpDataLoader.storeCartoonsToDB(mCtx, cartoonList);
		return cartoonList;
	}
	
	/**
	 * get cartoon list by given topic.
	 * 
	 * @param
	 * 	- topic: String
	 * 	- idx: page index.
	 * 	- count: cartoon count.
	 * 
	 * @return
	 * 	- cartoon list of given topic.
	 **/
	public ArrayList<CartoonInfo> getCartoonListByTopic(String topic, int idx, int count) {
		String topicCode = TempDataLoader.getTopicCode(topic);
		JSONArray jsonArray = mMuuClient.getCartoonsByTopic(topicCode, idx, count);
		if (jsonArray == null) return null;
		
		ArrayList<CartoonInfo> cartoonList = new ArrayList<CartoonInfo>();
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				JSONObject cartoonInfo = jsonArray.getJSONObject(i);
				CartoonInfo cartoon = new CartoonInfo(cartoonInfo);
				cartoonList.add(cartoon);
				mMuuClient.downloadCoverByUrl(cartoon.id, cartoon.coverUrl);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		mTmpDataLoader.storeCartoonsToDB(mCtx, cartoonList);
		return cartoonList;
	}
	
	/**
	 * get cartoons name of which contains the search string
	 * 
	 * @param
	 * 	- searchStr: String.
	 * 	- idx: int, page index
	 * 	- count: int, cartoon count
	 **/
	public ArrayList<CartoonInfo> getSearchCartoons(String searchStr, int idx, int count) {
		JSONArray jsonArray = mMuuClient.getCartoonsBySearchStr(searchStr, idx, count);
		if (jsonArray == null) return null;
		
		ArrayList<CartoonInfo> cartoonList = new ArrayList<CartoonInfo>();
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				JSONObject cartoonInfo = jsonArray.getJSONObject(i);
				CartoonInfo cartoon = new CartoonInfo(cartoonInfo);
				cartoonList.add(cartoon);
				mMuuClient.downloadCoverByUrl(cartoon.id, cartoon.coverUrl);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		mTmpDataLoader.storeCartoonsToDB(mCtx, cartoonList);
		return cartoonList;
	}
	
	/**
	 * get chapter info of given cartoon id.
	 * 
	 * @param
	 * 	- cartoonId: int
	 * 	- idx: page index.
	 * 	- count: cartoon count.
	 * 
	 * @return
	 * 	- chapter list of given type.
	 **/
	public ArrayList<ChapterInfo> getChapterInfo(int cartoonId, int idx, int count) {
		JSONArray jsonArray = mMuuClient.getChapterInfoByCartoonId(cartoonId, idx, count);
		if (jsonArray == null) return null;
		
		ArrayList<ChapterInfo> chapterList = new ArrayList<ChapterInfo>();
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				JSONObject info = jsonArray.getJSONObject(i);
				
				ChapterInfo chapterInfo = new ChapterInfo(info, cartoonId, idx);
				chapterList.add(chapterInfo);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		mTmpDataLoader.storeChaptersToDB(mCtx, chapterList);
		return chapterList;
	}
	
	/**
	 * get comment by given cartoon id.
	 * 
	 * @param
	 * 	- cartoonId: int
	 * 	- idx: page index.
	 * 	- count: cartoon count.
	 * 
	 * @return
	 * 	- cartoon list of given topic.
	 **/
	public ArrayList<Comment> getComments(int cartoonId, int idx, int count) {
		JSONArray jsonArray = mMuuClient.getCommentsByCartoonId(cartoonId, idx, count);
		if (jsonArray == null) return null;
		
		ArrayList<Comment> commentList = new ArrayList<Comment>();
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				JSONObject info = jsonArray.getJSONObject(i);
				Comment comment = new Comment(info, cartoonId);
				commentList.add(comment);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		mTmpDataLoader.storeCommentsToDB(mCtx, commentList);
		return commentList;
	}
}