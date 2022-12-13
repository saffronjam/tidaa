package com.example.labb3ctimesl.model.json;

public class CourseMetadata {
    public Title title;
    public String code;

    public static class Title {
        public String sv;
        public String en;

        public Title(String sv, String en) {
            this.sv = sv;
            this.en = en;
        }
    }

    public CourseMetadata(Title title, String code) {
        this.title = title;
        this.code = code;
    }

    public CourseMetadata() {
    }

    public static CourseMetadata getFailed(String courseCode) {
        return new CourseMetadata(new Title("Okänd", "Unknown"), courseCode);
    }
}
