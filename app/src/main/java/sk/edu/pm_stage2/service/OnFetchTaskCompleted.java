package sk.edu.pm_stage2.service;

import sk.edu.pm_stage2.storage.model.MovieModel;
import sk.edu.pm_stage2.storage.model.ReviewModel;

public interface OnFetchTaskCompleted<T> {
  void onFetchTaskCompleted(T[] objects);
}
