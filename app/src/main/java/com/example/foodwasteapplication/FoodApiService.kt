package com.example.foodwasteapplication

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface FoodApiService {
    @GET("api/v0/product/{barcode}.json")
    fun getProduct(@Path("barcode") barcode: String): Call<ProductResponse>
}