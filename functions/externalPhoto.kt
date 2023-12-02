package functions

import android.net.Uri

data class externalPhoto(

    val id: Long = 0L,
    val name: String = "",
    val width: Int = 0,
    val height: Int = 0,
    val contentUri: Uri = Uri.EMPTY


)
