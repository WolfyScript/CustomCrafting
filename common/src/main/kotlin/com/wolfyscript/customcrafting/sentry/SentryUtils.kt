package com.wolfyscript.customcrafting.sentry

import com.wolfyscript.customcrafting.util.CustomCraftingProperties
import io.sentry.Sentry
import io.sentry.SentryOptions
import io.sentry.SystemOutLogger
import io.sentry.log4j2.SentryAppender
import org.apache.logging.log4j.core.LoggerContext

/**
 * Initiates the Sentry SDK and inserts the [SentryAppender] into the Log4J logger.
 *
 * The Log4J Appender is necessary to catch errors that are not caught with try-catch or other means.
 * This way we can catch errors very early on in the plugin/mod lifecycle.
 */
fun initSentry() {
    val sentryAppender = SentryAppender.createAppender(
        "customcrafting:sentry",
        null,
        null,
        null,
        "", // Do not init sentry in the appender, we initiate it manually below
        false,
        null,
        null
    )
    if (sentryAppender != null) {
        sentryAppender.start()

        val logCtx = LoggerContext.getContext(false)
        val logConfig = logCtx.configuration
        logConfig.addAppender(sentryAppender)
        logConfig.rootLogger.addAppender(sentryAppender, org.apache.logging.log4j.Level.ERROR, null)
        logCtx.updateLoggers()
    }

    Sentry.init {
        it.dsn = "https://bfce6b20a3464f89a5255b24564315c1@errors.wolfyscript.com/1"
        it.release = CustomCraftingProperties.release
        it.isSendDefaultPii = false // don't send personal identifiable information (ip, computer name, etc.)
        it.tracesSampleRate = null // make sure tracing is always disabled. we don't care about performance monitoring.
        it.addInAppInclude("com.wolfyscript")
        it.isEnableUncaughtExceptionHandler = true
        it.isDebug = true
        it.setLogger(SystemOutLogger())

        it.beforeSend = SentryOptions.BeforeSendCallback({ event, hint ->
            // In case the error originates in an external logger we need to filter out unrelated errors
            // e.i. errors from other mods/plugins, vanilla minecraft errors, etc.
            val externalLogger = event.logger?.startsWith("com.wolfyscript")?.not() ?: false
            if (externalLogger) {
                // Filter out errors that do not contain the com.wolfyscript package
                // or the ones without any exceptions
                val inApp = event.exceptions?.any { exception ->
                    exception.stacktrace?.frames?.any { frame ->
                        frame.isInApp ?: false
                    } ?: false
                } ?: false

                if (!inApp) {
                    return@BeforeSendCallback null
                }
            }

            return@BeforeSendCallback event
        })
    }
}