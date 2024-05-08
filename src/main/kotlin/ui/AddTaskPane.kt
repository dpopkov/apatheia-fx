package io.dpopkov.apatheiafx.ui

import io.dpopkov.apatheiafx.model.WorkTask
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.util.StringConverter

class AddTaskPane(
    private val workTasks: ObservableList<WorkTask>,
    private val backgroundService: BackgroundService,
    styleName: String = "add-task-pane",
) : HBox(5.0) {
    private val taskTitleLabel = Label("Task Title:")
    private val taskTitleField = TextField("")
    private val parentLabel = Label("Parent:")
    private val selectParent = ChoiceBox(workTasks)
    private val btnAddTask = Button("Create")

    init {
        val stringConverter = object : StringConverter<WorkTask>() {
            override fun toString(obj: WorkTask?): String {
                return obj?.title ?: ""
            }

            override fun fromString(string: String?): WorkTask {
                TODO("Not yet implemented")
            }
        }
        selectParent.converter = stringConverter

        btnAddTask.setOnAction {
            val title = taskTitleField.text.trim()
            if (title.isNotEmpty()) {
                val parentTask: WorkTask = selectParent.selectionModel.selectedItem ?: WorkTask.root
                val task = WorkTask(title, parent = if (!parentTask.isRoot) parentTask else null)
                backgroundService.saveWorkTask(task) {
                    task.id = it.id
                    workTasks.add(task)
                }
            }
        }

        children.addAll(
            parentLabel,
            selectParent,
            taskTitleLabel,
            taskTitleField,
            btnAddTask,
        )
        alignment = Pos.CENTER_LEFT
        styleClass.add(styleName)
    }
}
