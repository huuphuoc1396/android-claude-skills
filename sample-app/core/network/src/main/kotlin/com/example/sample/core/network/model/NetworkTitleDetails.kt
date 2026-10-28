package com.example.sample.core.network.model

import com.google.gson.annotations.SerializedName

/** `movie/{id}` or `tv/{id}`; movies fill [title], [runtime], [releaseDate]; TV fills the rest. */
data class NetworkTitleDetails(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("overview") val overview: String? = null,
    @SerializedName("vote_average") val voteAverage: Double = 0.0,
    @SerializedName("vote_count") val voteCount: Int = 0,
    @SerializedName("runtime") val runtime: Int? = null,
    @SerializedName("episode_run_time") val episodeRunTime: List<Int>? = null,
    @SerializedName("release_date") val releaseDate: String? = null,
    @SerializedName("first_air_date") val firstAirDate: String? = null,
    @SerializedName("origin_country") val originCountry: List<String>? = null,
    @SerializedName("genres") val genres: List<NetworkGenre>? = null,
    @SerializedName("credits") val credits: NetworkCredits? = null,
    @SerializedName("recommendations") val recommendations: NetworkPage? = null,
    @SerializedName("release_dates") val releaseDates: NetworkReleaseDates? = null,
    @SerializedName("videos") val videos: NetworkVideos? = null,
)

data class NetworkGenre(
    @SerializedName("name") val name: String,
)

data class NetworkCredits(
    @SerializedName("cast") val cast: List<NetworkCast>,
)

data class NetworkCast(
    @SerializedName("name") val name: String,
    @SerializedName("profile_path") val profilePath: String? = null,
    @SerializedName("order") val order: Int = 0,
)

data class NetworkReleaseDates(
    @SerializedName("results") val results: List<NetworkCountryReleases>,
)

data class NetworkCountryReleases(
    @SerializedName("iso_3166_1") val country: String,
    @SerializedName("release_dates") val releaseDates: List<NetworkReleaseDate>,
)

data class NetworkVideos(
    @SerializedName("results") val results: List<NetworkVideo>,
)

/** A trailer, teaser, or clip; [key] is the id on [site], e.g. a YouTube video id. */
data class NetworkVideo(
    @SerializedName("key") val key: String,
    @SerializedName("site") val site: String,
    @SerializedName("type") val type: String,
    @SerializedName("official") val official: Boolean = false,
)

/** [type] 3 is a theatrical release. */
data class NetworkReleaseDate(
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("type") val type: Int,
)
