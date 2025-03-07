package com.pc.genzwardrobe.ui.presentation.product_details_screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.pc.genzwardrobe.R
import com.pc.genzwardrobe.core.domain.UserReview
import com.pc.genzwardrobe.ui.presentation.components.CustomTextField
import com.pc.genzwardrobe.ui.presentation.components.CustomTopAppBar
import com.pc.genzwardrobe.ui.presentation.components.Product_Image
import com.pc.genzwardrobe.ui.presentation.gender_product_screen.Icon_Text_Button
import com.pc.genzwardrobe.ui.presentation.home_screen.HomeScreenViewModel
import com.pc.genzwardrobe.utils.Utils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Add_Review_Screen(
    productId: String?,
    variantColor: String?,
    onNavBackClicked: () -> Unit,
    homeScreenViewModel: HomeScreenViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var selectedColor by remember { mutableStateOf("") }
    var selectedName by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf("") }

    val product by homeScreenViewModel.getReviewingProduct.collectAsState()

    LaunchedEffect(productId) {
        if (productId != null) {
            homeScreenViewModel.getReviewItem(productId)
        }
    }
    LaunchedEffect(product) {
        product?.productVariants?.let { variants ->
            for (variant in variants) {
                val key = variant.key

                if (variantColor == key) {
                    selectedColor = variant.value.color
                    selectedName = variant.value.variantName.toString()
                    selectedImage = variant.value.variantImages[0]!!
                }
            }
        }
    }

    val state = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    var selectedRating by remember { mutableIntStateOf(-1) }
    var selectedText by remember { mutableStateOf("") }

    var reviewText by remember { mutableStateOf("") }

    val selectedReviewImages = homeScreenViewModel.selectedProductReviewImages.collectAsState()

    var hasStoragePermission by remember { mutableStateOf(false) }
    var shouldShowRational by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        emptyArray()
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    LaunchedEffect(Unit) {
        hasStoragePermission = permissionToRequest.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allPermissionsGranted = permissions.all { it.value }

        if (allPermissionsGranted) {
            hasStoragePermission = true
        } else {
            shouldShowRational = true
        }
    }

    val getImages = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { homeScreenViewModel.selectReviewImages(it) }

    if (showPermissionDialog) {
        AlertDialog(
            title = {
                Text("Need image permission")
            },
            confirmButton = {
                Button(
                    onClick = {
                        Utils.showToast(context, "Storage Permission Granted")
                        launcher.launch(permissionToRequest)
                        showPermissionDialog = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showPermissionDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            },
            onDismissRequest = {
                showPermissionDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                text = "Review Product",
                onIconClicked = { onNavBackClicked() },
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) {
            HorizontalPager(
                state = state,
                userScrollEnabled = selectedText.isNotEmpty(),
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> {
                        Page_1(
                            selectedName,
                            selectedImage,
                            onStarClicked = { selectedStar, selectedStarText ->
                                selectedRating = selectedStar.plus(1)
                                selectedText = selectedStarText
                                coroutineScope.launch {
                                    state.animateScrollToPage(1)
                                }
                            }
                        )
                    }

                    1 -> {
                        Page_2(
                            image = selectedImage,
                            variantName = selectedName,
                            onAddPhotoVideoClicked = { index ->
                                when(index) {
                                    0 -> {
                                        if (hasStoragePermission) {
                                            Utils.showToast(context, "Storage permission granted!")
                                            getImages.launch("image/*")
                                        } else {
                                            if (shouldShowRational) {
                                                showPermissionDialog = true
                                            } else {
                                                launcher.launch(permissionToRequest)
                                            }
                                        }
                                    }
                                    1 -> {}
                                }
                            },
                            initialReviewValue = reviewText,
                            onReviewValueChange = { newReviewText -> reviewText = newReviewText},
                            onFinishClicked = { review ->

                                coroutineScope.launch {
                                    val images = selectedReviewImages.value.map { uri ->
                                        homeScreenViewModel.uploadReviewImages(uri)
                                    }
                                    val reviewItem = UserReview(
                                        userId = Utils.getCurrentUserId().toString(),
                                        userName = "",
                                        images = images,
                                        rating = selectedRating,
                                        comment = review,
                                        color = variantColor.toString(),
                                        title = selectedText,
                                        timeStamp = System.currentTimeMillis()
                                    )
                                    homeScreenViewModel.addReview(
                                        productId = product?.productId.toString(),
                                        productGender = product?.productGender.toString(),
                                        productCategory = product?.productCategory.toString(),
                                        productType = product?.productType.toString(),
                                        color = selectedColor,
                                        userReview = reviewItem
                                    )
                                    onNavBackClicked()
                                }
                            }
                        )
                    }
                }
            }
            Pager_Indicators(state)
        }
    }
}

@Composable
fun Pager_Indicators(
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pagerState.pageCount) {
            val color = if (pagerState.currentPage == it) Color.Blue else Color.LightGray

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(9.dp)
            )
        }
    }
}

@Composable
fun Page_1(
    variantName: String?,
    variantImage: String?,
    onStarClicked: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Product_Image(
            image = variantImage.toString(),
            modifier = Modifier
                .size(120.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = variantName.toString(),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(46.dp))
        Text(
            text = "Rate the product",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "How did you find this product based on your usage?",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(22.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(5) {
                val text = when (it) {
                    0 -> "Hated it"
                    1 -> "Didn't Like"
                    2 -> "Was Ok"
                    3 -> "Liked"
                    4 -> "Loved it"
                    else -> ""
                }
                Column(
                    modifier = Modifier
                        .clickable { onStarClicked(it, text) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = "Star",
                        modifier = Modifier
                            .size(34.dp)
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun Page_2(
    image: String?,
    variantName: String?,
    onAddPhotoVideoClicked: (Int) -> Unit,
    initialReviewValue: String,
    onReviewValueChange: (String) -> Unit,
    onFinishClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonTexts = listOf("Add Photo", "Add Video")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier.fillMaxWidth()
        ) {
            Product_Image(
                image = image.toString(),
                modifier = Modifier
                    .size(60.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = variantName.toString(),
                style = MaterialTheme.typography.titleLarge
            )
        }
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Add Photo or Video",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            buttonTexts.forEachIndexed { index, text ->
                val icon =
                    if (text == "Add Photo") Icons.Default.AddAPhoto else Icons.Default.Videocam
                Icon_Text_Button(
                    onButtonClicked = { onAddPhotoVideoClicked(index) },
                    icon = icon,
                    text = text
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.return_image_video_type),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(Modifier.height(16.dp))

        Text(
            text = "Write a review",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(8.dp))
        CustomTextField(
            initialValue = initialReviewValue,
            onInitialValueChanged = { onReviewValueChange(it) },
            keyboardType = KeyboardType.Text,
            singleLine = false,
            label = "How is the product? What do you like? What do you hate?",
            modifier = Modifier.height(150.dp)
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = { onFinishClicked(initialReviewValue) },
                enabled = initialReviewValue.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                shape = RectangleShape
            ) {
                Text("Continue")
            }
        }
    }
}