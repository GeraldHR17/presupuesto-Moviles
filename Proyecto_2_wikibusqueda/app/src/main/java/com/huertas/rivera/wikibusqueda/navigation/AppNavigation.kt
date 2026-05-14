package com.huertas.rivera.wikibusqueda.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.huertas.rivera.wikibusqueda.ui.screens.ArticleScreen
import com.huertas.rivera.wikibusqueda.ui.screens.SearchScreen
import com.huertas.rivera.wikibusqueda.util.Routes

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SEARCH,
        modifier = modifier
    ) {

        composable(Routes.SEARCH) {

            SearchScreen(

                onArticleClick = { articleKey ->

                    navController.navigate(
                        "${Routes.ARTICLE}/$articleKey"
                    )
                }
            )
        }

        composable(
            route = "${Routes.ARTICLE}/{articleKey}",

            arguments = listOf(
                navArgument("articleKey") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val articleKey =
                backStackEntry.arguments?.getString("articleKey") ?: ""

            ArticleScreen(

                articleKey = articleKey,

                onBackClick = {

                    navController.popBackStack()
                }
            )
        }
    }
}