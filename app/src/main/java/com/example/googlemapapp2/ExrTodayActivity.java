package com.example.googlemapapp2;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class ExrTodayActivity extends AppCompatActivity {
    private String TAG=this.getClass().getName();

    ActivityResultLauncher launcher;

    File selectedFile;
    PhotoView photoView;

    // 마지막 사진 가져오기
    Cursor cursor;
    UploadManager uploadManager=new UploadManager();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exr_today);

       // photoView=findViewById(R.id.photoView);

        Button bt_preview=findViewById(R.id.bt_preview);
        Button bt_regist=findViewById(R.id.bt_regist);
        Button bt_gps=findViewById(R.id.bt_gps);

        // 사진 띄우기
        bt_preview.setOnClickListener((v)->{
            openExternal();
        });

        bt_regist.setOnClickListener((v)->{
            Thread thread=new Thread(){
                @Override
                public void run() {
                    super.run();
                    upload();
                }
            };
            thread.start();
        });


        launcher=registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {

            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                Log.d(TAG,"요청에 대한 사용자의 반응 결과는? "+result);

                Iterator<String> iterater =result.keySet().iterator();     // 맵에 들어있는 키값만들 일렬로 늘어서게 한다

                // 키의 수 만큼
                while(iterater.hasNext()){
                    String permissionName=iterater.next();      // 권한 명

                    // 키를 이용하여 맵의 실제 데이터 접근하자
                    boolean granted=result.get(permissionName);

                    if(granted==false){

                        if(ActivityCompat.shouldShowRequestPermissionRationale(ExrTodayActivity.this, permissionName)){     // 한 번
                            Toast.makeText(ExrTodayActivity.this, "권한을 수락해야 이용이 가능합니다", Toast.LENGTH_SHORT).show();

                        }else{
                            // 수락을 2회 이상 거절
                            Toast.makeText(ExrTodayActivity.this, "정상적인 앱 이용을 위해서 설정에서 권한을 수락해주시오.", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }

        });
                /* 안드로이드의 새로운 권한 정책으로 인하여,
            앱 시작과 동시에 사용자로부터 권한 확인 및 수락 받기 */
        if( checkVersion()){
            // 최신 핸드폰이므로, 파일에 대한 접근보다 사용로부터 허락부터 받기
            //checkGranted();     // 허락을 받는 팝업

            if(checkGranted()){
            }else{

                // 권한 요청 시도
                launcher.launch(new String[]{

                        // 권한 팝업이 뜨는 그 로직
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                });
            }

        }else {

        }
    }


    // 1 가장 최신 사진 가져오기
    public void getPhoto(){

        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, //the album it in
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };
        cursor = getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

        // Put it in the image view
        if (cursor.moveToFirst()) {
            final ImageView imageView = (ImageView) findViewById(R.id.photoView);
            String imageLocation = cursor.getString(1);
            File imageFile = new File(imageLocation);

           // imageFile=selectedFile;

            Log.d(TAG, "imageFile이란 뭘까? "+imageFile);

            if (imageFile.exists()) {   // TODO: is there a better way to do this?
                Bitmap bm = BitmapFactory.decodeFile(imageLocation);
                imageView.setImageBitmap(bm);

                selectedFile=imageFile;

                Log.d(TAG, "전송할 이미지 파일의 크기 ? "+selectedFile.length());
            }
        }

    }


    // 2 사진 올리기
    public void upload(){
        ExrToday exrToday=new ExrToday();
        EditText t_title=findViewById(R.id.t_title);
        EditText t_writer=findViewById(R.id.t_writer);

        String title=t_title.getText().toString();
        String writer=t_writer.getText().toString();

        // 내용 대입하기
        exrToday.setTitle(title);
        exrToday.setWriter(writer);

        try {
            // 업로드 매니저에게 전달할 파일 대체하기
            Log.d(TAG,"전송할 파일 "+selectedFile);
            uploadManager.regist(exrToday, selectedFile);
            Log.d(TAG,"디티오랑 같이 전송된 거 맞냐구 "+exrToday);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void openExternal(){
        // 외부 저장소 접근하기
        File storage= Environment.getExternalStorageDirectory();
       // Log.d(TAG, storage.getAbsolutePath());

        // 외부 저장소에 하위 디렉토리 및 모든 파일의 목록을 조회해보자
        File[] files=storage.listFiles();
        //Log.d(TAG, "파일 디렉토리 및 파일 수"+files.length);

        getPhoto();

    }


    public Boolean checkVersion(){
        // 마시멜로 폰 부터 새로운 정팻 적용해야 하므로, 현재 사용자의폰 버전 확인
        Log.d(TAG,"sdk_int = "+ Build.VERSION.SDK_INT);     // 28
        Log.d(TAG,"마시멜로는 = "+ Build.VERSION_CODES.M );      //23

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            return true;
        }else{
            return false;
        }
    }


    // 사용자로부터 권한 수락을 요청하는 메서드
    public boolean checkGranted(){

        // 유저가 권한을 허락했는지 확인
        int read_permission= ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int write_permission=ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean result1=read_permission== PackageManager.PERMISSION_GRANTED;
        boolean result2=write_permission==PackageManager.PERMISSION_GRANTED;

        return result1&&result2;
    }


}