package io.dpopkov.apatheiafx.ui

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage

class App : Application() {
    override fun start(primaryStage: Stage) {
        primaryStage.title = "ApatheiaFX"
        val root = StackPane()
        with(primaryStage) {
            scene = Scene(root, 480.0, 360.0)
            show()
        }
    }
}
