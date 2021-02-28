package com.example.googlemap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //private String placeId = "ChIJeZhxjmg9aTQRu4ClJlnbzHk";

    public static final String HOME = "中部";
    public static final String MIAOLI = "苗栗縣";
    public static final String TAIZHONG = "台中市";
    public static final String NANTOU = "南投縣";
    public static final String ZHANGHUA = "彰化縣";
    public static final String YUNLIN = "雲林縣";
    private static final int REQUEST_CODE = 123;
//    public static final String HOME = "HOME"; todo add other keys

    private HashMap<String, ArrayList<RegionData>> regionMap = new HashMap<>();
    private Spinner spList;
    private ImageView tvInfo;
    private ImageButton next_btn;
    private String currentSelectCity = "";
    private String currentSelectRegion = "";
    private String listSelectName = "";
    private TextView text1;

    public void switchRegion(String region) {

        ArrayList<RegionData> regionDataList = regionMap.get(region);
        spList.setAdapter(new MyArrayAdapter(this, regionDataList));
        spList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {//Spinner點擊後
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RegionData mData = (RegionData) parent.getItemAtPosition(position);
                tvInfo.setImageResource(mData.getResId());
                listSelectName = mData.getName();
                setTitle(mData.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });//點擊事件
    }

    @Override
    public void onBackPressed() {
        if (currentSelectCity.length() == 0 && currentSelectRegion.length() == 0) {
            super.onBackPressed();
        } else if (currentSelectRegion.length() == 0) {
            text1.setText("請先選擇直轄市或縣區域");
            switchRegion(HOME);
        } else if (currentSelectCity.length() != 0) {
            text1.setText("請先選擇直轄市或縣區域");
            switchRegion(HOME);
        }
    }

