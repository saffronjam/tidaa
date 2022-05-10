package com.example.weatherthreepointo.model.json;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class WeatherData {
    private final Date validTime;
    private final List<Parameters> parameters;

    public WeatherData(Date validTime, List<Parameters> parameters) {
        this.validTime = validTime;
        this.parameters = parameters;
    }

    public Date getValidTime() {
        return validTime;
    }

    public List<Parameters> getParameters() {
        return parameters;
    }

    public static class Parameters {
        private final String name;
        private final String levelType;
        private final int level;
        private final String unit;
        private final List<Float> values;

        public Parameters(String name, String levelType, int level, String unit, List<Float> values) {
            this.name = name;
            this.levelType = levelType;
            this.level = level;
            this.unit = unit;
            this.values = values;
        }

        public String getName() {
            return name;
        }

        public String getLevelType() {
            return levelType;
        }

        public int getLevel() {
            return level;
        }

        public String getUnit() {
            return unit;
        }

        public List<Float> getValues() {
            return values;
        }
    }
}
