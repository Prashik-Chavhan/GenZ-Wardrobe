package com.pc.genzwardrobeadmin.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebasePagingSource @Inject constructor(
    val database: FirebaseDatabase
) : PagingSource<Int, Triple<String, String, List<String>>>() {
    override fun getRefreshKey(state: PagingState<Int, Triple<String, String, List<String>>>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Triple<String, String, List<String>>> {
        val currentPage = params.key ?: 1
        val pageSize = params.loadSize
        val startIndex = (currentPage - 1) * pageSize

        return try {
            val query = database
                .getReference("Admin")
                .child("OrderedProducts")
                .limitToFirst(currentPage * pageSize)

            val snapshot = query.get().await()

            val list = mutableListOf<Triple<String, String, List<String>>>()

            for (userSnapshot in snapshot.children) {
                val userId = userSnapshot.key

                val orderIds = mutableListOf<String>()

                var userName: String? = null

                for (orderSnapshot in userSnapshot.children) {
                    val orderId = orderSnapshot.key

                    if (orderId != null) {
                        orderIds.add(orderId)
                    }

                    if (userName == null) {
                        userName = orderSnapshot.child("userAddress/name").value as String
                    }
                }
                if (userName != null && userId != null) {
                    list.add(Triple(userName, userId, orderIds))
                }
            }

            val hasNexKey = snapshot.childrenCount >= pageSize

            LoadResult.Page(
                data = list.drop(startIndex).take(pageSize),
                prevKey = if (currentPage > 1) currentPage - 1 else null,
                nextKey = if (hasNexKey) currentPage + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}