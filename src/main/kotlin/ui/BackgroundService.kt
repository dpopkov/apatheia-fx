package io.dpopkov.apatheiafx.ui

import io.dpopkov.apatheiafx.backend.PomidorService
import io.dpopkov.apatheiafx.backend.WorkTaskService
import io.dpopkov.apatheiafx.model.Pomidor
import io.dpopkov.apatheiafx.model.WorkTask
import javafx.concurrent.Task
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

class BackgroundService(
    private val pomidorService: PomidorService,
    private val workTaskService: WorkTaskService,
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
                log.debug("Saved item: {}", saved)
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

    inner class RemoveTask(private val itemId: Long, updateUiAction: () -> Unit) : Task<Unit>() {
        init {
            setOnSucceeded {
                log.debug("Removed item by id={}", itemId)
                updateUiAction()
            }
        }

        override fun call() {
            pomidorService.removeById(itemId)
        }
    }

    fun removeById(id: Long, updateUiAction: () -> Unit) {
        pool.submit(RemoveTask(id, updateUiAction))
    }

    inner class UpdateTask(private val item: Pomidor) : Task<Unit>() {
        init {
            setOnSucceeded {
                log.debug("Updated item with id={}", item.id)
            }
        }

        override fun call() {
            pomidorService.update(item)
        }
    }

    fun update(item: Pomidor) {
        pool.submit(UpdateTask(item))
    }

    inner class SaveWorkTaskTask(private val item: WorkTask, updateUiAction: (WorkTask) -> Unit) : Task<WorkTask>() {
        init {
            setOnSucceeded {
                val saved = it.source.value as WorkTask
                log.debug("Saved task: {}", saved)
                updateUiAction(saved)
            }
        }

        override fun call(): WorkTask {
            return workTaskService.save(item)
        }
    }

    fun saveWorkTask(item: WorkTask, updateUiAction: (WorkTask) -> Unit) {
        pool.submit(SaveWorkTaskTask(item, updateUiAction))
    }

    inner class LoadAllWorkTasksTask(updateUiAction: (List<WorkTask>) -> Unit) : Task<List<WorkTask>>() {
        init {
            setOnSucceeded {
                val all = it.source.value as List<*>
                log.debug("Loading {} work tasks", all.size)
                @Suppress("UNCHECKED_CAST")
                updateUiAction(all as List<WorkTask>)
            }
        }

        override fun call(): List<WorkTask> {
            return workTaskService.getAll()
        }
    }

    fun loadAllTasks(updateUiAction: (List<WorkTask>) -> Unit) {
        pool.submit(LoadAllWorkTasksTask(updateUiAction))
    }

    inner class RemoveWorkTaskTask(private val workTaskId: Long, updateUiAction: () -> Unit) : Task<Unit>() {
        init {
            setOnSucceeded {
                log.debug("Removed work task by id={}", workTaskId)
                updateUiAction()
            }
        }

        override fun call() {
            workTaskService.removeById(workTaskId)
        }
    }

    fun removeWorkTask(item: WorkTask, updateUiAction: () -> Unit) {
        val itemId = item.id ?: throw IllegalStateException("Attempt to delete work item with id=null")
        pool.submit(RemoveWorkTaskTask(itemId, updateUiAction))
    }

    inner class UpdateWorkTaskTask(private val workTask: WorkTask) : Task<Unit>() {
        init {
            setOnSucceeded {
                log.debug("Updated work task with id={}", workTask.id)
            }
        }

        override fun call() {
            workTaskService.update(workTask)
        }
    }

    fun updateWorkTaskTitle(item: WorkTask, newTitle: String) {
        item.title = newTitle
        pool.submit(UpdateWorkTaskTask(item))
    }
}
