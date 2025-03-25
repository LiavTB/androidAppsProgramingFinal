//package com.example.myapplication.dal.repositories
//
//import android.content.Context
//import com.example.myapplication.dal.room.AppDatabase
//import com.example.myapplication.dal.services.MoviesApiService
//import com.example.myapplication.models.Movie
//
//class MovieRepository(private val context: Context) {
//    private val apiService: MoviesApiService = MoviesApiService.create()
//    private val localDb = AppDatabase.getDatabase(context)
//
//    suspend fun discoverMovies(page: Int = 1): List<Movie> {
//        val movies = apiService.discoverMovies(page = page).toMovies()
//        localDb.movieDao().insertAll(*movies.toTypedArray())
//        return movies
//    }
//
//    suspend fun getMovieById(id: Int): Movie {
//        var movie = localDb.movieDao().getMovieById(id)
//
//        if (movie != null) return movie
//
//        movie = apiService.getMovieById(id)
//        localDb.movieDao().insertAll(movie)
//
//        return movie;
//    }
//}