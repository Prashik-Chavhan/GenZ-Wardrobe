package com.pc.genzwardrobe.ui.presentation.my_account_screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.pc.genzwardrobe.core.domain.Transactions
import com.pc.genzwardrobe.ui.presentation.cart_screen.CartViewModel
import com.pc.genzwardrobe.ui.presentation.cart_screen.PaymentState
import com.pc.genzwardrobe.ui.presentation.components.Circular_Loader
import com.pc.genzwardrobe.ui.presentation.components.CustomTextField
import com.pc.genzwardrobe.ui.presentation.order_placing_screens.paymentViaWallet
import com.pc.genzwardrobe.ui.presentation.product_details_screen.Custom_Card
import com.pc.genzwardrobe.utils.Utils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Payment_Methods(
    onNavBackClicked: () -> Unit,
    viewModel: MyAccountsViewModel,
    cartViewModel: CartViewModel,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val coroutineScope = rememberCoroutineScope()

    val walletState = viewModel.walletData.collectAsState()
    val paymentState = cartViewModel.paymentState.collectAsState()

    var currentWalletAmount by remember { mutableIntStateOf(0) }

    val context = LocalContext.current
    val activity = context as Activity

    var enteredAmount by remember { mutableStateOf("") }
    val showBottomSheet = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    LaunchedEffect(paymentState.value) {
        when (val state = paymentState.value) {
            is PaymentState.Loading -> {
                Utils.showToast(context, "Loading")
                Log.d("WalletPayment", "Loading")
            }

            is PaymentState.Success -> {
                paymentViaWallet(
                    myAccountsViewModel = viewModel,
                    finalAmount = enteredAmount.toInt(),
                    walletAmount = currentWalletAmount,
                    type = "Deposit"
                )
                enteredAmount = ""
                showBottomSheet.hide()
                Utils.showToast(context, "Successful: ${state.paymentId}")
                Log.d("WalletPayment", "Successful: ${state.paymentId}")
                cartViewModel.resetPaymentState()
            }

            is PaymentState.Error -> {
                Utils.showToast(context, "Error")
                Log.e("WalletPayment", "Error: ${state.message}")
                cartViewModel.resetPaymentState()
            }

            else -> {}
        }
    }

    if (showBottomSheet.isVisible) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Dialog(
                onDismissRequest = { coroutineScope.launch { showBottomSheet.hide() } },
            ) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Enter Amount",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(Modifier.height(16.dp))
                    CustomTextField(
                        initialValue = enteredAmount,
                        onInitialValueChanged = { enteredAmount = it },
                        keyboardType = KeyboardType.Number,
                        label = "0",
                        modifier = Modifier.width(200.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            cartViewModel.startPayment(
                                activity = activity,
                                orderAmount = enteredAmount.toInt(),
                                email = "tester123@testing.com",
                                phoneNumber = Utils.getCurrentUserPhoneNumber() ?: ""
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        ),
                        modifier = Modifier.width(200.dp)
                    ) {
                        Text(
                            text = "Pay",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.secondary,
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Your balance",
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = "₹ $currentWalletAmount",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                        Column {
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        showBottomSheet.show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = "Add balance",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onNavBackClicked() }) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            ""
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (walletState.value) {
            is WalletUiState.Loading -> {
                Circular_Loader(modifier)
            }

            is WalletUiState.Success -> {
                val success = walletState.value as WalletUiState.Success

                currentWalletAmount = success.data.amount ?: 0

                val transactionsList = success.data.transactions?.values?.toList() ?: emptyList()

                if (transactionsList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No transactions",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    Transaction_Item(
                        transactions = transactionsList.sortedByDescending { it.timeStamp },
                        modifier = modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                    )
                }
            }

            is WalletUiState.Error -> TODO()
        }
    }
}

@Composable
fun Transaction_Item(
    transactions: List<Transactions>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        items(transactions) {
            Transactions_das(
                transactions = it,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun Transactions_das(
    transactions: Transactions,
    modifier: Modifier = Modifier
) {
    Custom_Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp)),
        elevation = 2.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Transaction_Data(transactions)
                Text(
                    text = "₹${transactions.amount}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    )
}

@Composable
fun Transaction_Data(transactions: Transactions,modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = if (transactions.type == "Deposit") Icons.Outlined.ArrowDownward else Icons.Outlined.ArrowUpward,
            contentDescription = "",
            tint = if (transactions.type == "Deposit") Color.Blue else Color.Red,
            modifier = Modifier.size(38.dp)
        )
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = transactions.type ?: "Type",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = transactions.date ?: "Date",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}
