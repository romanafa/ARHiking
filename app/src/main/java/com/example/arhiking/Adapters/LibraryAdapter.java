package com.example.arhiking.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arhiking.R;
import com.example.arhiking.Tour;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.HikeLibraryViewHolder> implements Filterable {
    private final ArrayList<Tour> hikeList;
    private final ArrayList<Tour> hikeListFull;

    public LibraryAdapter(ArrayList<Tour> hikes) {
        this.hikeList = hikes;
        hikeListFull = new ArrayList<>(hikeList);
    }

    @NonNull
    @Override
    public HikeLibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HikeLibraryViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_hike_library, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull HikeLibraryViewHolder holder, int position) {
        holder.setHike(hikeList.get(position));
    }

    @Override
    public int getItemCount() {
        return hikeList.size();
    }

    public static class HikeLibraryViewHolder extends RecyclerView.ViewHolder{
        public RoundedImageView libraryHikePoster;
        public TextView libraryHikeNameTextView, libraryHikeLocationTextView, libraryDistanceDataTextView,
                libraryDurationDataTextView, libraryAscentDataTextView, libraryElevationDataTextView,
                libraryDifficultyDataTextView,
                libraryCategoryDataTextView, libraryDateDataTextView;
        public RoundedImageView librarySmallImage1, librarySmallImage2, librarySmallImage3;
        public RatingBar libraryHikeRatingBar;

        public HikeLibraryViewHolder(@NonNull View itemView) {
            super(itemView);

            libraryHikePoster = itemView.findViewById(R.id.imageLibraryHikePoster);
            libraryHikeNameTextView = itemView.findViewById(R.id.libraryHikeNameTextView);
            libraryHikeLocationTextView = itemView.findViewById(R.id.libraryHikeLocationTextView);
            libraryDistanceDataTextView = itemView.findViewById(R.id.libraryDistanceDataTextView);
            libraryDurationDataTextView = itemView.findViewById(R.id.libraryDurationDataTextView);
            libraryAscentDataTextView = itemView.findViewById(R.id.libraryAscentDataTextView);
            libraryElevationDataTextView = itemView.findViewById(R.id.libraryElevationDataTextView);
            libraryDifficultyDataTextView = itemView.findViewById(R.id.libraryDifficultyDataTextView);
            libraryCategoryDataTextView = itemView.findViewById(R.id.libraryCategoryDataTextView);
            libraryDateDataTextView = itemView.findViewById(R.id.libraryDateDataTextView);
            libraryHikeRatingBar = itemView.findViewById(R.id.libraryHikeRatingBar);
            librarySmallImage1 = itemView.findViewById(R.id.librarySmallImage1);
            librarySmallImage2 = itemView.findViewById(R.id.librarySmallImage2);
            librarySmallImage3 = itemView.findViewById(R.id.librarySmallImage3);
        }

        void setHike(Tour hike){

            libraryHikePoster.setImageBitmap(hike.btmpPoster);
            librarySmallImage1.setImageBitmap(hike.btmpImage1);
            librarySmallImage2.setImageBitmap(hike.btmpImage2);
            librarySmallImage3.setImageBitmap(hike.btmpImage3);

            /* Default bilder:
            libraryHikePoster.setImageResource(hike.poster);
            librarySmallImage1.setImageResource(hike.image1);
            librarySmallImage2.setImageResource(hike.image2);
            librarySmallImage3.setImageResource(hike.image3);
            */

            libraryHikeNameTextView.setText(hike.name);
            libraryDateDataTextView.setText(hike.hikeDate);
            libraryHikeLocationTextView.setText(hike.hikeLocation);
            libraryDistanceDataTextView.setText(hike.hikeLength);
            libraryDurationDataTextView.setText(hike.hikeTime);
            libraryAscentDataTextView.setText(hike.hikeAscent);
            libraryElevationDataTextView.setText(hike.hikeElevation);
            libraryDifficultyDataTextView.setText(hike.hikeDifficulty);
            libraryCategoryDataTextView.setText(hike.hikeCategory);
            libraryHikeRatingBar.setRating(hike.rating);
        }
    }

    @Override
    public Filter getFilter(){
        return hikeLibraryFilter;
    }

    private Filter hikeLibraryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Tour> filteredList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0){
                filteredList.addAll(hikeListFull);
            }
            else{
                // filter pattern - not case sensitive, trim empty space in front or at the end of string
                String filterPattern = charSequence.toString().toLowerCase().trim();

                // loop through the list
                for (Tour hike : hikeListFull) {
                    if(hike.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(hike);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            // clear the list so only filtered items are added from filteredList
            hikeList.clear();
            hikeList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
