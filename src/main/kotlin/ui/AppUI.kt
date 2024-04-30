package io.dpopkov.apatheiafx.ui

import io.dpopkov.apatheiafx.AppMainSpring
import io.dpopkov.apatheiafx.backend.PomidorService
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.stage.WindowEvent
import org.springframework.boot.Banner
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext

class AppUI : Application() {
    private val closeListeners = mutableListOf<() -> Unit>()
    private lateinit var applicationContext: ConfigurableApplicationContext
    private lateinit var focusTimer: FocusTimer
    private lateinit var backgroundService: BackgroundService

    override fun init() {
        applicationContext = SpringApplicationBuilder(AppMainSpring::class.java)
            .bannerMode(Banner.Mode.OFF)
            .logStartupInfo(false)
            .build()
            .run()
        val pomidorService = applicationContext.getBean(PomidorService::class.java)
        backgroundService = BackgroundService(pomidorService)
        closeListeners.add {
            backgroundService.shutdown()
        }
        focusTimer = FocusTimer(backgroundService)
    }

    override fun start(primaryStage: Stage) {
        primaryStage.title = "ApatheiaFX"
        primaryStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST) { closeListeners.forEach { it() }}
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
