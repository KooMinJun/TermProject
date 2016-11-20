package my.kmucs.com.koo_timer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

/**
 * Created by Koo on 2016-11-20.
 */

public class SplashActivity extends Activity {
    private final int SPLASH_DISPLAY_LENGTH = 3000;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        ImageView iv = (ImageView)findViewById(R.id.splashimg);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(iv);
        Glide.with(this).load(R.raw.svg_stopwatch_dribbble).into(imageViewTarget);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                i = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(i);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
