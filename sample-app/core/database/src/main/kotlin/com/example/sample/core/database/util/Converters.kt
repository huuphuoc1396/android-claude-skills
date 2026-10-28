package com.example.sample.core.database.util

import androidx.room.TypeConverter
import com.example.sample.core.database.model.CachedCastMember
import com.example.sample.core.database.model.CachedTitle
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class Converters {
    @TypeConverter
    fun castToJson(cast: List<CachedCastMember>): String = Json.encodeToString(cast)

    @TypeConverter
    fun jsonToCast(json: String): List<CachedCastMember> = Json.decodeFromString(json)

    @TypeConverter
    fun titlesToJson(titles: List<CachedTitle>): String = Json.encodeToString(titles)

    @TypeConverter
    fun jsonToTitles(json: String): List<CachedTitle> = Json.decodeFromString(json)

    @TypeConverter
    fun stringsToJson(strings: List<String>): String = Json.encodeToString(strings)

    @TypeConverter
    fun jsonToStrings(json: String): List<String> = Json.decodeFromString(json)
}
