package lb.edu.ul.project.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import lb.edu.ul.project.Domain.Person;
import lb.edu.ul.project.Fragments.FilmographyFragment;
import lb.edu.ul.project.Fragments.PersonDetailsFragment;
import lb.edu.ul.project.R;

public class PersonProfileActivity extends AppCompatActivity {
    private Person person;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        person = (Person) getIntent().getSerializableExtra("person");
        if (person == null) {
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(person.getName());
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        setupViewPager();
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Profile");
                    break;
                case 1:
                    tab.setText("Filmography");
                    break;
            }
        }).attach();
    }

    private class ViewPagerAdapter extends FragmentStateAdapter {
        public ViewPagerAdapter(AppCompatActivity activity) {
            super(activity);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return PersonDetailsFragment.newInstance(person);
                case 1:
                    return FilmographyFragment.newInstance(person);
                default:
                    return PersonDetailsFragment.newInstance(person);
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
