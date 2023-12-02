@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class
)

package com.example.myapplication

import viewmodelspackage.viewmodel
import Views.galleryScreen
import android.content.Context
import android.database.ContentObserver
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.ui.theme.MyApplicationTheme
import functions.handlePhotos
import hilt.myApp
import kotlinx.coroutines.launch


//@AndroidEntryPoint
class MainActivity : ComponentActivity() {

//    private val viewModel: viewmodel by viewModels()
    lateinit var observer: ContentObserver
    var readPermissionGranted = false
    var writePermissionGranted = false
    lateinit var handlePhotos: handlePhotos
    lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>

    private val viewModel by viewModels<viewmodel>(

        factoryProducer = {

            object : ViewModelProvider.Factory{

                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return viewmodel(
                        api = myApp.appModule.apiRepository,
                        duckApi = myApp.appModule.duckRepository
                    )   as T
                }
            }



        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        initObserver()



        setContent {



            MyApplicationTheme {
                // A surface container using the 'background' color from the theme


                val state by viewModel.state.collectAsState()

                state.change = readPermissionGranted


                Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                ) {


                     galleryScreen(

                         state = state,
                         onEvent = viewModel::onEvent,
                         context = this

                     ) // Gallery Screen



                } // SET CONTENT



            } // THEME ENDS

        } // SETCONTENT ENDS





    } // ON CREATE ENDS


    override fun onDestroy() {

        contentResolver.unregisterContentObserver(observer)




    }











    inline fun initObserver(){


        observer = object : ContentObserver(null){

            override fun onChange(selfChange: Boolean) {


                readPermissionGranted = true

                Log.d("CHANGE.HERE",readPermissionGranted.toString())




            } // ON CHANGE ENDS

        } // OBSERVER ENDS


        contentResolver.registerContentObserver(

            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            observer

        ) // REGISTRY ENDS


    } // INIT ENDS
















}


































@Composable
fun bottomBar(){

        BottomAppBar() {

            Text(text = "Bottom Your", color = Color.Black)

        }

}




@Composable
fun drawer(context: Context){


    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val  snackHost = remember { SnackbarHostState() }
    var state = false

    ModalNavigationDrawer(drawerContent = {


                                          ModalDrawerSheet() {


                                              Column(modifier = Modifier.fillMaxSize()) {

                                                  Box( modifier = Modifier
                                                      .fillMaxWidth()
                                                      .padding(vertical = 64.dp)

                                                  ){
                                                      Text(text = "Header", fontSize = 60.sp, modifier = Modifier.align(
                                                          Center))
                                                  } /*Box ENDS*/


                                                  NavigationDrawerItem(
                                                      label = { Text(text = "Inbox") },
                                                      selected = state,
                                                      onClick = {
                                                          scope.launch {

                                                              drawerState.close()
                                                              snackHost.showSnackbar("INBOX")


                                                          }



                                                                } /*onClick Ends*/,
                                                      icon = {
                                                          Icon(
                                                              imageVector = Icons.Default.Email,
                                                              contentDescription = "Inbox"
                                                          )
                                                      }

                                                  )

                                                  NavigationDrawerItem(
                                                      label = { Text(text = "Shop") },
                                                      selected = state,
                                                      onClick = {


                                                          scope.launch {
                                                              drawerState.close()

                                                              val result = snackHost.showSnackbar(
                                                                                                  message = "SHOP HAS BEEN CLICKED",
                                                                                                  actionLabel = "Start Shoppin",
                                                                                                  duration = SnackbarDuration.Long )

                                                              when(result){

                                                                  SnackbarResult.ActionPerformed -> { myToast(context = context, msg = "SHOPPING") }

                                                                      SnackbarResult.Dismissed ->{  }


                                                              }
                                                      }


                                                       }/*onClick Ends*/,
                                                      icon = {
                                                          Icon(
                                                              imageVector = Icons.Default.ShoppingCart,
                                                              contentDescription = "Inbox"
                                                          )
                                                      }

                                                  )


                                              } // COLUMN END

                                          }
    },
                          drawerState = drawerState) {

        
        
        Scaffold(

            snackbarHost = { SnackbarHost(hostState = snackHost) },
            topBar = {

                TopAppBar(
                    title = { Text(text = "DRAWER") },
                     navigationIcon = {
                         IconButton(onClick = {

                             scope.launch {  drawerState.apply { if(isClosed) open()  else close() } }
                         }

                     ) {

                             Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu" )

                         }
                     }
                )

            },


            bottomBar = {bottomBar() },


            content = { paddingValues ->  Body(paddingValues,context) }





        )

    }


}

