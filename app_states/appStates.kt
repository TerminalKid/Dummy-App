package app_states

import my_api.photo
import my_api.toDo
import android.graphics.Bitmap
import androidx.core.graphics.createBitmap
import functions.externalPhoto

data class appStates(

    val myTodos: List<toDo> = emptyList(),
    val url: String? = "",



    val photos: List<photo> = emptyList(),
    val photo: photo = photo(createBitmap(1,1,Bitmap.Config.ARGB_8888), ""),
    var photoBmp : Bitmap? = createBitmap(1,1,Bitmap.Config.ARGB_8888),



    var launchCamera: Boolean  = false,
    var readPermissionGranted: Boolean = false,
    var writePermissionGranted: Boolean = false,
    var change: Boolean = false


    )
