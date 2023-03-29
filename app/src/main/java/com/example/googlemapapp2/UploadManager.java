package com.example.googlemapapp2;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UploadManager {
    private String TAG=this.getClass().getName();
    // http 통신을 위한 객체
    HttpURLConnection con;
    String host="http://172.30.1.60:7777/rest/exr/today";

    // 구분자 세트 (3총사)
    String boundary="**********";		// 하이픈으로 감쌀, 데이터의 경계기준 문자열
    String hypen="--";
    String line="\r\n";								// 줄바꿈 + 커서 앞으로 보내기
    File file;


    // 전송할 파일을 전달하면 서버로 전송
    public void regist(ExrToday exrToday, File file) throws MalformedURLException, IOException {
        Log.d(TAG,"호출되었나여?");
        Log.d(TAG,"넘어온 파일 상태"+file);
        Log.d(TAG,"넘어온 디티오 상태"+exrToday);

        this.file=file;
        URL url=new URL(host);
        con=(HttpURLConnection)url.openConnection();

        // header 구성하기
        con.setRequestProperty("Content-Type", "multipart/form-data;charset=utf-8;boundary="+boundary);
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setDoInput(true);

        con.setUseCaches(false);
        con.setConnectTimeout(1000);		// 지정된 시간만큼 기다림


        // body 구성하기 !
        DataOutputStream ds=new DataOutputStream(con.getOutputStream());

        // 텍스트 파라미터의 시작을 알리는 구분자 선언
        ds.writeBytes(hypen+boundary+line);		// 이 이후부터 파라미터로 인식!

        // 1 제목
        ds.writeBytes("Content-Disposition:form-data;name=\"title\""+line);
        ds.writeBytes("Content-Type:text/plaint;charset=utf-8"+line);

        // 값 지정 직후에는 라인으로 또 구분
        ds.writeBytes(line);
        ds.writeBytes(exrToday.getTitle()+line);


        // 2 작성자
        ds.writeBytes(hypen+boundary+line);
        ds.writeBytes("Content-Disposition:form-data;name=\"writer\""+line);
        ds.writeBytes("Content-Type:text/plaint;charset=UTF-8"+line);
        ds.writeBytes(line);
        ds.writeBytes(exrToday.getWriter()+line);


        // 3 파일 처리
        ds.writeBytes(hypen+boundary+line);
        ds.writeBytes("Content-Disposition:form-data;name=\"file\";filename=\""+file.getName()+"\""+line);
        ds.writeBytes("Content-Type:image/jpg"+line);
        ds.writeBytes(line);


        // 파일 쪼개기 전송
        FileInputStream fis=new FileInputStream(file);
        byte[] buff=new byte[1024];	// 한번 읽을 때마다 1

        int data=-1;
        while(true) {
            data=fis.read(buff);
            if(data==-1)break;
            ds.write(buff);
        }


        // 전송
        ds.writeBytes(line);
        ds.writeBytes(hypen+boundary+hypen+line);		// 끝맺음
        ds.flush();		// 버퍼처리된 출력 스트림
        fis.close();
        ds.close();


        //웹서버부터 받은 http 상태코드로 성공여부를 따져보자
        int status = con.getResponseCode();

        if(status == HttpURLConnection.HTTP_OK) {
            Log.d(TAG, "성공");
        }else {
            Log.d(TAG, "실패");
        }

    }



}
