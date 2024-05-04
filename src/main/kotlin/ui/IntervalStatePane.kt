package io.dpopkov.apatheiafx.ui

import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.HBox

class IntervalStatePane(
    setStateAction: (type: IntervalType) -> Unit
) : HBox(5.0) {
    private val btnFocus = ToggleButton("Focus")
    private val btnRest = ToggleButton("Short Rest")
    private val btnLongRest = ToggleButton("Long Rest")
    private val toggleGroup = ToggleGroup()

    init {
        toggleGroup.toggles.addAll(btnFocus, btnRest, btnLongRest)
        toggleGroup.selectedToggleProperty().addListener { _, _, newValue ->
            when(newValue) {
                btnFocus -> setStateAction(IntervalType.FOCUS)
                btnRest -> setStateAction(IntervalType.SHORT_REST)
                btnLongRest -> setStateAction(IntervalType.LONG_REST)
            }
        }
        toggleGroup.selectToggle(btnFocus)

        children.addAll(
            btnFocus, btnRest, btnLongRest
        )
    }

    fun setFocusedState() {
        toggleGroup.selectToggle(btnFocus)
    }

    fun setRestState() {
        toggleGroup.selectToggle(btnRest)
    }
}