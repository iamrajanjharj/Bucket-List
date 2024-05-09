package com.zybooks.to_dolist

import android.content.Context
import android.media.SoundPool
import android.media.AudioAttributes

class SoundEffects private constructor(context: Context) {

    private var soundPool: SoundPool? = null
    private val selectSoundIds = mutableListOf<Int>()

    companion object {
        private var instance: SoundEffects? = null

        fun getInstance(context: Context): SoundEffects {
            if (instance == null) {
                instance = SoundEffects(context)
            }
            return instance!!
        }
    }

    init {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(attributes)
            .build()

        soundPool?.let {
            selectSoundIds.add(it.load(context, R.raw.note_e, 1))
        }

    }

    fun playTone() {
        soundPool?.play(selectSoundIds[0], 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}