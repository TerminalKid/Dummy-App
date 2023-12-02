package app_events

import my_api.photo
import android.graphics.Bitmap

sealed interface appEvents{

object getMyTodos: appEvents
object getImageSRC: appEvents


data class getPhotos(val photos:  List<photo> ): appEvents
data class removeImage(val index: Int): appEvents
data class getImage(val name: String, val bmp: Bitmap): appEvents


}