package io.dpopkov.apatheiafx.ui

import io.dpopkov.apatheiafx.model.Pomidor
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.util.StringConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HistoryTable(
    private val items: ObservableList<Pomidor>
) {
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM HH:mm")

    fun buildTable(): Node {
        val datetimeFieldRenderer = TextFieldTableCell.forTableColumn<Pomidor, LocalDateTime>(
            object: StringConverter<LocalDateTime>() {
                override fun toString(obj: LocalDateTime): String {
                    return obj.format(dateTimeFormatter)
                }

                override fun fromString(string: String?): LocalDateTime {
                    TODO("Not implemented because the column is not editable yet")
                }
            }
        )
        val textFieldRenderer = TextFieldTableCell.forTableColumn<Pomidor>()
        val longFieldRenderer = TextFieldTableCell.forTableColumn<Pomidor, Long>(
            object : StringConverter<Long>() {
                override fun toString(obj: Long): String = obj.toString()

                override fun fromString(string: String?): Long {
                    TODO("Not implemented because the column is not editable yet")
                }
            }
        )
        val startColumn = TableColumn<Pomidor, LocalDateTime>("Start").apply {
            isEditable = false
            cellValueFactory = PropertyValueFactory("start")
            cellFactory = datetimeFieldRenderer
        }
        val nameColumn = TableColumn<Pomidor, String>("Name").apply {
            isEditable = false
            cellValueFactory = PropertyValueFactory("name")
            cellFactory = textFieldRenderer
        }
        val minutesColumn = TableColumn<Pomidor, Long>("Minutes").apply {
            isEditable = false
            cellValueFactory = PropertyValueFactory("durationMinutes")
            cellFactory = longFieldRenderer
        }
        val finishColumn = TableColumn<Pomidor, LocalDateTime>("Finish").apply {
            isEditable = false
            cellValueFactory = PropertyValueFactory("finish")
            cellFactory = datetimeFieldRenderer
        }
        val tableView = TableView(items).apply {
            placeholder = Text("No data exists")
            isEditable = false
            columns.addAll(
                startColumn,
                nameColumn,
                finishColumn,
                minutesColumn,
            )
        }
        return VBox(
            5.0,
            tableView,
        )
    }
}