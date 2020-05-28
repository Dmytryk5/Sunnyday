package com.dmytryk.sunnyday.extensions

fun String.toIconUrl(): String{
    return ("http://openweathermap.org/img/wn/$this@2x.png")
}