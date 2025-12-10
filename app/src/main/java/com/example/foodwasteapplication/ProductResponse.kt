package com.example.foodwasteapplication

data class ProductResponse(
    val product: Product?
)

data class Product(
    val product_name: String?,
    val image_url: String?
)