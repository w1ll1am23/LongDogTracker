package com.example.longdogtracker.features.settings.model

import com.example.longdogtracker.R

const val settingIdShowCorrectDayInUI = "SHOW_CORRECT_DAY_IN_UI"
const val settingIdShowStopWatch = "SHOW_STOP_WATCH"
const val settingIdAutoAdvanceToNextDate = "AUTO_ADVANCE_TO_NEXT_DATE"
const val settingIdCenturies = "CENTURIES"
const val settingScore = "SCORE"

val showTheCorrectDaySetting = Setting(
    settingIdShowCorrectDayInUI,
    R.string.setting_highlight_the_correct_day_heading,
    R.string.setting_highlight_the_correct_day_description,
)

val showSecondsElapsed = Setting(
    settingIdShowStopWatch,
    R.string.setting_show_seconds_elapsed_heading,
    R.string.setting_show_seconds_elapsed_description,
)

val autoAdvanceToNextDate = Setting(
    settingIdAutoAdvanceToNextDate,
    R.string.setting_auto_advance_to_next_date_heading,
    R.string.setting_show_seconds_elapsed_description,
)

val centuries = Setting(
    settingIdCenturies,
    R.string.setting_date_range_heading,
    R.string.setting_date_range_description,
    SettingType.RANGE
)

val resetCorrectCount = Setting(
    settingScore,
    R.string.setting_score_heading,
    R.string.setting_score_description,
    SettingType.RESET
)

val allSettingsList =
    listOf(centuries, autoAdvanceToNextDate, showSecondsElapsed, showTheCorrectDaySetting, resetCorrectCount)