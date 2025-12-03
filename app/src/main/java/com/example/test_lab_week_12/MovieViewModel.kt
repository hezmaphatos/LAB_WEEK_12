package com.example.test_lab_week_12

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test_lab_week_12.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.icu.util.Calendar

class MovieViewModel(private val movieRepository: MovieRepository)
    : ViewModel() {

    init {
        fetchPopularMovies()
    }

    private val _popularMovies = MutableStateFlow(emptyList<Movie>())
    val popularMovies: StateFlow<List<Movie>> = _popularMovies

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    private fun fetchPopularMovies() {

        val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()

        viewModelScope.launch(Dispatchers.IO) {
            movieRepository.fetchMovies()
                .catch { e ->
                    _error.value = "An exception occurred: ${e.message}"
                }
                .collect { movies ->

                    // 🔥 Filtering & Sorting
                    val processed = movies
                        .filter { movie ->
                            movie.releaseDate?.startsWith(currentYear) == true
                        }
                        .sortedByDescending { it.popularity }

                    // Update StateFlow
                    _popularMovies.value = processed
                }
        }
    }
}
