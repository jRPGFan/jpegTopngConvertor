package com.example.jpegtopngconvertor.presenters

import android.net.Uri
import com.example.jpegtopngconvertor.tools.ImageConvertor
import com.example.jpegtopngconvertor.views.IConvertorView
import com.github.terrakok.cicerone.Router
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.kotlin.addTo
import moxy.MvpPresenter

class ConvertorPresenter(private val imageConvertor: ImageConvertor, private val router: Router) :
    MvpPresenter<IConvertorView>() {

    private var selectedImageUri: Uri? = null
    private val disposable = CompositeDisposable()

    fun init() {
        viewState.init()
    }

    fun loadImage() {
        viewState.getImageFromGallery()
    }

    fun onImageSelected(selectedUri: Uri) {
        selectedImageUri = selectedUri
        viewState.showImage(selectedImageUri)
        viewState.conversionEnabled(true)
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    fun cancel() {
        viewState.showImage(null)
        viewState.conversionEnabled(false)
        viewState.showResult(null)
        viewState.showProgress(false)
        selectedImageUri = null
        disposable.clear()
    }

    fun convert() {
        viewState.showProgress(true)

        selectedImageUri?.let { uri ->
            imageConvertor.convertJpegToPng(uri).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation()).subscribe(
                    {
                        viewState.showResult(it)
                        viewState.showProgress(false)
                    },
                    {
                        viewState.showImage(null)
                        viewState.conversionEnabled(false)
                        viewState.showResult(null)
                        viewState.showProgress(false)
                        selectedImageUri = null
                        throw(it)
                    }
                ).addTo(disposable)
        }
    }

    fun backPressed(): Boolean {
        router.exit()
        return true
    }
}