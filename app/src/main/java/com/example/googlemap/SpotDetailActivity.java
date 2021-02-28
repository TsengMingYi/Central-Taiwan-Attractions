package com.example.googlemap;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class SpotDetailActivity extends AppCompatActivity {
    private static final long INTERVAL_18_HR = 18 * 60 * 60 * 1000;
    private static final long INTERVAL_24_HR = 24 * 60 * 60 * 1000;
    private static final long INTERVAL_42_HR = 42 * 60 * 60 * 1000;
    private static final long INTERVAL_48_HR = 48 * 60 * 60 * 1000;
    private TextView open_hourtime_1;
    private TextView open_hourtime_2;
    private TextView open_hourtime_3;
    private TextView open_hourtime_4;
    private TextView open_hourtime_5;
    private TextView open_hourtime_6;
    private TextView open_hourtime_7;
    private TextView temparature;
    private TextView detail;
    private TextView predictText;
    private TextView Wx;
    private TextView CIText;
    private TextView Wind_speed;
    private TextView wind_direction;
    private TextView Relative_Humidity;
    private TextView Dew_Point_Temperature;
    private TextView tomorrow_temperature_btn;
    private TextView tomorrow_Wx_btn;
    private TextView tomorrow_AT_btn;
    private TextView tomorrow_RH_btn;
    private TextView tomorrow_CI_btn;
    private TextView tomorrow_PoP6h_btn;
    private TextView tomorrow_WS_btn;
    private TextView tomorrow_WD_btn;
    private TextView tomorrow_TD_btn;

    private TextView aftertomorrow_temperature_btn;
    private TextView aftertomorrow_Wx_btn;
    private TextView aftertomorrow_AT_btn;
    private TextView aftertomorrow_RH_btn;
    private TextView aftertomorrow_CI_btn;
    private TextView aftertomorrow_PoP6h_btn;
    private TextView aftertomorrow_WS_btn;
    private TextView aftertomorrow_WD_btn;
    private TextView aftertomorrow_TD_btn;
    private ImageView PhotoImage;
    private TextView place_url;
    private TextView place_url2;
    private TextView telephone;
    private TextView attractionsRating;
    private String regionId;
    private String cityName;
    private String place_id;
    private String place_URL;
    private String attractions_name;
    private String photoReference;
    private String[] photoReference1;
    private ImageView imageView;
    private Bitmap bitmap;
    private int temp = 0;
    private int photoCounts = 0;
    private ArrayList<Bitmap> photoBitmaps = new ArrayList<>();
    private int currentIndex = 0;
    private Handler pollingHandler = new Handler();
    /**Dart是全物件導向,沒有基本資料形態(包含int char long都是物件)*/
    /**Java是包含物件導向,有基本資料形態(包含int char long都是基本資料形態)*/
    private Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            // change photo
            PhotoImage.setImageBitmap(photoBitmaps.get(currentIndex));
//            currentIndex++;
//            if(currentIndex >= photoBitmaps.size()){
//                currentIndex = 0;
//            }
            currentIndex = (currentIndex + 1) % photoBitmaps.size();
            // do the same thing after 3 secs
            pollingHandler.postDelayed(pollingRunnable, 3000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_detail);
        findView();
        Intent intent = getIntent();
        regionId = intent.getStringExtra("regionId");
        cityName = intent.getStringExtra("cityName");
        place_id = intent.getStringExtra("place_id");
        place_URL = intent.getStringExtra("place_url");
        attractions_name = intent.getStringExtra("attractions_name");
        setTitle(attractions_name);
        WeatherDataHelper.getCityDetail(cityName, new WeatherDataHelper.SuccessCallback() {
            @Override
            public void run(JSONObject json) {
                try {
                    if (!"true".equals(json.getString("success"))) {
                        Toast.makeText(SpotDetailActivity.this,
                                "get weather data failed, city: "
                                        + cityName, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    JSONObject records = json.getJSONObject("records");
                    JSONArray locations = records.getJSONArray("locations");
                    JSONObject dataset = locations.getJSONObject(0);
                    JSONArray location = dataset.getJSONArray("location");
                    JSONObject region = null;
                    for (int i = 0; i < location.length(); i++) {
                        JSONObject temp = location.getJSONObject(i);
                        String regionId = temp.getString("locationName");
                        if (SpotDetailActivity.this.regionId.equals(regionId)) {
                            region = temp;
                            break;
                        }
                    }
                    if (region == null) {
                        // todo maybe toast some measge
                        return;
                    }

                    JSONArray weatherElement = region.getJSONArray("weatherElement");
                    HashMap<String, JSONObject> jsonHashMap = new HashMap<>();
                    for (int i = 0; i < weatherElement.length(); i++) {
                        JSONObject jsonObject = weatherElement.getJSONObject(i);
                        String elementName = jsonObject.getString("elementName");
                        switch (elementName) {
                            case "Wx":
                            case "AT":
                            case "PoP6h":
                            case "T":
                            case "CI":
                            case "WS":
                            case "WD":
                            case "RH":
                            case "Td":
                                jsonHashMap.put(elementName, jsonObject);
                                break;
                        }
                    }
                    HashMap<String, JSONObject> weatherDataMap = new HashMap<>();
                    SimpleDateFormat dateFormat =
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    long now = System.currentTimeMillis();
                    Set<String> keySet = jsonHashMap.keySet();
                    for (String key : keySet) {
                        JSONObject jsonObject = jsonHashMap.get(key);
                        JSONArray dataWithTimeInterval = jsonObject.getJSONArray("time");

                        for (int i = 0; i < dataWithTimeInterval.length(); i++) {
                            JSONObject weatherData = dataWithTimeInterval.getJSONObject(i);
                            String timeData = "";
                            try {
                                timeData = weatherData.getString("dataTime");
                            } catch (JSONException e) {
                                timeData = weatherData.getString("startTime");
                            }

                            try {
                                Date date = dateFormat.parse(timeData);
                                long t = date.getTime();
                                if (t >= now) {
                                    if (weatherDataMap.get(key) == null) {
                                        weatherDataMap.put(key, weatherData);
                                    }
                                    break;
                                } else {
                                    weatherDataMap.put(key, weatherData);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    HashMap<String, String> finalValueMap = new HashMap<>();
                    for (String key : keySet) {
                        JSONObject weatherData = weatherDataMap.get(key);
                        JSONArray elementValue = weatherData.getJSONArray("elementValue");
                        for (int i = 0; i < elementValue.length(); i++) {
                            JSONObject measuresData = elementValue.getJSONObject(i);
                            String measureStr = measuresData.getString("measures");
                            switch (key) {
                                case "Wx":
                                    if ("自定義 Wx 文字".equals(measureStr)) {
                                        finalValueMap.put(key, measuresData.getString("value"));
                                    }
                                    break;
                                case "AT":
                                    if ("攝氏度".equals(measureStr)) {
                                        finalValueMap.put(key, measuresData.getString("value"));
                                    }
                                    break;
                                case "PoP6h":
                                    if ("百分比".equals(measureStr)) {
                                        finalValueMap.put(key, measuresData.getString("value"));
                                    }
                                    break;
                                case "T":
                                    if ("攝氏度".equals(measureStr)) {
                                        finalValueMap.put(key, measuresData.getString("value"));
                                    }
                                    break;
                                case "CI":
                                    if ("自定義 CI 文字".equals(measureStr)) {
                                        finalValueMap.put(key, measuresData.getString("value"));
                                    }
                                    break;
                                case "WS":
                                    if ("蒲福風級".equals(measureStr)) {
                                        finalValueMap.put(key, measuresData.getString("value"));
                                    }
                                    break;
                                case "WD":
                                    if ("8方位".equals(measureStr)) {
                                        finalValueMap.put(key, measuresData.getString("value"));
                                    }
                                    break;
                                case "RH":
                                    if ("百分比".equals(measureStr)) {
                                        finalValueMap.put(key, measuresData.getString("value"));
                                    }
                                    break;
                                case "Td":
                                    if ("攝氏度".equals(measureStr)) {
                                        finalValueMap.put(key, measuresData.getString("value"));
                                    }
                                    break;
                            }
                        }

                    }
                    temparature.setText(finalValueMap.get("T") + "℃");
                    detail.setText(finalValueMap.get("PoP6h") + "%");
                    predictText.setText(finalValueMap.get("CI"));
                    Wx.setText(finalValueMap.get("Wx"));
                    CIText.setText(finalValueMap.get("AT") + "℃");
                    Wind_speed.setText(finalValueMap.get("WS") + "級風");
                    wind_direction.setText(finalValueMap.get("WD"));
                    Relative_Humidity.setText(finalValueMap.get("RH")+"%");
                    Dew_Point_Temperature.setText(finalValueMap.get("Td")+"℃");


                    String[] needToShowKeys = new String[]{"Wx", "T", "CI", "PoP6h", "AT", "WS", "WD","RH","Td"};
                    HashMap<String, JSONObject> tomorrowDataMap = new HashMap<>();
                    HashMap<String, JSONObject> afterTomorrowDataMap = new HashMap<>();
                    for (String key : needToShowKeys) {
                        JSONObject jsonObject = jsonHashMap.get(key);
                        JSONArray dataWithTimeInterval = jsonObject.getJSONArray("time");

                        for (int i = 0; i < dataWithTimeInterval.length(); i++) {
                            JSONObject weatherData = dataWithTimeInterval.getJSONObject(i);
                            String timeData = "";
                            try {
                                timeData = weatherData.getString("dataTime");
                            } catch (JSONException e) {
                                timeData = weatherData.getString("startTime");
                            }

                            try {
                                Date date = dateFormat.parse(timeData);
                                long diff = date.getTime() - now;
                                if (diff > INTERVAL_18_HR && diff < INTERVAL_24_HR) {

                                    tomorrowDataMap.put(key, weatherData);
                                } else if (diff > INTERVAL_42_HR && diff < INTERVAL_48_HR) {

                                    afterTomorrowDataMap.put(key, weatherData);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    HashMap<String, String> finalTomorrowValueMap = new HashMap<>();
                    HashMap<String, String> finalAfterTomorrowValueMap = new HashMap<>();
                    for (String key : needToShowKeys) {
                        JSONObject tomorrowData = tomorrowDataMap.get(key);
                        JSONObject afterTomorrowData = afterTomorrowDataMap.get(key);

                        JSONArray tomorrowElementValue =
                                tomorrowData.getJSONArray("elementValue");
                        JSONArray afterTomorrowElementValue =
                                afterTomorrowData.getJSONArray("elementValue");
                        for (int i = 0; i < tomorrowElementValue.length(); i++) {
                            JSONObject tomorrowMeasuresData = tomorrowElementValue.getJSONObject(i);
                            JSONObject afterTomorrowMeasuresData = afterTomorrowElementValue.getJSONObject(i);
                            String tomorrowMeasureStr = tomorrowMeasuresData.getString("measures");
                            String afterTomorrowMeasureStr = afterTomorrowMeasuresData.getString("measures");
                            switch (key) {
                                case "Wx":
                                    if ("自定義 Wx 文字".equals(tomorrowMeasureStr)) {
                                        finalTomorrowValueMap.put(key, tomorrowMeasuresData.getString("value"));
                                    }
                                    if ("自定義 Wx 文字".equals(afterTomorrowMeasureStr)) {
                                        finalAfterTomorrowValueMap.put(key, afterTomorrowMeasuresData.getString("value"));
                                    }
                                    break;
                                case "PoP6h":
                                    if ("百分比".equals(tomorrowMeasureStr)) {
                                        finalTomorrowValueMap.put(key, tomorrowMeasuresData.getString("value"));
                                    }
                                    if ("百分比".equals(afterTomorrowMeasureStr)) {
                                        finalAfterTomorrowValueMap.put(key, afterTomorrowMeasuresData.getString("value"));
                                    }
                                    break;
                                case "T":
                                    if ("攝氏度".equals(tomorrowMeasureStr)) {
                                        finalTomorrowValueMap.put(key, tomorrowMeasuresData.getString("value"));
                                    }
                                    if ("攝氏度".equals(afterTomorrowMeasureStr)) {
                                        finalAfterTomorrowValueMap.put(key, afterTomorrowMeasuresData.getString("value"));
                                    }
                                    break;
                                case "AT":
                                    if ("攝氏度".equals(tomorrowMeasureStr)) {
                                        finalTomorrowValueMap.put(key, tomorrowMeasuresData.getString("value"));
                                    }
                                    if ("攝氏度".equals(afterTomorrowMeasureStr)) {
                                        finalAfterTomorrowValueMap.put(key, afterTomorrowMeasuresData.getString("value"));
                                    }
                                    break;
                                case "CI":
                                    if ("自定義 CI 文字".equals(tomorrowMeasureStr)) {
                                        finalTomorrowValueMap.put(key, tomorrowMeasuresData.getString("value"));
                                    }
                                    if ("自定義 CI 文字".equals(afterTomorrowMeasureStr)) {
                                        finalAfterTomorrowValueMap.put(key, afterTomorrowMeasuresData.getString("value"));
                                    }
                                    break;
                                case "WS":
                                    if ("蒲福風級".equals(tomorrowMeasureStr)) {
                                        finalTomorrowValueMap.put(key, tomorrowMeasuresData.getString("value"));
                                    }
                                    if ("蒲福風級".equals(afterTomorrowMeasureStr)) {
                                        finalAfterTomorrowValueMap.put(key, afterTomorrowMeasuresData.getString("value"));
                                    }
                                    break;
                                case "WD":
                                    if ("8方位".equals(tomorrowMeasureStr)) {
                                        finalTomorrowValueMap.put(key, tomorrowMeasuresData.getString("value"));
                                    }
                                    if ("8方位".equals(afterTomorrowMeasureStr)) {
                                        finalAfterTomorrowValueMap.put(key, afterTomorrowMeasuresData.getString("value"));
                                    }
                                    break;
                                case "RH":
                                    if ("百分比".equals(tomorrowMeasureStr)) {
                                        finalTomorrowValueMap.put(key, tomorrowMeasuresData.getString("value"));
                                    }
                                    if ("百分比".equals(afterTomorrowMeasureStr)) {
                                        finalTomorrowValueMap.put(key, afterTomorrowMeasuresData.getString("value"));
                                    }
                                    break;
                                case "Td":
                                    if ("攝氏度".equals(tomorrowMeasureStr)) {
                                        finalTomorrowValueMap.put(key, tomorrowMeasuresData.getString("value"));
                                    }
                                    if ("攝氏度".equals(afterTomorrowMeasureStr)) {
                                        finalTomorrowValueMap.put(key, afterTomorrowMeasuresData.getString("value"));
                                    }
                                    break;
                            }
                        }
                    }
                    tomorrow_TD_btn.setText(finalTomorrowValueMap.get("Td")+"℃");
                    tomorrow_RH_btn.setText(finalTomorrowValueMap.get("RH")+"%");
                    tomorrow_temperature_btn.setText(finalTomorrowValueMap.get("T") + "℃");
                    tomorrow_PoP6h_btn.setText(finalTomorrowValueMap.get("PoP6h") + "%");
                    tomorrow_CI_btn.setText(finalTomorrowValueMap.get("CI"));
                    tomorrow_Wx_btn.setText(finalTomorrowValueMap.get("Wx"));
                    tomorrow_AT_btn.setText(finalTomorrowValueMap.get("AT") + "℃");
                    tomorrow_WS_btn.setText(finalTomorrowValueMap.get("WS") + "級風");
                    tomorrow_WD_btn.setText(finalTomorrowValueMap.get("WD"));
                    aftertomorrow_TD_btn.setText(finalAfterTomorrowValueMap.get("Td")+"℃");
                    aftertomorrow_RH_btn.setText(finalAfterTomorrowValueMap.get("RH")+"%");
                    aftertomorrow_temperature_btn.setText(finalAfterTomorrowValueMap.get("T") + "℃");
                    aftertomorrow_PoP6h_btn.setText(finalAfterTomorrowValueMap.get("PoP6h") + "%");
                    aftertomorrow_CI_btn.setText(finalAfterTomorrowValueMap.get("CI"));
                    aftertomorrow_Wx_btn.setText(finalAfterTomorrowValueMap.get("Wx"));
                    aftertomorrow_AT_btn.setText(finalAfterTomorrowValueMap.get("AT") + "℃");
                    aftertomorrow_WS_btn.setText(finalAfterTomorrowValueMap.get("WS") + "級風");
                    aftertomorrow_WD_btn.setText(finalAfterTomorrowValueMap.get("WD"));
                    /**TreeMap和HashMap差别,TreeMap从小到大有排序,HashMap對應key跟value查找資料*/
                    // todo test 拿溫度資料
//                    JSONObject jsonObject = jsonHashMap.get("T");
//                    JSONArray jsonArray = jsonObject.getJSONArray("time");
//                    for(int i = 0;i<jsonArray.length();i++){
//                        String dataTime = jsonArray.getJSONObject(i).getString("dataTime");
//                    }


                    //todo instant time
//                    long min_diff_time = -1;
//                    int min_diff_index = -1;
//                    JSONArray jsonArray = jsonHashMap.get("T").getJSONArray("time");
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject t = jsonArray.getJSONObject(i);
//                        String dataTime = t.getString("dataTime");
//
//
//                        DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                        LocalDateTime localDateTime = LocalDateTime.parse(dataTime, DTF);
//                        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
//                        //atZone(ZoneId.systemDefault())計算手機所在地區時區
//                        Instant now = Instant.now();//當前時間
//                        long time = Math.abs(Duration.between(now, instant).toMillis());
//                        //計算當前時間與抓取到時間的時間差（絕對值）
//                        if(min_diff_time < 0){
//                            //如果一開始還沒有初始化,就讓他等於當前時間，索引第i個
//                            min_diff_time = time;
//                            min_diff_index = i;
//                        }else {
//                            if(time < min_diff_time){
//                                min_diff_time = time;
//                                min_diff_index = i;
//                            }
//                        }
//                    }
//
//                    JSONArray elementValue = jsonHashMap.get("T").getJSONArray("elementValue");
//                    String T = elementValue.getJSONObject(min_diff_index).getString("value");
//                    temparature.setText(T+"℃");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        SpotManager.getInstance().getSpotDetailByPlaceId(place_id, SpotDetailActivity.this, new SpotManager.DetailCallback() {
            @Override
            public void success(List opentime, String phoneNumber, String Rating, List photoImage, final Uri uri) {
                if (opentime != null && opentime.size() >= 7) {
                    open_hourtime_1.setText(opentime.get(0).toString());
                    open_hourtime_2.setText(opentime.get(1).toString());
                    open_hourtime_3.setText(opentime.get(2).toString());
                    open_hourtime_4.setText(opentime.get(3).toString());
                    open_hourtime_5.setText(opentime.get(4).toString());
                    open_hourtime_6.setText(opentime.get(5).toString());
                    open_hourtime_7.setText(opentime.get(6).toString());
                }
                telephone.setText(phoneNumber);
                attractionsRating.setText(Rating);
                Log.i("test11", photoImage.size() + "" + "nothing");
                Log.i("test110", photoImage.toString() + "" + "nothing");

                for (int i = 0; i < photoImage.size(); i++) {
                    //if(photoImage.get(i).equals("photoReference")){
                    photoReference = photoImage.get(i).toString();
                    photoReference1 = photoReference.split(",");


                    photoCounts = 0;
                    for (String photo : photoReference1) {
                        if (photo.contains("photoReference=")) {
                            photoCounts++;
                        }
                    }

                    temp = 0;
                    for (String photo : photoReference1) {
                        Log.i("test100", photo);
                        if (photo.contains("photoReference=")) {
                            photo = photo.substring(16, photo.length() - 1);
                            Log.i("test200", photo);
                            SpotManager.getInstance().getHttpBitmap("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                                            + photo + "&key=AIzaSyC3wtAGQhgGgMSyVstzl9GsPPm6k_BfxlE"
                                    , photoBitmaps
                                    , new Runnable() {
                                        @Override
                                        public void run() {
                                            temp++;
                                            if (temp == photoCounts) {
                                                // all photos is downloaded
                                                // can update the UI
                                                pollingHandler.post(pollingRunnable);
                                            }
                                        }
                                    });
                            Log.e("test", "get bitmap finish: " + bitmap);
//                            PhotoImage.setImageBitmap(bitmap);
                        }
                    }
                }
                if (uri != null && uri.toString().length() >= 1) {
                    place_url2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(uri.toString()));
                            try {
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {

                            }
                        }
                    });
                }
            }


            @Override
            public void fail(Exception e) {

            }
        });
    }

    private void findView() {
        imageView = findViewById(R.id.imageView);
        open_hourtime_1 = findViewById(R.id.open_hourtime_1);
        open_hourtime_2 = findViewById(R.id.open_hourtime_2);
        open_hourtime_3 = findViewById(R.id.open_hourtime_3);
        open_hourtime_4 = findViewById(R.id.open_hourtime_4);
        open_hourtime_5 = findViewById(R.id.open_hourtime_5);
        open_hourtime_6 = findViewById(R.id.open_hourtime_6);
        open_hourtime_7 = findViewById(R.id.open_hourtime_7);
        temparature = findViewById(R.id.temparature);
        detail = findViewById(R.id.detail);
        predictText = findViewById(R.id.predictText);
        Wx = findViewById(R.id.Wx);
        CIText = findViewById(R.id.CIText);
        Wind_speed = findViewById(R.id.Wind_speed);
        wind_direction = findViewById(R.id.wind_direction);
        Relative_Humidity = findViewById(R.id.Relative_Humidity);
        Dew_Point_Temperature = findViewById(R.id.Dew_Point_Temperature);
        tomorrow_AT_btn = findViewById(R.id.tomorrow_AT_btn);
        tomorrow_CI_btn = findViewById(R.id.tomorrow_CI_btn);
        tomorrow_PoP6h_btn = findViewById(R.id.tomorrow_PoP6h_btn);
        tomorrow_RH_btn = findViewById(R.id.tomorrow_RH_btn);
        tomorrow_TD_btn = findViewById(R.id.tomorrow_TD_btn);
        tomorrow_temperature_btn = findViewById(R.id.tomorrow_temperature_btn);
        tomorrow_WD_btn = findViewById(R.id.tomorrow_WD_btn);
        tomorrow_WS_btn = findViewById(R.id.tomorrow_WS_btn);
        tomorrow_Wx_btn = findViewById(R.id.tomorrow_Wx_btn);
        aftertomorrow_AT_btn = findViewById(R.id.aftertomorrow_AT_btn);
        aftertomorrow_CI_btn = findViewById(R.id.aftertomorrow_CI_btn);
        aftertomorrow_PoP6h_btn = findViewById(R.id.aftertomorrow_PoP6h_btn);
        aftertomorrow_RH_btn = findViewById(R.id.aftertomorrow_RH_btn);
        aftertomorrow_TD_btn = findViewById(R.id.aftertomorrow_TD_btn);
        aftertomorrow_temperature_btn = findViewById(R.id.aftertomorrow_temperature_btn);
        aftertomorrow_WD_btn = findViewById(R.id.aftertomorrow_WD_btn);
        aftertomorrow_WS_btn = findViewById(R.id.aftertomorrow_WS_btn);
        aftertomorrow_Wx_btn = findViewById(R.id.aftertomorrow_Wx_btn);
        attractionsRating = findViewById(R.id.attractionsRating);
        telephone = findViewById(R.id.telephone);
        PhotoImage = findViewById(R.id.PhotoImage);
        place_url = findViewById(R.id.place_url);
        place_url2 = findViewById(R.id.place_url2);
        place_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(place_URL));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {

                }
            }
        });
    }
}
