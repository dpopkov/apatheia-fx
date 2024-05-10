package io.dpopkov.apatheiafx.ui

import io.dpopkov.apatheiafx.model.WorkTask
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.control.cell.TextFieldTreeTableCell
import javafx.scene.control.cell.TreeItemPropertyValueFactory
import javafx.scene.layout.VBox
import javafx.util.StringConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val DATETIME_COLUMN_MIN_WIDTH = 105.0

class WorkTasksPane(
    workTasks: ObservableList<WorkTask>,
    private val backgroundService: BackgroundService,
) : VBox(
    5.0
) {
    private val treeTable: TreeTableView<WorkTask>
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM HH:mm")

    init {
        val root = TreeItem<WorkTask>()
        treeTable = TreeTableView(root)
        treeTable.isShowRoot = false
        treeTable.isTableMenuButtonVisible = true
        treeTable.contextMenu = ContextMenu(MenuItem("Remove").apply {
            setOnAction {
                val selected: TreeItem<WorkTask> = treeTable.selectionModel.selectedItem
                if (selected.children.isNotEmpty()) {
                    Alert(Alert.AlertType.WARNING).apply {
                        title = "Work Task Management"
                        headerText = "This Work Task has children. It can not be deleted!"
                        showAndWait()
                    }
                } else {
                    backgroundService.removeWorkTask(selected.value) {
                        workTasks.remove(selected.value)
                        selected.parent.children.remove(selected)
                    }
                }
            }
        })
        treeTable.isEditable = true

        initWorkTasksAdditionListener(root, workTasks)
        buildTreeTable()
        children.addAll(
            AddTaskPane(workTasks, backgroundService),
            treeTable
        )
    }

    private fun buildTreeTable() {
        val datetimeFieldRenderer = TextFieldTreeTableCell.forTreeTableColumn<WorkTask, LocalDateTime?>(
            object : StringConverter<LocalDateTime?>() {
                override fun toString(obj: LocalDateTime?): String {
                    return obj?.format(dateTimeFormatter) ?: ""
                }

                override fun fromString(string: String?): LocalDateTime {
                    TODO("Not implemented because the column is not editable yet")
                }
            }
        )
        val longFieldRenderer = TextFieldTreeTableCell.forTreeTableColumn<WorkTask, Long>(
            object : StringConverter<Long>() {
                override fun toString(obj: Long): String = obj.toString()

                override fun fromString(string: String?): Long {
                    TODO("Not implemented because the column is not editable yet")
                }
            }
        )
        val textFieldRenderer = TextFieldTreeTableCell.forTreeTableColumn<WorkTask>()
        val titleColumn = TreeTableColumn<WorkTask, String>("Title").apply {
            prefWidth = 140.0
            isEditable = true
            onEditCommit = EventHandler { evt: TreeTableColumn.CellEditEvent<WorkTask, String> ->
                val selectedItem: WorkTask = treeTable.selectionModel.selectedItem.value
                val newTitle = evt.newValue
                backgroundService.updateWorkTaskTitle(selectedItem, newTitle)
            }
            cellFactory = textFieldRenderer
            cellValueFactory = TreeItemPropertyValueFactory("title")
        }
        val startedColumn = TreeTableColumn<WorkTask, LocalDateTime>("Start").apply {
            minWidth = DATETIME_COLUMN_MIN_WIDTH
            cellValueFactory = TreeItemPropertyValueFactory("createdAt")
            cellFactory = datetimeFieldRenderer
        }
        val totalTimeColumn = TreeTableColumn<WorkTask, Long>("Total Time").apply {
            cellValueFactory = TreeItemPropertyValueFactory("totalPomidorsMinutes")
            cellFactory = longFieldRenderer
        }
        val totalIntervalsColumn = TreeTableColumn<WorkTask, Long>("Intervals").apply {
            cellValueFactory = TreeItemPropertyValueFactory("pomidorsCount")
            cellFactory = longFieldRenderer
        }
        val finishedColumn = TreeTableColumn<WorkTask, LocalDateTime>("Finish").apply {
            minWidth = DATETIME_COLUMN_MIN_WIDTH
            cellValueFactory = TreeItemPropertyValueFactory("finishedAt")
            cellFactory = datetimeFieldRenderer
        }
        treeTable.columns.addAll(
            titleColumn,
            startedColumn,
            totalTimeColumn,
            totalIntervalsColumn,
            finishedColumn,
        )
    }

    private fun initWorkTasksAdditionListener(rootOfTree: TreeItem<WorkTask>, workTasks: ObservableList<WorkTask>) {
        workTasks.addListener { change: ListChangeListener.Change<out WorkTask> ->
            if (change.next() && change.wasAdded()) {
                for (workTask in change.addedSubList) {
                    if (!workTask.isRoot) {
                        val item = TreeItem(workTask)
                        val parentTask = workTask.parent
                        if (parentTask == null || parentTask.isRoot) {
                            rootOfTree.children.add(item)
                        } else {
                            val parentTreeItem = rootOfTree.findParentItemRecursivelyForSubTask(parentTask)
                                ?: throw IllegalStateException("Cannot find parent in root children")
                            parentTreeItem.children.add(item)
                        }
                    }
                }
            }
        }
    }

    private fun TreeItem<WorkTask>.findParentItemRecursivelyForSubTask(task: WorkTask): TreeItem<WorkTask>? {
        if (this.value == task) {
            return this
        }
        if (this.children.isEmpty()) {
            return null
        }
        for (item in this.children) {
            if (item.value == task) {
                return item
            } else {
                val parentItem = item.findParentItemRecursivelyForSubTask(task)
                if (parentItem != null) {
                    return parentItem
                }
            }
        }
        return null
    }
}
