package sk.edu.pm_stage2.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sk.edu.pm_stage2.R;
import sk.edu.pm_stage2.storage.model.TrailerModel;
import sk.edu.pm_stage2.utils.Helpers;

public class TrailersAdapter extends RecyclerView.Adapter<TrailerViewHolder> {
  private static final String TAG = TrailersAdapter.class.getName();
  private final TrailerModel[] trailers;
  private final Context context;

  public TrailersAdapter(TrailerModel[] trailers, Context context) {
    this.trailers = trailers;
    this.context = context;
  }

  @NonNull
  @Override
  public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_list_item, parent, false);
    TrailerViewHolder holder = new TrailerViewHolder(v);
    return holder;
  }

  @Override
  public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
    try {
      final TrailerModel model = trailers[position];
      holder.trailerKey.setText(model.getLink());
      final String youtubeLink = Helpers.YOUTUBE_BASE_URL + model.getKey();
      holder.playButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink));
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          intent.setPackage("com.google.android.youtube");
          context.startActivity(intent);
        }
      });
      holder.shareButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
          sharingIntent.setType("text/plain");
          sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, youtubeLink);
          context.startActivity(Intent.createChooser(sharingIntent, "Share..."));
        }
      });
    } catch (IndexOutOfBoundsException e) {
      Log.e(TAG, e.getMessage());
    }
  }

  @Override
  public int getItemCount() {
    return trailers == null ? 0 : trailers.length;
  }

  public TrailerModel getTrailerAtPosition(final int position) {
    if (trailers == null || trailers.length <= position) {
      return null;
    }
    return trailers[position];
  }
}
