package com.pc.genzwardrobe.ui.presentation.home_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Support
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.pc.genzwardrobe.R
import com.pc.genzwardrobe.ui.presentation.components.Drawer_Item
import com.pc.genzwardrobe.ui.presentation.components.Gender_Drawer_Content
import com.pc.genzwardrobe.ui.presentation.components.HomePage_AppDetails
import com.pc.genzwardrobe.ui.presentation.components.Home_Page_Card
import com.pc.genzwardrobe.ui.presentation.components.MainTopAppBar
import com.pc.genzwardrobe.ui.presentation.components.Top_Sellers_List
import com.pc.genzwardrobe.ui.presentation.my_account_screens.LogOut_Delete_Alert_Box
import com.pc.genzwardrobe.utils.Utils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home_Screen(
    homeScreenViewModel: HomeScreenViewModel,
    onSeachCardClicked: () -> Unit,
    onHomePageCardClicked: (String, String, String) -> Unit,
    onDrawerProductTypeClicked: (String, String, String) -> Unit,
    onCartIconClicked: () -> Unit,
    onWishlistClicked: () -> Unit,
    onPersonalInformationClicked: () -> Unit,
    onMyOrdersClicked: () -> Unit,
    onManageAddressClicked: () -> Unit,
    onPaymentMethodClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val genderList = Utils.gendersList

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val allProductVariant by homeScreenViewModel.allProductVariant.collectAsState()
    val genderProductVariant by homeScreenViewModel.productVariantsByGender.collectAsState()
    var myAccount by remember { mutableStateOf(false) }
    var confirmLogOut by remember { mutableStateOf(false) }
    var selectedTopSellerGender by remember { mutableStateOf("") }

    LaunchedEffect(key1 = selectedTopSellerGender) {
        if (selectedTopSellerGender != "All") homeScreenViewModel.fetchProductVariantsByGender(
            selectedTopSellerGender
        )
    }

    if (confirmLogOut) {
        LogOut_Delete_Alert_Box(
            onDismissRequest = { confirmLogOut = false },
            text = "Are you sure want to log out?",
            onDismissButtonClicked = { confirmLogOut = false },
            onConfirmButtonClicked = {
                homeScreenViewModel.logOut()
                onLogoutClicked()
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.75f),
                drawerShape = RectangleShape,
                drawerContainerColor = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Gender_Drawer_Content(
                        onItemSelect = { gender, category, type ->
                            onDrawerProductTypeClicked(gender, category, type)
                        }
                    )
                    Drawer_Animated_Content(
                        title = "My Account",
                        isExpanded = myAccount,
                        onExpandChange = { myAccount = it },
                        content = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Drawer_My_Account(
                                    text = "Personal Information",
                                    onTextClicked = {
                                        onPersonalInformationClicked()
                                    }
                                )
                                Drawer_My_Account(
                                    text = "My Orders",
                                    onTextClicked = {
                                        onMyOrdersClicked()
                                    }
                                )
                                Drawer_My_Account(
                                    text = "Manage Address",
                                    onTextClicked = {
                                        onManageAddressClicked()
                                    }
                                )
                                Drawer_My_Account(
                                    text = "Payment Method",
                                    onTextClicked = {
                                        onPaymentMethodClicked()
                                    }
                                )
                                Drawer_My_Account(
                                    text = "Logout",
                                    onTextClicked = { confirmLogOut = true }
                                )
                            }
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                MainTopAppBar(
                    onMenuClicked = {
                        coroutineScope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    },
                    onWishlistClicked = { onWishlistClicked() },
                    onCartClicked = { onCartIconClicked() },
                )
            }
        ) { paddingValues ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(width = 2.dp, color = Color.Gray),
                    onClick = { onSeachCardClicked() },
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Search...",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Home_Page_Card(
                    buttonText1 = "Dresses & Jumpsuits",
                    image1 = R.drawable.dresses_jumpsuit,
                    buttonText2 = "Tops",
                    image2 = R.drawable.womens_top,
                    onCard1Clicked = { type ->
                        onHomePageCardClicked("Women's", "Topwear", type)
                    },
                    onCard2Clicked = { type ->
                        onHomePageCardClicked("Women's", "Topwear", type)
                    }
                )
                Home_Page_Card(
                    buttonText1 = "Casual Shirts",
                    image1 = R.drawable.casual_shirt,
                    buttonText2 = "Jackets",
                    image2 = R.drawable.mens_jacket,
                    onCard1Clicked = { type ->
                        onHomePageCardClicked("Men's", "Topwear", type)
                    },
                    onCard2Clicked = { type ->
                        onHomePageCardClicked("Men's", "Topwear", type)
                    }
                )
                Home_Page_Card(
                    buttonText1 = "Joggers",
                    image1 = R.drawable.joggers,
                    buttonText2 = "Jeans",
                    image2 = R.drawable.mens_jeans,
                    onCard1Clicked = { type ->
                        onHomePageCardClicked("Men's", "Bottomwear", type)
                    },
                    onCard2Clicked = { type ->
                        onHomePageCardClicked("Women's", "Bottomwear", type)
                    }
                )
                Spacer(Modifier.height(16.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HomePage_AppDetails(
                        icon = Icons.Outlined.LocalShipping,
                        text1 = "Free Shipping",
                        text2 = "Free shipping for order above ₹499"
                    )
                    HomePage_AppDetails(
                        icon = Icons.Outlined.Payments,
                        text1 = "Flexible Payment",
                        text2 = "Multiple secure payment options"
                    )
                    HomePage_AppDetails(
                        icon = Icons.Outlined.Support,
                        text1 = "24x7 Support",
                        text2 = "We support online all days."
                    )
                }
                Spacer(Modifier.height(16.dp))
                Top_Sellers_List(
                    allUiState = allProductVariant,
                    genderUiState = genderProductVariant,
                    onCategoryClicked = { genderCategory ->
                        selectedTopSellerGender = genderCategory.text
                    },
                    genderList = genderList
                )
            }
        }
    }
}

@Composable
fun Drawer_My_Account(
    text: String,
    onTextClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onTextClicked() }
    )
}

@Composable
fun Drawer_Animated_Content(
    isExpanded: Boolean,
    title: String,
    onExpandChange: (Boolean) -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Drawer_Item(
            title = title,
            isExpanded = isExpanded,
            onExpandChange = { onExpandChange(it) }
        )
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            content()
        }
    }
}