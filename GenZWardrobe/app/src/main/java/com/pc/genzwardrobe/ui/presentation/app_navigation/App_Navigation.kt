package com.pc.genzwardrobe.ui.presentation.app_navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.pc.genzwardrobe.ui.presentation.auth.Add_Personal_Info
import com.pc.genzwardrobe.ui.presentation.auth.AuthViewModel
import com.pc.genzwardrobe.ui.presentation.auth.Login_Screen
import com.pc.genzwardrobe.ui.presentation.auth.OtpScreen
import com.pc.genzwardrobe.ui.presentation.cart_screen.CartViewModel
import com.pc.genzwardrobe.ui.presentation.cart_screen.Cart_Screen
import com.pc.genzwardrobe.ui.presentation.gender_product_screen.Gender_Product_Screen
import com.pc.genzwardrobe.ui.presentation.home_screen.HomeScreenViewModel
import com.pc.genzwardrobe.ui.presentation.home_screen.Home_Screen
import com.pc.genzwardrobe.ui.presentation.home_screen.Search_Screen
import com.pc.genzwardrobe.ui.presentation.home_screen.Wishlist_Products
import com.pc.genzwardrobe.ui.presentation.my_account_screens.Manage_Address_Screen
import com.pc.genzwardrobe.ui.presentation.my_account_screens.Payment_Methods
import com.pc.genzwardrobe.ui.presentation.my_account_screens.Personal_Info
import com.pc.genzwardrobe.ui.presentation.my_account_screens.my_orders.My_Order_Detail
import com.pc.genzwardrobe.ui.presentation.my_account_screens.my_orders.My_Orders_Screen
import com.pc.genzwardrobe.ui.presentation.my_account_screens.my_orders.return_request.Return_Request
import com.pc.genzwardrobe.ui.presentation.order_placing_screens.Add_Address_Screen
import com.pc.genzwardrobe.ui.presentation.order_placing_screens.Order_Success
import com.pc.genzwardrobe.ui.presentation.order_placing_screens.Order_Summary_Screen
import com.pc.genzwardrobe.ui.presentation.product_details_screen.Add_Review_Screen
import com.pc.genzwardrobe.ui.presentation.product_details_screen.All_Reviews
import com.pc.genzwardrobe.ui.presentation.product_details_screen.Product_Details_Screen
import com.pc.genzwardrobe.ui.presentation.product_details_screen.Product_Highlight
import com.pc.genzwardrobe.ui.presentation.product_details_screen.Similar_Products

enum class AppScreens {
    LOGIN,
    OTP,
    ADD_PERSONAL_INFO,
    HOME,
    PERSONAL_INFORMATION,
    SEARCH_SCREEN,
    GENDER_PRODUCTS,
    PRODUCT_DETAILS_SCREEN,
    PRODUCT_HIGHLIGHT,
    SIMILAR_PRODUCTS,
    ADD_REVIEW,
    CART_VIEW,
    ADD_ADDRESS,
    ORDER_SUMMARY,
    ORDER_SUCCESS,
    MANAGE_ADDRESS,
    MY_ORDERS,
    PAYMENT_METHOD,
    MY_ORDER_DETAIL,
    WISHLIST_PRODUCTS,
    RETURN_REQUEST,
    ALL_REVIEWS
}

