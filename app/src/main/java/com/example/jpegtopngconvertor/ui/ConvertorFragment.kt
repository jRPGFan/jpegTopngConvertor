package com.example.jpegtopngconvertor.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.jpegtopngconvertor.App
import com.example.jpegtopngconvertor.databinding.FragmentConvertorBinding
import com.example.jpegtopngconvertor.presenters.ConvertorPresenter
import com.example.jpegtopngconvertor.tools.BackButtonListener
import com.example.jpegtopngconvertor.tools.ImageConvertor
import com.example.jpegtopngconvertor.views.IConvertorView
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

private const val READ_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
private const val WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
private const val IMAGE_PICKER = 111

class ConvertorFragment : MvpAppCompatFragment(), IConvertorView, BackButtonListener {

    private var _viewBinding: FragmentConvertorBinding? = null
    private val viewBinding get() = _viewBinding!!
    private val presenter: ConvertorPresenter by moxyPresenter {
        ConvertorPresenter(
            ImageConvertor(requireContext()),
            App.instance.router
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = FragmentConvertorBinding.inflate(inflater, container, false)
        presenter.init()
        return viewBinding.root
    }

    override fun init() {
        viewBinding.btnSelectImage.setOnClickListener { checkReadPermission() }
        viewBinding.btnConvert.setOnClickListener { convert() }
        viewBinding.btnCancel.setOnClickListener { cancel() }
        conversionEnabled(false)
        showProgress(false)
    }

    private fun checkReadPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), READ_PERMISSION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            val permission = arrayOf(READ_PERMISSION)

            requestPermissions(
                permission,
                100
            )
        } else {
            presenter.loadImage()
        }
    }

    override fun getImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpg"))
        startActivityForResult(intent, IMAGE_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICKER) {
            data?.data?.let { presenter.onImageSelected(it) }
        }
    }

    private fun convert() {
        checkWritePermissions()
    }

    private fun checkWritePermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), WRITE_PERMISSION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            permissionRequestWriteFile.launch(WRITE_PERMISSION)
//            val permission = arrayOf(WRITE_PERMISSION)
//
//            requestPermissions(
//                permission,
//                101
//            )
        } else {
            presenter.convert()
        }
    }

    private val permissionRequestWriteFile =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it)
                presenter.convert()
        }

    private fun cancel() {
        presenter.cancel()
        showProgress(false)
    }

    override fun showProgress(show: Boolean) {
        viewBinding.progressBar.isVisible = show
    }

    override fun showImage(uri: Uri?) {
        viewBinding.selectedImage.setImageURI(uri)
    }

    override fun showResult(uri: Uri?) {
        Glide.with(context).load(uri).into(viewBinding.convertedImage)
    }

    override fun conversionEnabled(enabled: Boolean) {
        viewBinding.btnConvert.isEnabled = enabled
        viewBinding.btnCancel.isEnabled = enabled
    }

    override fun backPressed() = presenter.backPressed()

    companion object {
        fun newInstance() = ConvertorFragment()
    }
}