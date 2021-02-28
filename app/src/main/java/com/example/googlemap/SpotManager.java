package com.example.googlemap;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import androidx.annotation.NonNull;

public class SpotManager {
    private static SpotManager instance;
    private String API_KEY = "AIzaSyC3wtAGQhgGgMSyVstzl9GsPPm6k_BfxlE";

    //public static final String testUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=ATtYBwL3j6ld_gLlIsufApGwPmlV9RxB9ESW8sjI98wXetND2wtgB69_GFN5hympREXCkDP5hJb4nsc5OYrS_vfhIxuhjWW0dIT4ELT4FklD13qQ425SV8dco9oURhvfHAdLZ8hfjXiqcVzS4laFvNnPo1jOkl73ihsJLeSy-EYRZzsDWC41&key=AIzaSyC3wtAGQhgGgMSyVstzl9GsPPm6k_BfxlE";
    private Set<Spot> spotSet = new HashSet<>();
    private int finishCounts = -1;

    private SpotManager() {
    }

    public static SpotManager getInstance() {
        if (instance == null) {
            instance = new SpotManager();
        }
        return instance;
    }

    public void addSpot(Spot spot) {
        spotSet.add(spot);
    }

    public Set<Spot> getSpotSet() {
        return spotSet;
    }

    public ArrayList<Spot> getSpotListByCityNameAndRegionId(String cityName, String regionId) {
        ArrayList<SpotManager.Spot> spotList = new ArrayList<>();
        for (SpotManager.Spot spot : spotSet) {
            if (spot.getCityName().equals(cityName) && spot.getRegionId().equals(regionId)) {
                spotList.add(spot);
            }
        }
        return spotList;
    }

    private void getSpotDetailsByType(final String rootName, final String regionId, final String type, final GetProductsCallback callback) {
        FirebaseFirestore.getInstance().collection(rootName)
                .document(regionId).collection(type)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("test3", document.getData().get("URL") + "");
                                Log.e("test3", document.getData().get("name") + "");
                                SpotManager.Spot spot = new SpotManager.Spot();
                                spot.setCityName(rootName);
                                spot.setName((String) document.getData().get("name"));
                                spot.setPlace_id(document.getId());
                                spot.setRegionId(regionId);
                                spot.setType(type);
                                spot.setUrl((String) document.getData().get("URL"));
                                SpotManager.getInstance().addSpot(spot);
                            }
                            finishCounts--;
                            Log.e("finishCounts", "finishCounts: "
                                    + finishCounts
                                    + ",regionId " + regionId
                                    + ", type " + type);
                            if (finishCounts <= 0) {
                                Log.e("enter if", "finishCounts: "
                                        + finishCounts
                                        + ",regionId " + regionId
                                        + ", type " + type);
                                callback.success();
                            }
                        } else {
                            callback.fail(task.getException());
                        }
                    }
                });

    }

    public void getSpotDetailsByCity(final String cityName, final GetProductsCallback callback) {
        FirebaseFirestore.getInstance().collection(cityName)//最外面的資料
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        //SpotManager.Spot spot = new SpotManager.Spot();
                        finishCounts = task.getResult().size() * 6;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {//document文件
                                getSpotDetailsByType(cityName, document.getId(), "一般景點", callback);
                                getSpotDetailsByType(cityName, document.getId(), "休閒農牧場或觀光工廠(DIY)", callback);
                                getSpotDetailsByType(cityName, document.getId(), "博物館或展示廳", callback);
                                getSpotDetailsByType(cityName, document.getId(), "古蹟或歷史建築", callback);
                                getSpotDetailsByType(cityName, document.getId(), "百貨公司或商圈", callback);
                                getSpotDetailsByType(cityName, document.getId(), "運動步道", callback);
                            }
                        } else {
                            Log.w("TEST", "Error getting documents.", task.getException());
                            callback.fail(task.getException());
                        }
                    }
                });
    }

    public void getSpotDetailByPlaceId(final String placeId, Context context, final DetailCallback detailCallback) {
        Places.initialize(context, API_KEY);
        PlacesClient placesClient = Places.createClient(context);
        //Specify the fields to return.
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.ADDRESS, Place.Field.OPENING_HOURS, Place.Field.PHONE_NUMBER, Place.Field.RATING
                , Place.Field.WEBSITE_URI, Place.Field.PHOTO_METADATAS);
        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
        //final FetchPlaceResponse request1 = FetchPlaceResponse.newInstance((Place) placeTypes);

        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse response) {
                Place place = response.getPlace();
                Log.i("test1", "Place found: " + place.getName() + " " + place.getAddress() + " " + place.getOpeningHours() + " "
                        + place.getPhoneNumber() + " " + place.getRating() + " " + place.getWebsiteUri() + " " + place.getPhotoMetadatas() + " " + place.getViewport());
                Log.i("test200000", "Place found: " + place.getWebsiteUri());

                OpeningHours openingHours = place.getOpeningHours();
                List<String> openingHoursString = openingHours == null ? null : openingHours.getWeekdayText();
                Uri uri = place.getWebsiteUri();
                Uri uriString = uri == null ? null : place.getWebsiteUri();
                detailCallback.success(openingHoursString, place.getPhoneNumber(), place.getRating().toString(), place.getPhotoMetadatas(), uriString);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e("test2", "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                    detailCallback.fail(exception);
                    // TODO: Handle error with given status code.
                }
            }
        });
    }
    public void getPlaceSpotDetailByPlaceType(Context context){
        Places.initialize(context, API_KEY);
        PlacesClient placesClient = Places.createClient(context);
        final List<Place.Type> placeTypes = Arrays.asList(Place.Type.RESTAURANT, Place.Type.HOSPITAL);
        //final List<Place.Type>placeTypes = Arrays.asList(Place.Type.RESTAURANT,Place.Type.HOSPITAL);
        //final FetchPlaceResponse fetchPlaceRequest = FetchPlaceResponse.newInstance((Place) placeTypes);

    }

    //    public void loadImage(ImageView imageView,String url1) {
