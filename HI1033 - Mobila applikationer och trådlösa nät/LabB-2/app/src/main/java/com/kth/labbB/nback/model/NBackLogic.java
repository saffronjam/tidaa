package com.kth.labbB.nback.model;

import java.util.Random;

public class NBackLogic {
    public static class Stats {
        private final int correctVisuals;
        private final int failedVisuals;
        private final int correctAudio;
        private final int failedAudio;
        private final int n;

        public Stats(int correctVisuals, int failedVisuals, int correctAudio, int failedAudio, int n) {
            this.correctVisuals = correctVisuals;
            this.failedVisuals = failedVisuals;
            this.correctAudio = correctAudio;
            this.failedAudio = failedAudio;
            this.n = n;
        }

        public int getCorrectVisuals() {
            return correctVisuals;
        }

        public int getFailedVisuals() {
            return failedVisuals;
        }

        public int getCorrectAudio() {
            return correctAudio;
        }

        public int getFailedAudio() {
            return failedAudio;
        }

        public int getN() {
            return n;
        }
    }
    public static class Position{
        private final int x, y;

        public Position(int x, int y){
            this.x = x;
            this.y = y;
        }

        public int getX(){
            return x;
        }

        public int getY(){
            return y;
        }

        public boolean equals(Position other){
            return this.x == other.x && this.y == other.y;
        }
    }

    private static NBackLogic singleton = null;

    private int n;
    public static final int SIZE = 3;

    private int round = 0;
    private int maxRounds;

    private Position[] stimuliVisuals;
    private char[] stimuliAudio;

    private static final char[] letters = new char[]{'C', 'H', 'K','B'};

    private int correctVisuals;
    private int failedVisuals;
    private int correctAudio;
    private int failedAudio;

    private boolean canGuessVisual;
    private boolean canGuessAudio;

    public static NBackLogic getInstance(){
        if(singleton == null){
            singleton = new NBackLogic();
        }
        return singleton;
    }

    public void restart(int n, int maxRounds){
        this.round = 0;
        this.n = n;
        this.maxRounds = maxRounds;
        this.stimuliVisuals = new Position[maxRounds];
        this.stimuliAudio = new char[maxRounds];

        this.correctVisuals = 0;
        this.failedVisuals = 0;
        this.correctAudio = 0;
        this.failedAudio = 0;

        Random random = new Random();
        for(int i = 0; i < stimuliVisuals.length; i++){
            stimuliVisuals[i] = new Position(Math.abs(random.nextInt()) % SIZE, Math.abs(random.nextInt()) % SIZE);
        }
        for(int i = 0; i < stimuliAudio.length; i++){
            stimuliAudio[i] = letters[Math.abs(random.nextInt()) % letters.length];
        }
    }

    public void onTick(){
        this.round++;
    }

    public boolean isFinished() {
        return this.round == this.maxRounds;
    }

    public Position getLastVisual(){
        return this.stimuliVisuals[round];
    }

    public char getLastAudio(){
        return this.stimuliAudio[round];
    }

    public int getN(){
        return n;
    }

    public boolean canChoose(){
        return this.round >= n;
    }

    public boolean makeAudioChoice(){
        if(!canChoose()){
            throw new IllegalStateException("Tried to choose audio when round number had not passed n");
        }
        boolean result = this.stimuliAudio[round] == this.stimuliAudio[round - n];
        if(result) {
            this.correctAudio++;
        }else{
            this.failedAudio++;
        }
        return result;
    }

    public boolean makeVisualChoice(){
        if(!canChoose()) {
            throw new IllegalStateException("Tried to choose position when round number had not passed n");
        }
        boolean result = this.stimuliVisuals[round].equals(this.stimuliVisuals[round - n]);
        if(result) {
            this.correctVisuals++;
        }else{
            this.failedVisuals++;
        }
        return result;
    }

    public int getRound(){
        return Math.min(this.round, this.maxRounds); 
    }

    public int getTotalRounds(){
        return this.maxRounds;
    }

    public int getNoCorrectVisualChoices(){
        return this.correctVisuals;
    }

    public int getNoCorrectAudioChoices(){
        return this.correctAudio;
    }

    public Stats getStats(){
        return new Stats(this.correctVisuals, this.failedVisuals, this.correctAudio, this.failedAudio, this.n);
    }
}
