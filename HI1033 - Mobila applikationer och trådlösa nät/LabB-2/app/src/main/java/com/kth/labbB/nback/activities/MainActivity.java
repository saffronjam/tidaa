package com.kth.labbB.nback.activities;

import static com.kth.labbB.nback.model.NBackLogic.SIZE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.gson.Gson;
import com.kth.labbB.nback.AnimationUtils;
import com.kth.labbB.nback.R;
import com.kth.labbB.nback.SquareLayout;
import com.kth.labbB.nback.TicActivityUtils;
import com.kth.labbB.nback.model.NBackLogic;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private NBackLogic nBackLogic;

    private ImageView[] imageViews;
    private LinearProgressIndicator roundProgressLPI;
    private TextView visualMatchInfoTV;
    private TextView audioMatchInfoTV;
    private TextView nBackInfoTV;
    private Button audioBtn;
    private Button visualBtn;
    private Button actionBtn;
    private Button saveBtn;
    private ImageButton settingsIB;
    private ImageButton statsIB;
    private SquareLayout gameboardSL;
    private ConstraintLayout background;
    private TableLayout visualTbl;
    private TableLayout audioTbl;

    private TextToSpeech textToSpeach;
    private static final int utteranceId = 42;

    // Gameloop
    private Handler gameLoopHandler;
    private Runnable gameLoopRunnable;
    private int tickDelay = 3000;
    private boolean running = false;
    private boolean played = false;

    // Cache
    private int rounds = 10;
    private boolean visual = true;
    private boolean audio = true;
    private int n = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageViews = loadReferencesToImageViews();

        audioBtn = findViewById(R.id.audioBtn);
        audioBtn.setOnClickListener(v -> onAudioChoice());
        visualBtn = findViewById(R.id.visualBtn);
        visualBtn.setOnClickListener(v -> onVisualChoice());
        actionBtn = findViewById(R.id.actionBtn);
        actionBtn.setOnClickListener(v -> onActionBtnClick());
        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(v -> onSave());

        settingsIB = findViewById(R.id.settingsBtn);
        settingsIB.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
        statsIB = findViewById(R.id.statsBtn);
        statsIB.setOnClickListener(v -> {
            Intent intent = new Intent(this, StatsActivity.class);
            startActivity(intent);
        });

        roundProgressLPI = findViewById(R.id.roundInfo);
        visualMatchInfoTV = findViewById(R.id.visualMatchInfo);
        audioMatchInfoTV = findViewById(R.id.audioMatchInfo);
        nBackInfoTV = findViewById(R.id.back_nInfo);
        gameboardSL = findViewById(R.id.squareLayout);
        background = findViewById(R.id.totalBackground);
        visualTbl = findViewById(R.id.tableVisual);
        audioTbl = findViewById(R.id.tableAudio);

        nBackLogic = NBackLogic.getInstance();
    }

    // NB! Cancel the current and queued utterances, then shut down the service to
    // de-allocate resources
    @Override
    protected void onPause() {
        if (textToSpeach != null) {
            textToSpeach.stop();
            textToSpeach.shutdown();
        }
        super.onPause();
    }

    // Initialize the text-to-speech service - we do this initialization
    // in onResume because we shutdown the service in onPause
    @Override
    protected void onResume() {
        super.onResume();
        textToSpeach = new TextToSpeech(getApplicationContext(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            textToSpeach.setLanguage(Locale.UK);
                        }
                    }
                });
        onGameReset();
    }

    public void startGameLoop() {
        if (gameLoopHandler != null) {
            return;
        }

        gameLoopHandler = new Handler(Looper.getMainLooper());
        gameLoopRunnable = new Runnable() {
            @Override
            public void run() {
                gameLoopHandler.postDelayed(this, tickDelay);
                if (!nBackLogic.isFinished()) {
                    nBackLogic.onTick();
                }
                onTick();
            }
        };
        gameLoopHandler.postDelayed(gameLoopRunnable, tickDelay);
    }

    public void stopGameLoop() {
        if (gameLoopHandler == null) {
            return;
        }

        gameLoopHandler.removeCallbacks(gameLoopRunnable);
        gameLoopHandler = null;
        gameLoopRunnable = null;
    }

    public void onTick() {
        if (nBackLogic.isFinished()) {
            onGameOver();
            return;
        }

        syncUi();
        updateImageViews();
        if (audio) {
            sayIt(String.valueOf(nBackLogic.getLastAudio()));
        }
    }

    public void syncUi() {
        int value = (running ? (int) (100.0f * ((float) (nBackLogic.getRound() + 1) / (float) nBackLogic.getTotalRounds())) : 0);
        roundProgressLPI.setProgressCompat(value, true);
        nBackInfoTV.setText(String.valueOf(nBackLogic.getN()));

        if (!running) {
            actionBtn.setVisibility(View.VISIBLE);
            actionBtn.setText("Start");
        } else {
            actionBtn.setVisibility(View.INVISIBLE);
        }

        if (played) {
            saveBtn.setVisibility(View.VISIBLE);
        } else {
            saveBtn.setVisibility(View.GONE);
        }

        boolean canChoose = nBackLogic.canChoose();
        if (canChoose && running) {
            setButtonEnabled(audioBtn, true);
            setButtonEnabled(visualBtn, true);
        } else {
            setButtonEnabled(audioBtn, false);
            setButtonEnabled(visualBtn, false);
        }
    }

    public void onGameReset() {
        running = false;
        played = false;
        stopGameLoop();

        syncSettings();
        nBackLogic.restart(n, rounds);
        actionBtn.setVisibility(View.VISIBLE);
        syncUi();
    }

    public void onGameRestart() {
        running = true;
        played = false;

        syncSettings();

        actionBtn.setVisibility(View.INVISIBLE);

        stopGameLoop();
        nBackLogic.restart(n, rounds);
        onTick();

        syncUi();
        startGameLoop();
    }

    public void onGameOver() {
        running = false;
        played = true;

        setButtonEnabled(audioBtn, false);
        setButtonEnabled(visualBtn, false);
        stopGameLoop();
        syncSettings();
        syncUi();

        actionBtn.setVisibility(View.VISIBLE);

        sayIt("Game finished");
    }

    public void onAudioChoice() {
        boolean match = nBackLogic.makeAudioChoice();
        setButtonEnabled(audioBtn, false);
        audioMatchInfoTV.setText(String.valueOf(nBackLogic.getNoCorrectAudioChoices()));
        onChoice(match);
    }

    public void onVisualChoice() {
        boolean match = nBackLogic.makeVisualChoice();
        setButtonEnabled(visualBtn, false);
        visualMatchInfoTV.setText(String.valueOf(nBackLogic.getNoCorrectVisualChoices()));
        onChoice(match);
    }

    public void onChoice(boolean match) {
        if (match) {
            AnimationUtils.fadeFromSuccessfulColor(background);
        } else {
            AnimationUtils.fadeFromFailColor(background);
        }
    }

    public void onActionBtnClick() {
        onGameRestart();
    }

    public void onSave() {
        putSave();
        saveBtn.setVisibility(View.INVISIBLE);
    }

    public void putSave(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String statsString = sharedPref.getString("stats", null);
        ArrayList<NBackLogic.Stats> allStats;
        if (statsString != null) {
            allStats = new ArrayList<>(Arrays.asList(new Gson().fromJson(statsString, NBackLogic.Stats[].class)));
        } else {
            allStats = new ArrayList<>();
        }

        NBackLogic.Stats stats = nBackLogic.getStats();
        allStats.add(0, stats);

        String allStatsString = new Gson().toJson(allStats);
        sharedPref.edit()
                .putString("stats", allStatsString)
                .apply();
    }

    public void setButtonEnabled(Button button, boolean enabled) {
        if (!enabled) {
            button.setBackgroundResource(R.drawable.rounded_grey_bkg);
        } else {
            button.setBackgroundResource(R.drawable.rounded_blue_bkg);
        }
        button.setEnabled(enabled);
    }

    private void sayIt(String utterance) {
        textToSpeach.speak(utterance, TextToSpeech.QUEUE_FLUSH,
                null, "" + utteranceId);
    }

    private void updateImageViews() {
        NBackLogic.Position lastVisual = nBackLogic.getLastVisual();
        int index = lastVisual.getY() * SIZE + lastVisual.getX();
        imageViews[index].setBackgroundColor(R.attr.colorPrimary);
        AnimationUtils.fadeInImageView(imageViews[index]);
    }

    private void syncSettings() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String tryGetRounds = sharedPref.getString("rounds", null);
        if (tryGetRounds != null) {
            rounds = Integer.parseInt(tryGetRounds.substring("rounds_".length()));
        }
        visual = sharedPref.getBoolean("visual_stim", false);
        audio = sharedPref.getBoolean("audio_stim", false);
        if (!visual && !audio) {
            Toast.makeText(getApplicationContext(), "No stimuli set, default to visual", Toast.LENGTH_SHORT).show();
            visual = true;
        }

        String tryGetSeconds = sharedPref.getString("seconds", null);
        if (tryGetSeconds != null) {
            tickDelay = Integer.parseInt(tryGetSeconds.substring("seconds_".length())) * 1000;
        }
        String tryGetN = sharedPref.getString("nback", null);
        if (tryGetN != null) {
            n = Integer.parseInt(tryGetN.substring("nback_".length()));
        }

        if (!visual) {
            visualBtn.setVisibility(View.GONE);
            visualTbl.setVisibility(View.GONE);
            gameboardSL.setVisibility(View.INVISIBLE);
        } else {
            visualBtn.setVisibility(View.VISIBLE);
            gameboardSL.setVisibility(View.VISIBLE);
            visualTbl.setVisibility(View.VISIBLE);
        }
        if (!audio) {
            audioBtn.setVisibility(View.GONE);
            audioTbl.setVisibility(View.GONE);
        } else {
            audioBtn.setVisibility(View.VISIBLE);
            audioTbl.setVisibility(View.VISIBLE);
        }
    }

    private ImageView[] loadReferencesToImageViews() {
        ImageView[] imgViews = new ImageView[SIZE * SIZE];
        imgViews[0] = findViewById(R.id.imageView0);
        imgViews[1] = findViewById(R.id.imageView1);
        imgViews[2] = findViewById(R.id.imageView2);
        imgViews[3] = findViewById(R.id.imageView3);
        imgViews[4] = findViewById(R.id.imageView4);
        imgViews[5] = findViewById(R.id.imageView5);
        imgViews[6] = findViewById(R.id.imageView6);
        imgViews[7] = findViewById(R.id.imageView7);
        imgViews[8] = findViewById(R.id.imageView8);

        return imgViews;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // to prevent the staus bar from reappearing in landscape mode when,
        // for example, a dialog is shown
        if (hasFocus)
            TicActivityUtils.setStatusBarHiddenInLandscapeMode(this);
        Log.i("DEBUG", "onWindowFocusChanged");
    }
}