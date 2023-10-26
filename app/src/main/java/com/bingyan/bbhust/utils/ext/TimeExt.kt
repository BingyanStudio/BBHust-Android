package com.bingyan.bbhust.utils.ext

import com.bingyan.bbhust.utils.DAY
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * 时间相关扩展函数类
 */

val String.formatHumanized: String
    get() = ((toLongOrNull(10) ?: 0L) * 1000L).formatHumanized

val String.formatDateTime: String
    get() = ((toLongOrNull(10) ?: 0L) * 1000L).formatDateTime
val String.formatDate: String
    get() {
        val formatter = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
        return formatter.format(((toLongOrNull(10) ?: 0L) * 1000L))
    }

val String.calculateDay: String
    get() = ((System.currentTimeMillis()//当前时间
            - (toLongOrNull(10) ?: 0L) * 1000L//减去距离时间（一般为注册时间）
            ) / DAY + 1).toString(10)//除以天数

val Int.formatHumanized: String
    get() = (toLong() * 1000L).formatHumanized

val Int.formatDateTime: String
    get() = (toLong() * 1000L).formatDateTime

//日期人性化转换
val Long.formatHumanized: String
    get() {
        val startDate = this
        val endTime = System.currentTimeMillis()     //获取毫秒数
        val timeDifference = endTime - startDate
        val second = timeDifference / 1000    //计算秒

        if (second < 60) {
            return "刚刚"
        } else {
            val minute = second / 60
            if (minute < 60) {
                return "$minute 分钟前"
            } else {
                val hour = minute / 60
                if (hour < 24
                    && toCalendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance()
                        .get(Calendar.DAY_OF_YEAR)//24小时内且为当天
                ) {
                    return "$hour 小时前"
                } else {
                    if (toCalendar.get(Calendar.YEAR) == Calendar.getInstance()
                            .get(Calendar.YEAR)//如果是当年
                    ) {
                        val day = toCalendar.get(Calendar.DAY_OF_YEAR)
                        val curDay = Calendar.getInstance()
                            .get(Calendar.DAY_OF_YEAR)
                        return when (curDay - day) {
                            1 -> {
                                val formatter = SimpleDateFormat("昨天 HH:mm", Locale.CHINA)
                                formatter.format(startDate)
                            }

                            2 -> {
                                val formatter = SimpleDateFormat("前天 HH:mm", Locale.CHINA)
                                formatter.format(startDate)
                            }

                            else -> {
                                val formatter = SimpleDateFormat("MM月dd日 HH:mm", Locale.CHINA)
                                formatter.format(startDate)
                            }
                        }
                    } else {
                        val formatter = SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA)
                        return formatter.format(startDate)
                    }
                }
            }
        }
    }

//日期人性化转换
val Long.formatDateTime: String
    get() {
        val startDate = this
        val formatter = SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA)
        return formatter.format(startDate)
    }

//日期人性化转换
val Long.formatOnlyDate: String
    get() {
        val startDate = this
        val formatter = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
        return formatter.format(startDate)
    }


val Long.toDate get() = Date(this)
val Long.toCalendar: Calendar
    get() {
        val cal = Calendar.getInstance()
        cal.time = this.toDate
        return cal
    }


//时间戳转日期
val Long.formatDate: String
    get() {
        return if (this.toCalendar.get(Calendar.YEAR) == Calendar.getInstance()
                .get(Calendar.YEAR)
        ) {
            val formatter = SimpleDateFormat("MM月dd日", Locale.CHINA)
            formatter.format(this)
        } else {
            val formatter = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
            formatter.format(this)
        }
    }


