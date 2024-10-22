import 'package:android_usage_stats/models/configuration_info.dart';
import 'package:android_usage_stats/models/network_info.dart';
import 'package:android_usage_stats/models/event_info.dart';
import 'package:android_usage_stats/models/usage_info.dart';
import 'package:flutter/services.dart';
import 'models/event_usage_info.dart';
import 'dart:async';

class AndroidUsageStats {
  // Private constructor to prevent instantiation of this class.
  AndroidUsageStats._();

  // MethodChannel for communicating with native Android code.
  static const MethodChannel _channel = MethodChannel('android_usage_stats');

  /// Checks if the app has permission to access usage stats.
  ///
  /// Returns a Future that resolves to a boolean indicating permission status.
  static Future<bool?> isPermissionGranted() async {
    bool? isPermission = await _channel.invokeMethod('isPermissionGranted');
    return isPermission;
  }

  /// Requests permission to access usage stats from the user.
  ///
  /// This method does not return a value.
  static Future<void> requestPermission() async {
    await _channel.invokeMethod('requestPermission');
  }

  /// Queries events within a specified date range.
  ///
  /// Takes start and end dates as parameters and returns a Future that resolves
  /// to a list of EventUsageInfo objects.
  static Future<List<EventUsageInfo>> queryEvents(
      DateTime startDate, DateTime endDate) async {
    // Convert start and end dates to milliseconds since epoch.
    int end = endDate.millisecondsSinceEpoch;
    int start = startDate.millisecondsSinceEpoch;

    // Prepare a map of the start and end times to send to the native method.
    Map<String, int> interval = {'start': start, 'end': end};

    // Call the native method and await the result.
    List events = await _channel.invokeMethod('queryEvents', interval);

    // Map the result to a list of EventUsageInfo objects.
    List<EventUsageInfo> result =
        events.map((item) => EventUsageInfo.fromMap(item)).toList();
    return result;
  }

  /// Queries configuration changes within a specified date range.
  ///
  /// Takes start and end dates as parameters and returns a Future that resolves
  /// to a list of ConfigurationInfo objects.
  static Future<List<ConfigurationInfo>> queryConfiguration(
      DateTime startDate, DateTime endDate) async {
    // Convert start and end dates to milliseconds since epoch.
    int end = endDate.millisecondsSinceEpoch;
    int start = startDate.millisecondsSinceEpoch;

    // Prepare a map of the start and end times to send to the native method.
    Map<String, int> interval = {'start': start, 'end': end};

    // Call the native method and await the result.
    List configs = await _channel.invokeMethod('queryConfiguration', interval);

    // Map the result to a list of ConfigurationInfo objects.
    List<ConfigurationInfo> result =
        configs.map((item) => ConfigurationInfo.fromMap(item)).toList();
    return result;
  }

  /// Queries event statistics within a specified date range.
  ///
  /// Takes start and end dates as parameters and returns a Future that resolves
  /// to a list of EventInfo objects.
  static Future<List<EventInfo>> queryEventStats(
      DateTime startDate, DateTime endDate) async {
    // Convert start and end dates to milliseconds since epoch.
    int end = endDate.millisecondsSinceEpoch;
    int start = startDate.millisecondsSinceEpoch;

    // Prepare a map of the start and end times to send to the native method.
    Map<String, int> interval = {'start': start, 'end': end};

    // Call the native method and await the result.
    List eventsStats = await _channel.invokeMethod('queryEventStats', interval);

    // Map the result to a list of EventInfo objects.
    List<EventInfo> result =
        eventsStats.map((item) => EventInfo.fromMap(item)).toList();
    return result;
  }

  /// Queries usage statistics within a specified date range.
  ///
  /// Takes start and end dates as parameters and returns a Future that resolves
  /// to a list of UsageInfo objects.
  static Future<List<UsageInfo>> queryUsageStats(
      DateTime startDate, DateTime endDate) async {
    // Convert start and end dates to milliseconds since epoch.
    int end = endDate.millisecondsSinceEpoch;
    int start = startDate.millisecondsSinceEpoch;

    // Prepare a map of the start and end times to send to the native method.
    Map<String, int> interval = {'start': start, 'end': end};

    // Call the native method and await the result.
    List usageStats = await _channel.invokeMethod('queryUsageStats', interval);

    // Map the result to a list of UsageInfo objects.
    List<UsageInfo> result =
        usageStats.map((item) => UsageInfo.fromMap(item)).toList();
    return result;
  }

  /// Queries and aggregates usage statistics within a specified date range.
  ///
  /// Takes start and end dates as parameters and returns a Future that resolves
  /// to a map of aggregated usage statistics with the package name as the key
  /// and UsageInfo as the value.
  static Future<Map<String, UsageInfo>> queryAndAggregateUsageStats(
      DateTime startDate, DateTime endDate) async {
    // Convert start and end dates to milliseconds since epoch.
    int end = endDate.millisecondsSinceEpoch;
    int start = startDate.millisecondsSinceEpoch;

    // Prepare a map of the start and end times to send to the native method.
    Map<String, int> interval = {'start': start, 'end': end};

    // Call the native method and await the result.
    Map usageAggStats =
        await _channel.invokeMethod('queryAndAggregateUsageStats', interval);

    // Map the result to a map of package names and UsageInfo objects.
    Map<String, UsageInfo> result = usageAggStats
        .map((key, value) => MapEntry(key as String, UsageInfo.fromMap(value)));
    return result;
  }

  /// Queries network usage statistics within a specified date range.
  ///
  /// Takes start and end dates as parameters and an optional network type.
  /// Returns a Future that resolves to a list of NetworkInfo objects.
  static Future<List<NetworkInfo>> queryNetworkUsageStats(
    DateTime startDate,
    DateTime endDate, {
    NetworkType networkType = NetworkType.all,
  }) async {
    // Convert start and end dates to milliseconds since epoch.
    int end = endDate.millisecondsSinceEpoch;
    int start = startDate.millisecondsSinceEpoch;

    // Prepare a map of the start and end times, including the network type, to send to the native method.
    Map<String, int> interval = {
      'start': start,
      'end': end,
      'type': networkType.value,
    };

    // Call the native method and await the result.
    List events =
        await _channel.invokeMethod('queryNetworkUsageStats', interval);

    // Map the result to a list of NetworkInfo objects.
    List<NetworkInfo> result =
        events.map((item) => NetworkInfo.fromMap(item)).toList();
    return result;
  }

  /// Queries network usage statistics for a specific package within a specified date range.
  ///
  /// Takes start and end dates as parameters, a required package name,
  /// and an optional network type. Returns a Future that resolves to a NetworkInfo object.
  static Future<NetworkInfo> queryNetworkUsageStatsByPackage(
    DateTime startDate,
    DateTime endDate, {
    required String packageName,
    NetworkType networkType = NetworkType.all,
  }) async {
    // Convert start and end dates to milliseconds since epoch.
    int end = endDate.millisecondsSinceEpoch;
    int start = startDate.millisecondsSinceEpoch;

    // Prepare a map of the start and end times, including the network type and package name, to send to the native method.
    Map<String, dynamic> interval = {
      'start': start,
      'end': end,
      'type': networkType.value,
      'packageName': packageName,
    };

    // Call the native method and await the result.
    Map response = await _channel.invokeMethod(
        'queryNetworkUsageStatsByPackage', interval);

    // Return a NetworkInfo object mapped from the response.
    return NetworkInfo.fromMap(response);
  }
}
