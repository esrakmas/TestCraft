import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class PhotoHandler(private val activity: Activity) {
    private val photoUri: Uri? = null

    fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK)
    }

    fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        activity.startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?, onPhotoSelected: (Uri?) -> Unit) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    val selectedImage = data?.data
                    onPhotoSelected(selectedImage)
                }
                REQUEST_IMAGE_CAPTURE -> {
                    val photoUri = data?.data
                    onPhotoSelected(photoUri)
                }
            }
        }
    }

    companion object {
        const val REQUEST_IMAGE_PICK = 1
        const val REQUEST_IMAGE_CAPTURE = 2
    }
}
