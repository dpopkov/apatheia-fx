package io.dpopkov.apatheiafx.ui

import javafx.scene.media.AudioClip

private const val DEFAULT_WAV = "pomodoro-f1.wav"

class Player(
    wavResource: String = "sound/$DEFAULT_WAV"
) {
    private val clip: AudioClip

    init {
        val mediaUrl = this::class.java.classLoader.getResource(wavResource)
            ?: throw IllegalStateException("Cannot find sound resource: $wavResource")
        clip = AudioClip(mediaUrl.toExternalForm())
    }

    fun play() {
        clip.play()
    }
}
