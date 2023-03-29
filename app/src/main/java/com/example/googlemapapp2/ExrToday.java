package com.example.googlemapapp2;

import java.io.File;
import java.util.List;

import lombok.Data;

@Data
public class ExrToday {
	private int exr_today_idx;
	private String title;
	private String writer;
	private String content;
	private String regdate;
	private int recommend;
	private int hit;

	// 서버로 전송할 파일 객체
	private File file;
}
