package com.example.jpegtopngconvertor.views

import android.net.Uri
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface IConvertorView : MvpView {
    fun init()
    fun getImageFromGallery()
    fun showImage(uri: Uri?)
    fun showResult(uri: Uri?)
    fun conversionEnabled(enabled: Boolean)
    fun showProgress(show: Boolean)
}