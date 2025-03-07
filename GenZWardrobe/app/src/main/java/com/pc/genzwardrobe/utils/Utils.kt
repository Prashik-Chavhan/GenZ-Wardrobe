package com.pc.genzwardrobe.utils

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.pc.genzwardrobe.BuildConfig
import com.pc.genzwardrobe.core.domain.GenderCategories
import com.pc.genzwardrobe.core.domain.GenderCategory
import com.pc.genzwardrobe.core.domain.ProductCategory
import com.pc.genzwardrobe.core.domain.UserAddress
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.random.Random

object Utils {

    const val GEO_CODING_API_KEY = BuildConfig.GEOCODING_API_KEY

    fun getCurrentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun checkAddressStatus(
        itemId: Int,
        getAllUserAddresses: List<UserAddress>,
        isAddressNotSaved: () -> Unit,
        isAddressSaved: (Int) -> Unit,
    ) {
        if (getAllUserAddresses.isEmpty()) {
            isAddressNotSaved()
        } else {
            isAddressSaved(itemId)
        }
    }

    fun getCurrentUserPhoneNumber(): String? {
        return FirebaseAuth.getInstance().currentUser?.phoneNumber
    }

    fun generateRandomId(): String {
        return (1..25).map { (('a'..'z') + ('A'..'Z') + (0..9)).random() }.joinToString("")
    }

    fun generateOrderId(): String {
        val firstDigit = Random.nextInt(1, 10)
        val nextFiveDigits = (1..5).map { (0..9).random() }.joinToString("")

        return "#$firstDigit$nextFiveDigits"
    }

    fun getTodaysDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return currentDate.format(formatter)
    }

    fun Long.toHumanReadableTime(
        pattern: String = "hh:mm",
        locale: Locale = Locale.getDefault()
    ): String {
        val time = Date(this)
        val formatter = SimpleDateFormat(pattern, locale)
        return formatter.format(time)
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    object GenderLists {
        fun genderLists(): List<GenderCategories> {
            return listOf(
                GenderCategories(
                    name = "Men's",
                    categories = listOf(
                        ProductCategory(
                            name = "Topwear",
                            type = listOf(
                                "Oversized T-shirts",
                                "Casual Shirts",
                                "Polos",
                                "Solid T-shirts",
                                "Classic Fit T-shirts",
                                "Oversized Full Sleeve",
                                "Dropcut T-shirts",
                                "Co-ord sets",
                                "Jackets",
                                "Hoodies & Sweatshirts",
                                "Full Sleeve T-shirts"
                            )
                        ),
                        ProductCategory(
                            name = "Bottomwear",
                            type = listOf(
                                "Pants", "Jeans", "Joggers",
                                "Shorts", "Boxers & Innerwear", "Pajamas"
                            )
                        )
                    )
                ),
                GenderCategories(
                    "Women's",
                    listOf(
                        ProductCategory(
                            "Topwear",
                            listOf(
                                "Oversized T-shirts",
                                "Shirts", "Tops",
                                "Relaxed Fit T-Shirts",
                                "Dresses & Jumpsuits",
                                "Co-ord Sets",
                                "Hoodies & Sweatshirts",
                                "Sweaters",
                                "Jackets"
                            )
                        ),
                        ProductCategory(
                            "Bottomwear",
                            listOf("Joggers", "Pants", "Jeans", "Shorts & Skirts", "Cargos"),
                        )
                    )
                )
            )
        }
    }

    val gendersList = listOf(
        GenderCategory(
            0,
            "All"
        ),
        GenderCategory(
            1,
            "Men's"
        ),
        GenderCategory(
            2,
            "Women's"
        )
    )

    val orderStatus = listOf(
        "Your order has been placed and under processing.",
        "Your order is being prepared.",
        "Your order has been confirmed.",
        "Your item have been packed for shipping.",
        "Your order has been handed over to the courier/logistics provider and is on its way.",
        "Out for Delivery",
        "Your order has been successfully delivered.",
        "An attempt to deliver the order failed.",
        "You initiated a return, and it has been processed.",
        "Refund for a returned or canceled order is being processed.",
        "The refund for the order has been completed successfully.",
        "Your order has been canceled successfully."
    )

    val returnReasonTexts = listOf(
        "Don't wont the product anymore",
        "Received a broken/damaged item",
        "Received a different color/size",
        "The product received is defective",
        "Product is missing in the package",
        "Don't like the style/fit of the product",
        "Quality of the product not as expected",
        "Product description is wrong on app/website",
        "Received wrong item"
    )

    fun moreDetailsForReturn(selectedReason: Int): List<String> {
        return when (selectedReason) {
            0 -> listOf(
                "Changed my mind after purchasing",
                "Found a better alternative elsewhere",
                "No longer needed the product"
            )

            1 -> listOf(
                "Product was damaged during shipping",
                "Packaging was crushed/torn on arrival",
                "Item arrived with missing or broken parts"
            )

            2 -> listOf(
                "Ordered the correct size but received the wrong one",
                "Color is different from the images shown on the app/website",
                "Variation in size or color compared to what was expected"
            )

            3 -> listOf(
                "Product is not functioning as expected",
                "The item has visible manufacturing defects",
                "Certain features of the product are not working"
            )

            4 -> listOf(
                "Package arrived but missing some items",
                "Received an empty package or incomplete order",
                "Some accessories or parts were not included"
            )

            5 -> listOf(
                "The product looks different from the images online",
                "Fit is too tight/loose compared to expected sizing",
                "Style does not match my preferences"
            )

            6 -> listOf(
                "Material feels cheap or low quality",
                "Product durability is not as advertised",
                "Expected better finishing and build quality"
            )

            7 -> listOf(
                "Specifications do not match what was delivered",
                "Key product details are inaccurate",
                "Features listed on the website are missing in the product"
            )

            8 -> listOf(
                "Ordered a different item but got something else",
                "Wrong model/version of the product received",
                "Completely different product shipped by mistake"
            )

            else -> listOf("null")
        }
    }
}