package sk.edu.pm_stage2.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import sk.edu.pm_stage2.R;

public class TrailerViewHolder extends RecyclerView.ViewHolder {
  public TextView trailerKey;
  public ImageButton playButton;
  public ImageButton shareButton;

  public TrailerViewHolder(View itemView) {
    super(itemView);
    trailerKey = itemView.findViewById(R.id.txt_trailer_key);
    playButton = itemView.findViewById(R.id.btn_play);
    shareButton = itemView.findViewById(R.id.btn_share);
  }
}
