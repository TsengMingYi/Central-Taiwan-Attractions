package com.example.googlemap;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class WeatherDataHelper {
    private static final String TAG = WeatherDataHelper.class.getSimpleName();
    private static final String CMD_PREFIX = "https://opendata.cwb.gov.tw/api/v1/rest/datastore/";
    private static final String fixParams = "?Authorization=CWB-3B5F675C-A3F2-49C5-83F1-3BECB0183603&format=JSON";

    private static HashMap<String, String> cityNumberMap = new HashMap<>();

    public static void prepare() {
        cityNumberMap.put("苗栗縣", "013");
        cityNumberMap.put("彰化縣", "017");
        cityNumberMap.put("南投縣", "021");
        cityNumberMap.put("雲林縣", "025");
        cityNumberMap.put("台中市", "073");
        //        cityNumberMap.put("新竹市", "053");
////        cityNumberMap.put("臺北市", "061");
////        cityNumberMap.put("宜蘭縣", "001");
//        cityNumberMap.put("桃園市", "005");
//        cityNumberMap.put("新竹縣", "009");
//        cityNumberMap.put("臺南市", "077");
//        cityNumberMap.put("連江縣", "081");
//        cityNumberMap.put("金門縣", "085");
//        cityNumberMap.put("屏東縣", "033");
//        cityNumberMap.put("臺東縣", "037");
//        cityNumberMap.put("花蓮縣", "041");
//        cityNumberMap.put("澎湖縣", "045");
//        cityNumberMap.put("基隆市", "049");
//        cityNumberMap.put("嘉義市", "057");
//        cityNumberMap.put("高雄市", "065");
//        cityNumberMap.put("新北市", "069");
//        cityNumberMap.put("嘉義縣", "029");
    }

    public static void getCityDetail(String cityName, final SuccessCallback successCallback) {
        String urlPath = CMD_PREFIX + "F-D0047-" +
                cityNumberMap.get(cityName) + fixParams;
        getWeatherDataAsync(urlPath, 8000, successCallback);
    }


    private static void getWeatherDataAsync(final String urlPath, final int timeout,
                                            final SuccessCallback successCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpsURLConnection connection = null;

                URL url = null;
                try {
                    url = new URL(urlPath);
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.setUseCaches(true);
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(timeout);
                    connection.setConnectTimeout(timeout);
                    if (HttpsURLConnection.HTTP_OK == connection.getResponseCode()) {
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        final StringBuilder response = new StringBuilder();
                        char chars[] = new char[1024];
                        int len;
                        while ((len = reader.read(chars, 0, 1024)) != -1) {
                            response.append(new String(chars, 0, len));
                        }
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    successCallback.run(new JSONObject(response.toString()));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        Log.e(TAG, "error code: " + connection.getResponseCode());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (null != connection) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public interface SuccessCallback {
        void run(JSONObject json);
    }
}
