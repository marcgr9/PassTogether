package ro.htv.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    const val defaultProfilePicture = "https://firebasestorage.googleapis.com/v0/b/hackthevirus-e1fed.appspot.com/o/profile_pics%2FdefaultImage.png?alt=media&token=3dbf7708-1b86-4228-803d-02d5ff09fd46"

    enum class Responses {
        OK, ERROR
    }

    enum class Errors {
        EMPTY
    }
    const val PICK_IMAGE_RC = 13

    fun convertFromUnix(unix: String): String = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Date(unix.toLong() * 1000))

    fun yearsSince(unix: String): String {
        val date = unix
        val now = SimpleDateFormat("dd/MM/yyyy").format(Date().time).toString()

        val dateSplit = date.split("/")
        val nowSplit = now.split("/")

        var years = nowSplit[2].toInt() - dateSplit[2].toInt()
        if (nowSplit[1].toInt() < dateSplit[1].toInt()) {
            years--;
        } else if (nowSplit[1].toInt() == dateSplit[1].toInt() && nowSplit[0].toInt() > dateSplit[0].toInt()) years--;


        return years.toString()
    }
}
