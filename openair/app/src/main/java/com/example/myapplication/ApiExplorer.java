package com.example.myapplication;

import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.os.StrictMode;
import android.widget.TextView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ApiExplorer {
    double latitude;
    double longitude;

    public ApiExplorer(double lat, double lng){
        latitude = lat;
        longitude = lng;
    }

    class LatXLngY
    {
        double x;
        double y;

    }

    ArrayList<String> getWeather(){
        StrictMode.enableDefaults();
        boolean categorycheck = false;
        boolean valuecheck = false;

        String category= null;
        String value = null;

        String tem = null; //온도
        boolean temf = true;
        String skystat = null; //하늘상태
        boolean skyf = true;
        String hum = null; //습도
        boolean humf = true;
        String rainstat = null; // 강수상태
        boolean rainf = true;

        ArrayList<String> list = new ArrayList<>();

        long now = System.currentTimeMillis();

        Date date = new Date(now);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//        SimpleDateFormat sdf1 = new SimpleDateFormat("hhmm");
        SimpleDateFormat sdf1 = new SimpleDateFormat("hh");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
//        cal.add(Calendar.HOUR, -1);

        String getDate = sdf.format(cal.getTime());

        if(cal.get(Calendar.MINUTE) < 45)
            cal.add(Calendar.HOUR, -1);
        String getTime = sdf1.format(cal.getTime()) + "30";


//        String getTime = sdf1.format(cal.getTime());

        Log.i("datesetapi", " sdf : " + getDate);
        Log.i("datesetapi", " sdf1 : " + getTime);

        LatXLngY xy = convertGRID_GPS(latitude, longitude);

        String x = Long.toString(Math.round(xy.x));
        String y = Integer.toString((int)xy.y);

        try {
            StringBuilder urlBuilder = new StringBuilder("http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastTimeData"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=IKSdOfyn95SYlPoc8mu0z8Yr%2BOh%2FBoM6%2FnXvxjw8uzxUJpG7wLnaryk4FvVrVeVy6%2FnrEYLI5TwYMGfHOnfvnQ%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(getDate, "UTF-8")); /*‘15년 12월 1일 발표*/
            urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(getTime, "UTF-8")); /*06시30분 발표(30분 단위) - 매시각 45분 이후 호출*/
            urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(x, "UTF-8")); /*예보지점 X 좌표값*/
            urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(y, "UTF-8")); /*예보지점 Y 좌표값*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("150", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
            urlBuilder.append("&" + URLEncoder.encode("_type","UTF-8") + "=" + URLEncoder.encode("xml", "UTF-8")); /*xml(기본값), json*/
            URL url = new URL(urlBuilder.toString());


            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            Log.i("urlerror", url.toString());
            parser.setInput(url.openStream(), "UTF-8");

//            Log.i("urlerror", url.toString());

            int parserEvent = parser.getEventType();

            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG://parser가 시작 태그를 만나면 실행
                        if (parser.getName().equals("category")) { //title 만나면 내용을 받을수 있게 하자
                            categorycheck = true;
                        }
                        if (parser.getName().equals("fcstValue")) { //address 만나면 내용을 받을수 있게 하자
                            valuecheck = true;
                        }
                        break;

                    case XmlPullParser.TEXT://해당 태그의 내용인 경우
                        if (categorycheck) { //isTitle이 true일 때 태그의 내용을 저장.
                            category = parser.getText();
                            categorycheck = false;
                        }
                        if (valuecheck) { //isAddress이 true일 때 태그의 내용을 저장.
                            value = parser.getText();
                            valuecheck = false;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("item")) {
                            switch(category) {
                                case "T1H":
                                    if(temf){
                                        tem = "기온 : " + value + "˚C";
                                        list.add(2, tem);
                                        temf=false;
                                    }
                                    break;
                                case "SKY":
                                    if(skyf){
                                        skystat = value;
                                        list.add(1, skystat);
                                        skyf=false;
                                    }
                                    break;
                                case "REH":
                                    if(humf){
                                        hum = "습도 : " + value + "%";
                                        list.add(3, hum);
                                        humf = false;
                                    }

                                    break;

                                case "PTY":
                                    if(rainf){
                                        rainstat = value;
                                        list.add(0, rainstat);
                                        rainf = false;
                                    }
                                    break;
                            }
                        }
                        break;
                }
                parserEvent = parser.next();
            }
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    LatXLngY convertGRID_GPS(double lat_X, double lng_Y )
    {
        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영 위도1(degree)
        double SLAT2 = 60.0; // 투영 위도2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        double XO = 43; // 기준점 X좌표(GRID)
        double YO = 136; // 기1준점 Y좌표(GRID)

        //
        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )
        //

        double DEGRAD = Math.PI / 180.0;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);
        LatXLngY rs = new LatXLngY();

        double ra = Math.tan(Math.PI * 0.25 + (lat_X) * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        double theta = lng_Y * DEGRAD - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;
        rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5);
        rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);

        return rs;
    }

}