//    private void getSpotDetailsByType(final String rootName, final String regionId, final String type) {
//        FirebaseFirestore.getInstance().collection(rootName)
//                .document(regionId).collection(type)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.e("test3", document.getData().get("URL") + "");
//                                Log.e("test3", document.getData().get("name") + "");
//                                SpotManager.Spot spot = new SpotManager.Spot();
//                                spot.setCityName(rootName);
//                                spot.setName((String) document.getData().get("name"));
//                                spot.setPlace_id(document.getId());
//                                spot.setRegionId(regionId);
//                                spot.setType(type);
//                                spot.setUrl((String) document.getData().get("URL"));
//                                SpotManager.getInstance().addSpot(spot);
//                            }
//                        }
//                    }
//                });
//
//    }
//
//    private void getSpotDetailsByCity(final String cityName) {
//        FirebaseFirestore.getInstance().collection(cityName)//最外面的資料
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {//document文件
//                                getSpotDetailsByType(cityName, document.getId(), "一般景點");
//                                getSpotDetailsByType(cityName, document.getId(), "休閒農牧場或觀光工廠DIY");
//                                getSpotDetailsByType(cityName, document.getId(), "博物館或展示廳");
//                                getSpotDetailsByType(cityName, document.getId(), "古蹟或歷史建築");
//                                getSpotDetailsByType(cityName, document.getId(), "百貨公司或商圈");
//                                getSpotDetailsByType(cityName, document.getId(), "運動步道");
//                            }
//                        } else {
//                            Log.w("TEST", "Error getting documents.", task.getException());
//                        }
//                    }
//                });
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        initial();
        switchRegion(HOME);
        //getSpotDetailsByCity("台中市");
//        FirebaseFirestore.getInstance().collection("苗栗縣")//最外面的資料
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {//document文件
//
//
//                                Map data = document.getData();
//                                Log.e("test", document.getId());
//                                FirebaseFirestore.getInstance().collection("苗栗縣")
//                                        .document(document.getId()).collection("景點")
//                                        .get()
//                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                if (task.isSuccessful()) {
//                                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                                        Log.e("test3", document.getData().get("URL") + "");
//                                                    }
//                                                }
//                                            }
//                                        });
//
//                            }
//
//                        } else {
//                            Log.w("TEST", "Error getting documents.", task.getException());
//
//                        }
//                    }
//                });

//        Places.initialize(getApplicationContext(), API_KEY);
//        placesClient = Places.createClient(this);
//         //Specify the fields to return.
//        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
//                Place.Field.ADDRESS, Place.Field.OPENING_HOURS,Place.Field.PHONE_NUMBER);
//
//        // Construct a request object, passing the place ID and fields array.
//        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
//
//        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
//            @Override
//            public void onSuccess(FetchPlaceResponse response) {
//                Place place = response.getPlace();
//                Log.i("test1", "Place found: " + place.getName()+" "+place.getAddress()+" "+place.getOpeningHours()+" "
//                +place.getPhoneNumber());
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                if (exception instanceof ApiException) {
//                    final ApiException apiException = (ApiException) exception;
//                    Log.e("test2", "Place not found: " + exception.getMessage());
//                    final int statusCode = apiException.getStatusCode();
//                    // TODO: Handle error with given status code.
//                }
//            }
//        });
    }

    private void initial() {
        WeatherDataHelper.prepare();
        SpeechManager.prepare(MainActivity.this);
//        WeatherDataHelper.getCityDetail("台中市", new WeatherDataHelper.SuccessCallback() {
//            @Override
//            public void run(JSONObject json) {
//                Log.e("WeatherDetail:",json.toString());
//            }
//        });

        ArrayList<RegionData> arrayList = new ArrayList<>();
        arrayList.add(new RegionData(R.drawable.p1, MIAOLI, HOME));
        arrayList.add(new RegionData(R.drawable.p2, TAIZHONG, HOME));
        arrayList.add(new RegionData(R.drawable.p3, NANTOU, HOME));
        arrayList.add(new RegionData(R.drawable.p4, ZHANGHUA, HOME));
        arrayList.add(new RegionData(R.drawable.p5, YUNLIN, HOME));
        regionMap.put(HOME, arrayList);
        arrayList = new ArrayList<>();
        arrayList.add(new RegionData(R.drawable.m1, "苑裡鎮", "苗栗"));
        arrayList.add(new RegionData(R.drawable.m2, "三義鄉", "苗栗"));
        arrayList.add(new RegionData(R.drawable.m3, "卓蘭鎮", "苗栗"));
        arrayList.add(new RegionData(R.drawable.m4, "泰安鄉", "苗栗"));
        arrayList.add(new RegionData(R.drawable.m5, "通霄鎮", "苗栗"));
        arrayList.add(new RegionData(R.drawable.m6, "銅鑼鄉", "苗栗"));
        arrayList.add(new RegionData(R.drawable.m7, "大湖鄉", "苗栗"));
        arrayList.add(new RegionData(R.drawable.m8, "西湖鄉", "苗栗"));
        arrayList.add(new RegionData(R.drawable.m9, "苗栗市", "苗栗"));
        arrayList.add(new RegionData(R.drawable.m10, "公館鄉", "苗栗"));
        arrayList.add(new RegionData(R.drawable.m11, "獅潭鄉", "苗栗"));
        arrayList.add(new RegionData(R.drawable.m12, "南庄鄉", "苗栗"));
        arrayList.add(new RegionData(R.drawable.m13, "後龍鎮", "苗栗"));
        arrayList.add(new RegionData(R.drawable.m14, "造橋鄉", "苗栗"));
        arrayList.add(new RegionData(R.drawable.m15, "頭屋鄉", "苗栗"));
        arrayList.add(new RegionData(R.drawable.m16, "竹南鎮", "苗栗"));
        arrayList.add(new RegionData(R.drawable.m17, "頭份市", "苗栗"));
        arrayList.add(new RegionData(R.drawable.m18, "三灣鄉", "苗栗"));
        regionMap.put(MIAOLI, arrayList);
        // todo ADD OTHER DATA
        arrayList = new ArrayList<>();
        arrayList.add(new RegionData(R.drawable.zhongqu, "中區", "台中"));
        arrayList.add(new RegionData(R.drawable.beiqu, "北區", "台中"));
        arrayList.add(new RegionData(R.drawable.beitunqu, "北屯區", "台中"));
        arrayList.add(new RegionData(R.drawable.nanqu, "南區", "台中"));
        arrayList.add(new RegionData(R.drawable.nantunqu, "南屯區", "台中"));
        arrayList.add(new RegionData(R.drawable.houliqu, "后里區", "台中"));
        arrayList.add(new RegionData(R.drawable.hepingqu, "和平區", "台中"));
        arrayList.add(new RegionData(R.drawable.waipuqu, "外埔區", "台中"));
        arrayList.add(new RegionData(R.drawable.daanqu, "大安區", "台中"));
        arrayList.add(new RegionData(R.drawable.dajiaqu, "大甲區", "台中"));
        arrayList.add(new RegionData(R.drawable.daduqu, "大肚區", "台中"));
        arrayList.add(new RegionData(R.drawable.daliqu, "大里區", "台中"));
        arrayList.add(new RegionData(R.drawable.dayaqu, "大雅區", "台中"));
        arrayList.add(new RegionData(R.drawable.taipingqu, "太平區", "台中"));
        arrayList.add(new RegionData(R.drawable.xinshequ, "新社區", "台中"));
        arrayList.add(new RegionData(R.drawable.dongshiqu, "東勢區", "台中"));
        arrayList.add(new RegionData(R.drawable.dongqu, "東區", "台中"));
        arrayList.add(new RegionData(R.drawable.wuqiqu, "梧棲區", "台中"));
        arrayList.add(new RegionData(R.drawable.shaluqu, "沙鹿區", "台中"));
        arrayList.add(new RegionData(R.drawable.qingshuiqu, "清水區", "台中"));
        arrayList.add(new RegionData(R.drawable.tanziqu, "潭子區", "台中"));
        arrayList.add(new RegionData(R.drawable.wuriqu, "烏日區", "台中"));
        arrayList.add(new RegionData(R.drawable.shigangqu, "石岡區", "台中"));
        arrayList.add(new RegionData(R.drawable.shengangqu, "神岡區", "台中"));
        arrayList.add(new RegionData(R.drawable.xiqu, "西區", "台中"));
        arrayList.add(new RegionData(R.drawable.xitunqu, "西屯區", "台中"));
        arrayList.add(new RegionData(R.drawable.fengyuanqu, "豐原區", "台中"));
        arrayList.add(new RegionData(R.drawable.wufengqu, "霧峰區", "台中"));
        arrayList.add(new RegionData(R.drawable.longjingqu, "龍井區", "台中"));
        regionMap.put(TAIZHONG, arrayList);
        arrayList = new ArrayList<>();
        arrayList.add(new RegionData(R.drawable.zhongliaoxiang, "中寮鄉", "南投"));
        arrayList.add(new RegionData(R.drawable.renaixiang, "仁愛鄉", "南投"));
        arrayList.add(new RegionData(R.drawable.xinyixiang, "信義鄉", "南投"));
        arrayList.add(new RegionData(R.drawable.nantoushi, "南投市", "南投"));
        arrayList.add(new RegionData(R.drawable.mingjianxiang, "名間鄉", "南投"));
        arrayList.add(new RegionData(R.drawable.guoxingxiang, "國姓鄉", "南投"));
        arrayList.add(new RegionData(R.drawable.pulizhen, "埔里鎮", "南投"));
        arrayList.add(new RegionData(R.drawable.shuilixiang, "水里鄉", "南投"));
        arrayList.add(new RegionData(R.drawable.zhushanzhen, "竹山鎮", "南投"));
        arrayList.add(new RegionData(R.drawable.caotunzhen, "草屯鎮", "南投"));
        arrayList.add(new RegionData(R.drawable.jijizhen, "集集鎮", "南投"));
        arrayList.add(new RegionData(R.drawable.yuchixiang, "魚池鄉", "南投"));
        arrayList.add(new RegionData(R.drawable.luguxiang, "鹿谷鄉", "南投"));
        regionMap.put(NANTOU, arrayList);
        arrayList = new ArrayList<>();
        arrayList.add(new RegionData(R.drawable.yuanlinshi, "員林市", "彰化"));
        arrayList.add(new RegionData(R.drawable.puxinxiang, "埔心鄉", "彰化"));
        arrayList.add(new RegionData(R.drawable.puyanxiang, "埔鹽鄉", "彰化"));
        arrayList.add(new RegionData(R.drawable.beitouxiang, "埤頭鄉", "彰化"));
        arrayList.add(new RegionData(R.drawable.lugangzhen, "鹿港鎮", "彰化"));
        arrayList.add(new RegionData(R.drawable.xizhouxiang, "溪州鄉", "彰化"));
        arrayList.add(new RegionData(R.drawable.xihuzhen, "溪湖鎮", "彰化"));
        arrayList.add(new RegionData(R.drawable.zhanghuashi, "彰化市", "彰化"));
        arrayList.add(new RegionData(R.drawable.fuxingxiang, "福興鄉", "彰化"));
        arrayList.add(new RegionData(R.drawable.xianxixiang, "線西鄉", "彰化"));
        arrayList.add(new RegionData(R.drawable.ershuixiang, "二水鄉", "彰化"));
        arrayList.add(new RegionData(R.drawable.erlinzhen, "二林鎮", "彰化"));
        arrayList.add(new RegionData(R.drawable.dacunxiang, "大村鄉", "彰化"));
        arrayList.add(new RegionData(R.drawable.dachengxiang, "大城鄉", "彰化"));
        arrayList.add(new RegionData(R.drawable.beidouzhen, "北斗鎮", "彰化"));
        arrayList.add(new RegionData(R.drawable.yongjingxiang, "永靖鄉", "彰化"));
        arrayList.add(new RegionData(R.drawable.tianzhongzhen, "田中鎮", "彰化"));
        arrayList.add(new RegionData(R.drawable.tianweixiang, "田尾鄉", "彰化"));
        arrayList.add(new RegionData(R.drawable.zhutangxiang, "竹塘鄉", "彰化"));
        arrayList.add(new RegionData(R.drawable.shengangxiang, "伸港鄉", "彰化"));
        arrayList.add(new RegionData(R.drawable.xiushuixiang, "秀水鄉", "彰化"));
        arrayList.add(new RegionData(R.drawable.hemeizhen, "和美鎮", "彰化"));
        arrayList.add(new RegionData(R.drawable.shetouxiang, "社頭鄉", "彰化"));
        arrayList.add(new RegionData(R.drawable.fenyuanxiang, "芬園鄉", "彰化"));
        arrayList.add(new RegionData(R.drawable.huatanxiang, "花壇鄉", "彰化"));
        arrayList.add(new RegionData(R.drawable.fangyuanxiang, "芳苑鄉", "彰化"));
        regionMap.put(ZHANGHUA, arrayList);
        arrayList = new ArrayList<>();
        arrayList.add(new RegionData(R.drawable.douliushi, "斗六市", "雲林"));
        arrayList.add(new RegionData(R.drawable.dounanzhen, "斗南鎮", "雲林"));
        arrayList.add(new RegionData(R.drawable.shuilinxiang, "水林鄉", "雲林"));
        arrayList.add(new RegionData(R.drawable.beigangzhen, "北港鎮", "雲林"));
        arrayList.add(new RegionData(R.drawable.gukengxiang, "古坑鄉", "雲林"));
        arrayList.add(new RegionData(R.drawable.taixixiang, "台西鄉", "雲林"));
        arrayList.add(new RegionData(R.drawable.sihuxiang, "四湖鄉", "雲林"));
        arrayList.add(new RegionData(R.drawable.xiluozhen, "西螺鎮", "雲林"));
        arrayList.add(new RegionData(R.drawable.dongshixiang, "東勢鄉", "雲林"));
        arrayList.add(new RegionData(R.drawable.linneixiang, "林內鄉", "雲林"));
        arrayList.add(new RegionData(R.drawable.huweizhen, "虎尾鎮", "雲林"));
        arrayList.add(new RegionData(R.drawable.lunbeixiang, "崙背鄉", "雲林"));
        arrayList.add(new RegionData(R.drawable.mailiaoxiang, "麥寮鄉", "雲林"));
        arrayList.add(new RegionData(R.drawable.citongxiang, "莿桐鄉", "雲林"));
        arrayList.add(new RegionData(R.drawable.baozhongxiang, "褒忠鄉", "雲林"));
        arrayList.add(new RegionData(R.drawable.erlunxiang, "二崙鄉", "雲林"));
        arrayList.add(new RegionData(R.drawable.kouhuxiang, "口湖鄉", "雲林"));
        arrayList.add(new RegionData(R.drawable.tukuzhen, "土庫鎮", "雲林"));
        arrayList.add(new RegionData(R.drawable.dabeixiang, "大埤鄉", "雲林"));
        arrayList.add(new RegionData(R.drawable.yuanzhangxiang, "元長鄉", "雲林"));
        regionMap.put(YUNLIN, arrayList);
    }


    private void findView() {
        spList = findViewById(R.id.spinner);
        tvInfo = findViewById(R.id.imageView);
        next_btn = findViewById(R.id.next_btn);
        text1 = findViewById(R.id.text1);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentSelectCity.length() == 0 && currentSelectRegion.length() == 0) {
                    text1.setText("再選擇鄉鎮市區域");
                    currentSelectCity = listSelectName;
                    switchRegion(listSelectName);
                    setTitle(listSelectName);
                }
                // else if (currentSelectRegion.length() == 0) {
//                    currentSelectRegion = listSelectName;
//                    Intent intent =
//                            new Intent(MainActivity.this, Main2Activity.class);
////                    intent.putExtra("source", currentSelectName);
//                    intent.putExtra("regionId", currentSelectRegion);
//                    intent.putExtra("cityName", currentSelectCity);
//                    startActivity(intent);
//                }
                else if (currentSelectCity.length() != 0) {
                    text1.setText("再選擇鄉鎮市區域");
                    if (listSelectName.equals(TAIZHONG)) {
                        currentSelectCity = listSelectName;
                        switchRegion(listSelectName);
                        setTitle(listSelectName);
                        return;
                    } else if (listSelectName.equals(MIAOLI)) {
                        currentSelectCity = listSelectName;
                        switchRegion(listSelectName);
                        setTitle(listSelectName);
                        return;
                    } else if (listSelectName.equals(NANTOU)) {
                        currentSelectCity = listSelectName;
                        switchRegion(listSelectName);
                        setTitle(listSelectName);
                        return;
                    } else if (listSelectName.equals(ZHANGHUA)) {
                        currentSelectCity = listSelectName;
                        switchRegion(listSelectName);
                        setTitle(listSelectName);
                        return;
                    } else if (listSelectName.equals(YUNLIN)) {
                        currentSelectCity = listSelectName;
                        switchRegion(listSelectName);
                        setTitle(listSelectName);
                        return;
                    }
                    currentSelectRegion = listSelectName;
                    Intent intent = new Intent(MainActivity.this, Main2Activity.class);
//                    intent.putExtra("source", currentSelectName);
                    intent.putExtra("regionId", currentSelectRegion);
                    intent.putExtra("cityName", currentSelectCity);
                    startActivity(intent);
                }
            }
        });

    }

    private boolean checkPermission2() {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        int granted = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO);
        if (granted != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPermission2();
    }

}
