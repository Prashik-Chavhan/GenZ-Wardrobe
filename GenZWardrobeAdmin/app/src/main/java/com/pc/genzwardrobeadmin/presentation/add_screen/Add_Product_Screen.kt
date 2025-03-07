package com.pc.genzwardrobeadmin.presentation.add_screen

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.pc.genzwardrobeadmin.R
import com.pc.genzwardrobeadmin.core.domain.product.Product
import com.pc.genzwardrobeadmin.core.domain.product.ProductHighlight
import com.pc.genzwardrobeadmin.core.domain.product.ProductVariant
import com.pc.genzwardrobeadmin.core.domain.product.SizeStock
import com.pc.genzwardrobeadmin.presentation.AddProductUiState
import com.pc.genzwardrobeadmin.presentation.MainViewModel
import com.pc.genzwardrobeadmin.presentation.add_screen.components.Add_Or_Edit_Product
import com.pc.genzwardrobeadmin.presentation.add_screen.components.CustomTextField
import com.pc.genzwardrobeadmin.presentation.add_screen.components.PermissionDialog
import com.pc.genzwardrobeadmin.presentation.home_screen.Product_Image
import com.pc.genzwardrobeadmin.utils.Utils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Add_Product_Screen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val selectedProductImages = viewModel.selectedProductImages.collectAsState()
    val addProductUiState = viewModel.addProductUiState.collectAsState()
    val selectedImage = viewModel.selectedImage.collectAsState()

    var productFabric by rememberSaveable { mutableStateOf("") }
    var productSleeve by rememberSaveable { mutableStateOf("") }
    var productCollar by rememberSaveable { mutableStateOf("") }
    var productPattern by rememberSaveable { mutableStateOf("") }
    var productFaded by rememberSaveable { mutableStateOf("") }
    var productRise by rememberSaveable { mutableStateOf("") }
    var productDistressed by rememberSaveable { mutableStateOf("") }
    var productBrand by rememberSaveable { mutableStateOf("") }
    var selectedGender by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var highlightDescription by rememberSaveable { mutableStateOf("") }
    var highlightTitle by rememberSaveable { mutableStateOf("") }

    var occasionValue by rememberSaveable { mutableStateOf("") }
    var closerValue by rememberSaveable { mutableStateOf("") }

    val currentTimeMillis = System.currentTimeMillis()

    var variantTitle by rememberSaveable { mutableStateOf("") }
    var variantColor by rememberSaveable { mutableStateOf("") }
    var sizeName by rememberSaveable { mutableStateOf("") }
    var variantOriginalPrice by rememberSaveable { mutableStateOf("") }
    var variantDiscount by rememberSaveable { mutableStateOf("") }
    var sizePrice by rememberSaveable { mutableStateOf("") }
    var sizeStock by rememberSaveable { mutableStateOf("") }
    val variants = remember { mutableStateMapOf<String, ProductVariant>() }
    val productHighlight = remember { mutableStateMapOf<String, ProductHighlight>() }
    var isLoadingAddingVariant by remember { mutableStateOf(false) }

    var showPermissionDialog by remember { mutableStateOf(false) }
    var showLoadingDialog by remember { mutableStateOf(false) }
    var showHighlightLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    var hasStoragePermission by remember { mutableStateOf(false) }
    var shouldShowRationale by remember { mutableStateOf(false) }
    val context = LocalContext.current


    val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Scoped storage, no need for specific permissions in most cases
        emptyArray()
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    LaunchedEffect(Unit) {
        hasStoragePermission = permissionsToRequest.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        val allPermissionsGranted = permission.all { it.value }

        if (allPermissionsGranted) {
            hasStoragePermission = true
            Log.d("StoragePermission", "Permission granted")
        } else {
            shouldShowRationale = true
            Log.d("StoragePermission", "Permission denied")
        }
    }

    val getProductImages =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uri: List<Uri> ->
            viewModel.selectProductImages(uri)
        }

    val getImage =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            viewModel.selectImage(uri)
        }

    if (showPermissionDialog) {
        PermissionDialog(
            onPermissionGranted = {
                Utils.showToast(context, "Storage Permission Granted")
                launcher.launch(permissionsToRequest)
            },
            onPermissionDenied = {
                showPermissionDialog = false
            },
            onDismissClicked = {
                showPermissionDialog = false
            }
        )
    }

    if (showLoadingDialog || isLoadingAddingVariant || showHighlightLoading) {
        BasicAlertDialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = false
            ),
            modifier = Modifier
                .height(120.dp)
                .shadow(
                    elevation = 3.dp
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "We are processing, please wait...",
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        LaunchedEffect(key1 = addProductUiState.value) {
            when (addProductUiState.value) {
                is AddProductUiState.Loading -> {
                    showLoadingDialog = true
                }

                is AddProductUiState.Success -> {
                    showLoadingDialog = false
                    Utils.showToast(context, "Product is live now!!")
                }

                is AddProductUiState.Error -> {
                    showLoadingDialog = false
                    Utils.showToast(
                        context,
                        "Error: ${(addProductUiState.value as AddProductUiState.Error).message}"
                    )
                }

                else -> {
                    showLoadingDialog = false
                }
            }
        }
    }
