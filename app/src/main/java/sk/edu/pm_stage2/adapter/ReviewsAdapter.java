package sk.edu.pm_stage2.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sk.edu.pm_stage2.R;
import sk.edu.pm_stage2.storage.model.ReviewModel;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewViewHolder> {
  private static final String TAG = ReviewsAdapter.class.getName();
  private final ReviewModel[] reviews;

  public ReviewsAdapter(ReviewModel[] reviews) {
    this.reviews = reviews;
  }

  @NonNull
  @Override
  public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item, parent, false);
    ReviewViewHolder holder = new ReviewViewHolder(v);
    return holder;
  }

  @Override
  public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
    try {
      final ReviewModel model = reviews[position];
      holder.author.setText(model.getAuthor());
      holder.reviewText.setText(model.getReviewText());
    } catch (IndexOutOfBoundsException e) {
      Log.e(TAG, e.getMessage());
    }
  }

  @Override
  public int getItemCount() {
    return reviews == null ? 0 : reviews.length;
  }

  public ReviewModel getReviewAtPosition(final int position) {
    if (reviews == null || reviews.length <= position) {
      return null;
    }
    return reviews[position];
  }
}
