package com.pmdm.adogtale.checkout

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.pmdm.adogtale.R
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.CompletableFuture

class CheckoutActivity : AppCompatActivity() {
    lateinit var paymentSheet: PaymentSheet
    lateinit var customerConfig: PaymentSheet.CustomerConfiguration
    lateinit var paymentIntentClientSecret: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        Log.i("CheckoutActivity", "paymentSheet")

        prepareDataToCheckout()

        val checkoutButton = findViewById<Button>(R.id.checkout_button_pagar)

        checkoutButton.setOnClickListener {
            presentPaymentSheet()
        }

    }

    fun prepareDataToCheckout(): CompletableFuture<Void> {

        val client = OkHttpClient()
        val url =
            "https://oyster-app-lo6gv.ondigitalocean.app/"
        val request = Request.Builder().url(url).build()

        val responseFuture = CompletableFuture.runAsync {
            client.newCall(request)
                .execute()
                .use { response ->

                    if (!response.isSuccessful) {
                        Log.e("CheckoutActivity", "Error getting API distance response");
                        Log.e("CheckoutActivity", response.toString())

                        return@runAsync
                    }

                    val bodyString = response.body?.string()
                    val responseJson = JSONObject(bodyString)

                    paymentIntentClientSecret = responseJson.getString("paymentIntent")
                    customerConfig = PaymentSheet.CustomerConfiguration(
                        responseJson.getString("customer"),
                        responseJson.getString("ephemeralKey")
                    )
                    val publishableKey = responseJson.getString("publishableKey")
                    PaymentConfiguration.init(this, publishableKey)



                    Log.i("CheckoutActivity", response.toString())
                    return@runAsync
                }
        }

        return responseFuture;

    }

    fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                Log.i("CheckoutActivity", "Canceled")
            }

            is PaymentSheetResult.Failed -> {
                Log.i("CheckoutActivity", "Error: ${paymentSheetResult.error}")
            }

            is PaymentSheetResult.Completed -> {
                // Display for example, an order confirmation screen
                Log.i("CheckoutActivity", "Completed")
                prepareDataToCheckout()
            }
        }


    }

    fun presentPaymentSheet() {
        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = "My merchant name",
                customer = customerConfig,
                // Set `allowsDelayedPaymentMethods` to true if your business handles
                // delayed notification payment methods like US bank accounts.
                allowsDelayedPaymentMethods = true
            )
        )
    }

}