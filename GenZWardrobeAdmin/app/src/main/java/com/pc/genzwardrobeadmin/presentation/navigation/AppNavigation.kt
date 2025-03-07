package com.pc.genzwardrobeadmin.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.pc.genzwardrobeadmin.presentation.MainViewModel
import com.pc.genzwardrobeadmin.presentation.Search_Screen
import com.pc.genzwardrobeadmin.presentation.add_screen.Add_Product_Screen
import com.pc.genzwardrobeadmin.presentation.auth_screen.AuthViewModel
import com.pc.genzwardrobeadmin.presentation.auth_screen.Login_Screen
import com.pc.genzwardrobeadmin.presentation.auth_screen.OtpScreen
import com.pc.genzwardrobeadmin.presentation.home_screen.Home_Screen
import com.pc.genzwardrobeadmin.presentation.order_screen.Order_Summary
import com.pc.genzwardrobeadmin.presentation.order_screen.Ordered_Products_Screen

enum class AppScreens {
    LOGIN,
    OTP,
    HOME,
    SEARCH,
    ADD_PRODUCT,
    ORDER_DETAILS,
    ORDER_SUMMARY
}

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

val bottomNav = listOf(
    BottomNavItem(
        "Home",
        AppScreens.HOME.name,
        Icons.Default.Home
    ),
    BottomNavItem(
        "Add Product",
        AppScreens.ADD_PRODUCT.name,
        Icons.Default.Add
    ),
    BottomNavItem(
        "Ordered Items",
        AppScreens.ORDER_DETAILS.name,
        Icons.Default.Menu
    )
)

val bottomBarRoutes = setOf(
    AppScreens.HOME.name,
    AppScreens.ADD_PRODUCT.name,
    AppScreens.ORDER_DETAILS.name
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(modifier: Modifier = Modifier) {

    val navController: NavHostController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentScreen = if (currentUser != null) AppScreens.HOME.name else AppScreens.LOGIN.name

    val authViewModel: AuthViewModel = hiltViewModel()
    val mainViewModel: MainViewModel = hiltViewModel()


    Scaffold(
        topBar = {
            bottomNav.forEach { item ->
                if (currentRoute == item.route && currentRoute in bottomBarRoutes) {
                    TopAppBar(
                        title = {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        actions = {
                            IconButton(
                                onClick = { navController.navigate(AppScreens.SEARCH.name) }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = "Search"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                    )
                }
            }
        },
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.height(70.dp)
                ) {
                    bottomNav.forEach { item ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (currentRoute == item.route) {
                                HorizontalDivider(
                                    thickness = 4.dp,
                                    modifier = Modifier
                                        .width(56.dp)
                                        .align(Alignment.CenterHorizontally),
                                    color = MaterialTheme.colorScheme.background
                                )
                            }
                            this@NavigationBar.NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.background,
                                    unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                                    indicatorColor = Color.Transparent
                                ),
                                selected = currentRoute == item.route,
                                onClick = {
                                    if (currentRoute != item.route) {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    }
                                }
                            )

                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = currentScreen,
            modifier = modifier
                .padding(paddingValues)
        ) {
            composable(AppScreens.LOGIN.name) {
                Login_Screen(
                    onContinueClicked = {
                        navController.navigate("${AppScreens.OTP.name}/$it")
                    },
                    authViewModel = authViewModel
                )
            }
            composable(
                route = "${AppScreens.OTP.name}/{phoneNumber}",
                arguments = listOf(
                    navArgument("phoneNumber") { type = NavType.StringType }
                )
            ) { navBackStackEntry ->
                val phoneNumber = navBackStackEntry.arguments?.getString("phoneNumber")
                OtpScreen(
                    onVerifyClicked = {
                        navController.navigate(AppScreens.HOME.name)
                    },
                    phoneNumber = phoneNumber,
                    authViewModel = authViewModel
                )
            }
            composable(AppScreens.HOME.name) {
                Home_Screen(
                    viewModel = mainViewModel
                )
            }
            composable(AppScreens.ADD_PRODUCT.name) {
                Add_Product_Screen(
                    viewModel = mainViewModel
                )
            }
            composable(AppScreens.ORDER_DETAILS.name) {
                Ordered_Products_Screen(
                    mainViewModel,
                    onNextIconClicked = { userId, orderId ->
                        navController.navigate("${AppScreens.ORDER_SUMMARY.name}/$userId/$orderId")
                    }
                )
            }
            composable(
                route = "${AppScreens.ORDER_SUMMARY.name}/{userId}/{orderId}",
                arguments = listOf(
                    navArgument("orderId") { type = NavType.StringType },
                    navArgument("userId") { type = NavType.StringType }
                )
            ) {
                val orderId = it.arguments?.getString("orderId")
                val userId = it.arguments?.getString("userId")

                Order_Summary(
                    orderId = orderId,
                    userId = userId,
                    onNavBackClicked = { navController.navigateUp() },
                    mainViewModel
                )
            }

            composable(AppScreens.SEARCH.name) {
                Search_Screen(
                    onNavBackClicked = { navController.navigateUp() },
                    viewModel = mainViewModel
                )
            }
        }
    }
}