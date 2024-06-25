package com.example.simper

import com.google.gson.Gson


data class License(
    val name: String,
    val url: String
)

data class Phonetic(
    val text: String,
    val audio: String,
    val sourceUrl: String,
    val license: License
)

data class Definition(
    val definition: String,
    val example: String?,
    val synonyms: List<String>,
    val antonyms: List<String>
)

data class Meaning(
    val partOfSpeech: String,
    val definitions: List<Definition>,
    val synonyms: List<String>,
    val antonyms: List<String>
)

data class WordDefinition(
    val word: String,
    val phonetics: List<Phonetic>,
    val meanings: List<Meaning>,
    val license: License,
    val sourceUrls: List<String>
)
