package com.shy.lucky.luckycircle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private LuckyCircle lc_circle;
    private ImageView iv_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amain_activity);

        lc_circle = (LuckyCircle) findViewById(R.id.lc_circle);
        iv_start = (ImageView) findViewById(R.id.iv_start);

        iv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!lc_circle.isRunning()){
                    lc_circle.mSpeed = 30;
                    iv_start.setImageResource(R.drawable.stop);
                }else if(!lc_circle.isShouldEnd()){
                    lc_circle.luckyEnd();
                    iv_start.setImageResource(R.drawable.start);

                }
            }
        });

    }




}
