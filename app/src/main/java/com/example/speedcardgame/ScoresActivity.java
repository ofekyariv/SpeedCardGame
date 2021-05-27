package com.example.speedcardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

public class ScoresActivity extends AppCompatActivity {
    private DBHelper dataManager = new DBHelper(this);
    private TextView tvChart1;
    private String chart1;
    private TextView tvChart2;
    private String chart2;
    private TextView tvChart3;
    private String chart3;
    private TextView tvChart4;
    private String chart4;
    private Levels levels;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        initViews();
    }

    private void initViews() {
        tvChart1 = findViewById(R.id.tvChart1);
        tvChart2 = findViewById(R.id.tvChart2);
        tvChart3 = findViewById(R.id.tvChart3);
        tvChart4 = findViewById(R.id.tvChart4);

        Cursor cursor = dataManager.TopScorers();
        for (int i = 0;i < cursor.getCount();i++) {
            id = i + 1;
            if (i == 0) {
                chart1 = String.valueOf(id) + "\n";
                chart2 = cursor.getString(1) + "\n";
                levels = new Levels(cursor.getInt(3));
                chart3 = "Level " + levels.getLevel() + "\n";
                chart4 = cursor.getString(4) + " seconds\n";
            }
            else {
                chart1 += String.valueOf(id) + "\n";
                chart2 += cursor.getString(1) + "\n";
                levels = new Levels(cursor.getInt(3));
                chart3 += "Level " + levels.getLevel() + "\n";
                chart4 += cursor.getString(4) + " seconds\n";
            }
            cursor.moveToNext();
        }
        tvChart1.setText(chart1);
        tvChart2.setText(chart2);
        tvChart3.setText(chart3);
        tvChart4.setText(chart4);
    }
}