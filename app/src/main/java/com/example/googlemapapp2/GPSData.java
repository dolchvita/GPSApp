package com.example.googlemapapp2;

import java.util.List;

import lombok.Data;

// gps 데이터를 받을 객체
@Data
public class GPSData {
	private int member_idx;
	private double lati;
	private double longi;
}
