package com.example.coronamaskmap.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.coronamaskmap.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class home_fragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnMarkerClickListener{

    final String LOG = "테스트";
    final String TAG = getClass().getSimpleName() + "::";
    private View view;
    private MapView mapView = null;
    private GoogleMap mgoogleMap;
    private Marker currentMarker = null;
    public static ArrayList<corona_item> corona_list = new ArrayList();
    private int apiRequestCount;
    private ArrayList<Marker> markerList = new ArrayList();

    public static boolean startFlagForCoronaApi;
    private FusedLocationProviderClient mFusedLocationProviderClient; // Deprecated된 FusedLocationApi를 대체
    private LocationRequest locationRequest;
    private Location mCurrentLocatiion;

    private final LatLng mDefaultLocation = new LatLng(37.555183, 126.970752);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000 * 60 * 1;  // 1분 단위 시간 갱신
    private static final int FASTEST_UPDATE_INTERVAL_MS = 1000 * 30 ; // 30초 단위로 화면 갱신

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null){
            mCurrentLocatiion = savedInstanceState.getParcelable(KEY_LOCATION);
            CameraPosition mcameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        view = inflater.inflate(R.layout.home_fragment, container, false);

        mapView = (MapView)view.findViewById(R.id.map);
        if (mapView != null){
            mapView.onCreate(savedInstanceState);
        }

        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // Fragement에서의 OnCreateView를 마치고, Activity에서 onCreate()가 호출되고 나서 호출되는 메소드이다.
        // Activity와 Fragment의 뷰가 모두 생성된 상태로, View를 변경하는 작업이 가능한 단계다.
        super.onActivityCreated(savedInstanceState);

        //액티비티가 처음 생성될 때 실행되는 함수
        MapsInitializer.initialize(getActivity());

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // 정확도를 최우선적으로 고려
                .setInterval(UPDATE_INTERVAL_MS) // 위치가 Update 되는 주기
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS); // 위치 획득후 업데이트되는 주기

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);

        // FusedLocationProviderClient 객체 생성
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mgoogleMap = googleMap;
        setDefaultLocation(); // GPS를 찾지 못하는 장소에 있을 경우 지도의 초기 위치가 필요함.

        getLocationPermission();

        updateLocationUI();

        getDeviceLocation();
        mgoogleMap.setOnCameraIdleListener(this);
        //버튼클릭리스너
        mgoogleMap.setOnMarkerClickListener(this);
    }

    private void setDefaultLocation() {
        if (currentMarker != null){
            currentMarker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(mDefaultLocation);
        markerOptions.title("위치정보 가져올 수 없음");
        markerOptions.snippet("위치 권한과 GPS 활성 여부 확인하세요");
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mgoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 15);
        mgoogleMap.moveCamera(cameraUpdate);
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        }else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (mgoogleMap == null){
            return;
        }
        try {
            if (mLocationPermissionGranted){
                mgoogleMap.setMyLocationEnabled(true);
                mgoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            }else {
                mgoogleMap.setMyLocationEnabled(false);
                mgoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mCurrentLocatiion = null;
                getLocationPermission();
            }
        }catch (SecurityException e){
            Log.e("Exception: %s", e.getMessage());
        }
    }

    String getCurrentAddress(LatLng latlng) {
        // 위치 정보와 지역으로부터 주소 문자열을 구한다.
        List<Address> addressList = null ;
        Geocoder geocoder = new Geocoder( getActivity(), Locale.getDefault());

        // 지오코더를 이용하여 주소 리스트를 구한다.
        try {
            addressList = geocoder.getFromLocation(latlng.latitude,latlng.longitude,1);
        } catch (IOException e) {
            Toast. makeText( getActivity(), "위치로부터 주소를 인식할 수 없습니다. 네트워크가 연결되어 있는지 확인해 주세요.", Toast.LENGTH_SHORT ).show();
            e.printStackTrace();
            return "주소 인식 불가" ;
        }

        if (addressList.size() < 1) { // 주소 리스트가 비어있는지 비어 있으면
            return "해당 위치에 주소 없음" ;
        }

        // 주소를 담는 문자열을 생성하고 리턴
        Address address = addressList.get(0);
        StringBuilder addressStringBuilder = new StringBuilder();
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressStringBuilder.append(address.getAddressLine(i));
            if (i < address.getMaxAddressLineIndex())
                addressStringBuilder.append("\n");
        }

        return addressStringBuilder.toString();
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                Location location = locationList.get(locationList.size() - 1);

                LatLng currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());

                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "Time :" + CurrentTime() + " onLocationResult : " + markerSnippet);

                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);
                mCurrentLocatiion = location;
            }
        }

    };

    private String CurrentTime(){
        Date today = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
        return time.format(today);
    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if (currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        currentMarker = mgoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mgoogleMap.moveCamera(cameraUpdate);
    }

    private void getDeviceLocation() {
        try {
            if(mLocationPermissionGranted) {
                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        }catch (SecurityException e){
            Log.e("Exception : %s", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onCameraIdle() {
        removeMarkerAll();
        Log.d(LOG, TAG + "onCameraIdle()");
        String lat = String.valueOf(mgoogleMap.getCameraPosition().target.latitude);
        String lon = String.valueOf(mgoogleMap.getCameraPosition().target.longitude);
        startFlagForCoronaApi = true;
        CoronaApi coronaApi = new CoronaApi();
        coronaApi.execute(lat, lon, "");

        apiRequestCount = 0;
        final Handler temp_Handler = new Handler();
        temp_Handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(LOG, TAG + "run()");
                if (apiRequestCount < 100) {
                    Log.d(LOG, TAG + "apiRequestCount < 100");
                    if (startFlagForCoronaApi) {
                        Log.d(LOG, TAG + "startFlagForCoronaApi");
                        apiRequestCount++;
                        temp_Handler.postDelayed(this, 100);
                    }else {
                        Log.d(LOG, TAG + "before drawMarker()");
                        drawMarker();
                    }
                }else {
                    Toast.makeText(getActivity(), "호출에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                }
            }
        },100);

    }

    private void removeMarkerAll() {
        for (Marker marker : markerList) {
            marker.remove();
        }
    }

    private void drawMarker() {
        Log.d(LOG, TAG + "drawMarker()");
        for (int i = 0; i < corona_list.size(); i++){
            corona_item item = corona_list.get(i);
            String remain_stat = item.getRemain_stat();
            switch (remain_stat) {
                case "plenty" : {
                    remain_stat = "100개 이상";
                    Marker marker = mgoogleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(item.getLat()), Double.parseDouble(item.getLng())))
                            .title(item.getName())
                            .snippet(item.getAddr() + "@" + item.getCreated_at() + "@" + item.getRemain_stat() + "@" + item.getStock_at() + "@" + item.getType())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green)));
                    markerList.add(marker);
                    break;
                }
                case "some" : {
                    remain_stat = "30개 이상 100개 미만";
                    Marker marker = mgoogleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(item.getLat()), Double.parseDouble(item.getLng())))
                            .title(item.getName())
                            .snippet(item.getAddr() + "@" + item.getCreated_at() + "@" + item.getRemain_stat() + "@" + item.getStock_at() + "@" + item.getType())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue)));
                    markerList.add(marker);
                    break;
                }
                case "few" : {
                    remain_stat = "2개 이상 30개 미만";
                    Marker marker = mgoogleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(item.getLat()), Double.parseDouble(item.getLng())))
                            .title(item.getName())
                            .snippet(item.getAddr() + "@" + item.getCreated_at() + "@" + item.getRemain_stat() + "@" + item.getStock_at() + "@" + item.getType())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_yellow)));
                    markerList.add(marker);
                    break;
                }
                case "empty" : {
                    remain_stat = "1개 이하";
                    Marker marker = mgoogleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(item.getLat()), Double.parseDouble(item.getLng())))
                            .title(item.getName())
                            .snippet(item.getAddr() + "@" + item.getCreated_at() + "@" + item.getRemain_stat() + "@" + item.getStock_at() + "@" + item.getType())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_red)));
                    markerList.add(marker);
                    break;
                }
            }
        }
        return;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        if (mFusedLocationProviderClient != null) {
            Log.d(TAG, "onStop : removeLocationUpdates");
            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (mLocationPermissionGranted) {
            Log.d(TAG, "onResume : requestLocationUpdates");
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            if (mgoogleMap!=null)
                mgoogleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() { // 프래그먼트와 관련된 View 가 제거되는 단계
        super.onDestroyView();
        if (mFusedLocationProviderClient != null) {
            Log.d(TAG, "onDestroyView : removeLocationUpdates");
            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(getContext(),marker.getTitle() + "\n"+ marker.getPosition(), Toast.LENGTH_LONG).show();
        return false;
    }
}