//
//        try {
//            URL url = new URL(url1);
//            imageView.setImageBitmap(BitmapFactory.decodeStream(url.openStream()));
//        } catch (Exception e) {
//
//        }
//    }
    public void getHttpBitmap(final String url, final ArrayList<Bitmap> photoBitmaps, final Runnable runnable) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL myFileURL;

                    Log.e("test", "enter getHttpBitmap");
                    myFileURL = new URL(url);
//獲得連線
                    HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
//設定超時時間為6000毫秒，conn.setConnectionTiem(0);表示沒有時間限制
                    conn.setConnectTimeout(6000);
//連線設定獲得資料流
                    conn.setDoInput(true);
//不使用快取
                    conn.setUseCaches(false);
//這句可有可無，沒有影響
//conn.connect();
//得到資料流
                    Log.e("test", "this line ok");
                    InputStream is = conn.getInputStream();
                    Log.e("test", "this line 2 ok");
//解析得到圖片
                    final Bitmap bitmap = BitmapFactory.decodeStream(is);
                    photoBitmaps.add(bitmap);

                    Log.e("test", "bitmap finish in function");

                    runnable.run();
//關閉資料流
                    is.close();
                } catch (Exception e) {
                    Log.e("testeee", "error: " + e.toString());
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public interface DetailCallback {
        void success(List opentime, String phoneNumber, String Rating, List PhotoImage, Uri uri);

        void fail(Exception e);
    }

    public interface GetProductsCallback {
        void success();

        void fail(Exception e);
    }

    public static class Spot {
        private String place_id;
        private String name;
        private String url;
        private String type;
        private String regionId;
        private String cityName;
        private String url2;

        public String getPlace_id() {
            return place_id;
        }

        public void setPlace_id(String place_id) {
            this.place_id = place_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getRegionId() {
            return regionId;
        }

        public void setRegionId(String regionId) {
            this.regionId = regionId;
        }

//        public String getUrl2() {
////            return url2;
////        }
////
////        public void setUrl2(String url2) {
////            this.url2 = url2;
////        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Spot spot = (Spot) o;
            return Objects.equals(place_id, spot.place_id) &&
                    Objects.equals(name, spot.name) &&
                    Objects.equals(url, spot.url) &&
                    Objects.equals(type, spot.type) &&
                    Objects.equals(regionId, spot.regionId) &&
                    Objects.equals(cityName, spot.cityName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(place_id, name, url, type, regionId, cityName);
        }
    }
}
