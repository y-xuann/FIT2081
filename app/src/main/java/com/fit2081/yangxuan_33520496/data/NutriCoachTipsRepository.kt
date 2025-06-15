package com.fit2081.yangxuan_33520496.data

import androidx.lifecycle.LiveData

class NutriCoachTipsRepository (private val dao: NutriCoachTipsDao){
    suspend fun insertTip(tip: NutriCoachTips) = dao.insertTip(tip)
    suspend fun getTipsByClinician(clinicianId: String): List<NutriCoachTips> =
        dao.getTipsByClinician(clinicianId)

}