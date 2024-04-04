package io.dpopkov.apatheiafx.ui

import javafx.collections.ObservableList

enum class IntervalType(
    val defaultSeconds: Int,
    private val styleName: String,
) {
    FOCUS(25 * 60, "pomodoro-focus"),
    SHORT_REST(5 * 60, "pomodoro-rest");

    fun setStyleOn(styleClasses: ObservableList<String>) {
        entries.map { it.styleName }.filter { it != styleName }.forEach {
            styleClasses.remove(it)
        }
        if (!styleClasses.contains(styleName)) {
            styleClasses.add(styleName)
        }
    }
}
