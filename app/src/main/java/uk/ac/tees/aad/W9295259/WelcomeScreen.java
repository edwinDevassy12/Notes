package uk.ac.tees.aad.W9295259;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class WelcomeScreen extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        ImageView image = (ImageView)findViewById(R.id.logo);
        Animation animation1 =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.move);
        image.startAnimation(animation1);


        TextView text = findViewById(R.id.introText);
        text.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blink));

        Thread myThread = new Thread()
        {
            @Override
            public void run() {
                try {
                    sleep(3500);
                    Intent intent;
                    intent = new Intent(getApplicationContext(),Login.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}
