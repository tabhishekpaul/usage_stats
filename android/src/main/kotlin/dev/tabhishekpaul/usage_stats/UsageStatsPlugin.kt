package dev.tabhishekpaul.usage_stats
// Package declaration for organizing the UsageStatsPlugin class within the specified namespace.

import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.PluginRegistry.Registrar
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.withContext
import androidx.annotation.NonNull
import kotlinx.coroutines.launch
import android.content.Context
import android.os.Build
// Imports necessary libraries for Flutter integration, coroutine handling, and Android functionality.

public class UsageStatsPlugin : FlutterPlugin, MethodCallHandler {
    // The main class for the plugin that implements FlutterPlugin for engine integration
    // and MethodCallHandler to manage method calls from the Flutter side.

    private lateinit var channel: MethodChannel
    // A MethodChannel for communicating with Dart. It will allow method calls and results to be exchanged
    // between the Flutter app and the native Android code.

    private var mContext: Context? = null
    // A nullable Context variable to hold the application context, which is required for accessing
    // system services like usage statistics.

    companion object {
        // Companion object to define static methods that can be accessed without an instance of the class.
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            // This method is used to register the plugin with the Flutter engine, especially useful for older
            // Flutter versions that use the Registrar method.
            val channel = MethodChannel(registrar.messenger(), "android_usage_stats")
            // Creates a new MethodChannel with a specific name, which will be used to communicate with the Flutter app.

            var plugin = UsageStatsPlugin()
            // Creates an instance of the UsageStatsPlugin class.
            plugin.setContext(registrar.context())
            // Initializes the plugin's context using the application context provided by the registrar.
            channel.setMethodCallHandler(plugin)
            // Sets the instance of the plugin as the MethodCallHandler for the channel, enabling it to handle
            // method calls from Dart.
        }
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        // This method is called when the plugin is attached to the Flutter engine. It sets up the channel
        // for method calls.
        channel = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "android_usage_stats")
        // Initializes the MethodChannel for communication with the Flutter app, specifically using the Dart executor.

        channel.setMethodCallHandler(this)
        // Registers the current instance as the handler for incoming method calls.
        setContext(flutterPluginBinding.applicationContext)
        // Sets the application context from the Flutter binding, which is essential for accessing system resources.
    }


    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        // This method is called when the plugin is detached to the Flutter engine. It release the channel
        // for method calls.
        channel.setMethodCallHandler(null)
    }

    private fun setContext(context: Context) {
        // Helper method to set the context for the plugin, allowing other methods to use it.
        this.mContext = context
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        // This method handles incoming method calls from the Flutter side. It uses a when statement
        // to determine which method was called and respond accordingly.
        when (call.method) {
            "queryEvents" -> {
                // Handles the "queryEvents" method call to retrieve usage events within a specified timeframe.
                var start: Long = call.argument<Long>("start") as Long
                // Extracts the start timestamp from the method arguments.
                var end: Long = call.argument<Long>("end") as Long
                // Extracts the end timestamp from the method arguments.
                result.success(UsageStats.queryEvents(mContext!!, start, end))
                // Calls the UsageStats utility to fetch events and sends the result back to Flutter.
            }
            "isPermissionGranted" -> {
                // Handles the "isPermissionGranted" method call to check if the necessary permissions are granted.
                result.success(Utils.isPermissionGranted(mContext!!))
                // Uses the Utils class to check for permissions and returns the result.
            }
            "requestPermission" -> {
                // Handles the "requestPermission" method call to request usage stats permission from the user.
                Utils.requestPermission(mContext!!)
                // Calls a method in the Utils class to initiate the permission request process.
            }
            "queryConfiguration" -> {
                // Handles the "queryConfiguration" method call to retrieve configuration data within a timeframe.
                var start: Long = call.argument<Long>("start") as Long
                // Extracts the start timestamp from the method arguments.
                var end: Long = call.argument<Long>("end") as Long
                // Extracts the end timestamp from the method arguments.
                result.success(UsageStats.queryConfig(mContext!!, start, end))
                // Calls the UsageStats utility to fetch configuration data and sends the result back to Flutter.
            }
            "queryEventStats" -> {
                // Handles the "queryEventStats" method call for querying event statistics, available only on API level 28 and above.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    var start: Long = call.argument<Long>("start") as Long
                    // Extracts the start timestamp from the method arguments.
                    var end: Long = call.argument<Long>("end") as Long
                    // Extracts the end timestamp from the method arguments.
                    result.success(UsageStats.queryEventStats(mContext!!, start, end))
                    // Calls the UsageStats utility to fetch event statistics and sends the result back to Flutter.
                } else {
                    result.error("API Error",
                        "Requires API Level 28",
                        "Target should be set to 28 to use this API"
                    )
                    // Returns an error if the API level is less than 28.
                }
            }
            "queryAndAggregateUsageStats" -> {
                // Handles the "queryAndAggregateUsageStats" method call for aggregated usage statistics.
                var start: Long = call.argument<Long>("start") as Long
                // Extracts the start timestamp from the method arguments.
                var end: Long = call.argument<Long>("end") as Long
                // Extracts the end timestamp from the method arguments.
                result.success(UsageStats.queryAndAggregateUsageStats(mContext!!, start, end))
                // Calls the UsageStats utility to fetch aggregated usage statistics and sends the result back.
            }
            "queryUsageStats" -> {
                // Handles the "queryUsageStats" method call to retrieve raw usage statistics.
                var start: Long = call.argument<Long>("start") as Long
                // Extracts the start timestamp from the method arguments.
                var end: Long = call.argument<Long>("end") as Long
                // Extracts the end timestamp from the method arguments.
                result.success(UsageStats.queryUsageStats(mContext!!, start, end))
                // Calls the UsageStats utility to fetch usage statistics and sends the result back.
            }
            "queryNetworkUsageStats" -> {
                // Handles the "queryNetworkUsageStats" method call to fetch network usage statistics.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val start: Long = call.argument<Long>("start") as Long
                    // Extracts the start timestamp from the method arguments.
                    val end: Long = call.argument<Long>("end") as Long
                    // Extracts the end timestamp from the method arguments.
                    val type: Int = call.argument<Int>("type") as Int
                    // Extracts the network type from the method arguments.

                    GlobalScope.launch(Dispatchers.Main) {
                        // Launches a coroutine on the main thread to avoid blocking the UI.
                        val netResult = withContext(Dispatchers.IO) {
                            // Switches to IO context for performing network operations.
                            NetworkStats.queryNetworkUsageStats(
                                context = mContext!!,
                                startDate = start,
                                endDate = end,
                                type = type
                            )
                            // Calls the NetworkStats utility to query network usage statistics.
                        }
                        result.success(netResult)
                        // Sends the network usage statistics result back to Flutter.
                    }
                } else {
                    result.error(
                        "API Error",
                        "Requires API Level 23",
                        "Target should be set to 23 to use this API"
                    )
                    // Returns an error if the API level is less than 23.
                }
            }
            "queryNetworkUsageStatsByPackage" -> {
                // Handles the "queryNetworkUsageStatsByPackage" method call to fetch network usage statistics by package name.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val start: Long = call.argument<Long>("start") as Long
                    // Extracts the start timestamp from the method arguments.
                    val end: Long = call.argument<Long>("end") as Long
                    // Extracts the end timestamp from the method arguments.
                    val type: Int = call.argument<Int>("type") as Int
                    // Extracts the network type from the method arguments.
                    val packageName: String = call.argument<String>("packageName") as String
                    // Extracts the package name from the method arguments.

                    GlobalScope.launch(Dispatchers.Main) {
                        // Launches a coroutine on the main thread for querying network statistics.
                        val netResult = withContext(Dispatchers.IO) {
                            // Switches to IO context for performing network operations.
                            NetworkStats.queryNetworkUsageStatsByPackage(
                                context = mContext!!,
                                startDate = start,
                                endDate = end,
                                type = type,
                                packageName = packageName
                            )
                            // Calls the NetworkStats utility to query network usage statistics by package name.
                        }
                        result.success(netResult)
                        // Sends the network usage statistics by package name result back to Flutter.
                    }
                } else {
                    result.error(
                        "API Error",
                        "Requires API Level 23",
                        "Target should be set to 23 to use this API"
                    )
                    // Returns an error if the API level is less than 23.
                }
            }
            else -> {
                result.notImplemented()
                // If the method called does not match any of the expected methods, return that the method is not implemented.
            }
        }
    }
}
