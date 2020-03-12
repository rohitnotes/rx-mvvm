package com.example.android.databinding.basicsample.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import com.example.android.databinding.basicsample.data.remote.MovieAPI
import com.example.android.databinding.basicsample.data.remote.response.tvshow.poular.TvShowResponse
import com.example.android.databinding.basicsample.data.source.impl.TvShowRepositoryImpl
import com.example.android.databinding.basicsample.ui.viewmodel.viewstate.TvShowViewState
import com.example.android.databinding.basicsample.utils.RxSingleSchedulers
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Observable
import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.Charset


class TvShowViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var api: MovieAPI

    @Mock
    private lateinit var observer: Observer<TvShowViewState>

    @Mock
    private lateinit var repository: TvShowRepositoryImpl

    @Mock
    private lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var lifeCycle: Lifecycle

    private lateinit var viewModel: TvShowViewModel

    @Before
    @Throws(Exception::class)
    fun setup() {
        MockitoAnnotations.initMocks(this)
        lifeCycle = LifecycleRegistry(lifecycleOwner)
        repository = TvShowRepositoryImpl(RxSingleSchedulers.TEST_SCHEDULER)
        viewModel = TvShowViewModel(repository)
        viewModel.tvShowListState.observeForever(observer)

    }

    @Test
    fun testNull() {
        Mockito.`when`(api.getTvShows("ac313fc1138a0ed697567a0dedddc6cd")).thenReturn(null)
        assertNotNull(viewModel.getTvShow("ac313fc1138a0ed697567a0dedddc6cd"))
        Assert.assertTrue(viewModel.tvShowListState.hasObservers())
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun testTVShowAvailability() {
        val connection = URL("http://api.themoviedb.org/3/tv/popular?api_key=ac313fc1138a0ed697567a0dedddc6cd").openConnection()
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
        Mockito.`when`(api.getTvShows("ac313fc1138a0ed697567a0dedddc6cd")).thenReturn(Observable.just(TvShowResponse()))
        verify(observer).onChanged(TvShowViewState.SUCCESS_STATE)
    }


    @Test
    fun testApiFetchDataError() {
        Mockito.`when`(api.getTvShows("ac313fc1138a0ed697567a0dedddc6cd")).thenReturn(Observable.error(Throwable("Api Error")))
        viewModel.getTvShow("ac313fc1138a0ed697567a0dedddc6cd")
        verify(observer).onChanged(TvShowViewState.ERROR_STATE)
    }
}