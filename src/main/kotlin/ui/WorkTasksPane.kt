package io.dpopkov.apatheiafx.ui

import io.dpopkov.apatheiafx.model.WorkTask
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeTableColumn
import javafx.scene.control.TreeTableView
import javafx.scene.control.cell.TextFieldTreeTableCell
import javafx.scene.control.cell.TreeItemPropertyValueFactory
import javafx.scene.layout.VBox
import javafx.util.StringConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val DATETIME_COLUMN_MIN_WIDTH = 105.0

class WorkTasksPane : VBox(
    5.0
) {
    private val treeTable: TreeTableView<WorkTask>
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM HH:mm")

    init {
        val root = TreeItem<WorkTask>()
        treeTable = TreeTableView(root)
        treeTable.isShowRoot = false

        initItemsUnder(root)
        buildTreeTable()
        children.addAll(
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
        val titleColumn = TreeTableColumn<WorkTask, String>("Title").apply {
            prefWidth = 140.0
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

    private fun initItemsUnder(root: TreeItem<WorkTask>) {
        val task1 = WorkTask("task-1")
        val task11 = WorkTask("task-11", parent=task1)
        val task12 = WorkTask("task-12", parent=task1)
        val item1 = TreeItem<WorkTask>(task1)
        val item11 = TreeItem(task11)
        val item12 = TreeItem(task12)
        item1.children.addAll(item11, item12)

        val task2 = WorkTask("task-2")
        val task21 = WorkTask("task-21", parent=task2)
        val task22 = WorkTask("task-22", parent=task2)
        val item2 = TreeItem<WorkTask>(task2)
        val item21 = TreeItem<WorkTask>(task21)
        val item22 = TreeItem<WorkTask>(task22)
        item2.children.addAll(item21, item22)

        root.children.addAll(item1, item2)
    }
}
