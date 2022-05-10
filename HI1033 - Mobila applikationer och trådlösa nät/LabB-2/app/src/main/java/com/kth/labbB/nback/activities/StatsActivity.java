package com.kth.labbB.nback.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import com.google.gson.Gson;
import com.kth.labbB.nback.R;
import com.kth.labbB.nback.StatsListAdapter;
import com.kth.labbB.nback.model.NBackLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatsActivity extends AppCompatActivity {

    private final List<NBackLogic.Stats> stats = new ArrayList<NBackLogic.Stats>();
    private final StatsListAdapter statsRvAdapter = new StatsListAdapter(stats);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView statsListRV = findViewById(R.id.statsRecyclerView);
        statsListRV.setAdapter(statsRvAdapter);
        statsListRV.setLayoutManager(new LinearLayoutManager(this));

        Button clearBtn = findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(v -> {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPref.edit()
                    .putString("stats", null)
                    .apply();
            syncStatsList();
        });

        syncStatsList();
    }

    private void syncStatsList() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String statsString = sharedPref.getString("stats", null);
        List<NBackLogic.Stats> newStats;

        if (statsString != null) {
            newStats = Arrays.asList(new Gson().fromJson(statsString, NBackLogic.Stats[].class));
        } else {
            newStats = new ArrayList<>();
        }

        stats.clear();
        stats.addAll(newStats);
        statsRvAdapter.notifyDataSetChanged();
    }
}