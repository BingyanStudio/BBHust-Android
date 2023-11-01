package com.bingyan.bbhust

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.bingyan.bbhust.ui.provider.LocalNav
import com.bingyan.bbhust.ui.screen.browser.BrowserScreen
import com.bingyan.bbhust.ui.screen.feed.FeedScreen
import com.bingyan.bbhust.ui.screen.index.AppScaffold
import com.bingyan.bbhust.ui.screen.login.LoginScreen
import com.bingyan.bbhust.utils.LOGIN_URL
import com.bingyan.bbhust.utils.REGISTER_URL
import com.bingyan.bbhust.utils.ifElse
import com.bingyan.bbhust.utils.kv
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNav(nav: NavHostController = rememberAnimatedNavController()) {
    AnimatedNavHost(
        navController = nav,
        startDestination = kv.token.isNullOrBlank().ifElse(AppRoute.LOGIN, AppRoute.MAIN)
    ) {
        animateCompose(
            AppRoute.MAIN,
        ) {
            AppScaffold.View(nav)
        }
        animateCompose(AppRoute.LOGIN) {
            LoginScreen.View()
        }

        animateCompose(
            AppRoute.BROWSER + "?url={url}",
            listOf(navArgument("url") { type = NavType.StringType })
        ) {
            BrowserScreen(nav = nav, url = it.arguments?.getString("url") ?: "")
        }

        animateCompose(
            AppRoute.BROWSER_SERVICE + "?url={url}",
            listOf(navArgument("url") { type = NavType.StringType })
        ) {
            BrowserScreen(nav = nav, url = it.arguments?.getString("url") ?: "", service = true)
        }

        animateCompose(
            AppRoute.POST+"/{id}",
            listOf(navArgument("id"){type= NavType.StringType})
        ){
                FeedScreen(id = it.arguments?.getString("id")?:"", nav =nav , reply = false)
        }

        animateCompose(
            AppRoute.POST_REPLY+"/{id}",
            listOf(navArgument("id"){type= NavType.StringType})
        ){
            FeedScreen(id = it.arguments?.getString("id")?:"", nav =nav , reply = true)
        }
//        animateCompose(
//            AppRoute.memory("{id}"),
//            listOf(navArgument("id") { type = NavType.StringType })
//        ) {
//            FeedScreen.View(id = it.arguments?.getString("id") ?: "")
//        }
    }
}

private val animation = spring<IntOffset>(stiffness = 200f, dampingRatio = 0.7f)

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.animateCompose(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) =
    composable(
        route,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it }, animationSpec = animation)
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it / 2 }, animationSpec = animation)
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it / 2 }, animationSpec = animation)
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it }, animationSpec = animation)
        },
        arguments = arguments,
        deepLinks = deepLinks,
        content = content
    )

@Composable
fun nav() = LocalNav.current


fun NavHostController.replace(route: String) {
    this.popBackStack()
    this.navigate(
        route, NavOptions.Builder()
            .setEnterAnim(R.anim.enter)
            .setExitAnim(R.anim.exit)
            .setPopUpTo(AppRoute.MAIN, true).build()
    )
}

fun NavHostController.push(route: String) {
    this.navigate(
        route, NavOptions.Builder()
            .setEnterAnim(R.anim.enter)
            .setExitAnim(R.anim.exit)
            .build()
    )
}

fun NavHostController.pop() {
    this.popBackStack()
}

object AppRoute {
    const val MAIN = "main"
    const val ABOUT = "about"
    const val OPEN_SOURCE = "about/open_source"
    const val TIMELINE = "about/timeline"
    const val POST_CREATE = "post/create"
    const val SETTINGS = "settings"
    const val POST = "post/feed"
    const val POST_REPLY = "post/feed_reply"
    const val USER = "user"
    const val USER_CHANGE = "user/change"
    const val LOGIN = "user/login"
    const val MESSAGE_OFFICIAL = "message/official"
    const val MESSAGE_LIKE = "message/like"
    const val MESSAGE_FOLLOW = "message/follow"
    const val MESSAGE_AT = "message/at"
    const val MY_LIKE = "me/like"
    const val MY_MARK = "me/mark"
    const val MY_FEED = "me/feed"
    const val MY_COMMENT = "me/comment"
    const val FOLLOWING = "user/following"
    const val FOLLOWED = "user/followed"
    const val IMAGE_CROP = "crop"
    const val BROWSER = "browser"
    const val BROWSER_SERVICE = "browser_service"
    const val SEARCH = "search"

    fun post(id: String) = "$POST/$id"
    fun postReply(id: String) = "$POST_REPLY/$id"
    fun user(id: String) = "$USER/$id"

    object H5 {
        val login = "$BROWSER_SERVICE?url=$LOGIN_URL"
        val register = "$BROWSER_SERVICE?url=$REGISTER_URL"
    }
}