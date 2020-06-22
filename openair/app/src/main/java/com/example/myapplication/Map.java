package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class Map extends FragmentActivity implements OnMapReadyCallback {

    InputMethodManager imm;
    private GoogleMap mMap;
    private Geocoder geocoder;
    private Button button1;
    private Button button2;
    private EditText editText;


    double latitude;
    double longitude;

    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent(); /*데이터 수신*/

        latitude = intent.getExtras().getDouble("lat");
        longitude = intent.getExtras().getDouble("lng");

        editText = (EditText) findViewById(R.id.editText);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        button1 =  (Button)findViewById(R.id.button1);
        button2 = (Button)findViewById(R.id.button2);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); //getMapAsync must be called on the main thread.
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        // SupportMapFragment을 통해 레이아웃에 만든 fragment의 ID를 참조하고 구글맵을 호출한다.
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
        // 구글 맵 객체를 불러온다.

        geocoder = new Geocoder(this);


        LatLng current = new LatLng(latitude, longitude);
        // 구글 맵에 표시할 마커에 대한 옵션 설정
        MarkerOptions makerOptions = new MarkerOptions();
        MarkerOptions makerOptions1 = new MarkerOptions();
        LatLng foot1 = new LatLng(36.093161, 128.449256); //킹풋살장
        LatLng foot2 = new LatLng(36.132943, 128.459939); //강동풋살구장
        LatLng foot3 = new LatLng(36.128801, 128.331013); //구미 LM풋살파크
        LatLng foot4 = new LatLng(36.074369, 128.354129); //남구미 풋살존
        LatLng foot5 = new LatLng(36.165163, 128.357882); //비바풋살클럽
        LatLng foot6 = new LatLng(36.154098, 128.316300); //스피드풋살클럽
        LatLng foot7 = new LatLng(36.103445, 128.386118); //예스구미풋볼클럽
        LatLng golf1 = new LatLng(36.161582, 128.449619); //골프존카운티 선산
        LatLng golf2 = new LatLng(36.171798, 128.482136); //구미 컨트리 클럽
        LatLng golf3 = new LatLng(36.083211, 128.469293); //구미 마이다스 골프 아카데미
        LatLng fish1 = new LatLng(36.093635, 128.430672); //낚시꾼실내낚시터
        LatLng fish2 = new LatLng(36.054525, 128.340429); //봇또랑가든낚시터
        LatLng fish3 = new LatLng(36.097452, 128.476506); //사각지유료낚시터
        LatLng fish4 = new LatLng(36.116873, 128.329838); //형곡낚시터

        makerOptions
                .position(current)
                .title("현재 위치").snippet("내 위치");


        // 마커를 생성한다.
        mMap.addMarker(makerOptions);
        mMap.addMarker(new MarkerOptions().position(foot1).title("킹풋살장 경상북도 구미시 인동동 329-1").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).alpha(0.5f));
        mMap.addMarker(new MarkerOptions().position(foot2).title("강동풋살구장 경상북도 구미시 금전동 701-37").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).alpha(0.5f));
        mMap.addMarker(new MarkerOptions().position(foot3).title("LM 풋살파크 구미시 원평2동 원평동 1008-1번지 6층 경부선구미종합역사 구미시 경상북도 KR").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).alpha(0.5f));
        mMap.addMarker(new MarkerOptions().position(foot4).title("남구미풋살존 경상북도 구미시 오태동 산94-18").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).alpha(0.5f));
        mMap.addMarker(new MarkerOptions().position(foot5).title("비바풋살클럽 경상북도 구미시 고아읍 송림리 382-1").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).alpha(0.5f));
        mMap.addMarker(new MarkerOptions().position(foot6).title("스피드풋살클럽 경상북도 구미시 봉곡동 178").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).alpha(0.5f));
        mMap.addMarker(new MarkerOptions().position(foot7).title("예스구미풋볼클럽 경상북도 구미시 공단동 257-16").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).alpha(0.5f));
        mMap.addMarker(new MarkerOptions().position(golf1).title("골프존카운티 선산 경상북도 구미시 산동면 강동로 953-73").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).alpha(0.5f));
        mMap.addMarker(new MarkerOptions().position(golf2).title("구미 컨트리 클럽 경상북도 구미시 장천면 송백로 229").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).alpha(0.5f));
        mMap.addMarker(new MarkerOptions().position(golf3).title("구미 마이다스 골프 아카데미 경상북도 칠곡군 가산면 학하2길 54-171").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).alpha(0.5f));
        mMap.addMarker(new MarkerOptions().position(fish1).title("낚시꾼실내낚시터 구미시 진평동 진평동 84-8번지 1층 구미시 경상북도 KR").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)).alpha(0.5f));
        mMap.addMarker(new MarkerOptions().position(fish2).title("봇또랑가든낚시터 경상북도 칠곡군 북삼읍 율리 553-5 KR").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)).alpha(0.5f));
        mMap.addMarker(new MarkerOptions().position(fish3).title("사각지 유료낚시터 경상북도 구미시 신동 57").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)).alpha(0.5f));
        mMap.addMarker(new MarkerOptions().position(fish4).title("형곡낚시터 형곡동 120-6번지 구미시 경상북도 KR").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)).alpha(0.5f));

        //카메라 위치로 옮긴다.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));

        // 입력 이벤트
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    address = null; //이전에 저장된 address 값 초기화
                    hideKeyboard(); //키보드 내리기

                    //Enter키눌렀을떄 처리
                    String str = editText.getText().toString();
                    if(str.getBytes().length <= 0){//빈값이 넘어올때의 처리
                        Toast.makeText(getApplicationContext(), "주소를 입력하세요.", Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    List<Address> addressList = null;
                    try {
                        // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
                        addressList = geocoder.getFromLocationName(
                                str, // 주소
                                10); // 최대 검색 결과 개수
                        if (addressList == null || addressList.size() <= 0) {
                            Toast.makeText(getApplicationContext(), "검색한 위치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mMap.clear(); //다시 검색할 때마다 마커 지움
                    /**구분 원리**/
                    //System.out.println(addressList.get(0).toString());
                    // 콤마를 기준으로 split
                    String[] splitStr = addressList.get(0).toString().split(",");
                    address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1, splitStr[0].length() - 2); // 주소
                    address = address.replaceFirst("대한민국 ", "");

                    String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
                    String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도

                    // 좌표(위도, 경도) 생성
                    LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    // 마커 생성
                    MarkerOptions mOptions2 = new MarkerOptions();
                    mOptions2.title("검색 결과");
                    mOptions2.snippet(address);
                    mOptions2.position(point);
                    // 마커 추가
                    mMap.addMarker(mOptions2);
                    // 해당 좌표로 화면 줌
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 10));
                    return true;
                }
                return false;
            }

            private void hideKeyboard() {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

            }
        });
        ////////////////////
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkInternetServicesStatus()){
                    showDialogForLocationServiceSetting();
                }
                else{
                    address = null; //이전에 저장된 address 값 초기화
                    imm.hideSoftInputFromWindow(button1.getWindowToken(), 0);

                    //Enter키눌렀을떄 처리
                    String str = editText.getText().toString();
                    if(str.getBytes().length <= 0){//빈값이 넘어올때의 처리
                        Toast.makeText(getApplicationContext(), "주소를 입력하세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    List<Address> addressList = null;
                    try {
                        // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
                        addressList = geocoder.getFromLocationName(
                                str, // 주소
                                10); // 최대 검색 결과 개수
                        if (addressList == null || addressList.size() <= 0) {

                            Toast.makeText(getApplicationContext(), "검색한 위치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mMap.clear(); //다시 검색할 때마다 마커 지움
                    /**구분 원리**/
                    // 콤마를 기준으로 split
                    String[] splitStr = addressList.get(0).toString().split(",");
                    address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1, splitStr[0].length() - 2); // 주소
                    address = address.replaceFirst("대한민국 ", "");

                    String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
                    String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도

                    // 좌표(위도, 경도) 생성
                    LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    // 마커 생성
                    MarkerOptions mOptions2 = new MarkerOptions();
                    mOptions2.title("검색 결과");
                    mOptions2.snippet(address);
                    mOptions2.position(point);
                    // 마커 추가
                    mMap.addMarker(mOptions2);
                    // 해당 좌표로 화면 줌
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
                }

            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (address == null) {
                    Toast.makeText(getApplicationContext(), "검색된 주소가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent addr = new Intent();

                addr.putExtra("address", address);
                setResult(RESULT_OK, addr);
                finish();
            }
        });

    }
    //현재 인터넷서비스 상태 확인
    public boolean checkInternetServicesStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo state = connectivityManager.getActiveNetworkInfo();

        if(state != null && state.isConnected() && state.getType() == ConnectivityManager.TYPE_WIFI)    return true;
        else return false;
    }
    //gps, INTERNET 활성화 다이얼로그 생성
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("인터넷 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 무선 네트워크 연결이 필요합니다.");
        builder.setCancelable(false);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(callGPSSettingIntent);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.create().show();
    }
}