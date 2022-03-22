package com.jhon.mvvmlearning.model

class QuoteProvider {
    companion object {
        fun random() : QuoteModel{
            val position = (0..3).random()
            return quote[position]
        }

        private val quote = listOf(
            QuoteModel(
                quote = "Los ordenadores son inútiles. Sólo pueden darte respuestas",
                author = "Pablo Picasso"
            ),
            QuoteModel(
                quote = "Los ordenadores son como los bikinis. Ahorran a la gente el hacer muchas conjeturas",
                author = "Sam Ewing"
            ),
            QuoteModel(
                quote = "Tienen ordenadores, y pueden tener otras armas de destrucción masiva",
                author = "Janet Reno"
            ),
            QuoteModel(
                quote = "Es genial trabajar con ordenadores. No discuten, lo recuerdan todo y no se beben tu cerveza",
                author = "Paul Leary"
            ),
        )
    }
}