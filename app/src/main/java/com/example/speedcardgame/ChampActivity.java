package com.example.speedcardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static com.example.speedcardgame.PlayActivity.beginner;
import static com.example.speedcardgame.PlayActivity.easy;
import static com.example.speedcardgame.PlayActivity.hard;
import static com.example.speedcardgame.PlayActivity.reflex;

public class ChampActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champ);
        initViews();
    }

    private void initViews() {
        if (getIntent().getExtras()!=null&&getIntent().getExtras().getInt("phase") == 4){
            AlertDialog.Builder builder = new AlertDialog.Builder(ChampActivity.this);
            builder.setTitle("Champions!");
            builder.setMessage("You did it! You won the championship!");
            builder.setCancelable(false);
            builder.setPositiveButton("Go Back To Menu", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(ChampActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        btnPlay = findViewById(R.id.btnPlayTour);
        btnPlay.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(getIntent().getExtras()!=null){
            if (getIntent().getExtras().getInt("phase") == 1){
                Intent intent = new Intent(this,GameActivity.class);
                intent.putExtra("Mode",easy);
                intent.putExtra("championship",true);
                intent.putExtra("phase", 1);
                startActivity(intent);
            }

            if (getIntent().getExtras().getInt("phase") == 2){
                Intent intent = new Intent(this,GameActivity.class);
                intent.putExtra("Mode",hard);
                intent.putExtra("championship",true);
                intent.putExtra("phase", 2);
                startActivity(intent);
            }

            if (getIntent().getExtras().getInt("phase") == 3){
                Intent intent = new Intent(this,GameActivity.class);
                intent.putExtra("Mode",reflex);
                intent.putExtra("championship",true);
                intent.putExtra("phase", 3);
                startActivity(intent);
            }
        }

    }
}