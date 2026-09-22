package com.example.data.repository

import android.util.Log
import com.example.model.UserReport
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

/**
 * Interface for reporting inappropriate content and behavior in Hodal.live,
 * storing moderation records in Cloud Firestore.
 */
interface IFirestoreReportRepository {
    suspend fun submitReport(report: UserReport): Result<Unit>
}

/**
 * Concrete implementation storing user and content reports under the 'reports'
 * collection in Cloud Firestore.
 */
class FirestoreReportRepository(
    private val firestore: FirebaseFirestore? = UserRepository.getInitializedFirestoreInstance()
) : IFirestoreReportRepository {

    companion object {
        private const val TAG = "FirestoreReportRepo"
        const val REPORTS_COLLECTION = "reports"
    }

    override suspend fun submitReport(report: UserReport): Result<Unit> {
        val db = firestore
        if (db == null) {
            Log.w(TAG, "Firestore instance not ready. Simulated local report submission.")
            return Result.success(Unit)
        }

        return try {
            val docRef = db.collection(REPORTS_COLLECTION).document(report.reportId)
            val data = mapOf(
                "reportId" to report.reportId,
                "reporterId" to report.reporterId,
                "reporterName" to report.reporterName,
                "reportedUserId" to report.reportedUserId,
                "reportedUserName" to report.reportedUserName,
                "targetType" to report.targetType.name,
                "targetContent" to (report.targetContent ?: ""),
                "reason" to report.reason,
                "additionalDetails" to report.additionalDetails,
                "timestamp" to report.timestamp,
                "serverTimestamp" to FieldValue.serverTimestamp(),
                "status" to report.status
            )
            docRef.set(data, SetOptions.merge()).await()
            Log.d(TAG, "Successfully persisted report ${report.reportId} in Firestore 'reports'")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to submit report to Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }
}
