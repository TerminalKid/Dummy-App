@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class
)

package Views

import app_events.appEvents
import app_states.appStates
import functions.handlePhotos
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.widget.GridView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.ui.theme.MyApplicationTheme
import functions.sdk29AndUp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import my_api.photo
import java.io.File
import java.util.UUID


lateinit var bitmap: Bitmap


@Composable
fun galleryScreen(state: appStates, onEvent: (appEvents) -> Unit, context: Context){
    val handlePhotos = remember { handlePhotos(state,onEvent,context) }
    val start = remember { mutableStateOf(true) }





        SideEffect {

            handlePhotos.updateOrRequestPermissions()


        }
        handlePhotos.launchPermission()



    LaunchedEffect(key1 = 1, block = {


        handlePhotos.getMyPhotos()
    }
    )














    var show by remember { mutableStateOf(false) }
    var photosList = listOf<photo>()
    val snackHostState = remember { SnackbarHostState() }

    val takePhoto = rememberLauncherForActivityResult(

        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = {bmp ->

            if(bmp != null)
                handlePhotos.savePhotoExternally(UUID.randomUUID().toString() ,bmp)


              getMyPics(handlePhotos)
        }

    )

    Scaffold(

        snackbarHost = {

                       SnackbarHost(hostState = snackHostState )
        }, //END OF SNACK BAR

        floatingActionButton = {

            FloatingActionButton(
               
                
                        onClick = {

                            onEvent(appEvents.getImageSRC)

                            handlePhotos.downloadPhoto(state.url.toString())

                            getMyPics(handlePhotos)


                        } // ON CLICK ENDS

            ){

                Text(text = "Go")


            }  // REAL BUTTON ENDS

        } , // floating Action Button
        
        bottomBar = {
            
            Button(onClick = {  takePhoto.launch() },) {

                Text(text = "Take Pic")
            }
        }
        


    ) {



        LazyVerticalGrid(
                                  columns = GridCells.Fixed(3),
                                //  horizontalArrangement = Arrangement.spacedBy(4.dp),

                                  state = rememberLazyGridState(),
                                  content = {




                                       items( state.photos, key = {photo->  state.photos.indexOf(photo) } ) { photo ->
                                           val index  = state.photos.indexOf(photo)


//
//                                           CoroutineScope(Dispatchers.Default).launch{
//
//                                               state.photoBmp = sdk29AndUp {
//
//                                               context.contentResolver.loadThumbnail(photo.uri,Size(photo.width,photo.height),null)
//                                           } ?: MediaStore.Images.Thumbnails.getThumbnail(context.contentResolver,photo.id,MediaStore.Images.Thumbnails.MINI_KIND,null)
//                                           }


                                           if (photo.uri != Uri.EMPTY)
                                           AsyncImage(
                                               contentScale = ContentScale.Crop,
                                               model = photo.uri,
                                               contentDescription = null,
                                               modifier = Modifier
                                                   .combinedClickable(

                                                       onLongClick = {


                                                           handlePhotos.deletePhoto(photo)


                                                           onEvent(appEvents.removeImage(index))


                                                       }, onClick = {}

                                                   )
                                                   .aspectRatio(ratio = 16f / 16f)//Modifier ends


                                           ) // IMAGE ENDS


                                           AsyncImage(

                                               contentScale = ContentScale.Crop,
                                               model = photo.file,
                                               contentDescription = null,
                                               modifier = Modifier
                                                   .combinedClickable(

                                                       onLongClick = {


                                                           handlePhotos.deletePhoto(photo)


                                                           onEvent(appEvents.removeImage(index))


                                                       }, onClick = {}

                                                   )
                                                   .aspectRatio(ratio = 16f / 16f)//Modifier ends


                                           ) // IMAGE ENDS

                                       } // ITEMS END


                                  }, // CONTENT ENDS

                                  contentPadding = it

                ) //StaggeredGrid Ends






    } // SCAFFOLD ENDS


}



 fun <T> decodeToBmp(file: T, context: Context ): Bitmap {

        var bytes: ByteArray

        when (file) {


            is File -> {
                bytes = file.readBytes()
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            } // FILE ENDS


            is Uri -> {

                if (Build.VERSION.SDK_INT < 28)
                    bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, file) ?: Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888)
                else {
                    val source = ImageDecoder.createSource(context.contentResolver, file)
                    bitmap = ImageDecoder.decodeBitmap(source)

                } // URI ENDS


            } // URI ENDS

        } // WHEN ENDS



    return bitmap

} // DECODE ENDS


fun getMyPics(handlePhotos: handlePhotos){



    CoroutineScope(Dispatchers.IO).launch {

        handlePhotos.getMyPhotos()


    } // SCOPE ENDS
}




    @ExperimentalFoundationApi
    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        MyApplicationTheme {



        }


    }

