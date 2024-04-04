package io.dpopkov.apatheiafx.ui

import javafx.scene.layout.Region

fun verticalStrut(height: Int) = Region().apply {
    prefHeight = height.toDouble()
    minHeight = height.toDouble()
}
