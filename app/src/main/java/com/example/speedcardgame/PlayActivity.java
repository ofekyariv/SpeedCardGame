package com.example.speedcardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {
    private LottieAnimationView btnBack;
    private Button btnBeginner;
    private Button btnEasy;
    private Button btnHard;
    private Button btnReflex;

    public static final int beginner = 18;
    public static final int easy = 15;
    public static final int hard = 12;
    public static final int reflex = 9;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initViews();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);

        btnBeginner = findViewById(R.id.btnBeginner);
        btnBeginner.setOnClickListener(this);

        btnEasy = findViewById(R.id.btnEasy);
        btnEasy.setOnClickListener(this);

        btnHard = findViewById(R.id.btnHard);
        btnHard.setOnClickListener(this);

        btnReflex = findViewById(R.id.btnReflex);
        btnReflex.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnBack){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
        if (view == btnBeginner){
            Intent intent = new Intent(this,GameActivity.class);
            intent.putExtra("Mode",beginner);
            intent.putExtra("championship",false);
            startActivity(intent);
        }
        if (view == btnEasy){
            Intent intent = new Intent(this,GameActivity.class);
            intent.putExtra("Mode",easy);
            intent.putExtra("championship",false);
            startActivity(intent);
        }
        if (view == btnHard){
            Intent intent = new Intent(this,GameActivity.class);
            intent.putExtra("Mode",hard);
            intent.putExtra("championship",false);
            startActivity(intent);
        }
        if (view == btnReflex){
            Intent intent = new Intent(this,GameActivity.class);
            intent.putExtra("Mode",reflex);
            intent.putExtra("championship",false);
            startActivity(intent);
        }
    }
}
/*Intent intent = new Intent(this,GameActivity.class);
            startActivity(intent);*/
/*<ListView
        android:id="@+id/lvDetails"
        android:layout_width=""
        android:layout_height=""
        android:layout_weight=""
        android:gravity="" /> */