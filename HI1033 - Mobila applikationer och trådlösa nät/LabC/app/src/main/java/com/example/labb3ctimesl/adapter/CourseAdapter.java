package com.example.labb3ctimesl.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.labb3ctimesl.R;
import com.example.labb3ctimesl.model.Course;

import java.util.List;
import java.util.Set;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    public interface CourseChoiceListerner {
        void onChoice(Course course, boolean add);
    }

    private CourseChoiceListerner courseChoiceListerner;
    private final List<Course> courses;
    private final Set<String> selectedCourses;

    public CourseAdapter(List<Course> courses, Set<String> selectedCourses) {
        this.courses = courses;
        this.selectedCourses = selectedCourses;
    }

    @NonNull
    @Override
    public CourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.course_card, parent, false);
        final CourseAdapter.ViewHolder vh = new CourseAdapter.ViewHolder(itemView);

        vh.title = itemView.findViewById(R.id.courseTitle);
        vh.icon = itemView.findViewById(R.id.courseIcon);
        vh.credits = itemView.findViewById(R.id.coursePoints);
        vh.code = itemView.findViewById(R.id.courseCode);
        vh.checkBox = itemView.findViewById(R.id.courseAddCheckBox);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.ViewHolder holder, int position) {
        Course entry = courses.get(position);
        holder.icon.setImageResource(getImageOnType(entry.getEducationalLevel().toString()));
        holder.title.setText(entry.getTitle());
        holder.credits.setText(entry.getCredits() + " " + entry.getCreditUnitAbber());
        holder.code.setText(entry.getCode());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (courseChoiceListerner != null) {
                courseChoiceListerner.onChoice(entry, isChecked);
            }
        });
        holder.checkBox.setChecked(selectedCourses.contains(entry.getCode()));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    private int getImageOnType(String type) {
        switch (type) {
            case "RESEARCH":
                return R.drawable.ic_baseline_biotech_24;
            case "BASIC":
            case "PREPARATORY":
                return R.drawable.ic_baseline_menu_book_24;
            case "ADVANCED":
                return R.drawable.ic_baseline_school_24;
            default:
                return R.drawable.ic_baseline_question_mark_24;
        }
    }

    public void setChoseCourseListener(CourseChoiceListerner courseChoiceListerner) {
        this.courseChoiceListerner = courseChoiceListerner;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView icon;
        public TextView code;
        public TextView title;
        public TextView credits;
        public CheckBox checkBox;

        public ViewHolder(View v) {
            super(v);
        }
    }
}
