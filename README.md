# edge-to-edge-decorator

## Описание

**Edge to edge decorator** - это класс-утилита, которая отвечает за окрашивание `statusBar` и `navigationBar` для поддержания **edge to edge (e2e)** режима.  

Концепция основана на [WindowPreferencesManager](https://github.com/material-components/material-components-android/blob/master/catalog/java/io/material/catalog/windowpreferences/WindowPreferencesManager.java)
из [приложения-каталога материальных комнонентов](https://github.com/material-components/material-components-android/tree/master/catalog).

## Проблема

С выходом андроид 10, компания Google представила жестовую навигацию и edge to edge режим. Этот режим означает, что
контент отрисовывается под системными компонентами `statusBar` и `navigationBar`, и телефон становится
визуально более безрамочным, добавляется поддержка вырезов для камер, а сами компоненты окрашиваются в прозрачный цвет.

Для того, чтобы добавить поддержку edge to edge в ваше приложение, нужно сделать 2 вещи:

### 1. Добавить поддержку системных отступов (insets)

Вы получаете размер системных компонентов и вставляете их
как `padding` в верстку для ваших компонентов. Insets поддерживается всеми версиями Android OS,
что позволяет реализовать концепцию edge to edge для всех пользователей.

Подробности можно почитать или посмотреть в [докладе Константина Цховребова](https://habr.com/ru/company/oleg-bunin/blog/488196/).  
Для реализации можно использовать библиотеку от Chris Banes [Insetter](https://github.com/chrisbanes/insetter).

### 2. Активировать режим edge-to-edge для `statusBar` и `navigationBar`. По факту вам нужно сделать их прозрачными

Тут существует одна проблема, которая находится глубоко в системе и исправить её после релиза OS уже нельзя.
Это изменение цвета иконок в системных компонентах (`statusBar` и `navigationBar`) со светлого на темный.
Поэтому нужно учитывать следующие правила, в зависимости от версии Android:

* до 6.0 версии android иконки `statusBar` и `navigationBar` всегда светлые и перекрасить их в темный цвет нельзя.  
Флаг `View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR` доступен с 23 API.
Если у вас контент всегда темного цвета, то проблем не будет.
Утилита, чтобы сохранить контрастность иконок на фоне контента, добавляет на системные компоненты наложение черного фона с 50% прозрачности;

* с версии 6.0 можно задать, белыми или черными будут иконки в `statusBar`.  
Однако `navigationBar` будет вести себя как в предыдущих версиях, поэтому наложение можно убрать только для `statusBar`.
Флаг `View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR` доступен с 26 API.

* с версии 8.0 можно выбрать белый или черный цвет иконок для обоих компонентов.
Поэтому наложения можно убрать полностью.
  
Подробнее про edge to edge и жестовую навигацию можно почитать в статье, которую написал [Chris Banes](https://medium.com/androiddevelopers/gesture-navigation-going-edge-to-edge-812f62e4e83e).

### Пример работы утилиты

| <img src="images/sample_21_api.gif" width="200" /> | <img src="images/sample_25_api.gif" width="200" /> | <img src="images/sample_28_api.gif" width="174" /> | <img src="images/sample_30_api.gif" width="178" /> |
|:--------------------------------------------------:|:--------------------------------------------------:|:--------------------------------------------------:|----------------------------------------------------|
|             Android 5.0 (API level 21)             |             Android 7.1 (API level 25)             |              Android 9 (API level 28)              |              Android 11 (API level 30)             |

## Как подключить?

### 1. Подключение библиотеки

```groovy
dependencies {
    implementation 'com.redmadrobot:edge-to-edge-decorator:1.0.0'
}
```

### 2. Тема приложения должна наследоваться от MaterialComponents

Для определение атрибутов темы приложения в простом режиме необходимо, чтобы тема вашего приложения
наследовалась от `Theme.MaterialComponents.*`.

Также стоит явно указать нужный цвет фона для `AppBarLayout` и `background` приложения:

```xml
<item name="colorPrimary">@color/colorPrimary</item> <!-- or colorPrimarySurface -->
<item name="android:windowBackground">@color/windowBackground</item>
```

Или указать свои значения программно в параметрах `appBarColorAttr` и `backgroundColorAttr`.

### 3. Выключение режима edge to edge

Если на каком-то экране вы захотите выключить edge to edge мод (параметр `isEdgeToEdgeEnabled = false`),
то в теме приложения следует указать цвета `statusBar` и `navigationBar`:

```xml
<item name="android:statusBarColor">@android:color/black</item>
<item name="android:navigationBarColor">@android:color/black</item>
```

### 4. Включить или выключить флаг дополнительной контрастности для `NavigationBar`

```xml
<item name="android:enforceNavigationBarContrast" tools:targetApi="q">false</item>
```

Подробнее про флаг `enforceNavigationBarContrast` можно почитать в статье, которую написал [Chris Banes](https://medium.com/androiddevelopers/gesture-navigation-going-edge-to-edge-812f62e4e83e).

### 5. Настройка утилиты под особенности проекта

Настройка параметров и активация режима edge to edge
```kotlin
EdgeToEdgeDecorator
    .updateConfig {
        // custom config
        isEdgeToEdgeEnabled = true
        appBarColorAttr = R.color.colorPrimary
        backgroundColorAttr = R.color.windowBackground
    }
    .apply(context, window)
```

### 6. Profit!


## Настройки edge to edge decorator

Утилита может работать в 3-х режимах:

1. Простой (работа по умолчанию).
    * Цвет `statusBar` и `navigationBar` - прозрачный;
    * Цвет иконок `statusBar` определяется по цвету `AppBarLayout`. Параметр `appBarColorAttr` (по умолчанию `R.attr.colorPrimarySurface`)
    * Цвет иконок `navigationBar` определяется по цвету фона вашего приложения. Параметр `backgroundColorAttr` (по умолчанию `android.R.attr.windowBackground`).
    * Активируется режим edge to edge.  
    В `window.decorView.systemUiVisibility` устанавливаются флаги `View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE`)

2. Кастомный - на экране нет `AppBarLayout` и/или под `navigationBar` должен отрисовываться другой контент, например `BottomNavigationMenu`.
    * Цвет `statusBar` и `navigationBar` - прозрачный;
    * Цвет иконок `statusBar` определяется по цвету, указанному в параметре `contentUnderStatusBarCustomColor`.
    * Цвет иконок `navigationBar` определяется по цвету, указанному в параметре `contentUnderNavBarCustomColor`.
    * Активируется режим edge to edge.

3. Режим дополнителькой контрастности.  
Если на экране цвет контента определить нельзя, то для `statusBar` и `navigationBar` можно указать конкретный цвет вместо прозрачного.
Параметры `statusBarEdgeToEdgeColor` и `navBarEdgeToEdgeColor`.

Для всех режимов можно указать свои цвета для поддержания совместимости на устройствах с более поздними версиями Android OS.  
Параметры `statusBarCompatibilityColor` и `navBarCompatibilityColor`.

### Утилита имеет dsl интерфейс для редактирования параметров

Пример:

```kotlin
override val edgeToEdgeCompatibilityManager = EdgeToEdgeDecorator.updateConfig {
    // custom config
    isEdgeToEdgeEnabled = true
    appBarColorAttr = R.color.colorPrimary
    backgroundColorAttr = R.color.windowBackground
}
```

### Полное описание параметров можно найти в классе [DefaultConfig](https://github.com/RedMadRobot/edge-to-edge-decorator/blob/5776dcd5bb126bdb157f7d08d6f3fa6cfe6f4e88/edge-to-edge-decorator/src/main/java/com/redmadrobot/e2e/decorator/EdgeToEdgeDecorator.kt#L147)

```kotlin
class DefaultConfig {

    /**
     * Флаг отвечает за включение/выключение edge to edge режима.
     */
    var isEdgeToEdgeEnabled = true

    /**
     * В простом edge to edge режиме. Цвет иконок statusBar устанавливается в соответствии
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
     * В простом edge to edge режиме. Цвет иконок navigationBar устанавливается в соответствии
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
     * [com.google.android.material.appbar.AppBarLayout], можно активировать кастомный режим edge to edge.
     *
     * Для этого нужно передать конкретный цвет контента под statusBar, например, [R.color.windowBackground]
     *
     * @see contentUnderNavBarCustomColor
     */
    @ColorRes
    var contentUnderStatusBarCustomColor: Int? = null

    /**
     * Если не подходит простой режим, например, под navigationBar должен отрисовываться другой контет
     * или [BottomNavigationMenu], можно активировать кастомный режим edge to edge.
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
     * Цвет иконок для statusBar можно менять только с 23 API. Для андроида с API ниже 23
     * используется цвет, который будет хорошо контрастировать с белыми иконками.
     *
     * По умолчанию, для сохранения эффекта edge to edge, используется черный цвет с 50% прозрачностью.
     *
     * @see navBarCompatibilityColor
     */
    @ColorInt
    var statusBarCompatibilityColor = ColorUtils.setAlphaComponent(Color.BLACK, 128)

    /**
     * Цвет иконок для navigationBar можно менять только с 26 API. Для андроида с API ниже 26
     * используется цвет, который будет хорошо контрастировать с белыми иконками.
     *
     * По умолчанию, для сохранения эффекта edge to edge, используется черный цвет с 50% прозрачностью.
     *
     * @see statusBarCompatibilityColor
     */
    @ColorInt
    var navBarCompatibilityColor = ColorUtils.setAlphaComponent(Color.BLACK, 128)
}
```

## Зависимости

Утилита использует следующие зависимости:

```kotlin
implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.10") // Kotlin
implementation("com.google.android.material:material:1.2.1") // Material components
```

## Feedback

Если вы столкнулись с какими-либо ошибками или у вас есть полезные предложения 
по улучшению этой библиотеки, не стесняйтесь создавать 
[issue](https://github.com/RedMadRobot/edge-to-edge-decorator/issues).

## LICENSE

>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
>OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
>MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
>IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
>CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
>TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
>SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
