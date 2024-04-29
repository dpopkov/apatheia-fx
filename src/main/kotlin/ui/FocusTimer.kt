package io.dpopkov.apatheiafx.ui

import io.dpopkov.apatheiafx.backend.PomidorService
import io.dpopkov.apatheiafx.model.Pomidor
import io.dpopkov.apatheiafx.model.FinishedPomidorsList
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

class FocusTimer(
    pomidorService: PomidorService,
    private val playSoundNotification: Boolean = true,
    private val showAlert: Boolean = true,
) {
    private val backgroundService = BackgroundService(pomidorService)
    private val timerUiContent = VBox(10.0)
    private var intervalType = IntervalType.FOCUS
    private val audioPlayer = AudioPlayer()
    private var focusTimer: Timer? = null
    private var secondsTimer: Timer? = null
    private val secondsProperty = SimpleIntegerProperty(IntervalType.FOCUS.defaultSeconds)
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
    private val finishedPomidors = FinishedPomidorsList()
    private var currentPomidor: Pomidor? = null
    private val finishedCountText = Text("").apply {
        textProperty().bind(finishedPomidors.count.asString())
    }

    init {
        backgroundService.loadAll { loaded: List<Pomidor> ->
            Platform.runLater { finishedPomidors.addAll(loaded) }
        }
    }

    fun buildPomodoroNode(): Node {
        val btnFocus = Button("Focus").apply {
            setOnAction { setFocusedState() }
        }
        val btnRest = Button("Short Rest").apply {
            setOnAction { setRestState() }
        }
        val btnLongRest = Button("Long Rest").apply {
            setOnAction { setLongRestState() }
        }
        val timerTxt = Text().apply {
            textProperty().bind(secondsBinding)
            styleClass.addAll("timer-output-text")
        }
        val intervalNameField = TextField("").apply {
            promptText = "Enter name for this interval"
        }
        val btnStart = Button("Start")
        btnStart.setOnAction {
            val intervalName = intervalNameField.text.trim()
            currentPomidor = if (intervalName.isNotEmpty()) {
                Pomidor(name = intervalName)
            } else {
                Pomidor() // use generated name
            }
            intervalNameField.clear()
            focusTimer = Timer(true).apply {
                schedule(IntervalTask(), (secondsProperty.value * 1000).toLong())
            }
            secondsTimer = Timer(true).apply {
                scheduleAtFixedRate(SecondsCountdownTask(), 1000L, 1000L)
            }
        }
        val btnStop = Button("Stop")
        btnStop.setOnAction {
            cancelTimers()
        }
        val btnReset = Button("Reset")
        btnReset.setOnAction {
            cancelTimers()
            updateSecondsProperty()
        }

        with(timerUiContent.children) {
            addAll(
                HBox(5.0, btnFocus, btnRest, btnLongRest),
                inputTestingTimeField,
                timerTxt,
                intervalNameField,
                HBox(
                    5.0,
                    btnStart,
                    btnStop,
                    btnReset,
                ),
                verticalStrut(10),
                HBox(Text("Finished intervals: "), finishedCountText),
                HistoryTable(finishedPomidors.items).buildTable(),
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
        setIntervalState(IntervalType.FOCUS)
    }

    private fun setRestState() {
        setIntervalState(IntervalType.SHORT_REST)
    }

    private fun setLongRestState() {
        setIntervalState(IntervalType.LONG_REST)
    }

    private fun setIntervalState(type: IntervalType) {
        type.setStyleOn(timerUiContent.styleClass)
        secondsProperty.value = type.defaultSeconds
        intervalType = type
    }

    private fun switchIntervalState() {
        when (intervalType) {
            IntervalType.FOCUS -> setRestState()
            IntervalType.SHORT_REST -> setFocusedState()
            IntervalType.LONG_REST -> setFocusedState()
        }
    }

    private fun cancelTimers() {
        secondsTimer?.cancel()
        focusTimer?.cancel()
    }

    private fun updateSecondsProperty() {
        if (inputTestingTimeField.text.isEmpty() || inputTestingTimeField.text.isBlank()) {
            secondsProperty.value = intervalType.defaultSeconds
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
                currentPomidor?.let {
                    it.finishIt()
                    backgroundService.save(it) {saved: Pomidor ->
                        Platform.runLater { finishedPomidors.add(saved) }
                    }
                }
                currentPomidor = null
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
