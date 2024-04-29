package io.dpopkov.apatheiafx.ui

import io.dpopkov.apatheiafx.backend.PomidorService
import io.dpopkov.apatheiafx.model.Pomidor
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread

class BackgroundService(
    private val pomidorService: PomidorService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun save(item: Pomidor, updateUiAction: (Pomidor) -> Unit) {
        thread(isDaemon = true) {
            val saved = pomidorService.save(item)
            log.debug("Saved: {}", saved)
            updateUiAction(saved)
        }
    }

    fun loadAll(updateUiAction: (List<Pomidor>) -> Unit) {
        thread(isDaemon = true) {
            val all = pomidorService.getAll()
            log.debug("Loading {} pomidor items", all.size)
            updateUiAction(all)
        }
    }
}
