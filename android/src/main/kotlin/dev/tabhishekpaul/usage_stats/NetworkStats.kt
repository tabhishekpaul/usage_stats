package dev.tabhishekpaul.usage_stats

import android.app.usage.NetworkStatsManager
import android.content.pm.ApplicationInfo
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import android.net.NetworkCapabilities
import android.annotation.SuppressLint
import android.app.usage.NetworkStats
import android.content.Context
import android.os.Build

// Enum to define network types.
private enum class NetworkType {
    All,
    WiFi,
    Mobile,
}

// Data class to store network stats (received and transmitted bytes).
private data class AppNetworkStats(
    val rxTotalBytes: Long,
    val txTotalBytes: Long
)

// Main object to provide functions related to network usage stats
object NetworkStats {

    /**
     * Resolves the network type from an integer input.
     *
     * @param type Integer representing the network type.
     * @return The corresponding NetworkType enum value.
     */
    private fun resolveNetworkType(type: Int): NetworkType {
        return when (type) {
            1 -> NetworkType.All
            2 -> NetworkType.WiFi
            3 -> NetworkType.Mobile
            else -> NetworkType.All
        }
    }

    /**
     * Queries network usage stats for a specific application package in the given time range.
     *
     * @param context The application context.
     * @param startDate The start timestamp for the query.
     * @param endDate The end timestamp for the query.
     * @param type The type of network (All, WiFi, Mobile).
     * @param packageName The package name of the specific application.
     * @return A map containing the network usage details for the given package.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun queryNetworkUsageStatsByPackage(
        context: Context,
        startDate: Long,
        endDate: Long,
        type: Int,
        packageName: String
    ): Map<String, String> {
        val networkType = resolveNetworkType(type)
        val networkStatsManager = context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
        val subscriberID = getSubscriberId(context)
        val appInfo = context.packageManager.getApplicationInfo(packageName, 0)

        val totalAppSummary = appInfo.getNetworkSummary(
            networkStatsManager, startDate, endDate, networkType, subscriberID
        )

        return mapOf(
            "packageName" to appInfo.packageName,
            "rxTotalBytes" to totalAppSummary.rxTotalBytes.toString(),
            "txTotalBytes" to totalAppSummary.txTotalBytes.toString()
        )
    }

    /**
     * Fetches network usage summary for an application based on network type.
     *
     * @param networkStatsManager The system's NetworkStatsManager instance.
     * @param startDate Start timestamp for the data range.
     * @param endDate End timestamp for the data range.
     * @param networkType Type of network (WiFi, Mobile, All).
     * @param subscriberID Subscriber ID (required for mobile data).
     * @return An AppNetworkStats object containing received and transmitted bytes.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun ApplicationInfo.getNetworkSummary(
        networkStatsManager: NetworkStatsManager,
        startDate: Long,
        endDate: Long,
        networkType: NetworkType,
        subscriberID: String? = null
    ): AppNetworkStats {
        return when (networkType) {
            NetworkType.Mobile -> getNetworkSummary(
                NetworkCapabilities.TRANSPORT_CELLULAR, networkStatsManager, startDate, endDate, subscriberID
            )
            NetworkType.WiFi -> getNetworkSummary(
                NetworkCapabilities.TRANSPORT_WIFI, networkStatsManager, startDate, endDate
            )
            NetworkType.All -> {
                val wifiStats = getNetworkSummary(
                    NetworkCapabilities.TRANSPORT_WIFI, networkStatsManager, startDate, endDate
                )
                val mobileStats = getNetworkSummary(
                    NetworkCapabilities.TRANSPORT_CELLULAR, networkStatsManager, startDate, endDate, subscriberID
                )
                AppNetworkStats(
                    rxTotalBytes = wifiStats.rxTotalBytes + mobileStats.rxTotalBytes,
                    txTotalBytes = wifiStats.txTotalBytes + mobileStats.txTotalBytes
                )
            }
        }
    }

    /**
     * Retrieves the subscriber ID (used for mobile network stats).
     * This method is only available on Android versions below Q.
     *
     * @param context The application context.
     * @return Subscriber ID or null if unavailable.
     */
    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getSubscriberId(context: Context): String? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) null
            else (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?)
                ?.subscriberId
        } catch (e: Exception) {
            ""  // Return empty string if any error occurs.
        }
    }

    /**
     * Queries network usage stats for all installed applications in the given time range.
     *
     * @param context The application context to access system services and package manager.
     * @param startDate The start timestamp for the query.
     * @param endDate The end timestamp for the query.
     * @param type The type of network (All, WiFi, Mobile).
     * @return A list of network stats per application with package name and data usage.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun queryNetworkUsageStats(
        context: Context,
        startDate: Long,
        endDate: Long,
        type: Int
    ): List<Map<String, String>> {
        val networkType = resolveNetworkType(type)
        val networkStatsManager = context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
        val subscriberID = getSubscriberId(context)

        val installedApplications = context.packageManager.getInstalledApplications(0)

        return installedApplications.map { appInfo ->
            val totalAppSummary = appInfo.getNetworkSummary(
                networkStatsManager, startDate, endDate, networkType, subscriberID
            )
            mapOf(
                "packageName" to appInfo.packageName,
                "rxTotalBytes" to totalAppSummary.rxTotalBytes.toString(),
                "txTotalBytes" to totalAppSummary.txTotalBytes.toString()
            )
        }
    }

    /**
     * Fetches network summary based on the network type (WiFi/Mobile).
     *
     * @param networkType The transport type (WiFi/Mobile).
     * @param networkStatsManager NetworkStatsManager instance.
     * @param startDate Start timestamp.
     * @param endDate End timestamp.
     * @param subscriberID Subscriber ID for mobile networks.
     * @return An AppNetworkStats object containing received and transmitted bytes.
     */
    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun ApplicationInfo.getNetworkSummary(
        networkType: Int,
        networkStatsManager: NetworkStatsManager,
        startDate: Long,
        endDate: Long,
        subscriberID: String? = null
    ): AppNetworkStats {
        return try {
            val queryDetails = networkStatsManager.queryDetailsForUid(
                networkType, subscriberID, startDate, endDate, uid
            )
            val bucket = NetworkStats.Bucket()
            var rxTotal = 0L
            var txTotal = 0L

            while (queryDetails.hasNextBucket()) {
                queryDetails.getNextBucket(bucket)
                rxTotal += bucket.rxBytes
                txTotal += bucket.txBytes
            }
            AppNetworkStats(rxTotal, txTotal)
        } catch (e: Exception) {
            AppNetworkStats(0, 0)  // Return zero stats on failure.
        }
    }
}
