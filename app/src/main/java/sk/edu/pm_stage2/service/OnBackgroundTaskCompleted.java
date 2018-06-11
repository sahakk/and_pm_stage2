package sk.edu.pm_stage2.service;

import sk.edu.pm_stage2.storage.entity.MovieEntity;

public interface OnBackgroundTaskCompleted {
  void onFetchMoviesTaskCompleted(MovieEntity[] movies);
}
