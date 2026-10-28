package com.example.sample.core.data.model

import com.example.sample.core.database.model.ArticleEntity
import com.example.sample.core.network.model.NetworkArticle
import java.time.Instant

internal fun NetworkArticle.asEntity() = ArticleEntity(
    id = id,
    title = title,
    summary = summary,
    url = url,
    newsSite = newsSite,
    publishedAt = publishedAt,
)

internal fun ArticleEntity.asExternalModel() = Article(
    id = id,
    title = title,
    summary = summary,
    url = url,
    newsSite = newsSite,
    publishedAt = Instant.parse(publishedAt),
)
