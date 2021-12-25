package com.example.swift.frontEnd.Remote

import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetroFitClient {
    val instance:Retrofit? = null
        get() = if(field == null) Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build() else field
}