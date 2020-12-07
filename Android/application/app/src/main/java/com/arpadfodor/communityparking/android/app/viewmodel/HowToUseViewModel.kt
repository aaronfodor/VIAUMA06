package com.arpadfodor.communityparking.android.app.viewmodel

import androidx.lifecycle.MutableLiveData
import com.arpadfodor.communityparking.android.app.model.TextToSpeechService
import com.arpadfodor.communityparking.android.app.viewmodel.utils.AppViewModel

class HowToUseViewModel : AppViewModel(){

    val isTextToSpeechSpeaking: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun subscribeTextToSpeechListeners(errorCallback: () -> Unit){

        val isSpeaking = TextToSpeechService.isSpeaking()
        isTextToSpeechSpeaking.postValue(isSpeaking)

        TextToSpeechService.setCallbacks(
            started = {
                isTextToSpeechSpeaking.postValue(true)
            },
            finished = {
                isTextToSpeechSpeaking.postValue(false)
            },
            error = {
                isTextToSpeechSpeaking.postValue(false)
                errorCallback()
            }
        )

    }

    fun textToSpeechButtonClicked(text: String){

        if(isTextToSpeechSpeaking.value == true){
            stopTextToSpeech()
        }
        else{
            startTextToSpeech(text)
        }

    }

    private fun startTextToSpeech(text: String){
        TextToSpeechService.speak(text)
    }

    private fun stopTextToSpeech(){
        TextToSpeechService.stop()
    }

}