package com.example.kp_luxurylife_part2

import kotlinx.coroutines.delay

object FakeApiService {

    private val fakeDestinations = listOf(
        Destination("Москва", "Россия", "Красная площадь, Кремль, музеи"),
        Destination("Париж", "Франция", "Эйфелева башня, Лувр, Нотр-Дам"),
        Destination("Нью-Йорк", "США", "Таймс-сквер, Центральный парк, Статуя Свободы"),
        Destination("Лондон", "Великобритания", "Биг-Бен, Букингемский дворец, Тауэр"),
        Destination("Дубай", "ОАЭ", "Бурдж-Халифа, Дубай-Молл, Пальма Джумейра"),
        Destination("Токио", "Япония", "Синдзюку, Сибуя, храмы, аниме-культура"),
        Destination("Берлин", "Германия", "Бранденбургские ворота, Берлинская стена, музеи"),
        Destination("Рим", "Италия", "Колизей, Пантеон, Ватикан"),
        Destination("Барселона", "Испания", "Саграда Фамилия, парк Гуэля, пляжи"),
        Destination("Амстердам", "Нидерланды", "Каналы, музеи, велосипеды"),
    )

    suspend fun searchDestinations(query: String): List<Destination> {
        delay(2000) // Эмуляция сетевого запроса (2 секунда)
        return fakeDestinations.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.country.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }
    }
}
