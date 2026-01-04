package lb.edu.ul.project.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import lb.edu.ul.project.Activities.DetailActivity;
import lb.edu.ul.project.Domain.Person;
import lb.edu.ul.project.R;

public class FilmographyAdapter extends RecyclerView.Adapter<FilmographyAdapter.FilmographyViewHolder> {
    private List<Person.FilmCredit> filmography;
    private Context context;

    public FilmographyAdapter(List<Person.FilmCredit> filmography, Context context) {
        this.filmography = filmography;
        this.context = context;
    }

    @NonNull
    @Override
    public FilmographyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filmography, parent, false);
        return new FilmographyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmographyViewHolder holder, int position) {
        Person.FilmCredit credit = filmography.get(position);

        holder.titleText.setText(credit.getTitle());
        holder.yearText.setText(credit.getYear());

        if (credit.getCharacterRole() != null && !credit.getCharacterRole().isEmpty()) {
            holder.roleText.setText(credit.getCharacterRole());
            holder.roleText.setVisibility(View.VISIBLE);
        } else {
            holder.roleText.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(credit.getPoster())
                .placeholder(R.drawable.profile)
                .into(holder.posterImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("id", credit.getMovieId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filmography.size();
    }

    static class FilmographyViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImage;
        TextView titleText, yearText, roleText;

        public FilmographyViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.filmPoster);
            titleText = itemView.findViewById(R.id.filmTitle);
            yearText = itemView.findViewById(R.id.filmYear);
            roleText = itemView.findViewById(R.id.filmRole);
        }
    }
}
