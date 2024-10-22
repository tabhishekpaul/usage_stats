package dev.tabhishekpaul.usage_stats

import android.app.usage.ConfigurationStats
import android.app.usage.UsageStatsManager
import androidx.annotation.RequiresApi
import android.app.usage.UsageEvents
import android.content.Context
import android.os.Build

object UsageStats {

    /**
     * Queries events between a given time range.
     *
     * @param context The application context.
     * @param startDate The start timestamp for the query.
     * @param endDate The end timestamp for the query.
     * @return A list of maps containing event details.
     */
    fun queryEvents(context: Context, startDate: Long, endDate: Long): ArrayList<Map<String, String>> {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val events: UsageEvents = usm.queryEvents(startDate, endDate)
        val eventsList: ArrayList<Map<String, String>> = arrayListOf()

        while (events.hasNextEvent()) {
            val event = UsageEvents.Event()
            events.getNextEvent(event)

            val e = mutableMapOf(
                "eventType" to event.eventType.toString(),
                "timeStamp" to event.timeStamp.toString(),
                "packageName" to event.packageName.toString(),
                "className" to event.className
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                e["shortcutId"] = event.shortcutId
            }
            eventsList.add(e)
        }
        return eventsList
    }

    /**
     * Aggregates usage stats by package between a time range.
     *
     * @param context The application context.
     * @param startDate The start timestamp.
     * @param endDate The end timestamp.
     * @return A map where keys are package names and values are usage details.
     */
    fun queryAndAggregateUsageStats(context: Context, startDate: Long, endDate: Long): Map<String, Map<String, String>> {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val usageStats = usm.queryAndAggregateUsageStats(startDate, endDate)
        val usageList = mutableMapOf<String, Map<String, String>>()

        for (packageName in usageStats.keys) {
            val packageUsage = usageStats[packageName]
            usageList[packageName] = mapOf(
                "firstTimeStamp" to packageUsage?.firstTimeStamp.toString(),
                "lastTimeStamp" to packageUsage?.lastTimeStamp.toString(),
                "lastTimeUsed" to packageUsage?.lastTimeUsed.toString(),
                "packageName" to packageUsage?.packageName.toString(),
                "totalTimeInForeground" to packageUsage?.totalTimeInForeground.toString()
            )
        }
        return usageList
    }

    /**
     * Queries configuration stats between a time range.
     *
     * @param context The application context.
     * @param startDate The start timestamp.
     * @param endDate The end timestamp.
     * @return A list of configuration stats details.
     */
    fun queryConfig(context: Context, startDate: Long, endDate: Long): ArrayList<Map<String, String>> {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val configs: List<ConfigurationStats> = usm.queryConfigurations(
            UsageStatsManager.INTERVAL_BEST, startDate, endDate
        )
        val configList: ArrayList<Map<String, String>> = arrayListOf()

        for (config in configs) {
            val c = mapOf(
                "activationCount" to config.activationCount.toString(),
                "totalTimeActive" to config.totalTimeActive.toString(),
                "configuration" to config.configuration.toString(),
                "lastTimeActive" to config.lastTimeActive.toString(),
                "firstTimeStamp" to config.firstTimeStamp.toString(),
                "lastTimeStamp" to config.lastTimeStamp.toString()
            )
            configList.add(c)
        }
        return configList
    }

    /**
     * Queries usage stats for all apps between a time range.
     *
     * @param context The application context.
     * @param startDate The start timestamp.
     * @param endDate The end timestamp.
     * @return A list of usage stats per application.
     */
    fun queryUsageStats(context: Context, startDate: Long, endDate: Long): ArrayList<Map<String, String>> {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val usageStats = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, startDate, endDate)
        val usageList: ArrayList<Map<String, String>> = arrayListOf()

        for (usage in usageStats) {
            val u = mapOf(
                "firstTimeStamp" to usage.firstTimeStamp.toString(),
                "lastTimeStamp" to usage.lastTimeStamp.toString(),
                "lastTimeUsed" to usage.lastTimeUsed.toString(),
                "packageName" to usage.packageName.toString(),
                "totalTimeInForeground" to usage.totalTimeInForeground.toString()
            )
            usageList.add(u)
        }
        return usageList
    }

    /**
     * Queries event stats for all events between a time range.
     *
     * @param context The application context.
     * @param startDate The start timestamp.
     * @param endDate The end timestamp.
     * @return A list of event stats details.
     */
    @RequiresApi(Build.VERSION_CODES.P)
    fun queryEventStats(context: Context, startDate: Long, endDate: Long): ArrayList<Map<String, String>> {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val eventStats = usm.queryEventStats(UsageStatsManager.INTERVAL_BEST, startDate, endDate)
        val eventList: ArrayList<Map<String, String>> = arrayListOf()

        for (event in eventStats) {
            val u = mapOf(
                "firstTimeStamp" to event.firstTimeStamp.toString(),
                "lastTimeStamp" to event.lastTimeStamp.toString(),
                "totalTime" to event.totalTime.toString(),
                "lastEventTime" to event.lastEventTime.toString(),
                "eventType" to event.eventType.toString(),
                "count" to event.count.toString()
            )
            eventList.add(u)
        }
        return eventList
    }
}
