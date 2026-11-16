package io.github.seijikohara.femto.data.common

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import java.util.Locale

internal val AppDefaultLocale: Locale = Locale.forLanguageTag("pt-BR")

/**
 * Pins [this] configuration to [AppDefaultLocale] (`pt-BR`) and aligns the JVM
 * default [Locale] so `Locale.getDefault()` consumers (calendar names, geocoder
 * requests, and date/number formatting) match the UI resources regardless of
 * the host device's OS language.
 */
internal fun Configuration.applyAppLocale(): Configuration =
    apply {
        Locale.setDefault(AppDefaultLocale)
        setLocale(AppDefaultLocale)
        setLocales(LocaleList(AppDefaultLocale))
    }

/**
 * Wraps [this] context in a configuration pinned to [AppDefaultLocale] (`pt-BR`).
 */
internal fun Context.withAppLocale(): Context =
    createConfigurationContext(Configuration(resources.configuration).applyAppLocale())
