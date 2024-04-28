package io.dpopkov.apatheiafx.ui

import io.dpopkov.apatheiafx.AppMainSpring
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import org.springframework.boot.Banner
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext

class AppUI : Application() {
    private lateinit var applicationContext: ConfigurableApplicationContext
    private val focusTimer = FocusTimer()

    override fun init() {
        applicationContext = SpringApplicationBuilder(AppMainSpring::class.java)
            .bannerMode(Banner.Mode.OFF)
            .logStartupInfo(true)
            .build()
            .run()
    }

    override fun start(primaryStage: Stage) {
        primaryStage.title = "ApatheiaFX"
        primaryStage.onCloseRequest = EventHandler {
            focusTimer.cancel()
        }

        val root = StackPane(
            focusTimer.buildPomodoroNode(),
        )
        with(primaryStage) {
            scene = Scene(root, 480.0, 480.0).apply {
                stylesheets.add("css/styles.css")
            }
            show()
        }
    }
}
