package com.example.cameinw.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cameinw.R;
import com.example.cameinw.api.UserApi;
import com.example.cameinw.dto.UserDTO;
import com.example.cameinw.model.Review;
import com.example.cameinw.model.User;
import com.example.cameinw.util.RetrofitUtils;
import com.example.cameinw.util.UserInfoUtils;
import com.example.cameinw.util.UserUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {

    private List<Review> reviews;
    private Context context;
    private String userToken;

    public ReviewAdapter(List<Review> reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
        userToken = UserInfoUtils.getUserToken(context);
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_component, parent, false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.review.setText("\""+review.getComment()+"\"");

        fetchUserOfTheReview(review, holder);

        if (review.getRating().toString().equals("ONE_STAR")) {
            holder.rating.setText("1/5");
        } else if (review.getRating().toString().equals("TWO_STARS")) {
            holder.rating.setText("2/5");
        } else if (review.getRating().toString().equals("THREE_STARS")) {
            holder.rating.setText("3/5");
        } else if (review.getRating().toString().equals("FOUR_STARS")) {
            holder.rating.setText("4/5");
        } else if (review.getRating().toString().equals("FIVE_STARS")) {
            holder.rating.setText("5/5");
        }

    }

    private void fetchUserOfTheReview(Review review, ReviewHolder holder) {
        UserApi userApi = RetrofitUtils.createRequest(UserApi.class, userToken);
        userApi.getUserByReviewId(review.getId()).
                enqueue(new Callback<UserDTO>() {
                    @Override
                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                        if (((Integer) response.code()).equals(200)) {
                            handleSuccessfulResponse(holder, response);
                        } else {
                            handleUnsuccessfulResponse(holder, response);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserDTO> call, Throwable t) {
                        handleFailure(t);
                    }
                });
    }

    private void handleUnsuccessfulResponse(ReviewHolder holder, Response<UserDTO> response) {
        Log.d("ReviewAdapter", String.valueOf(response.code()));
        holder.user.setText("User Name");
    }

    private void handleSuccessfulResponse(ReviewHolder holder, Response<UserDTO> response) {
        User user = UserUtils.processUserDTO(response.body());
        Log.d("ReviewAdapter", user.getTheUserName());

        holder.user.setText(user.getTheUserName());
    }

    private void handleFailure(Throwable t) {
        Log.d("ReviewAdapter", "something's wrong "+t);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ReviewHolder extends RecyclerView.ViewHolder {

        public TextView rating, review, user;
        public ReviewHolder(@NonNull View itemView) {
            super(itemView);
            rating = itemView.findViewById(R.id.theRating);
            review = itemView.findViewById(R.id.personReview);
            user = itemView.findViewById(R.id.userName_review);
        }
    }
}
