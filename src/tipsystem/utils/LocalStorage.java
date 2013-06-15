package tipsystem.utils;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class LocalStorage {
	
	public static void setInt(Context ctx, String key, int data) {

     	SharedPreferences prefs = ctx.getSharedPreferences("appData", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();	
	    editor.putInt(key, data);
	    editor.commit();
     	Log.e("LocalStorage", "setInt() saved: " + key);
	 }
	 
	  public static int getInt(Context ctx, String key) {

     	SharedPreferences prefs = ctx.getSharedPreferences("appData", Context.MODE_PRIVATE);
     	int data = prefs.getInt(key, 0);

     	return data;
	 }
	  
	public static void setString(Context ctx, String key, String data) {

     	SharedPreferences prefs = ctx.getSharedPreferences("appData", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();	
	    editor.putString(key, data);
	    editor.commit();
     	Log.e("LocalStorage", "setString() saved: " + key);
	 }
	 
	  public static String getString(Context ctx, String key) {

     	SharedPreferences prefs = ctx.getSharedPreferences("appData", Context.MODE_PRIVATE);
     	String data = prefs.getString(key, "");

     	return data;
	 }
	 
	 public static void setJSONArray(Context ctx, String key, JSONArray data) {

     	SharedPreferences prefs = ctx.getSharedPreferences("appData", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();	
	    editor.putString(key, data.toString());
	    editor.commit();
     	Log.e("LocalStorage", "setJSONArray() saved: " + key);
	 }
	 
	 public static JSONArray getJSONArray(Context ctx, String key) {

     	SharedPreferences prefs = ctx.getSharedPreferences("appData", Context.MODE_PRIVATE);
     	String data = prefs.getString(key, "[]");

		try {
			JSONArray jsons = new JSONArray(data);
	     	return jsons;
		} catch (JSONException e) {
			e.printStackTrace();
		}
     	return null;
	 }
}