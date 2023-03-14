package com.example.ocr_demo

data class Record(var minTransform: Int, var numOfCorrectChar: Int)

class AccuracyBenchMarker {

    fun minimumTransformOperation(original: String, target: String): Record {
        val table = MutableList<List<Record>>(original.length + 1) {
            MutableList(target.length + 1) { Record(0, 0) }
        }
        for (i in 0..target.length) {
            table[0][i].minTransform = i
        }
        for (i in 0..original.length) {
            table[i][0].minTransform = i
        }
        for (i in 1..original.length) {
            for (j in 1..target.length) {
                if (original[i - 1] == target[j - 1]) {
                    table[i][j].minTransform = table[i - 1][j - 1].minTransform
                    table[i][j].numOfCorrectChar = table[i - 1][j - 1].numOfCorrectChar + 1
                } else {
                    val minDisRecord = minOf(
                        table[i - 1][j - 1],
                        table[i][j - 1],
                        table[i - 1][j]
                    ) { r1, r2 ->
                        val sigh = r1.minTransform - r2.minTransform
                        if (sigh != 0) sigh else r2.numOfCorrectChar - r1.numOfCorrectChar
                    }
                    table[i][j].minTransform = minDisRecord.minTransform + 1
                    table[i][j].numOfCorrectChar = minDisRecord.numOfCorrectChar
                }
            }
        }
        return table[original.length][target.length]
    }

    /**
     *  the character error rate is calculated base on the formula in this article
     *  https://towardsdatascience.com/evaluating-ocr-output-quality-with-character-error-rate-cer-and-word-error-rate-wer-853175297510
     * */
    fun calculateCER(original: String, target: String): Double {
        val record = minimumTransformOperation(original, target)
        return 100.0 * record.minTransform.toDouble() / (record.minTransform.toDouble() + record.numOfCorrectChar.toDouble())
    }
}

fun String.formatText(): String {
    return this.replace(Regex("\\s+"), " ").trim().lowercase()
}