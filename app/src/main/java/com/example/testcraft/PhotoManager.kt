import android.app.Activity
import android.net.Uri
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class PhotoManager(private val activity: Activity) {

    private val storageReference = FirebaseStorage.getInstance().reference
    private val db = FirebaseFirestore.getInstance()

    fun showPhotoPreview(uri: Uri, imageView: ImageView) {
        imageView.setImageURI(uri)
    }

    fun savePhotoToFirebase(photoUri: Uri, ratingBar: RatingBar, titleSpinner: Spinner) {
        val fileName = UUID.randomUUID().toString()
        val photoRef = storageReference.child("photos/$fileName")

        photoRef.putFile(photoUri)
            .addOnSuccessListener {
                photoRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val data = hashMapOf(
                        "rating" to ratingBar.rating,
                        "title" to titleSpinner.selectedItem.toString(),
                        "imageUrl" to downloadUri.toString()
                    )
                    db.collection("questions").add(data)
                        .addOnSuccessListener {
                            Toast.makeText(activity, "Photo uploaded successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(activity, "Error uploading photo", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Error uploading photo", Toast.LENGTH_SHORT).show()
            }
    }
}
