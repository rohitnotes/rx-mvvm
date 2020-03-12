package com.example.android.databinding.basicsample.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import com.example.android.databinding.basicsample.data.remote.MovieAPI
import com.example.android.databinding.basicsample.data.remote.response.movie.nowplaying.MovieResponse
import com.example.android.databinding.basicsample.data.source.impl.MovieRepositoryImpl
import com.example.android.databinding.basicsample.ui.viewmodel.viewstate.MovieViewState
import com.example.android.databinding.basicsample.utils.RxSingleSchedulers
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Observable
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.Charset

@RunWith(JUnit4::class)
class MovieViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var api: MovieAPI

    @Mock
    private lateinit var observer: Observer<MovieViewState>

    @Mock
    private lateinit var repository: MovieRepositoryImpl

    @Mock
    private lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var lifeCycle: Lifecycle

    private lateinit var viewModel: MovieViewModel

    @Before
    @Throws(Exception::class)
    fun setup() {
        MockitoAnnotations.initMocks(this)
        lifeCycle = LifecycleRegistry(lifecycleOwner)
        repository = MovieRepositoryImpl(RxSingleSchedulers.TEST_SCHEDULER)
        viewModel = MovieViewModel(repository)
        viewModel.movieListState.observeForever(observer)

    }

    @Test
    fun testNull() {
        `when`(api.getMovies("ac313fc1138a0ed697567a0dedddc6cd")).thenReturn(null)
        assertNotNull(viewModel.getMovies("ac313fc1138a0ed697567a0dedddc6cd"))
        assertTrue(viewModel.movieListState.hasObservers())
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun testMovieAvailability() {
        val connection = URL("http://api.themoviedb.org/3/movie/now_playing?api_key=ac313fc1138a0ed697567a0dedddc6cd").openConnection()
        val response = connection.getInputStream()
        val buffer = StringBuffer()
        BufferedReader(InputStreamReader(response, Charset.defaultCharset())).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                buffer.append(line)
            }
        }
        assert(buffer.isNotEmpty())
    }

    @Test
    fun testApiFetchDataSuccess() {
        `when`(api.getMovies("ac313fc1138a0ed697567a0dedddc6cd")).thenReturn(Observable.just(MovieResponse()))
        viewModel.getMovies("ac313fc1138a0ed697567a0dedddc6cd")
        verify(observer).onChanged(MovieViewState.SUCCESS_STATE)
    }


    @Test
    fun testApiFetchDataError() {
        `when`(api.getMovies("")).thenReturn(Observable.error(Throwable("Api Error")))
        viewModel.getMovies("ac313fc1138a0ed697567a0dedddc6cd")
        verify(observer).onChanged(MovieViewState.LOADING_STATE)
        verify(observer).onChanged(MovieViewState.ERROR_STATE)
    }

}