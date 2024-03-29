package ar.edu.itba.ingesoft.ui.coursestaught;


import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import ar.edu.itba.ingesoft.CachedData.CoursesTeachersCache;
import ar.edu.itba.ingesoft.Classes.Course;
import ar.edu.itba.ingesoft.Classes.User;
import ar.edu.itba.ingesoft.Firebase.DatabaseConnection;
import ar.edu.itba.ingesoft.Interfaces.Adapters.OnListContentUpdatedListener;
import ar.edu.itba.ingesoft.Interfaces.Adapters.OnSelectionModeListener;
import ar.edu.itba.ingesoft.MainActivity;
import ar.edu.itba.ingesoft.R;
import ar.edu.itba.ingesoft.ui.recyclerviews.Adapters.AddCourseAdapter;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddCourseFragment extends Fragment implements OnListContentUpdatedListener<String>, OnSelectionModeListener {


    private CoursesTaughtViewModel viewModel;
    private AddCourseAdapter addCourseAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Button addCourseButton;

    private String university;

    private SearchView searchView=null;
    private SearchView.OnQueryTextListener queryTextListener;

    public AddCourseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_appbar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        /*todo borrar esto es solo para probar persistencia de cache*/
        for(Map.Entry<String, HashSet<User>> e : CoursesTeachersCache.getCourseTeachers().entrySet()){
            Log.v("Cached_Users", e.getKey() + " " + e.getValue().stream().reduce("",
                    (accum, x) -> accum = accum + " " + x.getName(),
                    (accum, accum2) -> accum2 = accum2 + accum
            ));
        }

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {

                    addCourseAdapter.getFilter().filter(newText);

                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    addCourseAdapter.getFilter().filter(query);

                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                // Not implemented here
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_add_course, container, false);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(MainActivity.SP, MODE_PRIVATE);
        this.university = sharedPreferences.getString(MainActivity.UNIV_SP, "");

        viewModel = (CoursesTaughtViewModel) ViewModelProviders.of(getActivity()).get(CoursesTaughtViewModel.class);

        //RecyclerView
        RecyclerView rV = root.findViewById(R.id.addCourseRecyclerView);
        addCourseAdapter = viewModel.getAddCourseAdapterLiveData(this).getValue();
        rV.setAdapter(addCourseAdapter);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        rV.setLayoutManager(linearLayoutManager);

        viewModel.getCourses(university).observe(this, new Observer<List<Course>>() {
            @Override
            public void onChanged(List<Course> courses) {
                addCourseAdapter.update(courses);
            }
        });

        addCourseButton = root.findViewById(R.id.addCourseButton);
        addCourseButton.setEnabled(false);
        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCourseAdapter.addSelectedCourses();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        addCourseAdapter.clearSelection();
    }

    @Override
    public void onSelectionModeEnabled() {
        addCourseButton.setEnabled(true);
    }

    @Override
    public void onSelectionModeDisabled() {
        addCourseButton.setEnabled(false);
    }

    @Override
    public void onContentUpdated(List<String> newList) {
        for(String c : newList) {
            if(!viewModel.getUser().getValue().getCoursesHashSet().contains(c)){
                viewModel.getUser().getValue().addCourse(c);
                //CoursesTeachersCache.addTeacherToCourse(viewModel.getUser().getValue(), c);
            }
        }

        (new DatabaseConnection()).UpdateCourses(this.university, FirebaseAuth.getInstance().getCurrentUser().getEmail(), viewModel.getUser().getValue().getCourses());
        Navigation.findNavController(getActivity(), R.id.coursesTaughtNavHost).popBackStack();

    }


}