@Composable
fun Body(padding: PaddingValues, context: Context){

    val scope2 = rememberCoroutineScope()
    val  snackHost= remember { SnackbarHostState() }




    Column(verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
              .fillMaxSize()
              .background(Color.White)
              .padding(padding)) {

       repeat(5) { Text(text = "Body Content", color = Color.Black,
        modifier = Modifier.clickable {
           myToast(context = context,"this Item")

        }  //Modifier ends
       ) /*TEXT ENDS*/

       }

    }
}


@Composable
fun dialog(){

   AlertDialog(onDismissRequest = {},

               confirmButton = {

                   TextButton(onClick = { /*TODO*/ }) {

                       Text(text = "SAVE")

                   }// TEXT ENDS

               } /*CONFIRM btn ENDS*/,

               dismissButton = {

                   TextButton(onClick = { /*TODO*/ }) {

                       Text(text = "BACK")

                   }// TEXT ENDS

               } /* BACK ENDS*/,


               text = {

                   Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {




                       
                   } // COLUMN ENDS
               } // TEXT ENDS






   )

}





fun myToast(context: Context,msg: String){



    Toast.makeText(context,msg,Toast.LENGTH_LONG).show()



}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {


       Column(modifier = modifier
           .fillMaxSize()
           .background(color = Color.Gray) ) {


           Box(modifier = modifier
               .clip(RoundedCornerShape(bottomStart = 7.dp, bottomEnd =  7.dp))){
           Row(
               modifier = modifier
                   .background(color = Color.DarkGray)
                   .padding(start = Dp(2f))
                   .padding(top = Dp(5f))
                   .padding(bottom = Dp(5f))
                   .fillMaxWidth()
           ) {


               Image(
                   painter = painterResource(id = R.drawable.back), contentDescription = "back",
                   modifier = modifier
                       .padding(top = Dp(8f))
                       .padding(end = Dp(5f))
                       .size(40.dp)
               )

               Image(
                   painter = painterResource(id = R.drawable.eye), contentDescription = "hey",
                   modifier = modifier
                       .size(50.dp)
                       .clip(CircleShape)
               )

               Column(
                   modifier = modifier
                       .padding(start = Dp(15f))
                       .padding(top = Dp(3f))
               ) {


                   Text(
                       text = "Active    ",
                       color = Color.Black,
                       modifier = modifier.padding(start = Dp(21f))

                   )
                   Text(
                       text = "3 mins ago",
                       color = Color.Gray
                   )


               }
           }


                Row(modifier = modifier
                    .fillMaxWidth()
                    .padding(end = Dp(15f))
                    .padding(top = Dp(7f)),
                    horizontalArrangement = Arrangement.End) {
                    Image(
                        painter = painterResource(id = R.drawable.video), contentDescription = "call",modifier = modifier
                            .padding(end = Dp(32f))
                            .size(40.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.call),
                        contentDescription = "call",modifier = modifier
                            .padding(end = Dp(7f))
                            .size(38.dp)
                    )

                    Image(painter = painterResource(id = R.drawable.more), contentDescription = "more",modifier = modifier
                        .size(35.dp))
                }



           }


       }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {


        //drawer(context = LocalContext.current)
        FloatingActionButton(onClick = { /*TODO*/ }) {
            
            Text(text = "Go")
            
        }

    }
}