package com.example.googlemap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Detail extends AppCompatActivity {
    private RecyclerView recyclerView;
    private String regionId;
    private String cityName;
    private ImageView microPhone;
    private EditText editText;
    private Adapter adapter = new Adapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        findView();
        Intent intent = getIntent();
        regionId = intent.getStringExtra("regionId");
        cityName = intent.getStringExtra("cityName");
        Log.e("cityName", "33: " + cityName);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
//        ArrayList<SpotManager.Spot> spotList = new ArrayList<>();
//        Set<SpotManager.Spot> spotSet =
//                SpotManager.getInstance().getSpotSet();
//        for (SpotManager.Spot spot : spotSet) {
//            String name = spot.getCityName();
//            if (name.equals("台中市")) {
//                spotList.add(spot);
//            }
//        }
        SpotManager.getInstance().getSpotDetailsByCity(cityName, new SpotManager.GetProductsCallback() {

            @Override
            public void success() {
                Log.e("success", "success");
                ArrayList<SpotManager.Spot> spotList = SpotManager.getInstance().getSpotListByCityNameAndRegionId(cityName, regionId);
                adapter.updateProductData(spotList);
                Log.e("ListSize", spotList.size() + "");
                setTitle(regionId);
//                        SpotManager.getInstance().getSpotDetailByPlaceId(spotList.get(0).getPlace_id(),Detail.this);
            }

            @Override
            public void fail(Exception e) {

            }
        });
        SpeechManager.getInstance().setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                Toast.makeText(Detail.this, "請說出你想查詢的景點名稱", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle results) {
                List<String> list = results.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION);
                ArrayList<SpotManager.Spot> spotList = SpotManager.getInstance().getSpotListByCityNameAndRegionId(cityName, regionId);
                for (String result : list) {
                    for (SpotManager.Spot spot : spotList) {
                        if (spot.getName().contains(result)) {
                            Intent intent =
                                    new Intent(Detail.this, SpotDetailActivity.class);
//                    intent.putExtra("source", currentSelectName);
                            intent.putExtra("regionId", spot.getRegionId());
                            intent.putExtra("cityName", spot.getCityName());
                            intent.putExtra("place_id", spot.getPlace_id());
                            intent.putExtra("place_url", spot.getUrl());
                            intent.putExtra("place_type", spot.getType());
                            intent.putExtra("attractions_name", spot.getName());
                            Detail.this.startActivity(intent);
                            return;
                        }
                    }
                }
                Toast.makeText(Detail.this,
                        "沒有符合的地名", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
    }

    private void findView() {
        recyclerView = findViewById(R.id.recyclerview);
        editText = findViewById(R.id.editText);
        microPhone = findViewById(R.id.microPhone);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
//文本显示的位置在EditText的最上方
        editText.setGravity(Gravity.BOTTOM);
        SpannableString ss = new SpannableString("一般景點|運動步道|百貨公司或商圈|休閒農牧場或觀光工廠(DIY)|博物館或展示廳|古蹟或歷史建築");
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(15, true);//设置字体大小 true表示单位是sp
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(new SpannedString(ss));
        //editText.setText("1233333333333333333333333333333333333333333333333333333333333333333333333333");
////改变默认的单行模式
        editText.setSingleLine(false);
//水平滚动设置为False
        editText.setHorizontallyScrolling(false);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String result = editable.toString();
                if (result.length() == 0) {
                    Log.e("tests:", "empty");
                } else {
                    Log.e("tests:", result);
                }
                adapter.updateFilterData1(result);
            }
        });
        microPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpeechManager.getInstance().startListen();
            }
        });
    }
}
