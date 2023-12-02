package my_api

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.createBitmap
import java.io.File

data class photo(

    val bitmap: Bitmap = createBitmap(1,1, Bitmap.Config.ARGB_8888),
    val name: String? = "",
    val uri: Uri = Uri.EMPTY,
    val id: Long = 0L,
    val width: Int = 0,
    val height: Int = 0,
    val file: File = File("")

)
