package ro.htv.utils

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import ro.htv.model.Response
import java.io.ByteArrayOutputStream

class StorageRepository {

    private val TAG = "HackTheVirus Storage"

    private val rootRef = FirebaseStorage.getInstance().reference
    private val storageRef = rootRef.child("profile_pics")

    fun uploadImage(img: Bitmap, uid: String): MutableLiveData<Response> {
        val response = MutableLiveData<Response>()

        val baos = ByteArrayOutputStream()
        val userRef = storageRef.child(uid)
        img.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        userRef.putBytes(image)
                .addOnSuccessListener {
                    userRef.downloadUrl
                            .addOnSuccessListener { uri ->
                                response.value = Response(
                                        Utils.Responses.OK,
                                        uri.toString()
                                )
                            }.addOnFailureListener {
                                // imi bag pula de esueaza aici
                            }

                }.addOnFailureListener {
                    response.value = Response(
                            Utils.Responses.ERROR,
                            it.message
                    )
                }

        return response
    }
}