package com.arpadfodor.communityparking.android.app.model

import com.arpadfodor.communityparking.android.app.model.api.ApiService
import com.arpadfodor.communityparking.android.app.model.api.BasicAuthInterceptor
import com.arpadfodor.communityparking.android.app.model.repository.UserRepository
import com.arpadfodor.communityparking.android.app.model.repository.dataclasses.User
import okhttp3.OkHttpClient

object AccountService {

    var userId = ""
    var userDisplayName = ""
    private var userPassword = ""

    private var isCurrentAccountGuest = true
    var rememberAccount = false

    fun getClient() : OkHttpClient{
        val httpClient = OkHttpClient.Builder().addInterceptor(
            BasicAuthInterceptor(userId, userPassword)
        ).build()
        return httpClient
    }

    fun login(email: String, name: String, password: String, success: () -> Unit, error: () -> Unit){

        val successLogic = {
            if(rememberAccount){
                val userToSave = User(email, password, name, "")
                UserRepository.saveUser(userToSave, {})
            }
            success()
        }

        val errorLogic = {
            error()
        }

        tryLogin(email, name, password, success = successLogic, error = errorLogic)

    }

    fun logout(success: () -> Unit, error: () -> Unit){

        userId = ""
        userDisplayName = ""
        userPassword = ""

        isCurrentAccountGuest = true

        UserRepository.deleteUser{ isSuccess ->
            if(isSuccess){
                success()
            }
            else{
                error()
            }
        }

    }

    fun loginAsGuest(success: () -> Unit, error: () -> Unit){

        userId = ""
        userDisplayName = ""
        userPassword = ""

        isCurrentAccountGuest = true
        success()

    }

    fun tryAutoLogin(success: () -> Unit, error: () -> Unit){

        UserRepository.getUser { user ->

            if(user == null){
                isCurrentAccountGuest = true
                error()
            }
            else{
                tryLogin(user.email, user.name, user.password, success, error)
            }

        }

    }

    fun sendPasswordToEmail(email: String, success: () -> Unit, error: () -> Unit){
        //TODO: API send password to email
        success()
    }

    fun registerAccount(email: String, name: String, password: String, success: () -> Unit, error: () -> Unit){

        login(email, name, password, success={

            ApiService.postApiUser(email, name, password, success={
                login(email, name, password, success, error)
            }, error=error)

        }, error=error)

    }

    fun deleteAccount(success: () -> Unit, error: () -> Unit){
        ApiService.deleteSelf(success={
            logout(success, error)
        }, error=error)
    }

    fun changeAccount(nameToSet: String = userDisplayName, passwordToSet: String = userPassword, success: () -> Unit, error: () -> Unit){
        ApiService.putSelf(userId, nameToSet, passwordToSet, success={
            login(userId, nameToSet, passwordToSet, success, error)
        }, error=error)
    }

    private fun tryLogin(email: String, name: String, password: String, success: () -> Unit, error: () -> Unit){

        userId = email
        userDisplayName = name
        userPassword = password
        isCurrentAccountGuest = false

        ApiService.initialize(getClient())
        ApiService.login(success, error)

    }

    fun isCurrentAccountGuest() : Boolean{
        return isCurrentAccountGuest
    }

    fun getDisplayUserName() : String{
        return userDisplayName
    }

    fun getDisplayUserEmail() : String{
        return userId
    }

}