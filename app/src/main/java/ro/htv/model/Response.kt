package ro.htv.model

import ro.htv.utils.Utils

data class Response(
        var status: Utils.Responses,
        var value: Any?
) {
    fun ok() = status == Utils.Responses.OK
}