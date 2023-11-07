package com.example.cameinw.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cameinw.R;
import com.example.cameinw.projections.PlaceProjection;
import com.example.cameinw.util.UserInfoUtils;

import java.util.List;

public class UserPlaceAdapter extends RecyclerView.Adapter<UserPlaceAdapter.UserPlaceHolder> {

    private List<PlaceProjection> places;
    private Context context;
    private String userToken;
    private PlaceRecyclerViewInt placeRecyclerViewInt;
    private PlaceProjection place;

    public UserPlaceAdapter(List<PlaceProjection> places, Context context, PlaceRecyclerViewInt placeRecyclerViewInt) {
        this.placeRecyclerViewInt = placeRecyclerViewInt;
        this.places = places;
        this.context = context;
        userToken = UserInfoUtils.getUserToken(context);
    }

    @NonNull
    @Override
    public UserPlaceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_places, parent, false);
        return new UserPlaceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPlaceHolder holder, int position) {
        place = places.get(position);
        populateText(holder, place);

        holder.constraintLayout.setOnClickListener(v -> {
            placeRecyclerViewInt.onPlaceClick(position);
        });

    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    private void populateText(UserPlaceHolder holder, PlaceProjection place) {
        holder.userPlaceAddress.setText(place.getAddress() + ", " + place.getCity());
        holder.namePlace.setText(place.getName());
        holder.costPlace.setText(place.getCost().toString());
        holder.descriptionPlace.setText(place.getDescription());
        holder.propertyType.setText(place.getType().toString());
    }

    public static class UserPlaceHolder extends RecyclerView.ViewHolder {

        public TextView userPlaceAddress, namePlace, costPlace, descriptionPlace, propertyType;
        public ConstraintLayout constraintLayout;
        public UserPlaceHolder(@NonNull View itemView) {
            super(itemView);

            namePlace = itemView.findViewById(R.id.userPlaceName);
            costPlace = itemView.findViewById(R.id.userPlaceCost);
            descriptionPlace = itemView.findViewById(R.id.userPlaceDescription);
            userPlaceAddress = itemView.findViewById(R.id.userPlaceAddress);
            constraintLayout = itemView.findViewById(R.id.userPlaceContainer);
            propertyType = itemView.findViewById(R.id.userPlaceType);
        }
    }
}
