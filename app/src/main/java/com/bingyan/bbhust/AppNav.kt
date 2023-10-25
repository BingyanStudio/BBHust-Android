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
import com.bingyan.bbhust.ui.screen.account.AccountScreen
import com.bingyan.bbhust.ui.screen.add.memory.AddMemoryScreen
import com.bingyan.bbhust.ui.screen.feed.FeedScreen
import com.bingyan.bbhust.ui.screen.home.MainContainer
import com.bingyan.bbhust.utils.ifElse
import com.bingyan.bbhust.utils.kv
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import java.net.ServerSocket

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNav(nav: NavHostController = rememberAnimatedNavController()) {
    AnimatedNavHost(
        navController = nav,
        startDestination = kv.token.isNullOrBlank().ifElse(AppRoute.account, AppRoute.index)
    ) {
        ServerSocket()
        animateCompose(
            AppRoute.index,
        ) {
            MainContainer.View()
        }
        animateCompose(AppRoute.account) {
            AccountScreen.View()
        }
        animateCompose(AppRoute.addMemory) {
            AddMemoryScreen.View()
        }

        animateCompose(
            AppRoute.memory("{id}"),
            listOf(navArgument("id") { type = NavType.StringType })
        ) {
            FeedScreen.View(id = it.arguments?.getString("id") ?: "")
        }
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
            .setPopUpTo(AppRoute.index, true).build()
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
    const val addMemory = "/addMemory"
    const val index = "/"
    const val account = "/account"
    fun memory(id: String) = "/memory/$id"
}