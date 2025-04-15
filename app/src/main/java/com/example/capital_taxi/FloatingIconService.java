package com.example.capital_taxi;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;

public class FloatingIconService extends Service {

    private WindowManager windowManager;
    private ImageView floatingIconView;

    @Override
    public void onCreate() {
        super.onCreate();

        // الحصول على WindowManager
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        // إنشاء العرض العائم (الإيقونة)
        floatingIconView = new ImageView(this);
        floatingIconView.setImageResource(R.drawable.safety); // حط الايقونة التي تريد عرضها

        // إعداد LayoutParams للإيقونة العائمة
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        // تحديد موقع الإيقونة
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.x = 0; // X position
        layoutParams.y = 100; // Y position (من أعلى الشاشة)

        // إضافة النافذة العائمة
        windowManager.addView(floatingIconView, layoutParams);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // إزالة الإيقونة العائمة عند إغلاق الخدمة
        if (windowManager != null && floatingIconView != null) {
            windowManager.removeView(floatingIconView);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
