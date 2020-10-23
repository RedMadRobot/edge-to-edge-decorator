package com.redmadrobot.e2e.decorator

import android.content.Context
import android.graphics.Color
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.View
import android.view.Window
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.google.android.material.color.MaterialColors

/**
 * Класс отвечает за окрашивание statusBar и navigationBar для поддержания edge-to-edge режима.
 * Концепция основана на WindowPreferencesManager из
 * [приложения-каталога материальных комнонентов](https://github.com/material-components/material-components-android/blob/master/catalog/java/io/material/catalog/windowpreferences/WindowPreferencesManager.java)
 */
object EdgeToEdgeDecorator {

    private const val EDGE_TO_EDGE_FLAGS = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)

    private var config = DefaultConfig()

    /**
     * Метод позволяет модифицировать параметры работы edge-to-edge режима.
     *
     * @see DefaultConfig
     */
    fun updateConfig(block: DefaultConfig.() -> Unit): EdgeToEdgeDecorator {
        config = DefaultConfig().apply(block)

        return this
    }

    /**
     * Метод активирует edge-to-edge с выбранными параметрами.
     *
     * @see [updateConfig]
     */
    fun apply(context: Context, window: Window) {
        val decorView = window.decorView

        @ColorInt
        val statusBarColor = getStatusBarColor(context)

        @ColorInt
        val navBarColor = getNavBarColor(context)

        window.statusBarColor = statusBarColor
        window.navigationBarColor = navBarColor

        decorView.systemUiVisibility = getEdgeToEdgeFlag(decorView) or
                getStatusBarFlags(context, statusBarColor) or
                getNavBarFlags(context, navBarColor)
    }

    @ColorInt
    private fun getStatusBarColor(context: Context): Int {
        val opaqueStatusBarColor =
            MaterialColors.getColor(context, android.R.attr.statusBarColor, javaClass.canonicalName)

        return when {
            !config.isEdgeToEdgeEnabled -> opaqueStatusBarColor
            VERSION.SDK_INT < VERSION_CODES.M -> config.statusBarCompatibilityColor
            else -> config.statusBarEdgeToEdgeColor
        }
    }

    @ColorInt
    private fun getNavBarColor(context: Context): Int {
        val opaqueNavBarColor =
            MaterialColors.getColor(context, android.R.attr.navigationBarColor, javaClass.canonicalName)

        return when {
            !config.isEdgeToEdgeEnabled -> opaqueNavBarColor
            VERSION.SDK_INT < VERSION_CODES.O -> config.navBarCompatibilityColor
            else -> config.navBarEdgeToEdgeColor
        }
    }


    @ColorInt
    private fun getContentUnderStatusBarColor(context: Context): Int {
        val customContentColor = config.contentUnderStatusBarCustomColor

        return if (customContentColor != null) {
            ContextCompat.getColor(context, customContentColor)
        } else {
            MaterialColors.getColor(context, config.appBarColorAttr, javaClass.canonicalName)
        }
    }

    private fun getStatusBarFlags(context: Context, @ColorInt statusBarColor: Int): Int {
        val isLightContent = isColorLight(getContentUnderStatusBarColor(context))
        val isLightStatusBar = isColorLight(statusBarColor)

        val needShowDarkStatusBarIcons = isLightStatusBar || (statusBarColor == Color.TRANSPARENT && isLightContent)

        return if (needShowDarkStatusBarIcons && VERSION.SDK_INT >= VERSION_CODES.M) {
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            0
        }
    }

    @ColorInt
    private fun getContentUnderNavigationBarColor(context: Context): Int {
        val customContentColor = config.contentUnderNavBarCustomColor

        return if (customContentColor != null) {
            ContextCompat.getColor(context, customContentColor)
        } else {
            MaterialColors.getColor(context, config.backgroundColorAttr, javaClass.canonicalName)
        }
    }

    private fun getNavBarFlags(context: Context, @ColorInt navBarColor: Int): Int {
        val isLightContent = isColorLight(getContentUnderNavigationBarColor(context))
        val isLightNavBar = isColorLight(navBarColor)

        val showDarkNavBarIcons = isLightNavBar || (navBarColor == Color.TRANSPARENT && isLightContent)

        return if (showDarkNavBarIcons && VERSION.SDK_INT >= VERSION_CODES.O) {
            View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            0
        }
    }

    private fun getEdgeToEdgeFlag(decorView: View): Int {
        val flagsWithoutEdgeToEdge = decorView.systemUiVisibility and EDGE_TO_EDGE_FLAGS.inv()
        val edgeToEdgeFlag = if (config.isEdgeToEdgeEnabled) EDGE_TO_EDGE_FLAGS else View.SYSTEM_UI_FLAG_VISIBLE

        return flagsWithoutEdgeToEdge or edgeToEdgeFlag
    }


    private fun isColorLight(@ColorInt color: Int): Boolean {
        return if (Color.alpha(color) != 255) {
            color != Color.TRANSPARENT && ColorUtils.calculateLuminance(color) > 0.5
        } else {
            val contrastWithWhiteText = ColorUtils.calculateContrast(Color.WHITE, color)
            val contrastWithBlackText = ColorUtils.calculateContrast(Color.BLACK, color)

            contrastWithBlackText > contrastWithWhiteText
        }
    }

    class DefaultConfig {

        /**
         * Флаг отвечает за включение/выключение edge-to-edge режима.
         */
        var isEdgeToEdgeEnabled = true

        /**
         * В простом edge-to-edge режиме. Цвет иконок statusBar устанавливается в соответствии
         * с цветом [com.google.android.material.appbar.AppBarLayout].
         *
         * Значение по умолчанию равно [R.attr.colorPrimarySurface].
         *
         * В этом случае цвет самого statusBar равен параметру [statusBarEdgeToEdgeColor],
         * по умолчанию [statusBarEdgeToEdgeColor] равен [Color.TRANSPARENT].
         *
         * Также в простом режиме используется [backgroundColorAttr]
         *
         * @see backgroundColorAttr
         * @see statusBarEdgeToEdgeColor
         */
        @AttrRes
        var appBarColorAttr = R.attr.colorPrimarySurface

        /**
         * В простом edge-to-edge режиме. Цвет иконок navigationBar устанавливается в соответствии
         * с цветом [android.R.attr.windowBackground]
         *
         * Значение по умолчанию равно [android.R.attr.windowBackground]
         *
         * В этом случае цвет самого navigationBar равен параметру [navBarCompatibilityColor],
         * по умолчанию [navBarCompatibilityColor] равен [Color.TRANSPARENT].
         *
         * Также в простом режиме используется [appBarColorAttr]
         *
         * @see appBarColorAttr
         * @see navBarCompatibilityColor
         */
        @AttrRes
        var backgroundColorAttr = android.R.attr.windowBackground

        /**
         * Если не подходит простой режим, например, для случаев, когда на экране нет
         * [com.google.android.material.appbar.AppBarLayout], можно активировать кастомный режим edge-to-edge.
         *
         * Для этого нужно передать конкретный цвет контента под statusBar, например, [R.color.windowBackground]
         *
         * @see contentUnderNavBarCustomColor
         */
        @ColorRes
        var contentUnderStatusBarCustomColor: Int? = null

        /**
         * Если не подходит простой режим, например, под navigationBar должен отрисовываться другой контет
         * или [BottomNavigationMenu], можно активировать кастомный режим edge-to-edge.
         *
         * Для этого нужно передать конкретный цвет контента под navigationBar, например, [R.color.bottomMenu]
         *
         * @see contentUnderStatusBarCustomColor
         */
        @ColorRes
        var contentUnderNavBarCustomColor: Int? = null

        /**
         * Если под statusBar контент не сплошного цвета, а, например, картинка,
         * то можно активировать режим дополнительной контрастности.
         *
         * По умолчанию используется [Color.TRANSPARENT]
         *
         * @see navBarEdgeToEdgeColor
         */
        @ColorInt
        var statusBarEdgeToEdgeColor = Color.TRANSPARENT

        /**
         * Если под navigationBar контент не сплошного цвета, а, например, картинка,
         * то можно активировать режим дополнительной контрастности.
         *
         * По умолчанию используется [Color.TRANSPARENT]
         *
         * @see statusBarEdgeToEdgeColor
         */
        @ColorInt
        var navBarEdgeToEdgeColor = Color.TRANSPARENT

        /**
         * Цвет иконок для statusBar можно менять только с 23 API. Для Android с API ниже 23
         * используется цвет, который будет хорошо контрастировать с белыми иконками.
         *
         * По умолчанию, для сохранения эффекта edge-to-edge, используется черный цвет с 50% прозрачностью.
         *
         * @see navBarCompatibilityColor
         */
        @ColorInt
        var statusBarCompatibilityColor = ColorUtils.setAlphaComponent(Color.BLACK, 128)

        /**
         * Цвет иконок для navigationBar можно менять только с 26 API. Для Android с API ниже 26
         * используется цвет, который будет хорошо контрастировать с белыми иконками.
         *
         * По умолчанию, для сохранения эффекта edge-to-edge, используется черный цвет с 50% прозрачностью.
         *
         * @see statusBarCompatibilityColor
         */
        @ColorInt
        var navBarCompatibilityColor = ColorUtils.setAlphaComponent(Color.BLACK, 128)
    }
}
