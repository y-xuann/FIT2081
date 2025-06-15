package com.fit2081.yangxuan_33520496.data;

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patient_table")
data class Patient(
    @PrimaryKey
    val userId: String,
    val name: String?,
    val password: String?,
    val phoneNumber: String,
    val sex: String,

    val heifaTotalScoreMale: Double,
    val heifaTotalScoreFemale: Double,

    val discretionaryHeifaScoreMale: Double,
    val discretionaryHeifaScoreFemale: Double,

    val vegetablesHeifaScoreMale: Double,
    val vegetablesHeifaScoreFemale: Double,

    val fruitHeifaScoreMale: Double,
    val fruitHeifaScoreFemale: Double,

    val fruitServeSize: Double,
    val fruitVariationScore: Double,

    val grainsAndCerealsHeifaScoreMale: Double,
    val grainsAndCerealsHeifaScoreFemale: Double,

    val wholegrainsHeifaScoreMale: Double,
    val wholegrainsHeifaScoreFemale: Double,

    val meatAndAlternativesHeifaScoreMale: Double,
    val meatAndAlternativesHeifaScoreFemale: Double,

    val dairyAndAlternativesHeifaScoreMale: Double,
    val dairyAndAlternativesHeifaScoreFemale: Double,

    val sodiumHeifaScoreMale: Double,
    val sodiumHeifaScoreFemale: Double,

    val alcoholHeifaScoreMale: Double,
    val alcoholHeifaScoreFemale: Double,

    val waterHeifaScoreMale: Double,
    val waterHeifaScoreFemale: Double,

    val sugarHeifaScoreMale: Double,
    val sugarHeifaScoreFemale: Double,

    val saturatedFatHeifaScoreMale: Double,
    val saturatedFatHeifaScoreFemale: Double,

    val unsaturatedFatHeifaScoreMale: Double,
    val unsaturatedFatHeifaScoreFemale: Double,
)

data class GenderScores(
    val heifaTotalScore: Double,
    val discretionaryHeifaScore: Double,
    val vegetablesHeifaScore: Double,
    val fruitHeifaScore: Double,
    val grainsAndCerealsHeifaScore: Double,
    val wholegrainsHeifaScore: Double,
    val meatAndAlternativesHeifaScore: Double,
    val dairyAndAlternativesHeifaScore: Double,
    val sodiumHeifaScore: Double,
    val alcoholHeifaScore: Double,
    val waterHeifaScore: Double,
    val sugarHeifaScore: Double,
    val saturatedFatHeifaScore: Double,
    val unsaturatedFatHeifaScore: Double
)