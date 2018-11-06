package com.example.natta.nich.view.payment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.*
import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.Window
import android.widget.Toast
import com.example.natta.nich.R
import com.example.natta.nich.data.Restaurant
import com.example.natta.nich.view.MainActivity
import com.example.natta.nich.viewmodel.PaymentViewModel
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.custom_dialog_payment.*
import kotlin.math.roundToInt

class PaymentActivity : AppCompatActivity() {
    private var sp: SharedPreferences? = null
    private val FROM_RESTAURANT = "FROM_RESTAURANT"
    private var total = 0.0
    private var model = PaymentViewModel()
    private var resKey = ""
    private var restaurant = Restaurant()
    private var selectPromtPay: Boolean = true
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar.title = "ชำระเงิน"
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(
                object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        if (response != null) {
//                                    Toast.makeText(applicationContext, response.permissionName, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                        token?.continuePermissionRequest()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        val dialogPermissionListener = DialogOnDeniedPermissionListener.Builder
                            .withContext(this@PaymentActivity)
                            .withTitle("การเข้าถึงหน่วยความจำ")
                            .withMessage("ต้องการเข้าถึงหน่วยความจำเพื่อบันทึกรูปภาพ")
                            .withButtonText("Ok")
                            .withIcon(R.mipmap.ic_launcher)
                            .build()
                        dialogPermissionListener.onPermissionDenied(response)
//                                finish()

                    }
                }
            ).onSameThread()
            .check()

        resKey = intent.getStringExtra("resKey")
        model = ViewModelProviders.of(this).get(PaymentViewModel::class.java)
        sp = getSharedPreferences("MY_ORDER", 0)
        val fromRes = sp!!.getInt(FROM_RESTAURANT, -1)
        model.setFromRestaurant(fromRes)

        val adapter = PaymentAdapter(this)
        list_payment.adapter = adapter
        list_payment.layoutManager = LinearLayoutManager(this)
        list_payment.setHasFixedSize(true)
        list_payment.setItemViewCacheSize(20)
        if(fromRes == 0){
            cash_PM.visibility = View.INVISIBLE
            textView_table_PM.visibility = View.GONE
            table_PM.visibility = View.GONE
        } else if (fromRes == 1) {
            val table = sp!!.getString("TABLE", "NotFound")
            model.setTable(table,resKey)
            textView_table_PM.visibility = View.VISIBLE
            table_PM.visibility = View.VISIBLE
            model.getTable().observe(this, Observer {
                table_PM.text = it.toString()
            })


        }
        model.getRestaurant(resKey).observe(this, Observer {
            if (it != null) {
                res_name_PM.text = it.restaurantName.toString()
                this.restaurant = it
            }
        })

        model.getMyOrder(resKey).observe(this, Observer {
            if (it != null) {
                orderAmount_PM.text = it.size.toString() + " รายการ"
                total_PM.text = model.getTotal().toString() + " ฿"
                total = model.getTotal()

                adapter.setData(it)
            }
        })

        btn_payment_PM.setOnClickListener { it ->
            val i = Intent(applicationContext, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            val text = renderStrQR(restaurant.promtPayID!!, total)
            val textP = "พร้อมเพย์ : ${restaurant.promtPayID}"
            val resName = restaurant.restaurantName
            //startActivity(i)
            if (selectPromtPay) {
                val dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.custom_dialog_payment)
                dialog.setCancelable(true)
                dialog.setCanceledOnTouchOutside(true)
                dialog.window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)

                dialog.btn_goMain_PM.setOnClickListener {
                    dialog.dismiss()
                    model.addOrder(selectPromtPay)
                    startActivity(i)
                }

                dialog.qrcode_PM.setOnLongClickListener {
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("promtpayID", text)
                    clipboard.primaryClip = clip
                    Toast.makeText(applicationContext, "คัดลอกลงคลิปบอร์ดเรียบร้อยแล้ว", Toast.LENGTH_LONG).show()
                    return@setOnLongClickListener false
                }
                try {
                    val barcodeEncoder = BarcodeEncoder()
                    val bitmap = barcodeEncoder.encodeBitmap(text, BarcodeFormat.QR_CODE, 1000, 1000)

                    val resources = this.resources
                    val scale = resources.displayMetrics.density

                    val newBitmap = bitmap.copy(bitmap.config, true)

                    val canvas = Canvas(newBitmap)
                    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                    val font = Typeface.createFromAsset(assets, "font/prompt.ttf")
                    paint.color = Color.BLACK
                    paint.typeface = Typeface.create(font, Typeface.NORMAL)
                    paint.textSize = (18 * scale).roundToInt().toFloat()

                    val bounds = Rect()
                    paint.getTextBounds(textP, 0, textP.length, bounds)
                    var x: Float = ((newBitmap.width - bounds.width()) / 2).toFloat()
                    var y: Float = (newBitmap.height - 1).toFloat()
                    canvas.drawText(textP, x, y, paint)

                    paint.textSize = (24 * scale).roundToInt().toFloat()
                    paint.typeface = Typeface.create(font, Typeface.BOLD)
                    paint.getTextBounds("$resName", 0, resName!!.length, bounds)
                    x = ((newBitmap.width - bounds.width()) / 2).toFloat()
                    y = (bounds.height() + 1).toFloat()
                    canvas.drawText("$resName", x, y, paint)


                    dialog.qrcode_PM.setImageBitmap(newBitmap)
                    dialog.saveQRCode_PM.setOnClickListener {
                        val saveUri = MediaStore.Images.Media.insertImage(
                            contentResolver,
                            newBitmap,
                            "$resName$text",
                            "Restaurant Name : $$resName ,PromtPayID : $text"
                        )
                        Toast.makeText(applicationContext, "บันทึกสำเร็จ", Toast.LENGTH_LONG).show()
                    }
                    dialog.show()
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
                }


            } else {
                model.addOrder(selectPromtPay)
                startActivity(i)
            }


        }
        cash_PM.setCardBackgroundColor(resources.getColor(R.color.background))
        promtPay_PM.setCardBackgroundColor(resources.getColor(R.color.colorCard))
        promtPay_PM.setOnClickListener {
            selectPromtPay = true
            promtPay_PM.setCardBackgroundColor(resources.getColor(R.color.colorCard))
            cash_PM.setCardBackgroundColor(resources.getColor(R.color.background))
        }
        cash_PM.setOnClickListener {
            selectPromtPay = false
            promtPay_PM.setCardBackgroundColor(resources.getColor(R.color.background))
            cash_PM.setCardBackgroundColor(resources.getColor(R.color.colorCard))
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun renderStrQR(promtPayID: String, total: Double): String {
        var pp_acc_id: String = ""
        val pp_amount: String = if (total != 0.0) String.format("54%02d%s", total.toString().length, total) else ""
        var pp_chksum: String = ""

        pp_acc_id = when {
            promtPayID.length == 15 -> // truemoney e-wallet
                "0315$promtPayID"
            promtPayID.length == 13 -> // card-id
                "0213$promtPayID"
            promtPayID.length == 10 -> // tel-no
                "01130066" + promtPayID.substring(1)
            else -> { // invalid acc_id
                ""
            }
        }

        val field_29: String = "0016A000000677010111$pp_acc_id"
        var pp_str: String = "000201010211" +
                "29" + field_29.length + field_29 +
                "5303764" +
                pp_amount +
                "5802TH" +
                "6304"
        pp_chksum = CRC16.checksum(pp_str)
        pp_str += pp_chksum
        return pp_str
    }


}
