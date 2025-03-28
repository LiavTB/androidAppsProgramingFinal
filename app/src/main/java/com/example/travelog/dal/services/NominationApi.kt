package com.example.travelog.dal.services

object NominationApi {

    fun autoCompleteName(name: String): List<String> {
        return listOf("New York", "New Jersey", "New Hampshire", "New Mexico", "New Orleans")
    }


}