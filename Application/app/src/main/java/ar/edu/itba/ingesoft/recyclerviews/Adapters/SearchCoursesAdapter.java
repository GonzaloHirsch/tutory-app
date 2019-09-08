package ar.edu.itba.ingesoft.recyclerviews.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ar.edu.itba.ingesoft.Classes.Course;
import ar.edu.itba.ingesoft.R;

public class SearchCoursesAdapter extends RecyclerView.Adapter<SearchCoursesAdapter.SearchCoursesViewHolder> {

    List<Course> courseList = new ArrayList<>();

    @NonNull
    @Override
    public SearchCoursesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_course, parent);
        return new SearchCoursesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchCoursesViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public class SearchCoursesViewHolder extends RecyclerView.ViewHolder {

        TextView universityTextView;
        TextView courseNameTextView;
        TextView teachersTextView;
        TextView specialTextView;

        public SearchCoursesViewHolder(@NonNull View itemView) {
            super(itemView);
            universityTextView = itemView.findViewById(R.id.universityNameTextView);
            courseNameTextView = itemView.findViewById(R.id.courseNameTextView);
            teachersTextView = itemView.findViewById(R.id.courseTeachersTextView);
            specialTextView = itemView.findViewById(R.id.courseSpecialTextView);
        }
    }
}