package com.passguard.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.passguard.app.ui.categories.CategoriesRoute
import com.passguard.app.ui.credential.CredentialDetailRoute
import com.passguard.app.ui.credential.CredentialEditorRoute
import com.passguard.app.ui.generator.PasswordGeneratorRoute
import com.passguard.app.ui.home.HomeRoute
import com.passguard.app.ui.importexport.ImportExportRoute
import com.passguard.app.ui.settings.SettingsRoute

object PassGuardDestinations {
    const val HOME = "home"
    const val ADD = "credential/add"
    const val EDIT = "credential/edit/{credentialId}"
    const val DETAIL = "credential/detail/{credentialId}"
    const val SETTINGS = "settings"
    const val CATEGORIES = "categories"
    const val IMPORT_EXPORT = "import_export"
    const val GENERATOR = "generator"
}

@Composable
fun PassGuardNavHost(
    navController: NavHostController,
    isExpanded: Boolean,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = PassGuardDestinations.HOME,
        modifier = modifier
    ) {
        composable(PassGuardDestinations.HOME) {
            HomeRoute(
                onCredentialClick = { id -> navController.navigate("credential/detail/$id") },
                onAddCredential = { navController.navigate(PassGuardDestinations.ADD) },
                onOpenSettings = { navController.navigate(PassGuardDestinations.SETTINGS) },
                onOpenCategories = { navController.navigate(PassGuardDestinations.CATEGORIES) },
                isExpanded = isExpanded,
                onOpenGenerator = { navController.navigate(PassGuardDestinations.GENERATOR) },
                onOpenImportExport = { navController.navigate(PassGuardDestinations.IMPORT_EXPORT) }
            )
        }
        composable(PassGuardDestinations.ADD) {
            CredentialEditorRoute(onDone = { navController.popBackStack() })
        }
        composable(
            PassGuardDestinations.EDIT,
            arguments = listOf(navArgument("credentialId") { type = NavType.LongType })
        ) {
            CredentialEditorRoute(onDone = { navController.popBackStack() })
        }
        composable(
            PassGuardDestinations.DETAIL,
            arguments = listOf(navArgument("credentialId") { type = NavType.LongType })
        ) {
            CredentialDetailRoute(onBack = { navController.popBackStack() })
        }
        composable(PassGuardDestinations.SETTINGS) {
            SettingsRoute(appViewModel = appViewModel, onBack = { navController.popBackStack() })
        }
        composable(PassGuardDestinations.CATEGORIES) {
            CategoriesRoute(onBack = { navController.popBackStack() })
        }
        composable(PassGuardDestinations.IMPORT_EXPORT) {
            ImportExportRoute(onBack = { navController.popBackStack() })
        }
        composable(PassGuardDestinations.GENERATOR) {
            PasswordGeneratorRoute(onBack = { navController.popBackStack() })
        }
    }
}
