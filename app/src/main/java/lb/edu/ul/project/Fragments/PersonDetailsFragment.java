package lb.edu.ul.project.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import lb.edu.ul.project.Domain.Person;
import lb.edu.ul.project.R;

public class PersonDetailsFragment extends Fragment {
    private static final String ARG_PERSON = "person";
    private Person person;

    public static PersonDetailsFragment newInstance(Person person) {
        PersonDetailsFragment fragment = new PersonDetailsFragment();
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
        View view = inflater.inflate(R.layout.fragment_person_details, container, false);

        ImageView profileImage = view.findViewById(R.id.profileImage);
        TextView nameText = view.findViewById(R.id.nameText);
        TextView roleText = view.findViewById(R.id.roleText);
        TextView birthDateText = view.findViewById(R.id.birthDateText);
        TextView biographyText = view.findViewById(R.id.biographyText);
        TextView knownForText = view.findViewById(R.id.knownForText);

        if (person != null) {
            nameText.setText(person.getName());
            roleText.setText(person.getRole());

            if (person.getImageUrl() != null && !person.getImageUrl().isEmpty()) {
                Glide.with(this)
                        .load(person.getImageUrl())
                        .placeholder(R.drawable.profile)
                        .into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.profile);
            }

            if (person.getBirthDate() != null && !person.getBirthDate().isEmpty()) {
                birthDateText.setText("Born: " + person.getBirthDate());
                birthDateText.setVisibility(View.VISIBLE);
            } else {
                birthDateText.setVisibility(View.GONE);
            }

            if (person.getBiography() != null && !person.getBiography().isEmpty()) {
                biographyText.setText(person.getBiography());
            } else {
                biographyText.setText("Biography not available.");
            }

            if (person.getKnownFor() != null && !person.getKnownFor().isEmpty()) {
                StringBuilder knownFor = new StringBuilder();
                for (int i = 0; i < person.getKnownFor().size(); i++) {
                    knownFor.append(person.getKnownFor().get(i));
                    if (i < person.getKnownFor().size() - 1) {
                        knownFor.append(", ");
                    }
                }
                knownForText.setText("Known for: " + knownFor.toString());
                knownForText.setVisibility(View.VISIBLE);
            } else {
                knownForText.setVisibility(View.GONE);
            }
        }

        return view;
    }
}
