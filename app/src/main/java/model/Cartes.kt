package model

class Cartes(
    val id: Int,
    val collection: String,
    val question: String,
    val reponse: String,
    val imageq: String,
    val imager: String
) {
    companion object {
        const val ID = "id"
        const val COLLECTION = "collection"
        const val QUESTION = "question"
        const val REPONSE = "reponse"
        const val IMAGEQ = "imagine_question"
        const val IMAGER = "imagine_reponse"
    }
}