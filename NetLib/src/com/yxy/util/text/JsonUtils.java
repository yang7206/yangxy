package com.yxy.util.text;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * 处理JSON相关工具类
 * @author yxy
 *
 */
public class JsonUtils {
	/**
	 * String
	 * JsonObject
	 * JsonArray
	 * @param responseBody
	 * @param charset
	 * @return
	 * @throws JSONException
	 */
	public static Object parseResponse(byte[] responseBody, String charset) throws JSONException {
        if (null == responseBody)
            return null;
        Object result = null;
        String jsonString = getResponseString(responseBody, charset);
        if (jsonString != null) {
            jsonString = jsonString.trim();
            if (jsonString.startsWith("{") || jsonString.startsWith("[")) {
                result = new JSONTokener(jsonString).nextValue();
            }
        }
        if (result == null) {
            result = jsonString;
        }
        return result;
    }
	
	public static String getResponseString(byte[] stringBytes, String charset) {
        try {
            return stringBytes == null ? null : new String(stringBytes, charset);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
	}
	
	public static String stringTypeValue(JSONObject object, String name) {
		Object value = object.opt(name);
		if (value == null) {
			return null;
		}
		if (JSONObject.NULL.equals(value)) {
			return null;
		}
		return value.toString();
	}
	
	public static Boolean booleanTypeValue(JSONObject object, String name) {
		Object value = object.opt(name);
		if (value == null) {
			return null;
		}
		if (JSONObject.NULL.equals(value)) {
			return null;
		}
		return (Boolean)value;
	}
	
	public static Number numberTypeValue(JSONObject object, String name) {
		Object value = object.opt(name);
		if (value == null) {
			return null;
		}
		if (JSONObject.NULL.equals(value)) {
			return null;
		}
		return (Number)value;
	}
	
	public static JSONArray arrayTypeValue(JSONObject object, String name) {
		Object value = object.opt(name);
		if (value == null) {
			return null;
		}
		if (JSONObject.NULL.equals(value)) {
			return null;
		}
		return (JSONArray)value;
	}
}
