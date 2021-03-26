package com.example.onlinestore

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.onlinestore.ui.main.CheckOutFragment
import com.example.onlinestore.utils.Hash
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*


class JazzPaymentActivity : AppCompatActivity() {
    var postData = ""
    var transactionIdString = ""
    private lateinit var mWebView: WebView

    private val Jazz_MerchantID = "MC18927"
    private val Jazz_Password = "vz07xyy0tt"
    private val Jazz_IntegritySalt = "7z538csyf7"

    private val paymentReturnUrl = "https://localhost.com/order.php"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jazz_payment)

        mWebView = findViewById<View>(R.id.webViewJazz) as WebView
        // Enable Javascript
        val webSettings = mWebView.settings
        webSettings.javaScriptEnabled = true

        mWebView.webViewClient = MyWebViewClient()
        webSettings.domStorageEnabled = true
        mWebView.addJavascriptInterface(FormDataInterface(), "FORMOUT")

        val intentData = intent.getIntExtra(CheckOutFragment.KEY_PRICE, -1)
        var price = intentData.toString()
        println("NasirLogs: price_before : $price")

        val values = price.split("\\.".toRegex()).toTypedArray()
        price = values[0]
        price += "00"
        println("NasirLogs: price : $price")

        val date = Date()
        val dateFormat = SimpleDateFormat("yyyyMMddkkmmss", Locale.getDefault())
        val dateString: String = dateFormat.format(date)
        println("NasirLogs: DateString : $dateString")

        // Convert Date to Calendar

        // Convert Date to Calendar
        val c: Calendar = Calendar.getInstance()
        c.time = date
        c.add(Calendar.HOUR, 1)

        // Convert calendar back to Date
        val currentDateHourPlusOne: Date = c.time
        val expiryDateString: String = dateFormat.format(currentDateHourPlusOne)
        println("NasirLogs: expiryDateString : $expiryDateString")

        transactionIdString = "T$dateString"
        println("NasirLogs: TransactionIdString : $transactionIdString")

        val pp_MerchantID = Jazz_MerchantID
        val pp_Password = Jazz_Password
        val IntegritySalt = Jazz_IntegritySalt
        val pp_ReturnURL = paymentReturnUrl
        val pp_Amount: String = price
        val pp_Version = "1.1"
        val pp_TxnType = ""
        val pp_Language = "EN"
        val pp_SubMerchantID = ""
        val pp_BankID = "TBANK"
        val pp_ProductID = "RETL"
        val pp_TxnCurrency = "PKR"
        val pp_BillReference = transactionIdString
        val pp_Description = "Description of transaction"
        var pp_SecureHash = "F6054F7AE08D3D288735D48263BC5EE7EC49934D62F5CCA7A0E3078BDB15185E"
        val pp_mpf_1 = "1"
        val pp_mpf_2 = "2"
        val pp_mpf_3 = "3"
        val pp_mpf_4 = "4"
        val pp_mpf_5 = "5"

        //sortedString = "cwu55225t6&1000&TBANK&billRef&Description of transaction&EN&MC10487&z740xw7fu0&RETL&http://localhost/jazzcash_part_3/order_placed.php&PKR&20201223202501&20201223212501&T20201223202501&1.1&1&2&3&4&5";
        //pp_SecureHash = php_hash_hmac(sortedString, IntegritySalt);

        var sortedString = ""
        sortedString += "$IntegritySalt&"
        sortedString += "$pp_Amount&"
        sortedString += "$pp_BankID&"
        sortedString += "$pp_BillReference&"
        sortedString += "$pp_Description&"
        sortedString += "$pp_Language&"
        sortedString += "$pp_MerchantID&"
        sortedString += "$pp_Password&"
        sortedString += "$pp_ProductID&"
        sortedString += "$pp_ReturnURL&"
        //sortedString += pp_SubMerchantID + "&";
        //sortedString += pp_SubMerchantID + "&";
        sortedString += "$pp_TxnCurrency&"
        sortedString += "$dateString&"
        sortedString += "$expiryDateString&"
        //sortedString += pp_TxnType + "&";
        //sortedString += pp_TxnType + "&";
        sortedString += "$transactionIdString&"
        sortedString += "$pp_Version&"
        sortedString += "$pp_mpf_1&"
        sortedString += "$pp_mpf_2&"
        sortedString += "$pp_mpf_3&"
        sortedString += "$pp_mpf_4&"
        sortedString += pp_mpf_5

        //pp_SecureHash = phpHashHmac(sortedString, IntegritySalt)
        pp_SecureHash = Hash.php_hash_hmac(sortedString, IntegritySalt)
        println("NasirLogs: sortedString : $sortedString")
        println("NasirLogs: pp_SecureHash : $pp_SecureHash")

        try {
            postData += URLEncoder.encode("pp_Version", "UTF-8")
                .toString() + "=" + URLEncoder.encode(pp_Version, "UTF-8") + "&"
            postData += URLEncoder.encode("pp_TxnType", "UTF-8")
                .toString() + "=" + pp_TxnType + "&"
            postData += URLEncoder.encode("pp_Language", "UTF-8")
                .toString() + "=" + URLEncoder.encode(pp_Language, "UTF-8") + "&"
            postData += URLEncoder.encode("pp_MerchantID", "UTF-8")
                .toString() + "=" + URLEncoder.encode(pp_MerchantID, "UTF-8") + "&"
            postData += URLEncoder.encode("pp_SubMerchantID", "UTF-8")
                .toString() + "=" + pp_SubMerchantID + "&"
            postData += URLEncoder.encode("pp_Password", "UTF-8")
                .toString() + "=" + URLEncoder.encode(pp_Password, "UTF-8") + "&"
            postData += URLEncoder.encode("pp_BankID", "UTF-8")
                .toString() + "=" + URLEncoder.encode(pp_BankID, "UTF-8") + "&"
            postData += URLEncoder.encode("pp_ProductID", "UTF-8")
                .toString() + "=" + URLEncoder.encode(pp_ProductID, "UTF-8") + "&"
            postData += URLEncoder.encode("pp_TxnRefNo", "UTF-8")
                .toString() + "=" + URLEncoder.encode(transactionIdString, "UTF-8") + "&"
            postData += URLEncoder.encode("pp_Amount", "UTF-8")
                .toString() + "=" + URLEncoder.encode(pp_Amount, "UTF-8") + "&"
            postData += URLEncoder.encode("pp_TxnCurrency", "UTF-8")
                .toString() + "=" + URLEncoder.encode(pp_TxnCurrency, "UTF-8") + "&"
            postData += URLEncoder.encode("pp_TxnDateTime", "UTF-8")
                .toString() + "=" + URLEncoder.encode(dateString, "UTF-8") + "&"
            postData += URLEncoder.encode("pp_BillReference", "UTF-8")
                .toString() + "=" + URLEncoder.encode(pp_BillReference, "UTF-8") + "&"
            postData += URLEncoder.encode("pp_Description", "UTF-8")
                .toString() + "=" + URLEncoder.encode(pp_Description, "UTF-8") + "&"
            postData += URLEncoder.encode("pp_TxnExpiryDateTime", "UTF-8")
                .toString() + "=" + URLEncoder.encode(expiryDateString, "UTF-8") + "&"
            postData += URLEncoder.encode("pp_ReturnURL", "UTF-8")
                .toString() + "=" + URLEncoder.encode(pp_ReturnURL, "UTF-8") + "&"
            postData += URLEncoder.encode("pp_SecureHash", "UTF-8")
                .toString() + "=" + pp_SecureHash + "&"
            postData += URLEncoder.encode("ppmpf_1", "UTF-8")
                .toString() + "=" + URLEncoder.encode(pp_mpf_1, "UTF-8") + "&"
            postData += URLEncoder.encode("ppmpf_2", "UTF-8")
                .toString() + "=" + URLEncoder.encode(pp_mpf_2, "UTF-8") + "&"
            postData += URLEncoder.encode("ppmpf_3", "UTF-8")
                .toString() + "=" + URLEncoder.encode(pp_mpf_3, "UTF-8") + "&"
            postData += URLEncoder.encode("ppmpf_4", "UTF-8")
                .toString() + "=" + URLEncoder.encode(pp_mpf_4, "UTF-8") + "&"
            postData += URLEncoder.encode("ppmpf_5", "UTF-8")
                .toString() + "=" + URLEncoder.encode(pp_mpf_5, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        println("NasirLogs: postData : $postData")

        mWebView.postUrl(
            "https://sandbox.jazzcash.com.pk/CustomerPortal/transactionmanagement/merchantform/",
            postData.toByteArray()
        )

    }

    /** onCreate()*/

    private inner class MyWebViewClient : WebViewClient() {
        private val jsCode = "" + "function parseForm(form){" +
                "var values='';" +
                "for(var i=0 ; i< form.elements.length; i++){" +
                "   values+=form.elements[i].name+'='+form.elements[i].value+'&'" +
                "}" +
                "var url=form.action;" +
                "console.log('parse form fired');" +
                "window.FORMOUT.processFormData(url,values);" +
                "   }" +
                "for(var i=0 ; i< document.forms.length ; i++){" +
                "   parseForm(document.forms[i]);" +
                "};"

        //private static final String DEBUG_TAG = "CustomWebClient";
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            if (url == paymentReturnUrl) {
                println("NasirLogs: return url cancelling")
                view.stopLoading()
                return
            }
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView, url: String) {
            //Log.d(DEBUG_TAG, "Url: "+url);
            if (url == paymentReturnUrl) {
                return
            }
            view.loadUrl("javascript:(function() { $jsCode})()")
            super.onPageFinished(view, url)
        }
    }

    private inner class FormDataInterface {
        @JavascriptInterface
        fun processFormData(url: String, formData: String) {
            val i = Intent(this@JazzPaymentActivity, MainActivity::class.java)
            println("NasirLogs: Url:$url form data $formData")
            if (url == paymentReturnUrl) {
                val values = formData.split("&".toRegex()).toTypedArray()
                for (pair in values) {
                    val nameValue = pair.split("=".toRegex()).toTypedArray()
                    if (nameValue.size == 2) {
                        println("NasirLogs: Name:" + nameValue[0] + " value:" + nameValue[1])
                        i.putExtra(nameValue[0], nameValue[1])
                        i.putExtra(CheckOutFragment.KEY_ORDER_REF_NO, transactionIdString)
                    }
                }
                setResult(RESULT_OK, i)
                finish()
                return
            }
        }
    }

/*
    private fun phpHashHmac(data: String, secret: String): String {
        var returnString = ""
        try {
            val sha256_HMAC: Mac = Mac.getInstance("HmacSHA256")
            val secret_key = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
            sha256_HMAC.init(secret_key)
            val res: ByteArray = sha256_HMAC.doFinal(data.toByteArray())
            returnString = bytesToHex(res)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return returnString
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexArray = "0123456789abcdef".toCharArray()
        val hexChars = CharArray(bytes.size * 2)
            var j = 0
            var v: Int
            while (j < bytes.size) {
                v = (bytes[j] and 0xFF.toByte()).toInt()
                Log.i("zzzz", "${bytes.size}, $v, ${v ushr 4}")
                if (v < 0) {
                    j++
                    continue
                }
                hexChars[j * 2] = hexArray[v ushr 4]
                hexChars[j * 2 + 1] = hexArray[v and 0x0F]
                j++
            }

        return String(hexChars)
    }
*/

}