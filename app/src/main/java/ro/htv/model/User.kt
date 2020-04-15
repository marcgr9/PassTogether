package ro.htv.model

import ro.htv.utils.Utils

data class User (
    var uid: String = "",
    var email: String = "",
    var name: String = "",
    var birthday: String = "",
    var profileImage: String = Utils.defaultProfilePicture,
    var karma: Int = 0,
    var commentsCount: Int = 0
)