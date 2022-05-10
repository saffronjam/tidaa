package com.kth.labbB.nback;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kth.labbB.nback.model.NBackLogic;

import java.util.List;

public class StatsListAdapter extends RecyclerView.Adapter<StatsListAdapter.ViewHolder> {
    private final List<NBackLogic.Stats> data;

    public StatsListAdapter(List<NBackLogic.Stats> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public StatsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.stats_card, parent, false);
        return new ViewHolder(rowItem);
    }

    @Override
    public void onBindViewHolder(StatsListAdapter.ViewHolder holder, int position) {
        holder.correctVisuals.setText(String.valueOf(data.get(position).getCorrectVisuals()));
        holder.failedVisuals.setText(String.valueOf(data.get(position).getFailedVisuals()));
        holder.correctAudio.setText(String.valueOf(data.get(position).getCorrectAudio()));
        holder.failedAudio.setText(String.valueOf(data.get(position).getFailedAudio()));
        holder.n.setText(String.valueOf(data.get(position).getN()));
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView correctVisuals;
        private final TextView failedVisuals;
        private final TextView correctAudio;
        private final TextView failedAudio;
        private final TextView n;

        public ViewHolder(View view) {
            super(view);
            this.n = view.findViewById(R.id.back_nInfo);
            this.correctVisuals = view.findViewById(R.id.visualMatchCorrect);
            this.failedVisuals = view.findViewById(R.id.visualMatchIncorrect);
            this.correctAudio = view.findViewById(R.id.audioMatchCorrect);
            this.failedAudio = view.findViewById(R.id.audioMatchIncorrect);
        }
    }
}