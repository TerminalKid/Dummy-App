package viewmodelspackage

import app_events.appEvents
import app_states.appStates
import my_api.apiRepository
import my_api.duckRepository
import my_api.toDo
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import my_api.myFunctions
import my_api.photo
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

//@HiltViewModel
class viewmodel /*@Inject */(

    private val api: apiRepository,

    private val duckApi: duckRepository
): ViewModel() {



   private val _state = MutableStateFlow(appStates())


    private val myTodos = MutableStateFlow(emptyList<toDo>())
    private val src = MutableStateFlow<String?>("")

    val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),appStates())






      fun onEvent(event: appEvents){






        when(event){


            is appEvents.getMyTodos -> {

                viewModelScope.launch {


                    val reponse = try {

                        myTodos.value = api.getTodoList().body()!!

                    } //TRY ENDS


                    catch (exception: HttpException){

                        Log.e("IYAPA",exception.message())
                        myTodos.value = emptyList()

                    } // CATCH ENDS


                    catch (exception: IOException){

                        Log.e("IYAPA",exception.toString())
                        myTodos.value= emptyList()


                    } // CATCH2 ENDS


                } //SCOPE ENDS



                _state.update{

                    it.copy( myTodos = myTodos.value )

                      } //UPDATE ENDS




            } //GETMYTODOS ENDS







           is appEvents.getImageSRC -> {

                viewModelScope.launch {

                    try {

                        src.value = duckApi.getDuckJson().body()!!.url

                        Log.d("SRC", src.value.toString())


                    } //TRY ENDS

                    catch (exception: HttpException){

                        Log.e("IYAPA",exception.message())


                    } // CATCH ENDS

                    catch (exception: Exception){

                        Log.e("IYAPA",exception.toString())


                    } // CATCH2 ENDS


                } //SCOPE ENDS


                _state.update{

                    it.copy( url = src.value )

                } //UPDATE ENDS


            } // SHOW IMAGE ENDS


            is appEvents.getImage -> {
                TODO() //WHEN CONDITION ENDS
            }


            is appEvents.getPhotos -> {








                _state.update {

                    it.copy(

                        photos = event.photos

                    )

                } // STATE ENDS





            } // GET MY PHOTOS ENDS



            is appEvents.removeImage -> {

                val thesePhotos = _state.value.photos.toMutableList()

                thesePhotos.removeAt(event.index)


                _state.update {

                    it.copy(
                        photos = thesePhotos.toList()
                    )
                } // STATE ENDS



            }  // UPDATE ENDS

        } // WHEN ENDS


      } // ONEVENT ENDS






} // CLASS ENDS
