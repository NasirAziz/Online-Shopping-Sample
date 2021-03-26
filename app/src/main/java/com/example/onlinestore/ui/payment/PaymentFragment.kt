/*
package com.example.onlinestore.ui.payment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import com.example.onlinestore.R
import com.example.onlinestore.ui.main.CartViewViewModel
import com.example.onlinestore.ui.main.CheckOutFragment
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.spec.SecretKeySpec


class PaymentFragment : Fragment() {

    companion object {
        fun newInstance() = PaymentFragment()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private val STORE_ID = "ENTER_STORE_ID"
    private val HASH_KEY = "ENTER_HASH_KEY"

    private val POST_BACK_URL1 = "http://localhost/easypay/order_confirm.php"
    private val POST_BACK_URL2 = "http://localhost/easypay/order_complete.php"

    //Live
    private val TRANSACTION_POST_URL1 = "https://easypay.easypaisa.com.pk/easypay/Index.jsf"
    private val TRANSACTION_POST_URL2 = "https://easypay.easypaisa.com.pk/easypay/Confirm.jsf"

    //Sandbox Testing
    //private final String TRANSACTION_POST_URL1   = "https://easypaystg.easypaisa.com.pk/easypay/Index.jsf";
    //private final String TRANSACTION_POST_URL2   = "https://easypaystg.easypaisa.com.pk/easypay/Confirm.jsf";
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    //Sandbox Testing
    //private final String TRANSACTION_POST_URL1   = "https://easypaystg.easypaisa.com.pk/easypay/Index.jsf";
    //private final String TRANSACTION_POST_URL2   = "https://easypaystg.easypaisa.com.pk/easypay/Confirm.jsf";
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    private lateinit var viewModel: PaymentViewModel
    private lateinit var mWebView: WebView
    private lateinit var price: String
    var orderRefNumString = ""


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.payment_fragment, container, false)

        mWebView = view.findViewById(R.id.webView)
        val webSettings = mWebView.settings
        webSettings.javaScriptEnabled = true

        mWebView.webViewClient = MyWebViewClient()
        webSettings.domStorageEnabled = true
        val date = Date()
        val dateFormat = SimpleDateFormat("yyyyMMDD HHMMSS", Locale.getDefault())
        val dateString: String = dateFormat.format(date)
        orderRefNumString = "T$dateString"
        println("AhmadLogs: orderRefNum : $orderRefNumString")

        // Convert Date to Calendar
        val c: Calendar = Calendar.getInstance()
        c.time = date
        c.add(Calendar.HOUR, 1)

        // Convert calendar back to Date
        val currentDateHourPlusOne: Date = c.time
        val expiryDateString: String = dateFormat.format(currentDateHourPlusOne)
        println("AhmadLogs: expiryDateString : $expiryDateString")

        price = CartViewViewModel.grandTotalAmount.value.toString()
        val storeId = STORE_ID
        val amount = price
        val postBackURL = POST_BACK_URL1
        val autoRedirect = "1"
        val paymentMethod = "MA_PAYMENT_METHOD"
        //OTC_PAYMENT_METHOD
        //MA_PAYMENT_METHOD
        //CC_PAYMENT_METHOD

        //hash generate
        var sortedString = ""
        sortedString += "$amount&"
        sortedString += "$autoRedirect&"
        sortedString += "$expiryDateString&"
        sortedString += "$orderRefNumString&"
        sortedString += "$paymentMethod&"
        sortedString += "$postBackURL&"
        sortedString += storeId
        //sortedString = amount=10&expiryDate=20150101 151515&orderRefNum=11001&postBackURL=http://localhost:9081/local/status.php&storeId=28

        val merchantHashedReq: String = getHash(sortedString, HASH_KEY)

        var postData = ""

        try {
            postData += (URLEncoder.encode("storeId=", "UTF-8")
                    + URLEncoder.encode(storeId, "UTF-8")) + "&"
            postData += (URLEncoder.encode("amount=", "UTF-8")
                    + URLEncoder.encode(amount, "UTF-8")) + "&"
            postData += (URLEncoder.encode("postBackURL=", "UTF-8")
                    + URLEncoder.encode(postBackURL, "UTF-8")) + "&"
            postData += (URLEncoder.encode("orderRefNum=", "UTF-8")
                    + URLEncoder.encode(orderRefNumString, "UTF-8")) + "&"
            postData += (URLEncoder.encode("expiryDate=", "UTF-8")
                    + URLEncoder.encode(expiryDateString, "UTF-8")) + "&"
            postData += (URLEncoder.encode("merchantHashedReq=", "UTF-8")
                    + URLEncoder.encode(merchantHashedReq, "UTF-8")) + "&"
            postData += (URLEncoder.encode("autoRedirect=", "UTF-8")
                    + URLEncoder.encode(autoRedirect, "UTF-8")) + "&"
            postData += (URLEncoder.encode("paymentMethod=", "UTF-8")
                    + URLEncoder.encode(paymentMethod, "UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        mWebView.postUrl(TRANSACTION_POST_URL1, postData.toByteArray())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(PaymentViewModel::class.java)
        //mWebView.loadUrl("https://stackoverflow.com/questions/6751583/is-there-a-method-that-works-like-start-fragment-for-result")
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            println("AhmadLogs: onPageStarted - url : $url")
            //http://localhost/easypay/order_confirm.php?auth_token=s234f5gH7jFb5d
            //http://localhost/easypay/order_complete.php?status=000&desc=completed&orderRefNumber=123
            val responseSplit = url.split("\\?").toTypedArray()
            val redirectUrl = responseSplit[0]
            val response = responseSplit[0]//TODO restore 0 -> 1
            println("AhmadLogs: onPageStarted - redirect_url : $redirectUrl")
            println("AhmadLogs: onPageStarted - response : $response")
            if (redirectUrl == POST_BACK_URL1) {
                println("AhmadLogs: return url1 cancelling")
                view.stopLoading()
                var authTokenString = ""
                val values = response.split("&").toTypedArray()
                for (pair in values) {
                    val nameValue = pair.split("=").toTypedArray()
                    if (nameValue.size == 2) {
                        println("AhmadLogs: Name:" + nameValue[0] + " value:" + nameValue[1])
                        if (nameValue[0] === "auth_token") {
                            authTokenString = nameValue[1]
                            break
                        }
                    }
                }
                var postData = ""
                postData += "auth_token=$authTokenString&"
                postData += "postBackURL=$POST_BACK_URL2"
                mWebView.postUrl(TRANSACTION_POST_URL2, postData.toByteArray())
            } else if (redirectUrl == POST_BACK_URL2) {
                //http://localhost/easypay/order_complete.php?status=000&desc=completed&orderRefNumber=123
                println("AhmadLogs: return url2 cancelling")
                view.stopLoading()
                //val i = Intent(this@PaymentActivity, MainActivity::class.java)
                val values = response.split("&").toTypedArray()
                for (pair in values) {
                    val nameValue = pair.split("=").toTypedArray()
                    if (nameValue.size == 2) {
                        println("AhmadLogs: Name:" + nameValue[0] + " value:" + nameValue[1])
                        //i.putExtra(nameValue[0], nameValue[1])
                        setFragmentResult(
                            CheckOutFragment.FRAGMENT_RESULT_KEY,
                            bundleOf(
                                nameValue[0] to nameValue[1],
                                "orderRef" to orderRefNumString
                            )
                        )
                    }
                }
                // setResult(RESULT_OK)
                //finish()
                requireActivity().supportFragmentManager.popBackStack()
                return
            } else {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                setFragmentResult(
                    CheckOutFragment.FRAGMENT_RESULT_KEY,
                    bundleOf(
                        CheckOutFragment.PAYMENT_STATUS_CODE to "failed",
                        "orderRef" to "null"

                    )
                )
                requireActivity().supportFragmentManager.popBackStack()

            }
            super.onPageStarted(view, url, favicon)
        }
    }

    private fun getHash(data: String, key: String): String {
        var hashString: String = ""
        try {
            val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            val secretKey = SecretKeySpec(key.toByteArray(), "AES")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val encryptedValue: ByteArray = cipher.doFinal(data.toByteArray())
            hashString = encodeToString(encryptedValue, DEFAULT)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        }

        return hashString
    }

}*/
