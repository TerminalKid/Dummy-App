package functions

import android.app.RecoverableSecurityException
import android.content.ContentQueryMap
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import app_events.appEvents
import app_states.appStates
import my_api.photo
import android.content.Context
import android.content.Context.COMPANION_DEVICE_SERVICE
import android.content.Context.MODE_PRIVATE
import android.content.IntentSender
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.util.TypedValue
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import coil.ImageLoader
import coil.request.ImageRequest
import dagger.hilt.android.qualifiers.ApplicationContext


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import java.lang.reflect.TypeVariable
import java.util.UUID


class handlePhotos constructor(state: appStates, onEvent: (appEvents) -> Unit, context: Context ) {

    val state by lazy {  state }
    val onEvent by lazy { onEvent }
    val context by lazy { context }
    lateinit var permissionLauncher: ActivityResultLauncher < Array<String> >
    lateinit var observer: ContentObserver
    lateinit var intentSenderLauncher: ActivityResultLauncher <IntentSenderRequest>
    lateinit var takeThis: () -> Unit


    fun savePhotoInternally(name: String, bmp: Bitmap)

     = try {

        context.openFileOutput("${name}.jpg", MODE_PRIVATE).use { stream ->

            if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream))
                throw IOException("Failed to Save")



        } //OPEN ENDS

        true

       }  //TRY ENDS

       catch (error: IOException) {

        Log.d("SAVING FAILED", error.message.toString())

        false


       } // CATCH ENDS








    fun deletePhoto(photo: photo)

        = try {


            if (photo.uri == Uri.EMPTY)
                 context.deleteFile(photo.name)


            else{



                try {

                    context.contentResolver.delete(photo.uri,null,null)

                } // SECOND TRY ENDS


                catch (error: SecurityException){

                   val intentSender =

                        when{

                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {

                                MediaStore.createDeleteRequest(context.contentResolver, listOf(photo.uri))

                            }

                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {

                                val exception = error as? RecoverableSecurityException
                                exception?.userAction?.actionIntent?.intentSender

                            }


                            else -> null

                            } // WHEN ENDS

                    intentSender?.let { sender ->


                        context.startIntentSender(sender as IntentSender,null,0,0,0,null)


                    }




                } // 1ST CATCH ENDS







           } // ELSE ENDS


        } //TRY ENDS

        catch (error: IOException) {

            Log.d("DELETING FAILED", error.message.toString())

        } // CATCH ENDS












    suspend fun loadPhotos(): List<photo>? {

        val photos = mutableListOf<photo>()


        return withContext(Dispatchers.IO) {


            val files = context.filesDir.listFiles()


            files?.filter { file ->

                file.canRead() && file.isFile && file.name.endsWith(".jpg")

            }?.map { file ->

                photos.add(photo(file = file, name = file.name))


            } ?: emptyList()


            val collection = sdk29AndUp {

                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI


            val projection = arrayOf(

                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT
            )


           // val contentResolver = context.contentResolver


            context.contentResolver.query(

                collection,
                projection,
                null,
                null,
                "${MediaStore.Images.Media.DISPLAY_NAME} ASC"

            )?.use { cursor ->

                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)



                while (cursor.moveToNext()) {

                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val width = cursor.getInt(widthColumn)
                    val height = cursor.getInt(heightColumn)
                    val contentUri = ContentUris.withAppendedId(

                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id,
                    ) // URI ENDS



                    /* var bitmap: Bitmap? = null

                   if(Build.VERSION.SDK_INT < 28)
                       bitmap = MediaStore.Images.Media.getBitmap(contentResolver,contentUri)

                   else{
                       val source = ImageDecoder.createSource(contentResolver,contentUri)
                       bitmap = ImageDecoder.decodeBitmap(source)

                   }


                 if(bitmap != null)
                    photos.add(photo(name = name))

   */

                    photos.add(

                        photo(
                            id = id,
                            name = name,
                            width = width,
                            height = height,

                            uri = contentUri
                        )

                    ) // PHOTOS ENDS


                } // WHILE ENDS


                photos.toList()


            } // USE


        } // WITH CONTEXT ENDS

    } // LOAD PHOTOS ENDS


        suspend fun getMyPhotos() {


            onEvent(appEvents.getPhotos(loadPhotos() ?: emptyList()))

        }  //GET MY PHOTOS ENDS


        fun downloadPhoto(src: String) {

            try {

                val loader = ImageLoader(context)

                val request = ImageRequest
                    .Builder(context)
                    .data(src)
                    .target { result ->

                        val bitmap = (result as BitmapDrawable).bitmap

                        savePhotoInternally(UUID.randomUUID().toString(), bitmap)

                    } // TARGET ENDS
                    .build()

                loader.enqueue(request)

            } // TRY ENDS

            catch (error: Exception) {

                Log.d("NETWORK ERROR", error.message.toString())

            }   // CATCH ENDS


        } // DOWNLOAD PHOTO ENDS


        fun updateOrRequestPermissions() {


            val hasReadPermission = ContextCompat.checkSelfPermission(

                context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE

            ) == PackageManager.PERMISSION_GRANTED


            val hasWritePermission = ContextCompat.checkSelfPermission(

                context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE

            ) == PackageManager.PERMISSION_GRANTED

            val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q


            state.readPermissionGranted = hasReadPermission
            state.writePermissionGranted = hasWritePermission || minSdk29


            val requestedPermission = mutableListOf<String>()


            if (!state.writePermissionGranted)
                requestedPermission.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (!state.readPermissionGranted)
                requestedPermission.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)


            if (requestedPermission.isNotEmpty())
                this.permissionLauncher.launch(requestedPermission.toTypedArray())


        } //UPDATE ENDS


        fun savePhotoExternally(name: String, bmp: Bitmap): Boolean {

            val imageCollection = sdk29AndUp {

                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI


            val contentValues = ContentValues().apply {

                put(MediaStore.Images.Media.DISPLAY_NAME, "$name.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.WIDTH, bmp.width)
                put(MediaStore.Images.Media.HEIGHT, bmp.height)

            } // CONTENT VALUES ENDS


            return try {

                context.contentResolver.insert(imageCollection, contentValues)?.also { uri ->

                    context.contentResolver.openOutputStream(uri).use { stream ->

                        if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream))
                            throw IOException("FAILED TO SAVE EXTERNALLY")


                    } // USE ENDS


                } ?: throw IOException("FAILED TO SAVE EXTERNALLY")

                true

            } // TRY ENDS

            catch (error: IOException) {

                Log.d("HERE", error.message.toString())
                false

            } // CATCH ENDS


        } // SAVE PHOTO EXTERNALLY ENDS


        @Composable

        fun launchPermission() {

            this.permissionLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

                    state.readPermissionGranted =
                        permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE]
                            ?: state.readPermissionGranted
                    state.writePermissionGranted =
                        permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
                            ?: state.writePermissionGranted


                } // RESULT ENDS


        } // PERMISSION ENDS


        /* suspend fun loadPhotosExternally(): List<photo> = withContext(Dispatchers.IO) {

            val collection = sdk29AndUp {

                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI


            val projection = arrayOf(

                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT
            )


            val photos = mutableListOf<photo>()
            val contentResolver = context.contentResolver


            context.contentResolver.query(

                collection,
                projection,
                null,
                null,
                "${MediaStore.Images.Media.DISPLAY_NAME} ASC"

            )?.use { cursor ->

                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)

                while (cursor.moveToNext()) {

                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val width = cursor.getInt(widthColumn)
                    val height = cursor.getInt(heightColumn)
                    val contentUri = ContentUris.withAppendedId(

                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id,
                    ) // URI ENDS


                    /* var bitmap: Bitmap? = null

                if(Build.VERSION.SDK_INT < 28)
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver,contentUri)

                else{
                    val source = ImageDecoder.createSource(contentResolver,contentUri)
                    bitmap = ImageDecoder.decodeBitmap(source)

                }


              if(bitmap != null)
                 photos.add(photo(name = name))

*/

                    photos.add(

                        photo(
                            id = id,
                            name = name,
                            width = width,
                            height = height,

                            uri = contentUri
                        )

                    )


                }

                photos.toList()


            } ?: emptyList()


        }  // WITH CONTEXT ENDS


        fun initObserver() {

            this.observer = object : ContentObserver(null) {

                override fun onChange(selfChange: Boolean) {

                    if (state.readPermissionGranted)
                        TODO()


                } // ON CHANGE ENDS

            } // OBSERVER ENDS






            context.contentResolver.registerContentObserver(

                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                observer
            )

        } // INIT ENDS



        */






    }