@Composable
fun App_Navigation(
    modifier: Modifier = Modifier
) {
    val navController: NavHostController = rememberNavController()

    val currentUser = FirebaseAuth.getInstance().currentUser
    val currentScreen = if (currentUser != null) AppScreens.HOME.name else AppScreens.LOGIN.name

    val authViewModel: AuthViewModel = hiltViewModel()
    val homeScreenViewModel: HomeScreenViewModel = hiltViewModel()
    val cartViewModel: CartViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = currentScreen,
        modifier = modifier,
        enterTransition = {
            slideInHorizontally(
                animationSpec = tween(600)
            ) + fadeIn()
        },
        exitTransition = {
            slideOutHorizontally(
                animationSpec = tween(600)
            ) + fadeOut()
        },
        popEnterTransition = {
            slideInHorizontally(
                animationSpec = tween(600)
            ) + fadeIn()
        },
        popExitTransition = {
            slideOutHorizontally(
                animationSpec = tween(600)
            ) + fadeOut()
        }

    ) {
        composable(AppScreens.LOGIN.name) {
            Login_Screen(
                onContinueClicked = {
                    navController.navigate(AppScreens.OTP.name)
                },
                authViewModel = authViewModel
            )
        }

        composable(route = AppScreens.OTP.name) {
            OtpScreen(
                onVerifyClicked = {
                    navController.navigate(AppScreens.ADD_PERSONAL_INFO.name)
                },
                onEditButtonClicked = { navController.navigateUp() },
                authViewModel = authViewModel
            )
        }

        composable(AppScreens.ADD_PERSONAL_INFO.name) {
            Add_Personal_Info(
                homeScreenViewModel = homeScreenViewModel,
                onSkipClicked = {
                    navController.navigate(AppScreens.HOME.name) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                onInfoSaved = {
                    navController.navigate(AppScreens.HOME.name) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(
            route = AppScreens.HOME.name
        ) {
            Home_Screen(
                homeScreenViewModel = homeScreenViewModel,
                onSeachCardClicked = {
                    navController.navigate(AppScreens.SEARCH_SCREEN.name)
                },
                onHomePageCardClicked = { gender, category, type ->
                    navController.navigate("${AppScreens.GENDER_PRODUCTS.name}/$gender/$category/$type")
                },
                onDrawerProductTypeClicked = { gender, category, type ->
                    navController.navigate("${AppScreens.GENDER_PRODUCTS.name}/$gender/$category/$type")
                },
                onCartIconClicked = {
                    navController.navigate(AppScreens.CART_VIEW.name)
                },
                onPersonalInformationClicked = {
                    navController.navigate(AppScreens.PERSONAL_INFORMATION.name)
                },
                onMyOrdersClicked = {
                    navController.navigate(AppScreens.MY_ORDERS.name)
                },
                onManageAddressClicked = {
                    navController.navigate(AppScreens.MANAGE_ADDRESS.name)
                },
                onPaymentMethodClicked = {
                    navController.navigate(AppScreens.PAYMENT_METHOD.name)
                },
                onLogoutClicked = {
                    navController.navigate(AppScreens.LOGIN.name) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                onWishlistClicked = { navController.navigate(AppScreens.WISHLIST_PRODUCTS.name) }
            )
        }

        composable(AppScreens.SEARCH_SCREEN.name) {
            Search_Screen(
                homeScreenViewModel = homeScreenViewModel,
                onNavBackClicked = { navController.navigateUp() },
                onSearchedItemClicked = { productId, variantColor ->
                    navController.navigate("${AppScreens.PRODUCT_DETAILS_SCREEN.name}/$productId/$variantColor")
                }
            )
        }

        composable(AppScreens.WISHLIST_PRODUCTS.name) {
            Wishlist_Products(
                onItemClicked = { productId, variantColor ->
                    navController.navigate("${AppScreens.PRODUCT_DETAILS_SCREEN.name}/$productId/$variantColor")
                },
                onBackClicked = { navController.navigateUp() }
            )
        }

        composable(
            route = "${AppScreens.GENDER_PRODUCTS.name}/{gender}/{category}/{type}",
            arguments = listOf(
                navArgument("gender") { type = NavType.StringType },
                navArgument("category") { type = NavType.StringType },
                navArgument("type") { type = NavType.StringType }
            )
        ) {
            val gender = it.arguments?.getString("gender")
            val category = it.arguments?.getString("category")
            val type = it.arguments?.getString("type")

            Gender_Product_Screen(
                homeScreenViewModel = homeScreenViewModel,
                gender = gender,
                category = category,
                type = type,
                onArrowBackIconClicked = { navController.navigateUp() },
                onVariantClicked = { productId, variantColor ->
                    navController.navigate("${AppScreens.PRODUCT_DETAILS_SCREEN.name}/$productId/$variantColor")
                }
            )
        }
        composable(
            route = "${AppScreens.PRODUCT_DETAILS_SCREEN.name}/{productId}/{variantColor}",
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType },
                navArgument("variantColor") { type = NavType.StringType }
            )
        ) {
            val productId = it.arguments?.getString("productId")
            val variantColor = it.arguments?.getString("variantColor")

            Product_Details_Screen(
                productId = productId,
                variantColor = variantColor,
                homeScreenViewModel = homeScreenViewModel,
                isAddressNotSaved = {
                    navController.navigate(AppScreens.ADD_ADDRESS.name)
                },
                isAddressSaved = { itemId ->
                    navController.navigate("${AppScreens.ORDER_SUMMARY.name}/$itemId")
                },
                cartViewModel = cartViewModel,
                goToCart = {
                    navController.navigate(AppScreens.CART_VIEW.name)
                },
                onAllDetailsClicked = { id ->
                    navController.navigate("${AppScreens.PRODUCT_HIGHLIGHT.name}/$id")
                },
                onAllSimilarClicked = { selectedVariant, productGender, productCategory, productType ->
                    navController.navigate("${AppScreens.SIMILAR_PRODUCTS.name}/$selectedVariant/$productGender/$productCategory/$productType")
                },
                onSimilarItemClicked = { id, color ->
                    navController.navigate("${AppScreens.PRODUCT_DETAILS_SCREEN.name}/$id/$color")
                },
                onRateProductClicked = { id, color ->
                    navController.navigate("${AppScreens.ADD_REVIEW.name}/$id/$color")
                },
                onAllReviewsClicked = { id ->
                    navController.navigate("${AppScreens.ALL_REVIEWS.name}/$id")
                }
            )
        }

        composable(
            route = "${AppScreens.ALL_REVIEWS.name}/{productId}",
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType }
            )
        ) {
            val productId = it.arguments?.getString("productId")
            All_Reviews(
                productId = productId,
                onNavBackClicked = { navController.navigateUp() },
                viewModel = homeScreenViewModel
            )
        }

        composable(
            route = "${AppScreens.ADD_REVIEW.name}/{productId}/{variantColor}",
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType },
                navArgument("variantColor") { type = NavType.StringType }
            )
        ) {
            val productId = it.arguments?.getString("productId")
            val variantColor = it.arguments?.getString("variantColor")

            Add_Review_Screen(
                productId = productId,
                variantColor = variantColor,
                onNavBackClicked = { navController.navigateUp() },
                homeScreenViewModel
            )
        }

        composable(
            route = "${AppScreens.SIMILAR_PRODUCTS.name}/{selectedVariant}/{productGender}/{productCategory}/{productType}",
            arguments = listOf(
                navArgument("selectedVariant") { type = NavType.StringType },
                navArgument("productGender") { type = NavType.StringType },
                navArgument("productCategory") { type = NavType.StringType },
                navArgument("productType") { type = NavType.StringType }
            )
        ) {

            val selectedVariant = it.arguments?.getString("selectedVariant")
            val productGender = it.arguments?.getString("productGender")
            val productCategory = it.arguments?.getString("productCategory")
            val productType = it.arguments?.getString("productType")

            Similar_Products(
                selectedVariant = selectedVariant,
                productGender = productGender,
                productCategory = productCategory,
                productType = productType,
                onNavBackClicked = {
                    navController.navigateUp()
                },
                homeScreenViewModel = homeScreenViewModel,
                onVariantClicked = { productId, variantColor ->
                    navController.navigate("${AppScreens.PRODUCT_DETAILS_SCREEN.name}/$productId/$variantColor")
                }
            )
        }
        composable(
            route = "${AppScreens.PRODUCT_HIGHLIGHT.name}/{productId}",
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType }
            )
        ) {
            val productId = it.arguments?.getString("productId")
            Product_Highlight(
                productId = productId,
                onBackIconClicked = {
                    navController.navigateUp()
                },
                homeScreenViewModel = homeScreenViewModel
            )
        }

        composable(
            route = AppScreens.CART_VIEW.name
        ) {
            Cart_Screen(
                cartViewModel = cartViewModel,
                isAddressNotSaved = {
                    navController.navigate(AppScreens.ADD_ADDRESS.name)
                },
                isAddressSaved = {
                    navController.navigate("${AppScreens.ORDER_SUMMARY.name}/${-1}")
                },
                homeScreenViewModel = homeScreenViewModel,
                onArrowBackIconClicked = { navController.navigateUp() },
                onBuyNowClicked = {
                    navController.navigate("${AppScreens.ORDER_SUMMARY.name}/$it")
                },
                onItemClicked = { productId, variantColor ->
                    navController.navigate("${AppScreens.PRODUCT_DETAILS_SCREEN.name}/$productId/$variantColor")
                }
            )
        }

        composable(AppScreens.ADD_ADDRESS.name) {
            Add_Address_Screen(
                homeScreenViewModel = homeScreenViewModel,
                onSaveClicked = {
                    navController.navigateUp()
                },
                cartViewModel = cartViewModel,
                onArrowBackIconClicked = { navController.navigateUp() },
            )
        }

        composable(
            route = "${AppScreens.ORDER_SUMMARY.name}/{itemId}",
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            val itemId = it.arguments?.getInt("itemId") ?: -1
            Order_Summary_Screen(
                itemId = itemId,
                cartViewModel = cartViewModel,
                homeScreenViewModel = homeScreenViewModel,
                onArrowBackIconClicked = { navController.navigateUp() },
                onPaymentComplete = { userName, orderId ->
                    navController.navigate("${AppScreens.ORDER_SUCCESS.name}/$userName/$orderId")
                },
                onItemClicked = { productId, variantColor ->
                    navController.navigate("${AppScreens.PRODUCT_DETAILS_SCREEN.name}/$productId/$variantColor")
                }
            )
        }

        composable(
            route = "${AppScreens.ORDER_SUCCESS.name}/{userName}/{orderId}",

            arguments = listOf(
                navArgument("userName") { type = NavType.StringType },
                navArgument("orderId") { type = NavType.StringType },
            )
        ) {
            val userName = it.arguments?.getString("userName")
            val orderId = it.arguments?.getString("orderId")

            Order_Success(
                userName = userName,
                orderId = orderId,
                onNavBackClicked = {

                },
                onContinueShoppingClicked = {
                    navController.navigate(AppScreens.HOME.name) {
                        popUpTo(AppScreens.HOME.name) {
                            inclusive = true
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                }
            )
        }

        composable(AppScreens.MANAGE_ADDRESS.name) {
            Manage_Address_Screen(
                onAddNewAddressClicked = {
                    navController.navigate(AppScreens.ADD_ADDRESS.name)
                },
                onArrowBackIconClicked = { navController.navigateUp() }
            )
        }

        composable(AppScreens.PERSONAL_INFORMATION.name) {
            Personal_Info(
                onNavBackClicked = { navController.navigateUp() },
                onAccountDelete = {
                    navController.navigate(AppScreens.LOGIN.name) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(AppScreens.PAYMENT_METHOD.name) {
            Payment_Methods(
                onNavBackClicked = { navController.navigateUp() },
                viewModel = hiltViewModel(),
                cartViewModel
            )
        }

        composable(AppScreens.MY_ORDERS.name) {
            My_Orders_Screen(
                onItemClicked = { orderId, itemId, totalAmount ->
                    navController.navigate("${AppScreens.MY_ORDER_DETAIL.name}/$orderId/$itemId/$totalAmount")
                },
                onNavBackClicked = { navController.navigateUp() }
            )
        }

        composable(
            route = "${AppScreens.MY_ORDER_DETAIL.name}/{orderId}/{itemId}/{totalAmount}",
            arguments = listOf(
                navArgument(name = "orderId") { type = NavType.StringType },
                navArgument(name = "itemId") { type = NavType.IntType },
                navArgument(name = "totalAmount") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            val orderId = it.arguments?.getString("orderId")
            val itemId = it.arguments?.getInt("itemId")
            val totalAmount = it.arguments?.getInt("totalAmount") ?: -1

            My_Order_Detail(
                orderId = orderId,
                itemId = itemId,
                totalAmount = totalAmount,
                onItemClicked = { oOrderId, oItemId ->
                    navController.navigate("${AppScreens.MY_ORDER_DETAIL.name}/$oOrderId/$oItemId/${-1}")
                },
                onNavBackClicked = { navController.navigateUp() },
                onReturnClicked = { name, price, image, newOrderId, newItemId ->
                    navController.navigate("${AppScreens.RETURN_REQUEST.name}/$name/$price/$image/$newOrderId/$newItemId")
                }
            )
        }

        composable(
            route = "${AppScreens.RETURN_REQUEST.name}/{name}/{price}/{image}/{newOrderId}/{newItemId}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("price") { type = NavType.IntType },
                navArgument("image") { type = NavType.StringType },
                navArgument("newOrderId") { type = NavType.StringType },
                navArgument("newItemId") { type = NavType.IntType }
            )
        ) {
            val name = it.arguments?.getString("name")
            val price = it.arguments?.getInt("price")
            val encodedImage = it.arguments?.getString("image")
            val newOrderId = it.arguments?.getString("newOrderId")
            val newItemId = it.arguments?.getInt("newItemId")

            Return_Request(
                name = name,
                price = price,
                image = encodedImage,
                newOrderId = newOrderId,
                newItemId = newItemId,
                onNavBackClicked = { navController.navigateUp() },
                afterSuccessful = {
                    navController.navigateUp()
                }
            )
        }
    }
}
