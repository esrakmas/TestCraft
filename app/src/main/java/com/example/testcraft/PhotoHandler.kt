package com.example.testcraft

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PhotoHandler(private val activity: Activity) {

    companion object {
        const val REQUEST_CODE_GALLERY = 1
        const val REQUEST_CODE_CAMERA = 2
        const val REQUEST_CAMERA_PERMISSION = 3
        const val REQUEST_GALLERY_PERMISSION = 4
    }

    private var photoPreview: ImageView? = null

    // Fotoğraf önizlemesi ImageView'e ayarlanıyor
    fun setPhotoPreview(imageView: ImageView) {
        photoPreview = imageView
    }

    // Galeriyi açma
    fun openGallery() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activity.startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY)
        } else {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_GALLERY_PERMISSION
            )
        }
    }

    // Kamerayı açma
    fun openCamera() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            activity.startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA)
        } else {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }
    }

    // onActivityResult ile sonuçları yönetme
    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_GALLERY -> {
                    val imageUri = data?.data
                    photoPreview?.setImageURI(imageUri)
                    photoPreview?.tag = imageUri.toString() // Fotoğraf URL'sini sakla
                }
                REQUEST_CODE_CAMERA -> {
                    val photo = data?.extras?.get("data") as? android.graphics.Bitmap
                    photoPreview?.setImageBitmap(photo)
                    // Fotoğraf URL'si burada kameradan çekildiği için doğrudan URI yoktur
                    photoPreview?.tag = "camera_photo"
                }
            }
        }
    }
}
