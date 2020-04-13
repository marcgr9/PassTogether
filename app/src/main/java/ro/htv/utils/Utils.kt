package ro.htv.utils

object Utils {
    val defaultProfilePicture = "https://firebasestorage.googleapis.com/v0/b/hackthevirus-e1fed.appspot.com/o/profile_pics%2FdefaultImage.png?alt=media&token=3dbf7708-1b86-4228-803d-02d5ff09fd46"

    enum class Responses {
        OK, ERROR
    }

    enum class Errors {
        EMPTY
    }
    val PICK_IMAGE_RC = 13
}