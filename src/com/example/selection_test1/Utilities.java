package com.example.selection_test1;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class Utilities {
	
	static String TAG = "Network";
	
	
	public static Map jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }
    public static Map toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }
    public static List toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
	public static HttpResponse GetRequest(String url){
		HttpResponse response = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));
			response = client.execute(request);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
	public static JSONObject ToJson(HttpResponse response){
		try {
			return (new JSONObject(EntityUtils.toString(response.getEntity())));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/*
	public Map<String, Object> pushUserChoice(String pid, String email, String choice) {
		String urlString = DATA.attendees_choice_url;

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("pid", pid));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("interested", choice));

		String responseString = jsonFromPostRequest(urlString, params, null);
		Object resultObject = resultObjectFromJson(responseString);
		Log.d(TAG, "Push result is: " + responseString);

		if (resultObject != null && (resultObject instanceof Map)) {
			try {
				return (Map<String, Object>)resultObject;
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}
	/* ==================== helper methods ==================== */

	/**
	 * extract the result value of dict, also report server error
	 *
	 * @param json
	 * @return value of 'result' key in json dict, null on any error
	 */
	/*
	public static Object resultObjectFromJson(String json) {

		Map<String, Object> resultMap = JSONUtil.mapFromJson(json);

		if (resultMap == null) {
			return null;
		}
		Object type = resultMap.get("type");
		Object result = resultMap.get("result");
		if (type == null || result == null || !(type instanceof String)) {
			return null;
		}
		if ("error".equals(type)) {
			Log.e(TAG, "server error: " + result.toString());
			return null;
		}
		return result;
	}

	public static String jsonFromPostRequest(String urlString,
			List<NameValuePair> params, Type type) {
		return jsonFromPostRequest(urlString, params, type,
				EventUtil.getCurrentEvent());
	}

	/**
	 *
	 * @param urlString
	 *            , can't be null
	 * @param params
	 *            list of parameters, could be null
	 * @return json response string from server, null if any error
	 */
	/*
	private static String jsonFromPostRequest(String urlString,
			List<NameValuePair> params, Type type, String eventID) {
		Date start = new Date();
		if (urlString == null || urlString.length() == 0) {
			return null;
		}
		if (params == null) {
			params = new ArrayList<NameValuePair>();
		}
		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		HttpURLConnection uc;
		try {
			uc = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			Log.e(TAG, "can't open connection");
			e.printStackTrace();
			return null;
		}
		// set connection meta
		try {
			uc.setRequestMethod("POST");
		} catch (ProtocolException e) {
			e.printStackTrace();
			return null;
		}
		uc.setConnectTimeout(Constants.WHOVA_CONNECTION_TIME_OUT);
		uc.setDoInput(true);
		uc.setDoOutput(true);

		String paramString = getQuery(params);

		OutputStream outStream = null;
		InputStream inStream = null;
		BufferedReader reader = null;
		try {
			// write post body
			outStream = uc.getOutputStream();
			outStream.write(paramString.getBytes(Constants.ENCODING_UTF8));
			Log.i(TAG, "send post request: " + urlString + "?" + paramString);
			// read result back
			inStream = uc.getInputStream();
			StringBuffer resultBuffer = new StringBuffer();
			String temp = null;
			reader = new BufferedReader(new InputStreamReader(inStream,
					Constants.ENCODING_UTF8));
			while (null != (temp = reader.readLine())) {
				resultBuffer.append(temp).append('\n');
			}
			String resultString = resultBuffer.toString();
			Log.i(TAG, "get response len: " + resultString.length());
			Log.v(TAG, "get response: " + resultString);
			if (type != Type.name_suggest && type != null) {
				long intervalInMilliseconds = new Date().getTime()
						- start.getTime();
				Tracking.getInstance().sendTiming("Network",
						intervalInMilliseconds, type.name(),
						Util.getNetworkConnectionType() + "");
			}

			return resultString;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (outStream != null) {
					outStream.close();
				}
				if (inStream != null) {
					inStream.close();
				}
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}


	/**
	 * Send post request to back end without specifying email address and event name
	 * Pretty much, this method is used before user signed in
	 *
	 * @param urlString
	 * @param params
	 * @param type
	 * @param eventID
	 * @return
	 *//*
    private static String jsonFromPostRequestWithoutEmail(String urlString,
            List<NameValuePair> params, Type type) {
        Date start = new Date();
        if (urlString == null || urlString.length() == 0) {
            return null;
        }
        if (params == null) {
            params = new ArrayList<NameValuePair>();
        }
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        HttpURLConnection uc;
        try {
            uc = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            Log.e(TAG, "can't open connection");
            e.printStackTrace();
            return null;
        }
        // set connection meta
        try {
            uc.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
            return null;
        }
        uc.setConnectTimeout(Constants.WHOVA_CONNECTION_TIME_OUT);
        uc.setDoInput(true);
        uc.setDoOutput(true);

        String paramString = getQuery(params);

        OutputStream outStream = null;
        InputStream inStream = null;
        BufferedReader reader = null;
        try {
            // write post body
            outStream = uc.getOutputStream();
            outStream.write(paramString.getBytes(Constants.ENCODING_UTF8));
            Log.i(TAG, "send post request: " + urlString + "?" + paramString);
            // read result back
            inStream = uc.getInputStream();
            StringBuffer resultBuffer = new StringBuffer();
            String temp = null;
            reader = new BufferedReader(new InputStreamReader(inStream,
                    Constants.ENCODING_UTF8));
            while (null != (temp = reader.readLine())) {
                resultBuffer.append(temp).append('\n');
            }
            String resultString = resultBuffer.toString();
            Log.i(TAG, "get response len: " + resultString.length());
            Log.v(TAG, "get response: " + resultString);
            if (type != Type.name_suggest && type != null) {
                long intervalInMilliseconds = new Date().getTime()
                        - start.getTime();
                Tracking.getInstance().sendTiming("Network",
                        intervalInMilliseconds, type.name(),
                        Util.getNetworkConnectionType() + "");
            }

            return resultString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
                if (inStream != null) {
                    inStream.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

	/**
	 *
	 * @param urlString
	 *            can't be null, make sure encode url before call
	 * @return json response string from server, null if any error
	 *//*
	public static String jsonFromGetRequest(String urlString) {
		if (urlString == null || urlString.length() == 0) {
			return null;
		}
		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		HttpURLConnection uc;
		try {
			uc = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			Log.e(TAG, "can't open connection");
			e.printStackTrace();
			return null;
		}
		// set connection meta
		try {
			uc.setRequestMethod("GET");
		} catch (ProtocolException e) {
			e.printStackTrace();
			return null;
		}
		uc.setConnectTimeout(Constants.WHOVA_CONNECTION_TIME_OUT);
		uc.setDoInput(true);

		InputStream inStream = null;
		BufferedReader reader = null;
		try {
			Log.v(TAG, "send get request: " + urlString);
			// read result back
			inStream = uc.getInputStream();
			StringBuffer resultBuffer = new StringBuffer();
			String temp = null;
			reader = new BufferedReader(new InputStreamReader(inStream,
					Constants.ENCODING_UTF8));
			while (null != (temp = reader.readLine())) {
				resultBuffer.append(temp).append('\n');
			}
			String resultString = resultBuffer.toString();
			Log.v(TAG, "get response: " + resultString);
			return resultString;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 *
	 * @param params
	 * @return utf-8 encoded post parameters string
	 * @throws UnsupportedEncodingException
	 *//*
	private static String getQuery(List<NameValuePair> params) {
		StringBuilder result = new StringBuilder();
		boolean first = true;

		for (NameValuePair pair : params) {
			if (first)
				first = false;
			else
				result.append("&");

			result.append(encodeURL(pair.getName()));
			result.append("=");
			result.append(encodeURL(pair.getValue()));
		}
		return result.toString();
	}

	public static String encodeURL(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			Log.w(TAG, "encodeURL error", e);
			return s;
		}
	}

	public static String decodeURL(String s) {
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.w(TAG, "encodeURL error", e);
			return s;
		}
	}*/
}
