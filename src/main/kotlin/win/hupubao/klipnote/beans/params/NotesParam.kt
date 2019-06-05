package win.hupubao.klipnote.beans.params

import javafx.scene.control.Pagination

class NotesParam {
    var pagination: Pagination? = null
    var searchText: String? = null

    constructor()

    constructor(pagination: Pagination?, searchText: String?) {
        this.pagination = pagination
        this.searchText = searchText
    }


}
