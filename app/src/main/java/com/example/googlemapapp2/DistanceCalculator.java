package com.example.googlemapapp2;

import java.util.List;

public class DistanceCalculator {

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 지구 반경(m)
        final int R = 6371;

        // 라디안으로 변환
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // 미터 단위로 변환
        return distance;
    }

    public static int getDistanceFromLine(List<GPSData> gpsList){
        double lineDistance = 0;
        for(int i =0;i<gpsList.size();i++){
            if(i+1<gpsList.size()){
                GPSData pointA = gpsList.get(i);
                GPSData pointB = gpsList.get(i+1);
                lineDistance += DistanceCalculator.calculateDistance(pointA.getLati(), pointA.getLongi(), pointB.getLati(), pointB.getLongi());
            }
        }
        return (int)lineDistance;
    }

}
