package model

class Collection(
    val id: Int,
    val name: String,
    val tag: String,
    val card_number: Int
) {
    companion object {
        const val ID = "id"
        const val NAME = "name"
        const val TAG = "tag"
        const val CARDNUMBER = "card_number"
    }
}