package win.hupubao.klipnote.listener

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.stage.Stage


class MouseDragListener(private val stage: Stage) : EventHandler<MouseEvent> {

    private var xOffset = 0.0
    private var yOffset = 0.0

    override fun handle(event: MouseEvent) {
        event.consume()
        if (event.eventType == MouseEvent.MOUSE_PRESSED) {
            xOffset = event.sceneX
            yOffset = event.sceneY
        } else if (event.eventType == MouseEvent.MOUSE_DRAGGED) {
            stage.x = event.screenX - xOffset
            if (event.screenY - yOffset < 0) {
                stage.y = 0.0
            } else {
                stage.y = event.screenY - yOffset
            }
        }
    }

    fun enableDrag(node: Node) {
        node.onMousePressed = this
        node.onMouseDragged = this
    }
}