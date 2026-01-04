package lb.edu.ul.project.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import lb.edu.ul.project.Domain.Theater;
import lb.edu.ul.project.R;

public class TheaterAdapter extends RecyclerView.Adapter<TheaterAdapter.TheaterViewHolder> {
    private List<Theater> theaters;
    private Context context;

    public TheaterAdapter(List<Theater> theaters, Context context) {
        this.theaters = theaters;
        this.context = context;
    }

    @NonNull
    @Override
    public TheaterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_theater, parent, false);
        return new TheaterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TheaterViewHolder holder, int position) {
        Theater theater = theaters.get(position);
        
        holder.nameText.setText(theater.getName());
        holder.addressText.setText(theater.getAddress());
        holder.distanceText.setText(String.format("%.1f km away", theater.getDistance()));
        holder.hoursText.setText(theater.getOpeningHours());

        holder.callButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + theater.getPhone()));
            context.startActivity(intent);
        });

        holder.directionsButton.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(theater.getAddress()));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            
            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(mapIntent);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(theater.getName() + " " + theater.getAddress()));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            context.startActivity(mapIntent);
        });
    }

    @Override
    public int getItemCount() {
        return theaters.size();
    }

    static class TheaterViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, addressText, distanceText, hoursText;
        MaterialButton callButton, directionsButton;

        public TheaterViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.theaterName);
            addressText = itemView.findViewById(R.id.theaterAddress);
            distanceText = itemView.findViewById(R.id.theaterDistance);
            hoursText = itemView.findViewById(R.id.theaterHours);
            callButton = itemView.findViewById(R.id.callButton);
            directionsButton = itemView.findViewById(R.id.directionsButton);
        }
    }
}
