package com.pc.genzwardrobe.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.database.FirebaseDatabase
import com.pc.genzwardrobe.core.domain.UserReview
import kotlinx.coroutines.tasks.await

class UserReviewsPagingSource(
    private val productId: String,
    private val sortBy: String
): PagingSource<Int, Pair<String, UserReview>>() {
    override fun getRefreshKey(state: PagingState<Int, Pair<String, UserReview>>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pair<String, UserReview>> {

        val currentPage = params.key ?: 1
        val pageSize = params.loadSize

        return try {
            val query = FirebaseDatabase
                .getInstance()
                .getReference("Admin")
                .child("AllProducts")
                .child(productId)
                .child("userReviews")
                .limitToFirst(currentPage * pageSize )

            val snapshot = query.get().await()

            val allReviews = mutableListOf<Pair<String, UserReview>>()

            for (userIds in snapshot.children) {
                for (userReviews in userIds.children) {
                    val key = userReviews.key
                    val item = userReviews.getValue(UserReview::class.java)

                    if (key != null && item != null) {
                        allReviews.add(Pair(key, item))
                    }
                }
            }

            val startIndex = ( currentPage - 1 ) * pageSize

            val sortedUserReviews = allReviews.sortedWith(
                when(sortBy) {
                    "Latest" -> compareByDescending { it.second.timeStamp }
                    "Positive" -> compareByDescending { it.second.rating }
                    "Negative" -> compareBy { it.second.rating }
                    else -> compareBy { it.first }
                }
            ).drop(startIndex).take(pageSize)

            LoadResult.Page(
                data = sortedUserReviews,
                prevKey = if (currentPage > 1 ) currentPage - 1 else null,
                nextKey = if (sortedUserReviews.size == pageSize) currentPage + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}