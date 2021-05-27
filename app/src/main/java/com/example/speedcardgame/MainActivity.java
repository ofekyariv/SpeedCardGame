package com.example.speedcardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LottieAnimationView btnPlay;
    private ImageButton btnChamp;
    private ImageButton btnScore;
    private ProgressBar progressBar;
    private TextView pLevel;
    private Button btnRules;

    private Levels levels;

    private TextView tvUsername;

    private boolean isPlay = false;
    private LottieAnimationView btnAudio;
    private String strLink;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        getSP();
        getXP();
    }



    private void getSP() {
        SharedPreferences sp = this.getSharedPreferences(getString(R.string.preference_file_key), 0);
        String spUserName = (sp.getString(getString(R.string.user_name), ""));
        tvUsername.setText("    Welcome ~" + spUserName + "~");
    }

    private void getXP() {
        SharedPreferences sp = this.getSharedPreferences(getString(R.string.preference_file_key), 0);
        String spUserName = (sp.getString(getString(R.string.user_name), ""));

        DBHelper dataManager = new DBHelper(this);
        Cursor cursor = dataManager.selectByName(spUserName);
        if (cursor != null && cursor.getCount() > 0) {
            levels = new Levels(cursor.getInt(cursor.getColumnIndex("xp")));
            progressBar.setMax(levels.getLevelXP());
            progressBar.setProgress(levels.getXpInLevel());
            pLevel.setText("Level " + levels.getLevel());
        }
    }

    private void initViews() {
        btnRules = findViewById(R.id.btnRules);
        btnRules.setOnClickListener(this);

        btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);

        btnChamp = findViewById(R.id.btnChamp);
        btnChamp.setOnClickListener(this);

        btnScore = findViewById(R.id.btnScore);
        btnScore.setOnClickListener(this);

        progressBar = findViewById(R.id.progressBar);
        pLevel = findViewById(R.id.pLevel);

        tvUsername = findViewById(R.id.tvUsername);

        serviceIntent = new Intent(this, PlayService.class);
        btnAudio = findViewById(R.id.btnAudio);
        btnAudio.setOnClickListener(this);
    }


    // screens
    @Override
    public void onClick(View view) {
        if (view == btnRules) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Rules");
            builder.setMessage("- At the beginning of the game, five cards will be opened in front of you and a personal stack on the right. In the center of the table will be the stacks.\n" +
                    "- Now the game begins, each player can place one card from his visible cards on one of the open stacks in the center.\n" +
                    "- The selected card must be higher or lower than the open card in one (for example 2 and a ace). \n" +
                    "- When the two opponents do not have any actions to perform, new cards must be opened from the central stacks.\n" +
                    "- There are no turns in Speed so play quickly!\n" +
                    "- When the number of open cards is less than five, you can access your personal stack and refile your open cards\n" +
                    "------- The winner is the first to finish his cards! -------");
            builder.setCancelable(true);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        if (view == btnPlay) {
            Intent intent = new Intent(this, PlayActivity.class);
            startActivity(intent);

        }
        if (view == btnChamp) {
            Intent intent = new Intent(this, ChampActivity.class);
            startActivity(intent);
        }
        if (view == btnScore) {
            Intent intent = new Intent(this, ScoresActivity.class);
            startActivity(intent);
        }
        if (view == btnAudio){
            buttonAudioClick();
        }
    }

    private void buttonAudioClick() {
        if (!isPlay){
            isPlay = true;
            btnAudio.resumeAnimation();
            playAudio();
        }
        else if (isPlay) {
            isPlay = false;
            btnAudio.pauseAnimation();
            stopAudio();
        }
    }

    private void playAudio() {
        strLink = "http://www.ilemon.mobi/fightnIncastle1.mp3";
        serviceIntent.putExtra("Link",strLink);
        try {
            startService(serviceIntent);
        }
        catch (Exception e){
            Log.e("Service ERROR", e.toString());
            e.printStackTrace();
        }
    }

    private void stopAudio() {
        try {
            stopService(serviceIntent);
        }
        catch (Exception e) {
            Log.e("Service ERROR", e.toString());
            e.printStackTrace();
        }
    }
}

