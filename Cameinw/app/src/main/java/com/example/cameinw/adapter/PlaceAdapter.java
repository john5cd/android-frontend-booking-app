package com.example.cameinw.adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.cameinw.R;
import com.example.cameinw.api.ImageApi;
import com.example.cameinw.model.Image;
import com.example.cameinw.model.Place;
import com.example.cameinw.service.AverageRating;
import com.example.cameinw.util.ImageUtils;
import com.example.cameinw.util.RetrofitUtils;
import com.example.cameinw.util.UserInfoUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceHolder> {

    private PlaceRecyclerViewInt placeRecyclerViewInt;
    private List<Place> places;
    private String userToken;
    private Context context;
    private Integer placeId;
    private List<Bitmap> fetchedImagesList = new ArrayList<>();


    public PlaceAdapter(PlaceRecyclerViewInt placeRecyclerViewInt, List<Place>
            places, Context context) {
        this.placeRecyclerViewInt = placeRecyclerViewInt;
        this.places = places;
        this.context = context;
        userToken = UserInfoUtils.getUserToken(context);
    }

    @NonNull
    @Override
    public PlaceAdapter.PlaceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_places, parent, false);
        return new PlaceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceAdapter.PlaceHolder holder, int position) {
        Place place = places.get(position);
        placeId = place.getId();
        Log.d("PlaceAdapter", "Place ID: " + place.getId());

        ImageApi imageApi = RetrofitUtils.createRequest(ImageApi.class, userToken);
        Call<List<Image>> call = imageApi.getImagesByPlaceId(placeId);
        call.enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, Response<List<Image>>
                    response) {
                if (response.isSuccessful()) {
                    Log.d("PlaceAdapter", "Response Body Size: " +
                            response.body().size());
                    handleSuccessfulPlaceImageResponse(response.body(), holder);
                } else {
                    handleUnsuccessfulResponse(response.code());
                    Log.d("PlaceAdapter", "FINAL fetchedImagesList Size: " +
                            fetchedImagesList.size());
                }
            }

            @Override
            public void onFailure(Call<List<Image>> call, Throwable t) {
                handleFailure(t.getMessage());
            }

        });

        holder.name.setText(place.getName());
        holder.cost.setText(place.getCost().toString()+" per day");
        holder.address.setText(place.getAddress());

        holder.placeContainer.setOnClickListener(v -> {
            placeRecyclerViewInt.onPlaceClick(position);
        });

        holder.mapPin.setOnClickListener(v -> {
            Context context = v.getContext();
            String myLatitude = place.getLatitude().toString();
            String myLongitude = place.getLongitude().toString();
            String label = "my address";
            Uri urlAddress =
                    Uri.parse("geo:"+myLatitude+","+myLongitude+"?q="+myLatitude+","+myLongitude+
                            "(" + label + ")");
            Intent intent = new Intent(Intent.ACTION_VIEW, urlAddress);
            intent.setPackage("com.google.android.apps.maps");
            startActivity(context, intent, null);
        });

        String averageRating = AverageRating.getAveragePlaceRating(place);

        holder.propertyRating.setText(averageRating);

    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    private void handleSuccessfulPlaceImageResponse(List<Image> placeImages,
                                                    PlaceHolder holder) {
        List<Integer> imageIdsToFetchImagesFor = new ArrayList<>();
        Log.d("PlaceAdapter", "placeImages Size: " + placeImages.size());
        for (Image image : placeImages) {
            imageIdsToFetchImagesFor.add(image.getId());
            Log.d("PlaceAdapter", "Image ID: " + image.getId());

        }

        Log.d("PlaceAdapter", "imageIdsToFetchImagesFor Size: " +
                imageIdsToFetchImagesFor.size());

        // Clear the existing data (if exists) in fetchedImagesList
        fetchedImagesList.clear();

        // Fetch images for the current place
        ImageUtils.fetchMultiplePlacesImageWithRetry(placeId,
                imageIdsToFetchImagesFor, userToken, 5, new ImageUtils.ImageFetchCallback()
                {
                    @Override
                    public void onImageFetchSuccess(int imageId, Bitmap placeImageBitmap) {
                        if (placeImageBitmap != null) {
                            fetchedImagesList.add(placeImageBitmap);
                            Log.d("PlaceAdapter", "fetchedImagesList Size: " +
                                    fetchedImagesList.size());


                            // Check if all images have been fetched
                            if (fetchedImagesList.size() == placeImages.size()) {
                                // Create the adapter after fetching all images
                                ImageAdapter adapter = new ImageAdapter(context,
                                        fetchedImagesList);

                                // Set the adapter to the ViewPager
                                holder.viewPager.setAdapter(adapter);

                                // Notify the adapter that data has changed
                                adapter.notifyDataSetChanged();
                            }
                        }

                    }

                    @Override
                    public void onImageFetchError(int imageId) {
                        handleImageFetchError();
                    }
                });
    }


    private void handleUnsuccessfulResponse(int responseCode) {
        Log.e("PlaceAdapter", "Unsuccessful response: " + responseCode);
    }

    private void handleFailure(String errorMessage) {
        Log.e("PlaceAdapter", "Error occurred: " + errorMessage);
    }

    private void handleImageFetchError() {
        //Toast.makeText(context, "Error fetching place image", Toast.LENGTH_SHORT).show();
    }

    public static class PlaceHolder extends RecyclerView.ViewHolder {
        TextView name, cost, propertyRating, address;
        ViewPager viewPager;
        ImageView mapPin;
        ConstraintLayout placeContainer;
        public PlaceHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.place_name);
            cost = itemView.findViewById(R.id.list_places_cost);
            propertyRating = itemView.findViewById(R.id.place_rating);
            placeContainer = itemView.findViewById(R.id.place_container);
            mapPin = itemView.findViewById(R.id.map_pin);
            viewPager = itemView.findViewById(R.id.viewPager);
            address = itemView.findViewById(R.id.openMaps);
        }
    }
}
