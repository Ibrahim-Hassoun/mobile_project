/*package lb.edu.ul.project.Adapters;

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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import lb.edu.ul.project.Activities.DetailActivity;
import lb.edu.ul.project.Domain.ListFilm;
import lb.edu.ul.project.R;

public class FilmListAdapter extends RecyclerView.Adapter<FilmListAdapter.ViewHolder> {
    ListFilm items;
    Context context;
    public FilmListAdapter(ListFilm items){
        this.items=items;
    }

    @NonNull
    @Override
    public FilmListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View inflate= LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_film,parent,false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmListAdapter.ViewHolder holder, int position) {
        holder.titleTxt.setText(items.getData().get(position).getTitle());
        RequestOptions requestOptions= new RequestOptions();
        requestOptions=requestOptions.transform(new CenterCrop(),new RoundedCorners(30));

        Glide.with(context)
                .load(items.getData().get(position).getPoster())
                .apply(requestOptions)
                .into(holder.pic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent=new Intent(holder.itemView.getContext(), DetailActivity.class);
                //intent.putExtra("id",items.getData().get(position).getId());
                //context.startActivity(intent);
                int adapterPosition = holder.getAdapterPosition(); // Dynamically fetch position
                if (adapterPosition != RecyclerView.NO_POSITION) { // Ensure position is valid
                    Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
                    intent.putExtra("id", items.getData().get(adapterPosition).getId());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.getData().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt;
        ImageView pic;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            titleTxt=itemView.findViewById(R.id.titleTxt);
            pic=itemView.findViewById(R.id.pic);
        }
    }
}

 */
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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import lb.edu.ul.project.Activities.DetailActivity;
import lb.edu.ul.project.Domain.Datum;
import lb.edu.ul.project.R;

public class FilmListAdapter extends RecyclerView.Adapter<FilmListAdapter.ViewHolder> {
    private final ArrayList<Datum> items;
    private final Context context;

    public FilmListAdapter(Context context, ArrayList<Datum> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public FilmListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_film, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull FilmListAdapter.ViewHolder holder, int position) {
        Datum movie = items.get(position);
        holder.titleTxt.setText(movie.getTitle());

        RequestOptions requestOptions = new RequestOptions()
                .transform(new CenterCrop(), new RoundedCorners(30));

        Glide.with(context)
                .load(movie.getPoster())
                .apply(requestOptions)
                .into(holder.pic);

        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition(); // Dynamically fetch position
            if (adapterPosition != RecyclerView.NO_POSITION) { // Ensure position is valid
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("id", items.get(adapterPosition).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}

