package ro.htv.model

import ro.htv.utils.Utils

// sa ma pis pe el java

data class PostsResponse(
        var status: Utils.Responses,
        var posts: ArrayList<Post>?
)