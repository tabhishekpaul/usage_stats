package dev.tabhishekpaul.usage_stats
// Package declaration for organizing the Utils object within the specified namespace.

import android.provider.Settings
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.net.Uri
// Imports necessary libraries for managing application settings, permissions, and Android context.

object Utils {
    // The Utils object serves as a utility class for common functions related to app permissions and settings.

    fun requestPermission(context: Context) {
        // This method initiates a request for usage access permission from the user.
        if (!isUsagePermission(context)) {
            // Checks if the permission is not already granted.
            try {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                // Creates an intent to open the usage access settings screen.
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                // Sets the flag to start the activity in a new task.
                intent.data = Uri.parse("package:" + context.packageName)
                // Sets the data URI to the current application's package name to direct the user to the correct settings page.
                context.startActivity(intent)
                // Starts the settings activity.
            } catch (e: Exception) {
                // Catches any exception that occurs while trying to start the activity.
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                // Creates a new intent in case the previous one failed.
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                // Sets the flag again to ensure the activity starts correctly.
                context.startActivity(intent)
                // Starts the settings activity.
            }
        }
    }

    fun isPermissionGranted(context: Context): Boolean {
        // This method checks if the app has permission to access usage statistics.
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager?
        // Retrieves the AppOpsManager system service to check app operations.
        val mode = appOps!!.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        // Checks the current mode for the GET_USAGE_STATS operation for the calling UID and package name.
        if (mode == AppOpsManager.MODE_ALLOWED) {
            // If the permission is granted, return true.
            return true
        }
        return false
        // Returns false if the permission is not granted.
    }

    private fun isUsagePermission(context: Context): Boolean {
        // A private helper method to determine if usage access permission is granted.
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager?
        // Retrieves the AppOpsManager system service.
        val mode = appOps!!.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        // Checks the current permission mode for usage statistics access.
        return mode == AppOpsManager.MODE_ALLOWED
        // Returns true if permission is allowed, false otherwise.
    }
}