//
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Add_Or_Edit_Product(
            initialProductBrand = productBrand,
            onProductBrandChanged = { productBrand = it },
            onSelectedTypeChange = { selectedType = it },
            selectedCategory = selectedCategory,
            onSelectedCategoryChange = { selectedCategory = it },
            selectedType = selectedType,
            initialDescriptionValue = description,
            onDescriptionChange = { description = it },
            initialFabricValue = productFabric,
            onFabricChanged = { productFabric = it },
            initialPatternValue = productPattern,
            onPatternValueChanged = { productPattern = it },
            initialRiseValue = productRise,
            onRiseValueChanged = { productRise = it },
            initialFadedValue = productFaded,
            onFadedValueChanged = { productFaded = it },
            initialSleeveType = productSleeve,
            onSleeveTypeChanged = { productSleeve = it },
            initialCollarValue = productCollar,
            onCollarValueChanged = { productCollar = it },
            initialDistressedValue = productDistressed,
            onDistressedValueChanged = { productDistressed = it },
            selectedGender = selectedGender,
            onSelectedGenderChange = { selectedGender = it },
            initialOccasionValue = occasionValue,
            onOccasionChanged = { occasionValue = it },
            initialCloserValue = closerValue,
            onCloserValueChanged = { closerValue = it }
        )
        CustomTextField(
            variantTitle,
            { variantTitle = it },
            "Variant Title",
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CustomTextField(
                variantColor,
                { variantColor = it },
                "Color",
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.weight(0.33f)
            )
            CustomTextField(
                variantOriginalPrice,
                { variantOriginalPrice = it },
                "Real Price",
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true,
                modifier = Modifier.weight(0.33f)
            )
            CustomTextField(
                variantDiscount,
                { variantDiscount = it },
                "Discount %",
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true,
                modifier = Modifier.weight(0.33f)
            )

        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CustomTextField(
                sizeName,
                { sizeName = it },
                "Size Name",
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.weight(0.5f)
            )
            CustomTextField(
                sizePrice,
                { sizePrice = it },
                "Size Price",
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true,
                modifier = Modifier.weight(0.5f)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CustomTextField(
                sizeStock,
                { sizeStock = it },
                "Size Stock",
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.weight(0.5f)
            )
            Custom_Button(
                buttonText = "Add Size",
                onButtonClicked = {
                    if (sizeName.isEmpty() && sizePrice.isEmpty() && sizeStock.isEmpty()) {
                        Utils.showToast(context, "Empty fields are not allowed")
                    } else {
                        val sizeDetails = SizeStock(
                            price = sizePrice.toDoubleOrNull() ?: 0.0,
                            stock = sizeStock.toIntOrNull() ?: 0
                        )
                        val existingVariant =
                            variants[variantColor] ?: ProductVariant(color = variantColor)
                        val updatedSizes = existingVariant.sizeDetails.toMutableMap().apply {
                            put(key = sizeName, value = sizeDetails)
                        }
                        variants[variantColor] = existingVariant.copy(sizeDetails = updatedSizes)
                        sizeName = ""
                        sizePrice = ""
                        sizeStock = ""
                    }
                },
                modifier = Modifier.weight(0.5f)
            )

        }
        if (selectedProductImages.value.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(selectedProductImages.value) { productUri ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd) {
                        Product_Image(
                            image = productUri.toString(),
                            modifier = Modifier.size(90.dp)
                        )
                        IconButton(
                            onClick = { viewModel.removeImage(productUri) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Image"
                            )
                        }
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Product Images will shown here")
            }
        }
        Custom_Button(
            buttonText = "Select Product Images",
            onButtonClicked = {
                if (hasStoragePermission) {
                    Utils.showToast(context, "Storage permission granted!")
                    getProductImages.launch("image/*")
                } else {
                    if (shouldShowRationale) {
                        showPermissionDialog = true
                    } else {
                        launcher.launch(permissionsToRequest)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Custom_Button(
            buttonText = "Add Product Variant",
            onButtonClicked = {
                isLoadingAddingVariant = true
                coroutineScope.launch {
                    val imageUri = selectedProductImages.value.map { uri ->
                        viewModel.uploadVariantImages(
                            image = uri,
                            folder1 = "Product variant images",
                            folder2 = "$selectedGender $selectedType",
                            folder3 = "$variantTitle $variantColor"
                        )
                    }

                    val variant = ProductVariant(
                        variantName = variantTitle,
                        color = variantColor,
                        sellCount = 0,
                        originalPrice = variantOriginalPrice.toInt(),
                        discount = variantDiscount.toInt(),
                        sizeDetails = variants[variantColor]?.sizeDetails ?: emptyMap(),
                        variantImages = imageUri
                    )

                    variants[variantColor] = variant
                    variantTitle = ""
                    variantColor = ""
                    variantOriginalPrice = ""
                    variantDiscount = ""
                    viewModel.clearImages()
                    isLoadingAddingVariant = false
                }
            },
            modifier = Modifier.fillMaxWidth()

        )
        Added_Variant_Details(variants = variants)
        CustomTextField(
            value = highlightTitle,
            onValueChange = { highlightTitle = it },
            label = "Title",
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        CustomTextField(
            value = highlightDescription,
            onValueChange = { highlightDescription = it },
            label = "Highlight description",
            singleLine = false,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Custom_Button(
                buttonText = "Add Image",
                onButtonClicked = {
                    if (hasStoragePermission) {
                        Utils.showToast(context, "Storage permission granted!")
                        getImage.launch("image/*")
                    } else {
                        if (shouldShowRationale) {
                            showPermissionDialog = true
                        } else {
                            launcher.launch(permissionsToRequest)
                        }
                    }
                },
                modifier = Modifier.weight(0.5f)

            )
            Custom_Button(
                buttonText = "Add Highlight",
                onButtonClicked = {
                    showHighlightLoading = true
                    selectedImage.value.let {
                        coroutineScope.launch {
                            if (it != null) {
                                val image = viewModel.uploadVariantImages(
                                    it,
                                    "Product highlight images",
                                    "$selectedGender $selectedCategory",
                                    selectedType
                                )
                                val productHighlights = ProductHighlight(
                                    title = highlightTitle,
                                    description = highlightDescription,
                                    image = image
                                )
                                productHighlight[highlightTitle] = productHighlights
                                highlightTitle = ""
                                highlightDescription = ""
                                viewModel.clearImage()
                                showHighlightLoading = false
                            }
                        }
                    }
                },
                modifier = Modifier.weight(0.5f)
            )
        }

        Custom_Button(
            buttonText = "Add Product",
            onButtonClicked = {
                if (selectedCategory.isEmpty() || selectedType.isEmpty() || description.isEmpty()) {
                    Utils.showToast(context, "Empty fields are not allowed")
                } else {
                    showLoadingDialog = true
                    val product = Product(
                        productId = Utils.generateRandomId(),
                        productCategory = selectedCategory,
                        productType = selectedType,
                        productGender = selectedGender,
                        productBrand = productBrand,
                        productVariants = variants.toMap(),
                        dateAdded = currentTimeMillis,
                        fabric = productFabric,
                        sleeveType = productSleeve,
                        collarType = productCollar,
                        pattern = productPattern,
                        rise = productRise,
                        distressed = productDistressed,
                        faded = productFaded,
                        description = description,
                        returnPolicy = R.string.return_policy,
                        productHighlight = productHighlight.toMap(),
                        occasion = occasionValue,
                        closure = closerValue,
                    )
                    viewModel.addProduct(product)
                    selectedGender = ""
                    selectedCategory = ""
                    description = ""
                    productFabric = ""
                    productSleeve = ""
                    variantTitle = ""
                    selectedType = ""
                    productBrand = ""
                    variants.clear()
                    showLoadingDialog = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun Custom_Button(
    buttonText: String,
    onButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onButtonClicked() },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        contentPadding = PaddingValues(14.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Text(
            text = buttonText,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun Added_Variant_Details(
    variants: Map<String, ProductVariant>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Added variants will shown here",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)

                ) {
                    Added_Variant_Info("Color")
                    Added_Variant_Info("Sizes")
                    Added_Variant_Info("Images")
                }
            }
            items(variants.entries.toList()) { (key, productVariant) ->
                Added_Variant_Item(
                    key,
                    productVariant.sizeDetails.size,
                    productVariant.variantImages.size
                )
            }
        }
    }
}

@Composable
fun Added_Variant_Info(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "$text:",
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
    )
}

@Composable
fun Added_Variant_Item(
    key: String,
    sizes: Int,
    images: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = key
        )
        Text(
            text = "$sizes"
        )
        Text(text = "$images")
    }
}