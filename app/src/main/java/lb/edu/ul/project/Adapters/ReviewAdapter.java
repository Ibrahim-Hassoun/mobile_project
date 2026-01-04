package lb.edu.ul.project.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lb.edu.ul.project.Domain.Review;
import lb.edu.ul.project.R;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<Review> reviews;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);

        holder.userNameTextView.setText(review.getUserName());
        holder.ratingBar.setRating(review.getRating());
        holder.ratingTextView.setText(String.format(Locale.getDefault(), "%.1f", review.getRating()));
        holder.reviewTextView.setText(review.getReviewText());

        // Format timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String dateStr = sdf.format(new Date(review.getTimestamp()));
        holder.dateTextView.setText(dateStr);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        RatingBar ratingBar;
        TextView ratingTextView;
        TextView reviewTextView;
        TextView dateTextView;

        ViewHolder(View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            reviewTextView = itemView.findViewById(R.id.reviewTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}
