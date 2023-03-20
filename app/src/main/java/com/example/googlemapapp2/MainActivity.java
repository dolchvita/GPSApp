package com.example.googlemapapp2;

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
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdate;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private String TAG = this.getClass().getName();

    SupportMapFragment mapFragment;

    private GoogleMap map;  // 맵 객체 멤버변수로 가지고 있기!

    ArrayList<LatLng> latlngList = new ArrayList<LatLng>(); // 라인으로 그릴 위도 경도를 저장하는 리스트

    LatLng sinchon=new LatLng(37.56288275392123, 126.94683778297095);

    /*-------------------------------------------------*/
    private List<String > listProviders;
    private LocationManager locationManager;


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
        //Button bt_regist = findViewById(R.id.bt_regist);
        Button bt_start=findViewById(R.id.bt_start);
        bt_line.setOnClickListener((v) -> {
            createArray();
        });

        bt_start.setOnClickListener((v)->{

        });

        /*-------------------------------------------------*/

        checkGrant();

    }

    // 사용자에게 권한을 부여하는 함수
    public void checkGrant(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재요청 확인
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)){
                //권한 재요청
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }else{
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
        }

        //1. 위치관리자 객체 생성
        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //여기를 NETWORK_PROVIDER로 잡는게 더 정확할 듯 싶다.
        Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(lastKnownLocation !=null){
            double lat = lastKnownLocation.getLatitude();
            double lng = lastKnownLocation.getLongitude();
            Log.d(TAG, "받아온 경도 값은 : "+lng+",,,받아온 위도값은 : "+lat);

        }
    }


    // 구글맵에 대한 초기 설정 함수
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
/*        MarkerOptions options=new MarkerOptions();
        options.position(new LatLng(37.55555, 126.88889));
        googleMap.addMarker(options);*/

        map=googleMap;
        Log.d(TAG, "넘어온 맵 객체 "+map);


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sinchon, 13));
      /*  map.animateCamera(CameraUpdateFactory.zoomIn());

        map.moveCamera(CameraUpdateFactory.newLatLng(sinchon));*/

        // 폴리라인 그리는 거 테스트 중
        //createPolyLine();

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                // 실시간으로 테스트중!
/*                latlngList.add(latLng);

                Log.d(TAG, "이 객체는 뭘까"+latLng);
                Log.d(TAG, "확인중"+latLng);
                MarkerOptions options=new MarkerOptions();
                options.position(latLng);
                options.title("출발");
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                // 정의한 마커 추가
                map.addMarker(options).showInfoWindow();*/

            }
        });
    }



    // 리스트를 받아서 라인을 그려주는 함수
    public void createPolyLine(JSONArray jsonArray){

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

        // 이제 해야할 일 : 제이슨 리스트로 담아서 보내기
        // 보내거나 db에 저장하거나... 디비작업은?


        option.color(Color.RED);
        // option.addSpan(new StyleSpan(Color.RED));
        option.width(12);
        option.geodesic(true);
        //option.fillColor(Color.BLUE);

        Polyline polyline = map.addPolyline(option);
        Log.d(TAG, "넘어온 라인 객체 "+polyline);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sinchon, 13));

    }



    // 저장하기 누르면 클릭된 위도, 경도를 담은 리스트를 그리기 함수로 넘김
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

    /*-----------------------------------------------------------------------------------------------------*/

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = 0.0;
        double longitude = 0.0;

        //실내에 있을 때는 잡히지 않는 그냥 거의 안잡히는 GPS_PROVIDER(GPS를 이용 위치로 제공)
        if(location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d(TAG + " GPS 위도는 :", Double.toString(latitude)+"경도는 :"+ Double.toString(longitude));
        }
        //가장 많이 잡히는 NETWORK_PROVIDER(기지국 와이파이를 이용위치로 제공)
        if(location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d(TAG + " NETWORK : ", Double.toString(latitude )+ '/' + Double.toString(longitude));
            Log.d(TAG, "한뭉터기");

            LatLng latLng=null;
            /*================================================*/

            // 실시간으로 들어오는 위도 경도를 한 객체로 받는다.
            latLng=new LatLng(latitude, longitude);

            latlngList.add(latLng); // 리스트 저장

            Log.d(TAG, "이 객체는 뭘까"+latLng);
            Log.d(TAG, "확인중"+latLng);
            MarkerOptions options=new MarkerOptions();
            options.position(latLng);
            options.title("출발");
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            // 정의한 마커 추가
            map.addMarker(options).showInfoWindow();
            // 실시간으로 마커 찍어보기!!

/*            MarkerOptions options=new MarkerOptions();
            options.position(new LatLng(latitude, longitude));
            map.addMarker(options);*/



        }
        //얘는 좌표값을 구하는 것이 아닌 다른 어플리케이션이나 서비스가 좌표값을 구하면 단순히 그 값을 받아 오기만 하는
        //전달자 역할 이라는데.. 어디에 쓰이는 건지 모르겠다.
        if(location.getProvider().equals(LocationManager.PASSIVE_PROVIDER)) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d(TAG + " PASSIVE : ", Double.toString(latitude )+ '/' + Double.toString(longitude));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
        Log.d(TAG+"현재 GPS_Provider의 상태는", Integer.toString(status) );
        Log.d(TAG+"현재 GPS_Provider의 provider는", provider );
        Log.d(TAG+"현재 GPS_Provider의 extra는", String.valueOf(extras));
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates( this::onLocationChanged);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //위치정보를 업데이트 하는 최소시간을 800ms(=0.8초) 로 잡음
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 800, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 800, 0, this);
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 800, 0, this);
    }
}