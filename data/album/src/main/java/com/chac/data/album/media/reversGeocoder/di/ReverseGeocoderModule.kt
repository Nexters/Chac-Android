package com.chac.data.album.media.reversGeocoder.di

import com.chac.data.album.media.reversGeocoder.AndroidReverseGeocoder
import com.chac.data.album.media.reversGeocoder.ReverseGeocoder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface ReverseGeocoderModule {
    @Binds
    @Singleton
    fun bindReverseGeocoder(reverseGeocoder: AndroidReverseGeocoder): ReverseGeocoder
}
