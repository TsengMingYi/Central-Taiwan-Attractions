package com.example.googlemap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    private TextView Featured_attractions;
    private ImageButton next_btn;
    private TextView Tourist_factory;
    private TextView Sports_trail;
    private TextView MuseumOrExhibition_hall;
    private TextView MonumentsOrHistoric_buildings;
    private TextView Department_storeOrBusiness_district;
    private String regionId;
    private String cityName;
    private int cout = 0;
    private int cout1 = 0;
    private int cout2 = 0;
    private int cout3 = 0;
    private int cout4 = 0;
    private int cout5 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        findView();
        Intent intent = getIntent();
        regionId = intent.getStringExtra("regionId");
        cityName = intent.getStringExtra("cityName");
        Log.e("cityName", "ww" + cityName);
        SpotManager.getInstance().getSpotListByCityNameAndRegionId(cityName, regionId);
        SpotManager.getInstance().getSpotDetailsByCity(cityName, new SpotManager.GetProductsCallback() {
            @Override
            public void success() {
                Log.e("success", "success");
                ArrayList<SpotManager.Spot> spotList = SpotManager.getInstance().getSpotListByCityNameAndRegionId(cityName, regionId);
                for (SpotManager.Spot spot : spotList) {
                    Log.e("資料：", spot.getType());
                    if (spot.getType().contains("一般景點")) {
                        cout++;
                    } else if (spot.getType().contains("博物館或展示廳")) {
                        cout1++;
                    } else if (spot.getType().contains("古蹟或歷史建築")) {
                        cout2++;
                    } else if (spot.getType().contains("百貨公司或商圈")) {
                        cout3++;
                    } else if (spot.getType().contains("運動步道")) {
                        cout4++;
                    } else if (spot.getType().contains("休閒農牧場或觀光工廠(DIY)")) {
                        cout5++;
                    }
                }
                Featured_attractions.setText(cout + "");
                Tourist_factory.setText(cout5 + "");
                Sports_trail.setText(cout4 + "");
                MuseumOrExhibition_hall.setText(cout1 + "");
                MonumentsOrHistoric_buildings.setText(cout2 + "");
                Department_storeOrBusiness_district.setText(cout3 + "");
            }

            @Override
            public void fail(Exception e) {

            }
        });
//
        setTitle(regionId);

//        switch (regionId){
//            case "中區":
//
//
//        }
    }

    private void findView() {
        Featured_attractions = findViewById(R.id.Featured_attractions);
        Tourist_factory = findViewById(R.id.Tourist_factory);
        MuseumOrExhibition_hall = findViewById(R.id.MuseumOrExhibition_hall);
        MonumentsOrHistoric_buildings = findViewById(R.id.MonumentsOrHistoric_buildings);
        Department_storeOrBusiness_district = findViewById(R.id.Department_storeOrBusiness_district);
        Sports_trail = findViewById(R.id.Sports_trail);
        next_btn = findViewById(R.id.next_btn);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this, Detail.class);
                intent.putExtra("regionId", regionId);
                intent.putExtra("cityName", cityName);
                startActivity(intent);
            }
        });
    }
}
