package com.example.speedcardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, View.OnDragListener, View.OnTouchListener  {
    private GameStrategy gameStrategy = new GameStrategy();
    private Cards[] p1OpenCards;
    private Cards[] p2OpenCards;

    private boolean started;
    private int cardPlayed;
    private int openNull1;
    private int openNull2;
    private int botLook;
    private static final int startAnimDuration = 300;

    private Cards topDesk1;
    private Cards topDesk2;

    private ImageButton oneCard1;
    private ImageButton oneCard2;
    private ImageButton oneCard3;
    private ImageButton oneCard4;
    private ImageButton oneCard5;
    private ImageButton myDeskCards;

    private ImageButton deskClose1;
    private ImageButton deskClose2;
    private ImageButton deskOpen1;
    private ImageButton deskOpen2;

    private ImageButton twoCard1;
    private ImageButton twoCard2;
    private ImageButton twoCard3;
    private ImageButton twoCard4;
    private ImageButton twoCard5;
    private ImageButton botDeskCards;
    private TextView tvTime;

    private ArrayList<ImageView> openCards1;
    private ArrayList<ImageView> openCards2;

    private Handler myHandler;
    private static int miliseconds;
    private static boolean running;

    Animation botInAnim;
    Animation botOutAnim;

    private SoundPool soundPool;
    private int cardDrop;
    private int flipCard;
    private int takingCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //phase 1 - check which sdk the user has
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
        {
            AudioAttributes aa = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(aa)
                    .build();
        }
        else
        {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,1);


        }
        //phase 2 -load files to sp
        cardDrop = soundPool.load(this,R.raw.carddrop,1);
        flipCard = soundPool.load(this,R.raw.flipcard,1);
        takingCard = soundPool.load(this,R.raw.takingcard,1);

        initViews();
        getSP();
        runTimer();
    }

    private boolean getSP() {
        SharedPreferences sp = this.getSharedPreferences(getString(R.string.preference_file_key), 0);
        boolean isNewGame = (sp.getBoolean(getString(R.string.first_game),true));
        return isNewGame;
    }

    private void initViews() {
        // אם היה משחק לפני - צור משחק חדש
        if (!getSP()){
            SharedPreferences sp = this.getSharedPreferences(getString(R.string.preference_file_key), 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(getString(R.string.first_game), true);
            editor.apply();
            recreate();
        }
        miliseconds = 0;

        openCards1 = new ArrayList<>();
        openCards2 = new ArrayList<>();
        // Player One :

        myDeskCards = findViewById(R.id.myDeskCards);
        myDeskCards.setOnClickListener(this);

        oneCard1 = findViewById(R.id.oneCard1);
        oneCard1.setOnTouchListener(this);
        oneCard1.setOnDragListener(this);

        oneCard2 = findViewById(R.id.oneCard2);
        oneCard2.setOnTouchListener(this);
        oneCard2.setOnDragListener(this);

        oneCard3 = findViewById(R.id.oneCard3);
        oneCard3.setOnTouchListener(this);
        oneCard3.setOnDragListener(this);

        oneCard4 = findViewById(R.id.oneCard4);
        oneCard4.setOnTouchListener(this);
        oneCard4.setOnDragListener(this);

        oneCard5 = findViewById(R.id.oneCard5);
        oneCard5.setOnTouchListener(this);
        oneCard5.setOnDragListener(this);


        // Desk :
        deskClose1 = findViewById(R.id.deskClose1);
        deskClose1.setOnClickListener(this);
        deskClose2 = findViewById(R.id.deskClose2);
        deskClose2.setOnClickListener(this);

        deskOpen1 = findViewById(R.id.deskOpen1);
        //deskOpen1.setOnClickListener(this);
        deskOpen2 = findViewById(R.id.deskOpen2);
        //deskOpen2.setOnClickListener(this);


        // Player Two :
        twoCard1 = findViewById(R.id.twoCard1);
        twoCard2 = findViewById(R.id.twoCard2);
        twoCard3 = findViewById(R.id.twoCard3);
        twoCard4 = findViewById(R.id.twoCard4);
        twoCard5 = findViewById(R.id.twoCard5);
        botDeskCards = findViewById(R.id.botDeskCards);
        tvTime = findViewById(R.id.tvTime);

        openCards1.add(oneCard1);
        openCards1.add(oneCard2);
        openCards1.add(oneCard3);
        openCards1.add(oneCard4);
        openCards1.add(oneCard5);

        openCards2.add(twoCard1);
        openCards2.add(twoCard2);
        openCards2.add(twoCard3);
        openCards2.add(twoCard4);
        openCards2.add(twoCard5);

        p1OpenCards = new Cards[]{gameStrategy.pOneCards.remove(), gameStrategy.pOneCards.remove(), gameStrategy.pOneCards.remove(), gameStrategy.pOneCards.remove(), gameStrategy.pOneCards.remove()};
        p2OpenCards = new Cards[]{gameStrategy.pTwoCards.remove(), gameStrategy.pTwoCards.remove(), gameStrategy.pTwoCards.remove(), gameStrategy.pTwoCards.remove(), gameStrategy.pTwoCards.remove()};

        botInAnim = AnimationUtils.loadAnimation(this,R.anim.botin);
        botOutAnim = AnimationUtils.loadAnimation(this, R.anim.botout);

        started = false;
        cardPlayed = -1;
        openNull1 = 0;
        openNull2 = 0;
        botLook = 0;

        topDesk1 = gameStrategy.centerCards.remove();
        topDesk2 = gameStrategy.centerCards.remove();
    }





    @Override
    public void onClick(View view) {
        final TypedArray images = getResources().obtainTypedArray(R.array.images);
        if ((view == deskClose1 || view == deskClose2)) {
            if (!started) {
                started = true;
                running = true;
                deskOpen1.setImageResource(images.getResourceId(topDesk1.getCardId(), -1));
                deskOpen2.setImageResource(images.getResourceId(topDesk2.getCardId(), -1));
                for (int i = 0; i < 5; i++) {
                    final ObjectAnimator oa1 = ObjectAnimator.ofFloat(openCards1.get(i), "scaleX", 1f, 0f);
                    final ObjectAnimator oa2 = ObjectAnimator.ofFloat(openCards1.get(i), "scaleX", 0f, 1f);
                    oa1.setInterpolator(new DecelerateInterpolator());
                    oa2.setInterpolator(new AccelerateDecelerateInterpolator());
                    oa1.setDuration(startAnimDuration);
                    oa2.setDuration(startAnimDuration);
                    oa1.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationCancel(animation);
                            for (int i = 0; i < 5; i++) {
                                openCards1.get(i).setImageResource(images.getResourceId(p1OpenCards[i].getCardId(), -1));
                                oa2.start();
                            }
                        }
                    });
                    oa1.start();


                    final ObjectAnimator oa3 = ObjectAnimator.ofFloat(openCards2.get(i), "scaleX", 1f, 0f);
                    final ObjectAnimator oa4 = ObjectAnimator.ofFloat(openCards2.get(i), "scaleX", 0f, 1f);
                    oa3.setInterpolator(new DecelerateInterpolator());
                    oa4.setInterpolator(new AccelerateDecelerateInterpolator());
                    oa3.setDuration(startAnimDuration);
                    oa4.setDuration(startAnimDuration);
                    oa3.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationCancel(animation);
                            for (int i = 0; i < 5; i++) {
                                openCards2.get(i).setImageResource(images.getResourceId(p2OpenCards[i].getCardId(), -1));
                                oa4.start();
                            }
                        }
                    });
                    oa3.start();
                }
                soundPool.play(flipCard,1,1,0,0,1);
            }

            // open new desk cards
            else if (!gameStrategy.allPlayable(p1OpenCards, p2OpenCards, topDesk1, topDesk2)) {
                topDesk1 = gameStrategy.centerCards.remove();
                topDesk2 = gameStrategy.centerCards.remove();
                deskOpen1.setImageResource(images.getResourceId(topDesk1.getCardId(), -1));
                deskOpen2.setImageResource(images.getResourceId(topDesk2.getCardId(), -1));
                if (gameStrategy.getCenterCards().isEmpty()) {
                    deskClose1.setImageResource(android.R.color.transparent);
                    deskClose2.setImageResource(android.R.color.transparent);
                }
            }
        }

        // open new cards
        if (view == myDeskCards && started == true && !gameStrategy.pOneCards.isEmpty()) {
            if (openNull1 > 0)
                soundPool.play(takingCard,1,1,0,0,1);
            for (int i = 0; i < 5; i++) {
                if (gameStrategy.pOneCards.isEmpty()) {
                    myDeskCards.setImageResource(android.R.color.transparent);
                    i = 4;
                }
                else if (p1OpenCards[i] == null) {
                    p1OpenCards[i] = gameStrategy.pOneCards.remove();
                    openCards1.get(i).setImageResource(images.getResourceId(p1OpenCards[i].getCardId(), -1));
                    openNull1--;
                }
            }
        }
    }


    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();
        float f = (float) 0.8;
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                if (v == oneCard1 && p1OpenCards[0] != null) {
                    cardPlayed = 0;
                }
                if (v == oneCard2 && p1OpenCards[1] != null) {
                    cardPlayed = 1;
                }
                if (v == oneCard3 && p1OpenCards[2] != null) {
                    cardPlayed = 2;
                }
                if (v == oneCard4 && p1OpenCards[3] != null) {
                    cardPlayed = 3;
                }
                if (v == oneCard5 && p1OpenCards[4] != null) {
                    cardPlayed = 4;
                }
                v.setAlpha(f);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                final TypedArray images = getResources().obtainTypedArray(R.array.images);
                if (cardPlayed != -1) {
                    if (gameStrategy.playable(p1OpenCards[cardPlayed].getCardNum(), topDesk1.getCardNum())) {
                        deskOpen1.setImageResource(images.getResourceId(p1OpenCards[cardPlayed].getCardId(), -1));
                        openCards1.get(cardPlayed).setImageResource(android.R.color.transparent);
                        topDesk1 = p1OpenCards[cardPlayed];
                        p1OpenCards[cardPlayed] = null;
                        openNull1++;
                        cardPlayed = -1;
                        soundPool.play(cardDrop, 1, 1, 0, 0, 1);
                    }
                    else if (gameStrategy.playable(p1OpenCards[cardPlayed].getCardNum(), topDesk2.getCardNum())) {
                        deskOpen2.setImageResource(images.getResourceId(p1OpenCards[cardPlayed].getCardId(), -1));
                        openCards1.get(cardPlayed).setImageResource(android.R.color.transparent);
                        topDesk2 = p1OpenCards[cardPlayed];
                        p1OpenCards[cardPlayed] = null;
                        openNull1++;
                        cardPlayed = -1;
                        soundPool.play(cardDrop, 1, 1, 0, 0, 1);
                    }

                    if (openNull1 == 5 && gameStrategy.pOneCards.isEmpty()) {
                        running = false;
                        gameStrategy.setWon();

                        SharedPreferences sp = this.getSharedPreferences(getString(R.string.preference_file_key), 0);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean(getString(R.string.first_game), false);
                        editor.apply();
                        String spUserName = (sp.getString(getString(R.string.user_name), ""));
                        DBHelper dataManager = new DBHelper(this);
                        dataManager.getWritableDatabase();
                        Cursor cursor = dataManager.selectByName(spUserName);
                        if (cursor != null && cursor.getCount() > 0) {

                            dataManager.setXP(3000/(miliseconds/10), cursor.getInt( cursor.getColumnIndex("_id") ));
                            if (miliseconds/10 < cursor.getInt(cursor.getColumnIndex("score")) || cursor.getInt(cursor.getColumnIndex("score")) == -1) {
                                dataManager.setScore(miliseconds / 10, cursor.getInt(cursor.getColumnIndex("_id")));
                            }

                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Very Well!");
                        builder.setMessage("You won the game in " + (miliseconds / 10) + " seconds!");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Play Again!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                myHandler.removeCallbacksAndMessages(null);
                                Intent intent = new Intent(GameActivity.this, GameActivity.class);
                                intent.putExtra("Mode",(int)getIntent().getExtras().get("Mode"));
                                startActivity(intent);
                                finish();
                                //recreate();
                            }
                        });
                        builder.setNegativeButton("Go Back To Menu", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                myHandler.removeCallbacksAndMessages(null);
                                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                //recreate();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    v.setAlpha(1);
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void runTimer() {
        myHandler = new Handler();
        // we are demanding to start the Timer ASAP by using Method post !!! of Handler
        myHandler.post(new Runnable() {
            @Override
            public void run()
            {
                // here is the activity state , we will get running in the following  cases : when onStart, onResume and Start button
                if (running) {
                    miliseconds++;
                    int milisecs = miliseconds%10;
                    int secs = (miliseconds/10)%60;
                    int minutes = (miliseconds/10)/60;
                    String time = String.format(Locale.getDefault(),
                            "%d:%02d:%d", minutes, secs, milisecs);
                    tvTime.setText(time);


                    if (miliseconds == 5) {
                        openCards2.get(0).startAnimation(botInAnim);
                    }

                    if (miliseconds % ((int) getIntent().getExtras().get("Mode")) == 0) {
                        openCards2.get(botLook).clearAnimation();
                        if (botLook == 0) {
                            if (openCards2.get(4).getAnimation() != null) {
                                openCards2.get(4).clearAnimation();
                            }
                        }
                        else {
                            if (openCards2.get(botLook - 1).getAnimation() != null) {
                                openCards2.get(botLook - 1).clearAnimation();
                            }
                        }


                        final TypedArray images = getResources().obtainTypedArray(R.array.images);
                        if (gameStrategy.playable(p2OpenCards[botLook].getCardNum(), topDesk1.getCardNum())) {
                            deskOpen1.setImageResource(images.getResourceId(p2OpenCards[botLook].getCardId(), -1));
                            topDesk1 = p2OpenCards[botLook];
                            if (!gameStrategy.getpTwoCards().isEmpty()) {
                                p2OpenCards[botLook] = gameStrategy.pTwoCards.remove();
                                openCards2.get(botLook).setImageResource(images.getResourceId(p2OpenCards[botLook].getCardId(), -1));
                            }
                            else {
                                p2OpenCards[botLook] = null;
                                openCards2.get(botLook).setImageResource(android.R.color.transparent);
                                botDeskCards.setImageResource(android.R.color.transparent);
                                openNull2++;
                            }
                        }
                        else if (gameStrategy.playable(p2OpenCards[botLook].getCardNum(), topDesk2.getCardNum())) {
                            deskOpen2.setImageResource(images.getResourceId(p2OpenCards[botLook].getCardId(), -1));
                            topDesk2 = p2OpenCards[botLook];
                            if (!gameStrategy.getpTwoCards().isEmpty()) {
                                p2OpenCards[botLook] = gameStrategy.pTwoCards.remove();
                                openCards2.get(botLook).setImageResource(images.getResourceId(p2OpenCards[botLook].getCardId(), -1));
                            }
                            else {
                                p2OpenCards[botLook] = null;
                                openCards2.get(botLook).setImageResource(android.R.color.transparent);
                                botDeskCards.setImageResource(android.R.color.transparent);
                                openNull2++;
                            }
                        }
                        else
                            openCards2.get(botLook).startAnimation(botOutAnim);

                        if (openNull2 == 5 && gameStrategy.pTwoCards.isEmpty()) {
                            running = false;
                            gameStrategy.setLost();

                            SharedPreferences sp = getSharedPreferences(getString(R.string.preference_file_key), 0);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putBoolean(getString(R.string.first_game), false);
                            editor.apply();

                            AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                            builder.setTitle("Try Getting Better!");
                            builder.setMessage("You lost the game in " + (miliseconds/10) + " seconds...");
                            builder.setCancelable(false);
                            builder.setPositiveButton("Try Again!", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    myHandler.removeCallbacksAndMessages(null);
                                    Intent intent = new Intent(GameActivity.this, GameActivity.class);
                                    intent.putExtra("Mode", (int) getIntent().getExtras().get("Mode"));
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            builder.setNegativeButton("Go Back To Menu", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    myHandler.removeCallbacksAndMessages(null);
                                    Intent intent = new Intent(GameActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                        botLook++;
                        if (botLook == 5) {
                            botLook = 0;
                        }
                        while (p2OpenCards[botLook] == null) {
                            botLook++;
                            if (botLook == 5) {
                                botLook = 0;
                            }
                        }

                        openCards2.get(botLook).startAnimation(botInAnim);
                    }


                }
                myHandler.postDelayed(this, 100);  // we are demanding to resume the Timer in 10 millisecs
            }
        });
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDragAndDrop(data, shadowBuilder, view, 0);
            //view.setVisibility(View.INVISIBLE);
            if (view == oneCard1 && p1OpenCards[0] != null) {
                cardPlayed = 0;
            }
            if (view == oneCard2 && p1OpenCards[1] != null) {
                cardPlayed = 1;
            }
            if (view == oneCard3 && p1OpenCards[2] != null) {
                cardPlayed = 2;
            }
            if (view == oneCard4 && p1OpenCards[3] != null) {
                cardPlayed = 3;
            }
            if (view == oneCard5 && p1OpenCards[4] != null) {
                cardPlayed = 4;
            }
            return true;
        }
        else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            if ((Math.abs(view.getX() - deskOpen1.getX())<300) && (Math.abs(view.getY() - deskClose1.getY())<300) || (Math.abs(view.getX() - deskOpen2.getX())<30) && (Math.abs(view.getY() - deskClose2.getY())<30)){
                final TypedArray images = getResources().obtainTypedArray(R.array.images);
                if (cardPlayed != -1) {
                    if (gameStrategy.playable(p1OpenCards[cardPlayed].getCardNum(), topDesk1.getCardNum())) {
                        deskOpen1.setImageResource(images.getResourceId(p1OpenCards[cardPlayed].getCardId(), -1));
                        openCards1.get(cardPlayed).setImageResource(android.R.color.transparent);
                        topDesk1 = p1OpenCards[cardPlayed];
                        p1OpenCards[cardPlayed] = null;
                        openNull1++;
                        cardPlayed = -1;

                    }
                    else if (gameStrategy.playable(p1OpenCards[cardPlayed].getCardNum(), topDesk2.getCardNum())) {
                        deskOpen2.setImageResource(images.getResourceId(p1OpenCards[cardPlayed].getCardId(), -1));
                        openCards1.get(cardPlayed).setImageResource(android.R.color.transparent);
                        topDesk2 = p1OpenCards[cardPlayed];
                        p1OpenCards[cardPlayed] = null;
                        openNull1++;
                        cardPlayed = -1;
                    }

                    if (openNull1 == 5 && gameStrategy.pOneCards.isEmpty()) {
                        running = false;
                        gameStrategy.setWon();
                        SharedPreferences sp = this.getSharedPreferences(getString(R.string.preference_file_key), 0);
                        String spUserName = (sp.getString(getString(R.string.user_name), ""));

                        DBHelper dataManager = new DBHelper(this);
                        dataManager.getWritableDatabase();
                        Cursor cursor = dataManager.selectByName(spUserName);
                        if (cursor != null && cursor.getCount() > 0) {
                            //if (cursor.getInt(cursor.getColumnIndex("score")) != -1 || (miliseconds / 10) < cursor.getInt(cursor.getColumnIndex("score"))) {
                                //dataManager.setXP(3000 / (miliseconds / 10), spUserName);
                            //}
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Very Well!");
                        builder.setMessage("You won the game in " + (miliseconds / 10) + " seconds!");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Play Again!", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                myHandler.removeCallbacksAndMessages(null);
                                Intent intent = new Intent(GameActivity.this, GameActivity.class);
                                intent.putExtra("Mode", (int) getIntent().getExtras().get("Mode"));
                                startActivity(intent);
                                finish();

                            }
                        });
                        builder.setNegativeButton("Go Back To Menu", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                myHandler.removeCallbacksAndMessages(null);
                                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            }
            view.setAlpha(1);
            return false;
        }
        else {
            return false;
        }
    }









    /* android:layout_alignParentRight="true"
            android:scaleType="fitCenter"*/

    /*<translate
        android:duration="500"
        android:fromYDelta="8"
        android:toYDelta="-8"
        android:startOffset="4000"

        ></translate>*/
}