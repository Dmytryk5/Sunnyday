package com.dmytryk.sunnyday.extensions

import android.view.View


fun View.makeInvisible(){
    this.visibility = View.INVISIBLE
}

fun View.makeVisible(){
    this.visibility = View.VISIBLE
}

fun View.makeItGone(){
    this.visibility = View.GONE
}
