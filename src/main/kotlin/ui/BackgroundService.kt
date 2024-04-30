package io.dpopkov.apatheiafx.ui

import io.dpopkov.apatheiafx.backend.PomidorService
import io.dpopkov.apatheiafx.model.Pomidor
import javafx.concurrent.Task
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

class BackgroundService(
    private val pomidorService: PomidorService
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val pool = Executors.newCachedThreadPool()

    fun shutdown() {
        pool.shutdown()
    }

    inner class SaveTask(private val item: Pomidor, updateUiAction: (Pomidor) -> Unit) : Task<Pomidor>() {
        init {
            setOnSucceeded {
                val saved = it.source.value as Pomidor
                log.debug("Saved using Task: {}", saved)
                updateUiAction(saved)
            }
        }

        override fun call(): Pomidor {
            return pomidorService.save(item)
        }
    }

    fun save(item: Pomidor, updateUiAction: (Pomidor) -> Unit) {
        pool.submit(SaveTask(item, updateUiAction))
    }

    inner class LoadAllTask(updateUiAction: (List<Pomidor>) -> Unit) : Task<List<Pomidor>>() {
        init {
            setOnSucceeded {
                val all = it.source.value as List<*>
                log.debug("Loading {} pomidor items", all.size)
                @Suppress("UNCHECKED_CAST")
                updateUiAction(all as List<Pomidor>)
            }
        }

        override fun call(): List<Pomidor> {
            return pomidorService.getAll()
        }
    }

    fun loadAll(updateUiAction: (List<Pomidor>) -> Unit) {
        pool.submit(LoadAllTask(updateUiAction))
    }
}
