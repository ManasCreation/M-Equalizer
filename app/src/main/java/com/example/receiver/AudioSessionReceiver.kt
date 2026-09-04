package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.audiofx.AudioEffect
import android.util.Log
import com.example.audio.AudioEngine

class AudioSessionReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "AudioSessionReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val sessionId = intent.getIntExtra(AudioEffect.EXTRA_AUDIO_SESSION, AudioEffect.ERROR_BAD_VALUE)
        val packageName = intent.getStringExtra(AudioEffect.EXTRA_PACKAGE_NAME) ?: "Unknown"

        Log.d(TAG, "Audio session event: action=$action, session=$sessionId, pkg=$packageName")

        if (sessionId != AudioEffect.ERROR_BAD_VALUE) {
            val engine = AudioEngine.getInstance(context.applicationContext)
            when (action) {
                AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION -> {
                    engine.setAudioSession(sessionId)
                }
                AudioEffect.ACTION_CLOSE_AUDIO_EFFECT_CONTROL_SESSION -> {
                    // Fall back to global session 0
                    engine.setAudioSession(0)
                }
            }
        }
    }
}
