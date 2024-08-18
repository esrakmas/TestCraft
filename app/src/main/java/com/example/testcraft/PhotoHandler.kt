package com.example.testcraft

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import android.widget.ImageView


class PhotoHandler(private val activity: Activity) {

    private lateinit var photoPreview: ImageView

    // ImageView'yi fotoğraf önizlemesi için ayarlayın
    fun setPhotoPreview(photoPreview: ImageView) {
        this.photoPreview = photoPreview
    }

    // Galeriyi açarak fotoğraf seçme işlemini başlatın
    fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY)
    }


    companion object {
        private const val REQUEST_CODE_GALLERY = 1
        private const val REQUEST_CODE_CAMERA = 2
    }
}
