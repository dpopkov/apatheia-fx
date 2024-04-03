package io.dpopkov.apatheiafx.ui

import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import java.util.*

private const val DEFAULT_FOCUS_SECONDS_INTERVAL = 25 * 60
private const val DEFAULT_SHORT_REST_SECONDS_INTERVAL = 5 * 60

private enum class IntervalType {
    FOCUS,
    SHORT_REST,
}

class FocusTimer(
    private val playSoundNotification: Boolean = true,
    private val showAlert: Boolean = true,
) {
    private val timerUiContent = VBox(10.0)
    private var intervalType = IntervalType.FOCUS
    private val audioPlayer = AudioPlayer()
    private var focusTimer: Timer? = null
    private var secondsTimer: Timer? = null
    private val secondsProperty = SimpleIntegerProperty(DEFAULT_FOCUS_SECONDS_INTERVAL)
    private val secondsBinding = Bindings.createStringBinding(
        { secondsProperty.value.formatSecondsToTime() },
        secondsProperty
    )
    private val inputTestingTimeField = TextField().apply {
        promptText = "Enter custom time interval MM:SS"
        setOnKeyTyped {
            if (text.isNotEmpty() && text.isNotBlank()) {
                secondsProperty.value = text.parseTimeToSeconds()
            }
        }
    }

    fun buildPomodoroNode(): Node {
        val btnFocus = Button("Focus").apply {
            setOnAction { setFocusedState() }
        }
        val btnRest = Button("Short Rest").apply {
            setOnAction { setRestState() }
        }
        val timerTxt = Text().apply {
            textProperty().bind(secondsBinding)
            styleClass.addAll("timer-output-text")
        }
        val btnStart = Button("Start")
        btnStart.setOnAction {
            println("Clicked Start")
            println("Scheduling timer for ${secondsProperty.value} seconds")
            focusTimer = Timer(true).apply {
                schedule(IntervalTask(), (secondsProperty.value * 1000).toLong())
            }
            secondsTimer = Timer(true).apply {
                scheduleAtFixedRate(SecondsCountdownTask(), 1000L, 1000L)
            }
        }
        val btnStop = Button("Stop")
        btnStop.setOnAction {
            println("Clicked Stop")
            cancelTimers()
        }
        val btnReset = Button("Reset")
        btnReset.setOnAction {
            println("Clicked Reset")
            cancelTimers()
            updateSecondsProperty()
        }

        with(timerUiContent.children) {
            addAll(
                HBox(5.0, btnFocus, btnRest),
                inputTestingTimeField,
                timerTxt,
                HBox(
                    5.0,
                    btnStart,
                    btnStop,
                    btnReset,
                ),
            )
        }
        timerUiContent.styleClass.addAll("pomodoro-pane")
        setFocusedState()
        return timerUiContent
    }

    fun cancel() {
        cancelTimers()
    }

    private fun setFocusedState() {
        with(timerUiContent.styleClass) {
            remove("pomodoro-rest")
            if (!contains("pomodoro-focus")) add("pomodoro-focus")
        }
        secondsProperty.value = DEFAULT_FOCUS_SECONDS_INTERVAL
        intervalType = IntervalType.FOCUS
    }

    private fun setRestState() {
        with(timerUiContent.styleClass) {
            remove("pomodoro-focus")
            if (!contains("pomodoro-rest")) add("pomodoro-rest")
        }
        secondsProperty.value = DEFAULT_SHORT_REST_SECONDS_INTERVAL
        intervalType = IntervalType.SHORT_REST
    }

    private fun switchIntervalState() {
        when (intervalType) {
            IntervalType.FOCUS -> setRestState()
            IntervalType.SHORT_REST -> setFocusedState()
        }
    }

    private fun cancelTimers() {
        secondsTimer?.cancel()
        focusTimer?.cancel()
    }

    private fun updateSecondsProperty() {
        if (inputTestingTimeField.text.isEmpty() || inputTestingTimeField.text.isBlank()) {
            secondsProperty.value = when (intervalType) {
                IntervalType.FOCUS -> DEFAULT_FOCUS_SECONDS_INTERVAL
                IntervalType.SHORT_REST -> DEFAULT_SHORT_REST_SECONDS_INTERVAL
            }
        } else {
            secondsProperty.value = inputTestingTimeField.text.parseTimeToSeconds()
        }
    }

    private fun Int.formatSecondsToTime(): String {
        val minutes = (this / 60).toString()
        val seconds = (this % 60).toString()
        return "${minutes.padStart(2, '0')}:${seconds.padStart(2, '0')}"
    }

    private fun String.parseTimeToSeconds(): Int {
        if (this.contains(':')) {
            if (this.endsWith(':')) {
                val minutes = this.trimEnd(':')
                return minutes.toInt() * 60
            }
            val tokens = this.split(':')
            val minutes = tokens[0]
            val seconds = tokens[1]
            return minutes.toInt() * 60 + seconds.toInt()
        } else {
            return this.toInt()
        }
    }

    private inner class IntervalTask : TimerTask() {
        override fun run() {
            Platform.runLater {
                secondsTimer?.cancel()
                if (playSoundNotification) {
                    audioPlayer.play()
                }
                if (showAlert) {
                    Alert(Alert.AlertType.INFORMATION).apply {
                        title = "Focus Interval"
                        headerText = "Interval is over!"
                        showAndWait()
                    }
                }
                switchIntervalState()
            }
        }
    }

    private inner class SecondsCountdownTask : TimerTask() {
        override fun run() {
            Platform.runLater {
                secondsProperty.value = secondsProperty.value.dec()
            }
        }
    }
}
