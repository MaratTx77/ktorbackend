package com.example.models

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.SizedCollection
import org.joda.time.DateTime

// Reflection to db table
object Vocabularies: IntIdTable("vocabularies") {
    val name = varchar("name", 255)
    val version = varchar("version", 20).default("1.0")
    val complexity = double("complexity").default(.0)
    val countOfWords = integer("countofwords").default(0)
    val description = text("description")
    val created = datetime("created").default(DateTime.now())
    val lastModifiedBy = datetime("lastmodifiedby").default(DateTime.now())
}

object Words: IntIdTable("words") {
    val english = varchar("english", 255).uniqueIndex()
    val russian = text("russian") // variances of translate separated by ";"
    val partOfSpeech = enumerationByName("partofspeech",20, PartOfSpeech::class)
    val url = text("url")
}

// table for Many-to-many relation
object VocabularyWords: IntIdTable("vocabulary_words") {
    val vocabulary = reference("vocabulary", Vocabularies)
    val word = reference("word", Words)
}

// Dao for vocabularies table
class Vocabulary(id: EntityID<Int>) : IntEntity(id) {
    companion object: IntEntityClass<Vocabulary>(Vocabularies) {
        // static methods
        fun addWord(newWord: Word, vocabulary: Vocabulary) {
            val z = vocabulary.words.toMutableList()
            z.add(newWord)
            vocabulary.words = SizedCollection(z)
        }
    }
    var name by Vocabularies.name
    var version by Vocabularies.version
    var complexity by Vocabularies.complexity
    var countOfWords by Vocabularies.countOfWords
    var description by Vocabularies.description
    var created by Vocabularies.created
    var lastModifiedBy by Vocabularies.lastModifiedBy

    var words by Word via VocabularyWords
}

class Word(id: EntityID<Int>) : IntEntity(id) {
    companion object: IntEntityClass<Word>(Words)
    var english by Words.english
    var russian by Words.russian
    var partOfSpeech by Words.partOfSpeech
}

class VocabularyWord(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<VocabularyWord>(VocabularyWords)
    var vocabulary = VocabularyWords.vocabulary
    var word = VocabularyWords.word
}

enum class PartOfSpeech { VERB, NOUN, ADJECTIVE, PRONOUN, NUMERAL, ADVERB}
