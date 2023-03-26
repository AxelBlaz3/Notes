package com.codex.common.decoder.di

import com.codex.common.decoder.StringDecoder
import com.codex.common.decoder.UriDecoder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class StringDecoderModule {

    @Binds
    abstract fun bindStringDecoder(
        uriDecoder: UriDecoder
    ): StringDecoder
}