package io.dpopkov.apatheiafx.ui

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage

class AppUI : Application() {
    private val focusTimer = FocusTimer()

    override fun start(primaryStage: Stage) {
        primaryStage.title = "ApatheiaFX"
        primaryStage.onCloseRequest = EventHandler {
            focusTimer.cancel()
        }

        val root = StackPane(
            focusTimer.buildPomodoroNode(),
        )
        with(primaryStage) {
            scene = Scene(root, 480.0, 240.0).apply {
                stylesheets.add("css/styles.css")
            }
            show()
        }
    }
}
