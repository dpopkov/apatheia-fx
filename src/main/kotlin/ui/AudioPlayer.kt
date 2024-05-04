package io.dpopkov.apatheiafx.ui

import javafx.scene.media.AudioClip
import org.slf4j.LoggerFactory

private const val DEFAULT_WAV = "pomodoro-f1.wav"

class AudioPlayer(
    wavResource: String = "sound/$DEFAULT_WAV"
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val clip: AudioClip

    init {
        val mediaUrl = this::class.java.getResource("/$wavResource")
            ?: throw IllegalStateException("Cannot find sound resource: $wavResource")
        val urlStr = mediaUrl.toExternalForm()
        log.debug("Loading media from path: {}", urlStr)
        if (urlStr.startsWith("jar:nested:")) {
            /*
                Это случай запуска из Jar.
                Данный Url вызовет ошибку при попытке загрузить его в AudioClip.
                Поэтому нужно произвести две замены.
             */
            val urlStrReplaced = urlStr
                .replace("jar:nested:", "jar:file:")
                .replace("jar/!BOOT-INF/classes/!/", "jar!/BOOT-INF/classes/")
            log.debug("Loading media from modified path: {}", urlStrReplaced)
            clip = AudioClip(urlStrReplaced)
        } else {
            clip = AudioClip(urlStr)
        }
    }

    fun play() {
        clip.play()
    }
}
