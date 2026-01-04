package lb.edu.ul.project.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import lb.edu.ul.project.Domain.Datum;
import lb.edu.ul.project.R;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.ViewHolder> {
    private List<Datum> movies;
    private OnRemoveClickListener onRemoveClickListener;
    private OnMovieClickListener onMovieClickListener;

    public interface OnRemoveClickListener {
        void onRemove(int position);
    }

    public interface OnMovieClickListener {
        void onClick(Datum movie);
    }

    public WatchlistAdapter(List<Datum> movies, OnRemoveClickListener removeListener, OnMovieClickListener clickListener) {
        this.movies = movies;
        this.onRemoveClickListener = removeListener;
        this.onMovieClickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_watchlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Datum movie = movies.get(position);

        holder.titleTextView.setText(movie.getTitle());
        holder.yearTextView.setText(movie.getYear());
        
        String rating = movie.getImdbRating();
        if (rating != null && !rating.isEmpty()) {
            holder.ratingTextView.setText("⭐ " + rating);
            holder.ratingTextView.setVisibility(View.VISIBLE);
        } else {
            holder.ratingTextView.setVisibility(View.GONE);
        }

        // Load poster image
        RequestOptions requestOptions = new RequestOptions()
                .transform(new CenterCrop(), new RoundedCorners(30));

        Glide.with(holder.itemView.getContext())
                .load(movie.getPoster())
                .apply(requestOptions)
                .into(holder.posterImageView);

        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (onMovieClickListener != null) {
                onMovieClickListener.onClick(movie);
            }
        });

        holder.removeButton.setOnClickListener(v -> {
            if (onRemoveClickListener != null) {
                onRemoveClickListener.onRemove(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView posterImageView;
        TextView titleTextView;
        TextView yearTextView;
        TextView ratingTextView;
        ImageButton removeButton;

        ViewHolder(View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.posterImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            yearTextView = itemView.findViewById(R.id.yearTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}
