class EventUsageInfo {
  EventUsageInfo({
    this.eventType,
    this.timeStamp,
    this.packageName,
    this.className,
    this.shortcutId,
  });

  /// Construct class from the json map
  factory EventUsageInfo.fromMap(Map map) => EventUsageInfo(
        eventType: map['eventType'],
        timeStamp: map['timeStamp'],
        packageName: map['packageName'],
        className: map['className'],
        shortcutId: map['shortcutId'],
      );

  final String? eventType;
  final String? timeStamp;
  final String? packageName;
  final String? className;
  final String? shortcutId;
}
