package ro.htv.model

import ro.htv.utils.Utils

data class Post(
        var idpost: String = "",
        var ownwer_uid: String = "",
        var owner_name: String = "",
        var owner_profilePicture: String = Utils.defaultProfilePicture,
        var topic: String = "",
        var text: String = "",
        var timestamp: String = "",
        var linkToImage: String = "",
        var post: Boolean = true,
        var parent: String = ""
)