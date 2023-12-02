package hilt

import my_api.apiRepository
import my_api.duckFunctions
import my_api.duckRepository
import my_api.myFunctions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object appModule {
//
//    @Provides
//    @Singleton
//    fun buildMyApi(): myFunctions
//    = Retrofit.Builder()
//        .baseUrl("https://jsonplaceholder.typicode.com")
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//        .create(myFunctions::class.java)
//
//    @Provides
//    @Singleton
//    fun buildMyDuckApi(): duckFunctions
//            = Retrofit.Builder()
//        .baseUrl("https://random-d.uk/api/v2/")
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//        .create(duckFunctions::class.java)
//
//
//
//    @Provides
//    @Singleton
//    fun activateApiRepository(api: myFunctions) = apiRepository(api)
//
//
//    @Provides
//    @Singleton
//    fun activateDuckApi(duckApi: duckFunctions) = duckRepository(duckApi)
//
//
//
//}


interface appModule{

    val api: myFunctions
    val duckApi: duckFunctions
    val apiRepository:apiRepository
    val duckRepository:duckRepository

}

class appMooduleImp(): appModule{

    override val api: myFunctions by lazy {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(myFunctions::class.java)

    }


    override val duckApi: duckFunctions by lazy {
        Retrofit.Builder()
            .baseUrl("https://random-d.uk/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(duckFunctions::class.java)

    }


    override val apiRepository: apiRepository
        get() = apiRepository(api)


    override val duckRepository: duckRepository
        get() = duckRepository(duckApi)






}


