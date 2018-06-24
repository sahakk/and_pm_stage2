package sk.edu.pm_stage2.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import sk.edu.pm_stage2.R;

public class ReviewViewHolder extends RecyclerView.ViewHolder {
  public TextView author;
  public TextView reviewText;

  public ReviewViewHolder(View itemView) {
    super(itemView);
    author = itemView.findViewById(R.id.txt_review_author);
    reviewText = itemView.findViewById(R.id.txt_review_text);
  }
}
