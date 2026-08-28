package com.example.domain.voice

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.data.local.entity.VoiceConfigEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.concurrent.TimeUnit

sealed class VoicePlaybackState {
    object Idle : VoicePlaybackState()
    data class Playing(val text: String, val mode: String) : VoicePlaybackState()
    data class Error(val message: String) : VoicePlaybackState()
}

sealed class VoiceInputState {
    object Idle : VoiceInputState()
    object Listening : VoiceInputState()
    data class Result(val recognizedText: String) : VoiceInputState()
    data class Error(val message: String) : VoiceInputState()
}

class VoiceEngine(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val tag = "VoiceEngine"

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private var mediaPlayer: MediaPlayer? = null
    private var speechRecognizer: SpeechRecognizer? = null

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val _playbackState = MutableStateFlow<VoicePlaybackState>(VoicePlaybackState.Idle)
    val playbackState: StateFlow<VoicePlaybackState> = _playbackState.asStateFlow()

    private val _inputState = MutableStateFlow<VoiceInputState>(VoiceInputState.Idle)
    val inputState: StateFlow<VoiceInputState> = _inputState.asStateFlow()

    init {
        initializeTts()
    }

    private fun initializeTts() {
        try {
            tts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.language = Locale.US
                    isTtsInitialized = true
                    Log.d(tag, "Native TextToSpeech initialized successfully.")
                } else {
                    Log.w(tag, "Native TextToSpeech initialization failed with status: $status")
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error initializing TextToSpeech", e)
        }
    }

    /**
     * Speaks the given text using either Custom Neural Voice (if configured) or native TTS.
     */
    fun speak(text: String, config: VoiceConfigEntity) {
        if (text.isBlank()) return
        stop()

        if (config.mode == "CUSTOM_VOICE" && config.apiKey.isNotBlank()) {
            speakWithCustomNeuralVoice(text, config)
        } else {
            speakWithNativeTts(text, config)
        }
    }

    private fun speakWithNativeTts(text: String, config: VoiceConfigEntity) {
        try {
            tts?.setPitch(config.pitch.coerceIn(0.5f, 2.0f))
            tts?.setSpeechRate(config.speechRate.coerceIn(0.5f, 2.0f))
            _playbackState.value = VoicePlaybackState.Playing(text, "SYSTEM_TTS")
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "dr_forex_tts_id")
        } catch (e: Exception) {
            Log.e(tag, "Failed to speak with native TTS", e)
            _playbackState.value = VoicePlaybackState.Error("TTS error: ${e.localizedMessage}")
        }
    }

    private fun speakWithCustomNeuralVoice(text: String, config: VoiceConfigEntity) {
        _playbackState.value = VoicePlaybackState.Playing(text, "CUSTOM_VOICE")
        scope.launch(Dispatchers.IO) {
            try {
                // ElevenLabs / Neural Voice endpoint
                val voiceId = if (config.voiceId.isBlank() || config.voiceId == "default") "21m00Tcm4TlvDq8ikWAM" else config.voiceId
                val url = "https://api.elevenlabs.io/v1/text-to-speech/$voiceId"

                val jsonBody = JSONObject().apply {
                    put("text", text)
                    put("model_id", "eleven_monolingual_v1")
                    put("voice_settings", JSONObject().apply {
                        put("stability", 0.75)
                        put("similarity_boost", 0.85)
                    })
                }

                val request = Request.Builder()
                    .url(url)
                    .addHeader("xi-api-key", config.apiKey.trim())
                    .addHeader("Content-Type", "application/json")
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful && response.body != null) {
                    val tempAudioFile = File(context.cacheDir, "dr_forex_custom_voice.mp3")
                    FileOutputStream(tempAudioFile).use { output ->
                        response.body!!.byteStream().copyTo(output)
                    }

                    withContext(Dispatchers.Main) {
                        playAudioFile(tempAudioFile, text)
                    }
                } else {
                    Log.w(tag, "Custom voice API call returned code: ${response.code}. Falling back to native TTS.")
                    withContext(Dispatchers.Main) {
                        speakWithNativeTts(text, config)
                    }
                }
            } catch (e: Exception) {
                Log.e(tag, "Failed custom neural voice stream", e)
                withContext(Dispatchers.Main) {
                    // Graceful fallback to offline TTS
                    speakWithNativeTts(text, config)
                }
            }
        }
    }

    private fun playAudioFile(file: File, text: String) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(file.absolutePath)
                setOnCompletionListener {
                    _playbackState.value = VoicePlaybackState.Idle
                }
                setOnErrorListener { _, what, extra ->
                    Log.e(tag, "MediaPlayer error: what=$what, extra=$extra")
                    _playbackState.value = VoicePlaybackState.Idle
                    true
                }
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e(tag, "Error playing audio file", e)
            _playbackState.value = VoicePlaybackState.Error("Audio player failed: ${e.localizedMessage}")
        }
    }

    /**
     * Starts voice speech recognition from the device microphone.
     */
    fun startListening(onResult: (String) -> Unit) {
        try {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                _inputState.value = VoiceInputState.Listening
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask Dr. Forex anything about the market...")
                }

                speechRecognizer?.destroy()
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {}
                        override fun onBeginningOfSpeech() {}
                        override fun onRmsChanged(rmsdB: Float) {}
                        override fun onBufferReceived(buffer: ByteArray?) {}
                        override fun onEndOfSpeech() {}
                        override fun onError(error: Int) {
                            Log.w(tag, "Speech recognizer error code: $error")
                            _inputState.value = VoiceInputState.Error("Voice input error code: $error")
                        }
                        override fun onResults(results: Bundle?) {
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            val recognized = matches?.firstOrNull() ?: ""
                            _inputState.value = if (recognized.isNotBlank()) {
                                onResult(recognized)
                                VoiceInputState.Result(recognized)
                            } else {
                                VoiceInputState.Idle
                            }
                        }
                        override fun onPartialResults(partialResults: Bundle?) {}
                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                    startListening(intent)
                }
            } else {
                _inputState.value = VoiceInputState.Error("Speech recognition not available on this device.")
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to start listening", e)
            _inputState.value = VoiceInputState.Error("Voice input failed: ${e.localizedMessage}")
        }
    }

    fun stop() {
        try {
            tts?.stop()
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            mediaPlayer = null
            speechRecognizer?.stopListening()
            _playbackState.value = VoicePlaybackState.Idle
            _inputState.value = VoiceInputState.Idle
        } catch (e: Exception) {
            Log.e(tag, "Error stopping voice engine", e)
        }
    }

    fun destroy() {
        stop()
        tts?.shutdown()
        tts = null
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
