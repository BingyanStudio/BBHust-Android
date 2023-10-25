# Compose 开发规范

界面统称为 Screen

## 1. Screen

为 Object，对外提供 View
函数，如 [FeedScreen](../app/src/main/java/com/bingyan/bbhust/ui/screen/feed/FeedScreen.kt)
，其余函数均为私有，如存在需要复用的组件，将其抽象为 Widget

## 2. ViewModel

1. 继承至 [BaseViewModel](../app/src/main/java/com/bingyan/bbhust/base/BaseViewModel.kt)，自定义
   State 和 Action
   同时初始化参数中传入默认 State 对象
2. 通过重写 `reduce` 函数处理 Action，返回新的 State
3. 如有特殊需求需要监听 State 变化，重写 `onChange` 函数

## 3. Widget

通用组件集合

## 4. Repository

数据仓库，用于分离数据来源与 ViewModel 的强耦合，同一个 Repository 可以有多个数据源，如网络请求、数据库操作等

## 5. Model

数据模型，包括网络请求、数据库操作等

### 6. i18n 国际化

所有 String 禁止硬编码，通过 `string(R.string.xxx)` 或 `string(R.string.xxx, args)` 引用字符串资源

如需新增字符串资源，需在 `strings.xml` 中添加对应的字符串资源，也可手动输入名称后使用 `Alt + Enter`
快捷键自动创建

### 7. 命名规范

1. 所有文件名、类名、函数名均使用大驼峰命名法，如 `FeedScreen.kt`
   ，文件夹/包名使用下划线命名法，如 `feed_screen`，变量名使用小驼峰命名法，如 `feedScreen`
2. 所有资源文件名均使用下划线命名法，如 `ic_feed.xml`
3. 所有资源文件名均以资源类型开头，如 `ic_feed.xml` 为图片资源，`bg_feed.xml` 为背景资源