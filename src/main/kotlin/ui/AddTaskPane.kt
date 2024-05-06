package io.dpopkov.apatheiafx.ui

import io.dpopkov.apatheiafx.model.WorkTask
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.TreeItem
import javafx.scene.layout.HBox

class AddTaskPane(
    private val rootOfTree: TreeItem<WorkTask>,
    styleName: String = "add-task-pane",
) : HBox(5.0) {
    private val taskTitleLabel = Label("Task Title:")
    private val taskTitleField = TextField("")
    private val btnAddTask = Button("Create")

    init {
        btnAddTask.setOnAction {
            val title = taskTitleField.text.trim()
            if (title.isNotEmpty()) {
                val task = WorkTask(title)
                val item = TreeItem(task)
                rootOfTree.children.add(item)
            }
        }

        children.addAll(
            taskTitleLabel,
            taskTitleField,
            btnAddTask,
        )
        alignment = Pos.CENTER_LEFT
        styleClass.add(styleName)
    }
}
