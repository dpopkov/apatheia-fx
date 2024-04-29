package io.dpopkov.apatheiafx.ui

import io.dpopkov.apatheiafx.AppMainSpring
import io.dpopkov.apatheiafx.backend.PomidorService
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
    private lateinit var focusTimer: FocusTimer
    private lateinit var pomidorService: PomidorService

    override fun init() {
        applicationContext = SpringApplicationBuilder(AppMainSpring::class.java)
            .bannerMode(Banner.Mode.OFF)
            .logStartupInfo(false)
            .build()
            .run()
        pomidorService = applicationContext.getBean(PomidorService::class.java)
        focusTimer = FocusTimer(pomidorService)
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
