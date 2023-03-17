package com.example.googlemapapp2;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private String TAG = this.getClass().getName();

    SupportMapFragment mapFragment;

    private GoogleMap map;  // 맵 객체 멤버변수로 가지고 있기!

    ArrayList<LatLng> latlngList = new ArrayList<LatLng>();

    // GPS 얻어오기
    LocationManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1-1) 맵 전용 프레그먼트 생성
        mapFragment = SupportMapFragment.newInstance();

        // 1-2) 맵 프레그먼트 제어 객체 사용  -- 이 객체의 역할은? 트랜잭션 처리!
        FragmentManager fragmentManager = getSupportFragmentManager();

        mapFragment.getMapAsync(this);

        // 1-3) 트랜잭션 객체의 역할은?  -- 지도 보여줄 UI 처리
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, mapFragment);
        transaction.commit();

        Button bt_line = findViewById(R.id.bt_line);
        Button bt_regist = findViewById(R.id.bt_regist);
        Button bt_gps = findViewById(R.id.bt_gps);

        bt_line.setOnClickListener((v) -> {
            createArray();
        });

        bt_gps.setOnClickListener((v) -> {
            getGPS();
        });


        // 전송 버튼
        bt_regist.setOnClickListener((v) -> {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    //regist();
                }
            };
            thread.start();
        });

    }

    // 스프링 컨트롤러에게 전달할 메서드

    public void getGPS() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions( MainActivity.this, new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
        }else {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                String provider = location.getProvider();
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                double altitude = location.getAltitude();

                Log.d(TAG,"테스트!"+longitude);
                Log.d(TAG,"테스트!"+latitude);
                Log.d(TAG,"테스트!"+altitude);
            }
        }
    }



    // 저장하기 누르면 클릭된 위도, 경도를 담은 리스트
    public void createArray(){
        Log.d(TAG, "클릭 감지");

        Log.d(TAG, "리스트가 쌓일까? "+latlngList);

        JSONArray jsonArray=new JSONArray();

        for(int i=0; i<latlngList.size(); i++){
            LatLng latLng=latlngList.get(i);
            jsonArray.put(latLng);
        }

        createPolyLine(jsonArray);

    }


    // 구글맵에 대한 설정 함수
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

/*        MarkerOptions options=new MarkerOptions();
        options.position(new LatLng(37.55555, 126.88889));
        googleMap.addMarker(options);*/

        map=googleMap;
        Log.d(TAG, "넘어온 맵 객체 "+map);



        // 폴리라인 그리는 거 테스트 중
        //createPolyLine();


        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                latlngList.add(latLng);

                Log.d(TAG, "이 객체는 뭘까"+latLng);
                Log.d(TAG, "확인중"+latLng);
                MarkerOptions options=new MarkerOptions();
                options.position(latLng);
                options.title("출발");
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));         // 여기 아이콘을 어디서 정의하죠?
                // 정의한 마커 추가
                map.addMarker(options).showInfoWindow();

            }
        });
    }

    public void createPolyLine(JSONArray jsonArray){

        // test 데이터
       /* JSONArray jsonArray=new JSONArray();
        jsonArray.put(new LatLng(37.56288275392123, 126.94683778297095));
        jsonArray.put(new LatLng(37.55906185816793, 126.94410917188688));
        jsonArray.put(new LatLng(37.55872803568523, 126.93776684847374));
        jsonArray.put(new LatLng(37.5428234, 126.9325981));
        jsonArray.put(new LatLng(37.5498977, 126.9427769));
        jsonArray.put(new LatLng(37.5561674, 126.9392701));
        jsonArray.put(new LatLng(37.5559517, 126.9325981));
        */


        // 폴리라인 객체
        PolylineOptions option = new PolylineOptions();

        // 어레이 테스트
        for(int i=0; i<jsonArray.length(); i++){
            try {
                LatLng latLng=(LatLng)jsonArray.get(i);
                option.add(latLng);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        /*
        option.add(new LatLng(37.56288275392123, 126.94683778297095),
                new LatLng(37.55906185816793, 126.94410917188688),
                new LatLng(37.55872803568523, 126.93776684847374),
                new LatLng(37.5428234, 126.9325981),
                new LatLng(37.5498977, 126.9427769),
                new LatLng(37.5561674, 126.9392701),
                new LatLng(37.5559517, 126.9325981)
                );


         */
        // 이제 해야할 일 : 제이슨 리스트로 담아서 보내기
        // 보내거나 db에 저장하거나... 디비작업은?


        option.color(Color.RED);
        // option.addSpan(new StyleSpan(Color.RED));
        option.width(12);
        option.geodesic(true);
        //option.fillColor(Color.BLUE);

        Polyline polyline = map.addPolyline(option);
        Log.d(TAG, "넘어온 라인 객체 "+polyline);
        LatLng sinchon=new LatLng(37.56288275392123, 126.94683778297095);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sinchon, 13));



    }


}