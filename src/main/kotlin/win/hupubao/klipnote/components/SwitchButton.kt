package win.hupubao.klipnote.components

import javafx.animation.FillTransition
import javafx.animation.ParallelTransition
import javafx.animation.TranslateTransition
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Parent
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.util.Duration

class SwitchButton : Parent() {
    private val switchedOn = SimpleBooleanProperty(false)

    private val translateAnimation = TranslateTransition(Duration.seconds(0.25))
    private val fillAnimation = FillTransition(Duration.seconds(0.25))

    private val animation = ParallelTransition(translateAnimation, fillAnimation)

    fun switchedOnProperty(): BooleanProperty {
        return switchedOn
    }

    init {
        val background = Rectangle(64.0, 32.0)
        background.arcWidth = 32.0
        background.arcHeight = 32.0
        background.fill = Color.WHITE
        background.stroke = Color.LIGHTGRAY

        val trigger = Circle(16.0)
        trigger.centerX = 16.0
        trigger.centerY = 16.0
        trigger.fill = Color.WHITE
        trigger.stroke = Color.LIGHTGRAY

        translateAnimation.node = trigger
        fillAnimation.shape = background

        children.addAll(background, trigger)

        switchedOn.addListener { obs, oldState, newState ->
            val isOn = newState!!
            translateAnimation.toX = (if (isOn) 64 - 32 else 0).toDouble()
            fillAnimation.fromValue = if (isOn) Color.WHITE else Color.LIGHTGREEN
            fillAnimation.toValue = if (isOn) Color.LIGHTGREEN else Color.WHITE

            animation.play()
        }

        setOnMouseClicked { event -> switchedOn.set(!switchedOn.get()) }
    }
}