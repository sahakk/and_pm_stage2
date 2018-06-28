package sk.edu.pm_stage2.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import sk.edu.pm_stage2.R;
import sk.edu.pm_stage2.storage.model.MovieModel;
import sk.edu.pm_stage2.ui.DetailsActivity;

public class MovieImageAdapter extends RecyclerView.Adapter<MovieImageAdapter.ViewHolder> {
  private static final String TAG = MovieImageAdapter.class.getName();
  private final MovieModel[] movies;
  private final Context mContext;
  private final int imageHeight;
  private final int imageWidth;
  private final String parcelKey;

  public MovieImageAdapter(Context context, MovieModel[] array) {
    movies = array;
    mContext = context;
    imageHeight = mContext.getResources().getInteger(R.integer.W_185_HEIGHT);
    imageWidth = mContext.getResources().getInteger(R.integer.W_185_WIDTH);
    parcelKey = mContext.getResources().getString(R.string.key_single_movie_parcel);
  }

  @NonNull
  @Override
  public MovieImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
    ViewHolder holder = new ViewHolder(v);
    return holder;
  }

  @Override
  public void onBindViewHolder(@NonNull MovieImageAdapter.ViewHolder holder, final int position) {
    try {
      if (movies == null || movies.length == 0) {
        return;
      }
      final MovieModel movie = movies[position];
      holder.movieTitle.setText(movie.getTitle());
      Picasso.
          with(mContext).
          load(movies[position].getImageFullPath()).
          resize(imageWidth, imageHeight).
          error(R.drawable.not_found).
          placeholder(R.drawable.searching).
          into(holder.moviePoster);
      holder.movieContainer.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent intent = new Intent(mContext, DetailsActivity.class);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          intent.putExtra(parcelKey, movie);
          mContext.startActivity(intent);
        }
      });
    } catch(IndexOutOfBoundsException e) {
      Log.e(TAG, e.getMessage());
    }
  }

  @Override
  public int getItemCount() {
    return movies == null ? 0 : movies.length;
  }

  public MovieModel getMovieAtPosition(final int position) {
    if (movies == null || movies.length == 0 || position < 0 || position >= movies.length) {
      return null;
    }
    return movies[position];
  }

  public MovieModel[] getAllAdapterMovies() {
    return movies;
  }

  protected static class ViewHolder extends RecyclerView.ViewHolder {
    private final TextView movieTitle;
    private final ImageView moviePoster;
    private final CardView movieContainer;

    public ViewHolder(View itemView) {
      super(itemView);
      movieTitle = itemView.findViewById(R.id.text_view_video_title);
      moviePoster = itemView.findViewById(R.id.image_view_poster);
      movieContainer = itemView.findViewById(R.id.video_poster_container);
    }
  }
}