package com.example.jpegtopngconvertor.tools

import android.net.Uri
import io.reactivex.rxjava3.core.Single

interface IImageConvertor {
    fun convertJpegToPng(uriTarget: Uri): Single<Uri>
}