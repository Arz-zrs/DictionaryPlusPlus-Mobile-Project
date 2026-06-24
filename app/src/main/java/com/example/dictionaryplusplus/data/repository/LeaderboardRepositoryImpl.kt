package com.example.dictionaryplusplus.data.repository

import com.example.dictionaryplusplus.domain.model.LeaderboardUser
import com.example.dictionaryplusplus.domain.repository.LeaderboardRepository
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): LeaderboardRepository {
    override fun observeLeaderboard(): Flow<List<LeaderboardUser>> = callbackFlow {
        val query = firestore.collection("leaderboard")
            .orderBy("total_score", Query.Direction.DESCENDING)
            .limit(50)

        val listener = query.addSnapshotListener { snapshots, exception ->
            if (exception != null) {
                close(exception)
                return@addSnapshotListener
            }
            val users = snapshots?.documents?.mapNotNull { document ->
                val uid = document.getString("uid") ?: return@mapNotNull null
                val name = document.getString("name") ?: "Anonymous"
                val score = document.getLong("total_score")?.toInt() ?: 0
                LeaderboardUser(uid, name, score)
            } ?: emptyList()
            trySend(users)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getTotalParticipantCount(): Result<Long> = try {
        val snapshot = firestore.collection("leaderboard")
            .count()
            .get(AggregateSource.SERVER)
            .await()
        Result.success(snapshot.count)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getUserRank(score: Int): Result<Int> = try {
        val snapshot = firestore.collection("leaderboard")
            .whereGreaterThan("total_score", score)
            .count()
            .get(AggregateSource.SERVER)
            .await()
        Result.success(snapshot.count.toInt() + 1)
    } catch (e: Exception) {
        Result.failure(e)
    }
}