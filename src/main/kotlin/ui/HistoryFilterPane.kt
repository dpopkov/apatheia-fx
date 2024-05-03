package io.dpopkov.apatheiafx.ui

import io.dpopkov.apatheiafx.model.Pomidor
import javafx.collections.transformation.FilteredList
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import java.util.function.Predicate
import java.util.regex.PatternSyntaxException

class HistoryFilterPane(
    private val filteredList: FilteredList<Pomidor>,
    filterPaneStyleName: String = "filter-history-pane",
    private val badRegexStyleName: String = "filter-history-bad-regex",
) : HBox(5.0) {
    private val nameFilterLabel = Label("Filter by name containing:")
    private val nameFilterField = TextField("")
    private val useRegexCheckBox = CheckBox("Use regex")

    init {
        useRegexCheckBox.apply {
            setOnAction {
                if (!useRegexCheckBox.isSelected) {
                    nameFilterField.styleClass.remove(badRegexStyleName)
                }
            }
        }
        nameFilterField.apply {
            textProperty().addListener { _, _, newValue ->
                filteredList.predicate = Predicate {
                    if (newValue == null || newValue.isBlank()) {
                        true
                    } else {
                        if (useRegexCheckBox.isSelected) {
                            try {
                                val re =  newValue.toRegex()
                                nameFilterField.styleClass.remove(badRegexStyleName)
                                it.name.contains(re)
                            } catch (e: PatternSyntaxException) {
                                if (!nameFilterField.styleClass.contains(badRegexStyleName)) {
                                    nameFilterField.styleClass.add(badRegexStyleName)
                                }
                                true
                            }
                        } else {
                            it.name.contains(newValue)
                        }
                    }
                }
            }
        }

        children.addAll(
            nameFilterLabel,
            nameFilterField,
            useRegexCheckBox,
        )
        styleClass.add(filterPaneStyleName)
    }
}
