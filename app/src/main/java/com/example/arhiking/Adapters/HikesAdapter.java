package com.example.arhiking.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arhiking.Tour;
import com.example.arhiking.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class HikesAdapter extends RecyclerView.Adapter<HikesAdapter.HikesViewHolder>{

    private final List<Tour> hikes;

    public HikesAdapter(List<Tour> hikes) {
        this.hikes = hikes;
    }

    @NonNull
    @Override
    public HikesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HikesViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_hike, parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull HikesViewHolder holder, int position) {
        holder.setHike(hikes.get(position));
    }

    @Override
    public int getItemCount() {
        return hikes.size();
    }

    static class HikesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final RoundedImageView imageHikePoster;
        private final TextView hikeName, hikeLocation, hikeLength, hikeDate, hikeElevation, hikeTime;
        private final RatingBar ratingBar;

        ImageButton hikeInfoButton;

        public HikesViewHolder(View view){
            super(view);
            imageHikePoster = view.findViewById(R.id.imageHikePoster);
            hikeName = view.findViewById(R.id.hikeName);
            hikeLocation = view.findViewById(R.id.hikeLocation);
            hikeLength = view.findViewById(R.id.hikeLength);
            hikeElevation = view.findViewById(R.id.hikeElevation);
            hikeDate = view.findViewById(R.id.hikeDate);
            hikeTime = view.findViewById(R.id.hikeTime);
            ratingBar = view.findViewById(R.id.hikeRatingBar);

            //hike info button in home fragment
            this.hikeInfoButton = (ImageButton) view.findViewById(R.id.hikeInfoButton);
            hikeInfoButton.setOnClickListener(this);
        }

        void setHike(Tour hike){
            imageHikePoster.setImageBitmap(hike.btmpPoster);
            hikeName.setText(hike.name);
            hikeLocation.setText(hike.hikeLocation);
            hikeLength.setText(hike.hikeLength);
            hikeElevation.setText(hike.hikeElevation);
            hikeDate.setText(hike.hikeDate);
            hikeTime.setText(hike.hikeTime);
            ratingBar.setRating(hike.rating);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "Will show hike info window", Toast.LENGTH_SHORT).show();
        }
    }
}
