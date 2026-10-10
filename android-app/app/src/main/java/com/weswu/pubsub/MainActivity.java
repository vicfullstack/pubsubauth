package com.weswu.pubsub;

import android.os.Build;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static MainActivity instance;
    public static TextView quicResText;
    public static TextView httpsResText;
    private Button loadImageBtn;
    private DisplayMetrics displayMetrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGrabber();
        if (Build.VERSION.SDK_INT >= 11) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        }
    }

    private void initGrabber(){
        loadImageBtn = (Button) findViewById(R.id.images_button);

        this.displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(this.displayMetrics);

    }

    public int getScreenHeight(){
        return  displayMetrics.heightPixels;
    }

    public int getScreenWidth(){
        return  displayMetrics.widthPixels;
    }


    public void onClickLoadImages(View view) {
        new AsyncTaskPubsub().execute("");
    }
}
