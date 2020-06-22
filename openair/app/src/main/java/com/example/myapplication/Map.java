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
        makerOptions
                .position(current)
                .title("현재 위치");

        // 마커를 생성한다.
        mMap.addMarker(makerOptions);

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
