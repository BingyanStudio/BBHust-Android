package com.bingyan.bbhust.utils

import com.bingyan.bbhust.CategoryPostQuery
import com.bingyan.bbhust.FollowingPostQuery
import com.bingyan.bbhust.HotPostsQuery
import com.bingyan.bbhust.PostQuery
import com.bingyan.bbhust.PostsQuery
import com.bingyan.bbhust.RecommendPostsQuery
import com.bingyan.bbhust.SearchQuery
import com.bingyan.bbhust.UserLikedPostQuery
import com.bingyan.bbhust.UserMarkedPostQuery
import com.bingyan.bbhust.UserPostQuery

val PostQuery.Post.toPost
    get() = PostsQuery.Post(
        id,
        PostsQuery.Author(
            author.id, author.username, author.avatar
        ),
        time,
        reply_time,
        title,
        digest,
        images,
        hot_value,
        category.map { PostsQuery.Category(it.id, it.name) },
        like_count,
        reply_count,
        liked,
        favorite
    )


val HotPostsQuery.Post.toPost
    get() = PostsQuery.Post(
        id,
        PostsQuery.Author(
            author.id, author.username, author.avatar
        ),
        time,
        reply_time,
        title,
        digest,
        images,
        hot_value,
        category.map { PostsQuery.Category(it.id, it.name) },
        like_count,
        reply_count,
        liked,
        favorite
    )

val RecommendPostsQuery.Post.toPost
    get() = PostsQuery.Post(
        id,
        PostsQuery.Author(
            author.id, author.username, author.avatar
        ),
        time,
        reply_time,
        title,
        digest,
        images,
        hot_value,
        category.map { PostsQuery.Category(it.id, it.name) },
        like_count,
        reply_count,
        liked,
        favorite
    )

val FollowingPostQuery.Post.toPost
    get() = PostsQuery.Post(
        id,
        PostsQuery.Author(
            author.id, author.username, author.avatar
        ),
        time,
        reply_time,
        title,
        digest,
        images,
        hot_value,
        category.map { PostsQuery.Category(it.id, it.name) },
        like_count,
        reply_count,
        liked,
        favorite
    )


val UserPostQuery.Node.toPost
    get() = PostsQuery.Post(
        id,
        PostsQuery.Author(
            author.id, author.username, author.avatar
        ),
        time,
        reply_time,
        title,
        digest,
        images,
        hot_value,
        category.map { PostsQuery.Category(it.id, it.name) },
        like_count,
        reply_count,
        liked,
        favorite
    )

val UserLikedPostQuery.Node.toPost
    get() = PostsQuery.Post(
        id,
        PostsQuery.Author(
            author.id, author.username, author.avatar
        ),
        time,
        reply_time,
        title,
        digest,
        images,
        hot_value,
        category.map { PostsQuery.Category(it.id, it.name) },
        like_count,
        reply_count,
        liked,
        favorite
    )
val UserMarkedPostQuery.Node.toPost
    get() = PostsQuery.Post(
        id,
        PostsQuery.Author(
            author.id, author.username, author.avatar
        ),
        time,
        reply_time,
        title,
        digest,
        images,
        hot_value,
        category.map { PostsQuery.Category(it.id, it.name) },
        like_count,
        reply_count,
        liked,
        favorite
    )

val SearchQuery.OnPost.toPost
    get() = PostsQuery.Post(
        id,
        PostsQuery.Author(
            author.id, author.username, author.avatar
        ),
        time,
        reply_time,
        title,
        digest,
        images,
        hot_value,
        category.map { PostsQuery.Category(it.id, it.name) },
        like_count,
        reply_count,
        liked,
        favorite
    )

val CategoryPostQuery.Post.toPost
    get() = PostsQuery.Post(
        id,
        PostsQuery.Author(
            author.id, author.username, author.avatar
        ),
        time, reply_time, title, digest, images,
        hot_value,
        category.map { PostsQuery.Category(it.id, it.name) },
        like_count, reply_count, liked, favorite
    )