#PROJECT SPECIFICATION
##Popular Movies, Stage 2
###Common Project Requirements
1. App is written solely in the Java Programming Language.
2. App conforms to common standards found in the Android Nanodegree General Project Guidelines.
3. App utilizes stable release versions of all libraries, Gradle, and Android Studio.
###User Interface - Layout
1. UI contains an element (e.g., a spinner or settings menu) to toggle the sort order of the movies by: most popular, highest rated.
2. Movies are displayed in the main layout via a grid of their corresponding movie poster thumbnails.
3. UI contains a screen for displaying the details for a selected movie.
4. Movie Details layout contains title, release date, movie poster, vote average, and plot synopsis.
5. Movie Details layout contains a section for displaying trailer videos and user reviews.
###User Interface - Function
1. When a user changes the sort criteria (most popular, highest rated, and favorites) the main view gets updated correctly.
2. When a movie poster thumbnail is selected, the movie details screen is launched.
3. When a trailer is selected, app uses an Intent to launch the trailer.
4. In the movies detail screen, a user can tap a button (for example, a star) to mark it as a Favorite. Tap the button on a favorite movie will unfavorite it.
###Network API Implementation
1. In a background thread, app queries the `/movie/popular` or `/movie/top_rated` API for the sort criteria specified in the settings menu.
2. App requests for related videos for a selected movie via the `/movie/{id}/videos` endpoint in a background thread and displays those details when the user selects a movie.
3. App requests for user reviews for a selected movie via the `/movie/{id}/reviews` endpoint in a background thread and displays those details when the user selects a movie.
###Data Persistence
1. The titles and IDs of the user's favorite movies are stored in a native SQLite database and exposed via a ContentProvider
OR stored using Room.
2. Data is updated whenever the user favorites or unfavorites a movie. No other persistence libraries are used.
3. When the "favorites" setting option is selected, the main view displays the entire favorites collection based on movie ids stored in the database.
###Android Architecture Components
1. If Room is used, database is not re-queried unnecessarily. LiveData is used to observe changes in the database and update the UI accordingly.
2. If Room is used, database is not re-queried unnecessarily after rotation. Cached LiveData from ViewModel is used instead.
#####Suggestions to Make Your Project Stand Out!
1. Extend the favorites database to store the movie poster, synopsis, user rating, and release date, and display them even when offline.
2. Implement sharing functionality to allow the user to share the first trailer’s YouTube URL from the movie details screen.

##Youtube link for the app functionality display

https://youtu.be/kWox-bNHVOU

###___________________________________
#Popular Movies, Stage 1 - completed.
##Common Project Requirements
###MEETS SPECIFICATIONS
1. App is written solely in the Java Programming Language.
2. Movies are displayed in the main layout via a grid of their corresponding movie poster thumbnails.
3. UI contains an element (i.e a spinner or settings menu) to toggle the sort order of the movies by: most popular, highest rated.
4. UI contains a screen for displaying the details for a selected movie.
5. Movie details layout contains title, release date, movie poster, vote average, and plot synopsis.
6. App utilizes stable release versions of all libraries, Gradle, and Android Studio.
##User Interface - Function
###MEETS SPECIFICATIONS
1. When a user changes the sort criteria (“most popular and highest rated”) the main view gets updated correctly.
2. When a movie poster thumbnail is selected, the movie details screen is launched.
##Network API Implementation
###MEETS SPECIFICATIONS
In a background thread, app queries the `/movie/popular` or `/movie/top_rated` API for the sort criteria specified in the settings menu.
##General Project Guidelines
###MEETS SPECIFICATIONS
App conforms to common standards found in the Android Nanodegree General Project Guidelines (NOTE: For Stage 1 of the Popular Movies App, it is okay if the app does not restore the data using onSaveInstanceState/onRestoreInstanceState)