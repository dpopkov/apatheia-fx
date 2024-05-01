package io.dpopkov.apatheiafx.ui

import io.dpopkov.apatheiafx.AppMainSpring
import io.dpopkov.apatheiafx.backend.PomidorService
import io.dpopkov.apatheiafx.model.Pomidor
import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
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
    private val finishedPomidors: ObservableList<Pomidor> = FXCollections.observableArrayList()
    private val sizeBinding = Bindings.createStringBinding(
        { finishedPomidors.size.toString() },
        finishedPomidors
    )
    private val finishedCountText = Text("").apply {
        textProperty().bind(sizeBinding)
    }

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
        focusTimer = FocusTimer(finishedPomidors, backgroundService)
    }

    override fun start(primaryStage: Stage) {
        primaryStage.title = "ApatheiaFX"
        primaryStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST) { closeListeners.forEach { it() }}
        primaryStage.onCloseRequest = EventHandler {
            focusTimer.cancel()
        }

        val root = StackPane(
            VBox(
                focusTimer.buildPomodoroNode(),
                HistoryTable(finishedPomidors, backgroundService).buildTable(),
                HBox(Text("Finished intervals: "), finishedCountText).apply {
                    styleClass.add("status-bar")
                },
            )
        )
        with(primaryStage) {
            scene = Scene(root, 480.0, 480.0).apply {
                stylesheets.add("css/styles.css")
            }
            show()
        }
    }
}
