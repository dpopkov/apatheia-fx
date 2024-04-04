package io.dpopkov.apatheiafx.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.time.LocalDateTime

class FinishedPomidorsList {
    val items: ObservableList<Pomidor> = FXCollections.observableArrayList()
    val count = SimpleIntegerProperty(0)

    init {
        add(
            Pomidor(
                name = "p-1",
                start = LocalDateTime.now(),
            ).apply { finishIt(LocalDateTime.now().plusMinutes(2)) }
        )
        add(
            Pomidor(
                name = "p-2",
                start = LocalDateTime.now().plusMinutes(3),
            ).apply { finishIt(LocalDateTime.now().plusMinutes(9)) }
        )
        add(
            Pomidor(
                name = "p-3",
                start = LocalDateTime.now().plusMinutes(13),
            ).apply { finishIt(LocalDateTime.now().plusMinutes(19)) }
        )
    }

    fun add(item: Pomidor) {
        if (!item.isFinished) {
            item.finishIt()
        }
        items.add(item)
        count.value = count.value.inc()
    }
}