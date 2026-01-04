package lb.edu.ul.project.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import lb.edu.ul.project.Adapters.FilmographyAdapter;
import lb.edu.ul.project.Domain.Person;
import lb.edu.ul.project.R;

public class FilmographyFragment extends Fragment {
    private static final String ARG_PERSON = "person";
    private Person person;

    public static FilmographyFragment newInstance(Person person) {
        FilmographyFragment fragment = new FilmographyFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PERSON, person);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            person = (Person) getArguments().getSerializable(ARG_PERSON);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filmography, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.filmographyRecyclerView);
        TextView emptyText = view.findViewById(R.id.emptyFilmographyText);

        if (person != null && person.getFilmography() != null && !person.getFilmography().isEmpty()) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            FilmographyAdapter adapter = new FilmographyAdapter(person.getFilmography(), getContext());
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        }

        return view;
    }
}
