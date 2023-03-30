package com.example.googlemapapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class LoginActivity extends AppCompatActivity {
    private String TAG = this.getClass().getName();

    EditText t_id;
    EditText t_pass;

    // 회원 번호를 저장할 멤버 변수
    public static Context context_login;
    int member_idx=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context_login=this;

        t_id=findViewById(R.id.t_id);
        t_pass=findViewById(R.id.t_pass);

        Button bt_login=findViewById(R.id.bt_login);

        // 로그인 버튼
        bt_login.setOnClickListener((v) ->{
            Thread thread=new Thread(){
                @Override
                public void run() {
                    Log.d(TAG,"저장 버튼 누름");
                    send();
                }
            };
            thread.start();
        });
    }


    public void send(){
        Log.d(TAG, "등록버튼");
        // 서버에서 요청 받을 준비
        BufferedWriter buffw=null;
        BufferedReader buffr=null;
        OutputStreamWriter os=null;
        InputStreamReader is=null;

        try {
            URL url=new URL("http://www.bodybuddy.kro.kr/auth/rest/login/android");
            URLConnection urlConnection =url.openConnection();
            HttpURLConnection httpCon=(HttpURLConnection)urlConnection;


            // 세팅
            httpCon.setRequestMethod("POST");
            httpCon.setDoOutput(true);
            httpCon.setRequestProperty("Content-Type", "application/json");

            String email=t_id.getText().toString();
            String password=t_pass.getText().toString();


            // 5-3) 파라미터 보낼 폼 데이터 만들기   -- 쿼리스트링화!
            String postData="{\"email\":\""+email+"\",\"password\":{\"pass\":\""+password+"\"}}";

            Log.d(TAG, "보낼 데이터 모습 ddd"+postData);

            // 보낼 객체 준비하고 -- 쓰기 write() 하면 끝!
            os=new OutputStreamWriter(httpCon.getOutputStream());
            buffw=new BufferedWriter(os);

            buffw.write(postData+"/n");
            buffw.flush();


            //데이터 요청 이후에 입력스트림 만들어야 한다..
            is = new InputStreamReader(httpCon.getInputStream(), "UTF-8");
            buffr = new BufferedReader(is);


            // 이 과정이 의문스러움..
            StringBuilder sb=new StringBuilder();
            while(true){
                String result=buffr.readLine();
                sb.append(result);
                if(result==null){
                    break;
                }
            }
            Log.d(TAG, "서버로부터 받은 응답 "+sb.toString());

            JSONObject jsonObj = new JSONObject(sb.toString());
            int members_idx = (Integer) jsonObj.get("member_idx");

            String json=sb.toString();
            Log.d(TAG,"멤버 아이디엑스 뽑기 : "+members_idx);

            // MainActivity 에서 참조할 것이기에 멤버변수로 대입하기
            member_idx=members_idx;

            Intent intent=new Intent(this, MainActivity.class);
            startActivity(intent);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } finally {
            if(buffw !=null){
                try {
                    buffw.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(os !=null){
                try {
                    os.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(is !=null){
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(buffr !=null){
                try {
                    buffr.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }



}