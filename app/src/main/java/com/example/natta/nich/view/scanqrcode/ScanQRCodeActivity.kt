package com.example.natta.nich.view.scanqrcode

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.natta.nich.R
import com.example.natta.nich.view.food.FoodActivity
import com.example.natta.nich.viewmodel.ScanQRCodeViewModel
import com.google.firebase.database.DatabaseException
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener
import com.karumi.dexter.listener.single.PermissionListener

class ScanQRCodeActivity : AppCompatActivity(), ScanQRFragment.ScanQRCodeResultListener {
    private val MIME_TEXT_PLAIN = "text/plain"
    private val TAG = "CheckNFC"
    private var editor: SharedPreferences.Editor? = null
    private var check: Boolean = true
    private lateinit var model: ScanQRCodeViewModel
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qrcode)
        model = ViewModelProviders.of(this).get(ScanQRCodeViewModel::class.java)
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(
                object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        if (response != null) {
//                                    Toast.makeText(applicationContext, response.permissionName, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        val dialogPermissionListener = DialogOnDeniedPermissionListener.Builder
                            .withContext(this@ScanQRCodeActivity)
                            .withTitle("การเข้าถึงกล้องถ่ายรูป")
                            .withMessage("ต้องการใช้งานกล้องเพื่อนสแกน QR CODE")
                            .withButtonText("Ok")
                            .withIcon(R.mipmap.ic_launcher)
                            .build()
                        dialogPermissionListener.onPermissionDenied(response)
                    }
                }
            ).onSameThread()
            .check()
        editor = this.getSharedPreferences("MY_ORDER", Context.MODE_PRIVATE).edit()

        Toast.makeText(applicationContext, "กดที่กล้องเพื่อ เปิด/ปิด แฟลช", Toast.LENGTH_LONG).show()
        showFragment()
    }

    private fun showFragment() {
        val fragment = ScanQRFragment()
        fragment.setOnScanQRCodeResultListener(this)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.qr_code_frame, fragment)
        fragmentTransaction.commit()
    }

    override fun onResult(result: String) {
        if (check) {
            check = false
            val r = result.split(",")
            try {
                model.getRestaurantName(r.first(), r.last())
                model.ready.observe(this, Observer {
                    if (it != null) {
                        if (it) {
                            if (model.tableStatus) {
                                val builder = AlertDialog.Builder(this@ScanQRCodeActivity)
                                builder.setTitle("คำเตือน")
                                builder.setCancelable(false)
                                builder.setMessage(
                                    "ร้าน ${model.restaurantName} โต๊ะ ${model.tableName} \n" +
                                            "คุณสามารถสั่ง/เพิ่มออเดอร์  ได้จากบัญชีผู้ใช้คุณเท่านั้น " +
                                            "ไม่สามารถให้ผู้ร่วมโต๊ะ สั่ง/เพิ่มออเดอร์ ได้จากบัญชีผู้ใช้อื่น"
                                )
                                builder.setPositiveButton("ตกลง") { _, _ ->
                                    val i = Intent(this, FoodActivity::class.java)
                                    i.putExtra("resKey", r.first())
                                    editor!!.putString("TABLE", r.last())
                                    editor!!.commit()
                                    startActivity(i)
                                    finish()

                                }
                                builder.setNegativeButton("ยกเลิก") { dialog, _ ->
                                    dialog.dismiss()
                                    finish()
                                }
                                val dialog = builder.create()
                                dialog.show()
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
                            } else {
                                val builder = AlertDialog.Builder(this@ScanQRCodeActivity)
                                builder.setTitle("คำเตือน")
                                builder.setCancelable(false)
                                builder.setMessage(
                                    "ร้าน ${model.restaurantName} โต๊ะ ${model.tableName} \nขออภัยโต๊ะนี้ไม่ว่าง กรุณาเลือกโต๊ะใหม่"
                                )
                                builder.setPositiveButton("ตกลง") { dialog, _ ->
                                    dialog.dismiss()
                                   // check = true
                                    finish()
                                }
                                builder.setNegativeButton("ยกเลิก") { dialog, _ ->
                                    dialog.dismiss()
                                    finish()
                                }

                                val dialog = builder.create()
                                dialog.show()
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY)

                            }

                        }
                    }
                })
            } catch (e: DatabaseException) {
                val builder = AlertDialog.Builder(this@ScanQRCodeActivity)
                builder.setTitle("คำเตือน")
                builder.setCancelable(false)
                builder.setMessage(
                    "QR Code ไม่ถูกต้อง กรุณาสแกนใหม่อีกครั้ง"
                )
                builder.setPositiveButton("ตกลง") { dialog, _ ->
                    dialog.dismiss()
                    check = true
//                                check = true
                    // finish()
                }
                builder.setNegativeButton("ยกเลิก") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }

                val dialog = builder.create()
                dialog.show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY)


            }

        }
    }

}
