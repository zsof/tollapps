package hu.zsof.tollapps.network.response

data class Response(
    var errorMessage: String = "",
    var successMessage: String = "",
    var success: Boolean = false,
)
