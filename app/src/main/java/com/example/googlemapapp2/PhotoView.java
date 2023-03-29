package com.example.googlemapapp2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

// 사진을 미리볼 수 있는 뷰
public class PhotoView extends View {
    Bitmap bitmap;      // 사진 데이터 다룰 때 사용

    ExrTodayActivity exrTodayActivity;

    // 이 뷰를 xml에서 사용하려면 생성자의 매개변수에 반드시 xml 태그 속성을 받을 수 있는 AttributeSet 명시!
    public PhotoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // 3-1) 사진 가져오기 위해 액티비티 가져오기
        exrTodayActivity=(ExrTodayActivity) context;
    }

    // 3-2) 메인액티비티가 보유한 자료형은 File형이므로 형변환 필요
    public void createBitmap() {
        FileInputStream fis= null;

        try {
            fis = new FileInputStream(exrTodayActivity.selectedFile);
            bitmap= BitmapFactory.decodeStream(fis);


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if(fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        // 이 메서드는 어디서 호출할까?  --> exrTodayActivity


    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(bitmap!=null){
            canvas.drawBitmap(bitmap,0,0,null);
        }

    }
}
