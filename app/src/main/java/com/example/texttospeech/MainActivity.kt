package com.example.texttospeech

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import com.example.texttospeech.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private var timeOfSpeakRequest: Long = 0L
    private lateinit var binding: ActivityMainBinding
    private lateinit var googleTTS: TextToSpeech

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (isGoogleEngineInstalled()) {
            createGoogleTTS()
        }

    }

    override fun onStart() {
        super.onStart()
        binding.swipeRefresh.setOnRefreshListener { assignFullSetOfVoicesToVoiceListView() }
    }

    private fun onTTSInitialized() {
        setUpWhatHappensWhenAVoiceItemIsClicked()
        setUtteranceProgressListenerOnTheTTS()
        assignFullSetOfVoicesToVoiceListView()
    }

    private fun createGoogleTTS() {
        googleTTS = TextToSpeech(this, { status ->
            if (status != TextToSpeech.ERROR) {
                onTTSInitialized()
            }
        }, "com.google.android.tts")
    }

    private fun setUpWhatHappensWhenAVoiceItemIsClicked() {
        binding.voiceListView.onItemClickListener =
            OnItemClickListener { parent, _, position, _ ->
                val desiredVoice = parent.adapter.getItem(position) as Voice

                if (googleTTS.setVoice(desiredVoice) == 0) {
                    speak()
                }
            }
    }

    private fun setUtteranceProgressListenerOnTheTTS() {
        val blurp: UtteranceProgressListener = object : UtteranceProgressListener() {
            @SuppressLint("SetTextI18n")
            override fun onStart(s: String) {
                runOnUiThread {
                    binding.progressView.setTextColor(Color.GREEN)
                    binding.progressView.text = "PROGRESS: STARTED"
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onDone(s: String) {
                runOnUiThread {
                    binding.progressView.setTextColor(Color.GREEN)
                    binding.progressView.text = "PROGRESS: DONE"
                }
            }

            @Deprecated("Deprecated in Java")
            @SuppressLint("SetTextI18n")
            override fun onError(s: String) {
                runOnUiThread {
                    binding.progressView.setTextColor(Color.RED)
                    binding.progressView.text = "PROGRESS: ERROR"
                }
            }
        }
        googleTTS.setOnUtteranceProgressListener(blurp)
    }

    @SuppressLint("SetTextI18n")
    private fun assignFullSetOfVoicesToVoiceListView() {
        googleTTS.stop()
        val tempVoiceList: MutableList<Voice> = ArrayList()
        for (v in googleTTS.voices!!) {
            Log.d("TAG",v.locale.country.toString())
            if (v.locale.country.contains("IN")) { // only English voices
                tempVoiceList.add(v)
            }
        }

//        tempVoiceList.sortWith { v1, v2 ->
//            v2.name.compareTo(v1.name, ignoreCase = true)
//        }
        val tempAdapter = VoiceAdapter(this, tempVoiceList)
        binding.voiceListView.adapter = tempAdapter
        binding.swipeRefresh.isRefreshing = false
        binding.progressView.setTextColor(Color.BLACK)
        binding.progressView.text = "PROGRESS: ..."
    }

    private fun speak() {
        val map = HashMap<String, String>()
        map[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "merp"
        timeOfSpeakRequest = System.currentTimeMillis()
        googleTTS.speak(binding.textToSpeak.text.toString(), TextToSpeech.QUEUE_FLUSH, map)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun isGoogleEngineInstalled(): Boolean {
        val ttsIntent = Intent()
        ttsIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA)
        val pm = packageManager
        val list = pm.queryIntentActivities(ttsIntent, PackageManager.GET_META_DATA)
        var googleIsInstalled = false
        for (i in list.indices) {
            val resolveInfoUnderScrutiny = list[i]
            val engineName = resolveInfoUnderScrutiny.activityInfo.applicationInfo.packageName
            if (engineName == "com.google.android.tts") {
                googleIsInstalled = true
            }
        }
        return googleIsInstalled
    }
}