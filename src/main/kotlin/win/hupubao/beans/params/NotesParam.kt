package win.hupubao.beans.params

import javafx.scene.control.Pagination
import win.hupubao.beans.Category

class NotesParam {
    var pagination: Pagination? = null
    var category: Category? = null
    var searchText: String? = null

    constructor()

    constructor(pagination: Pagination?, category: Category?, searchText: String?) {
        this.pagination = pagination
        this.category = category
        this.searchText = searchText
    }


}
