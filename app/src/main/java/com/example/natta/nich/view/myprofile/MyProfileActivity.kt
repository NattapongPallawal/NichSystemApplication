package com.example.natta.nich.view.myprofile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.natta.nich.R
import com.example.natta.nich.data.Customer
import com.example.natta.nich.viewmodel.MyProfileViewModel
import com.google.firebase.storage.FirebaseStorage
import com.zxy.tiny.Tiny
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.content_person.*
import kotlinx.android.synthetic.main.content_person_address.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MyProfileActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 111
    private var mCalendar = Calendar.getInstance()
    private var edtPerson = arrayListOf<EditText>()
    private var edtAddress = arrayListOf<EditText>()
    private lateinit var model: MyProfileViewModel
    private var customer = Customer()
    private var customerTemp = Customer()
    private lateinit var filePath: Bitmap
    private val storageRef = FirebaseStorage.getInstance()
    private val dateListener = DatePickerDialog.OnDateSetListener { _, p1, p2, p3 ->
        mCalendar.set(Calendar.YEAR, p1)
        mCalendar.set(Calendar.MONTH, p2)
        mCalendar.set(Calendar.DAY_OF_MONTH, p3)
        val f = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(f, Locale.getDefault())
        edt_birthday.setText(sdf.format(mCalendar.time).toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        setSupportActionBar(toolbar_profile)
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        model = ViewModelProviders.of(this).get(MyProfileViewModel::class.java)
        binding()
        initProfile()

        model.getCustomer().observe(this, Observer {
            if (it != null) {
                customer = it
                updateUI()
            }
        })
        btn_pick_date.setOnClickListener {
            DatePickerDialog(
                this, dateListener,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }


    }


    private fun chooseImage() {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE_REQUEST)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data?.data != null) {
            Tiny.getInstance()
                .source(MediaStore.Images.Media.getBitmap(this.contentResolver, data.data))
                .asFile()
                .withOptions(Tiny.FileCompressOptions())
                .compress { isSuccess, bitmap, outfile, t ->
                    if (isSuccess) {
                        Log.d("Compress", "success")
                        filePath = bitmap!!

                        uploadImage()

                    } else {
                        Log.d("Compress", "Unsuccess ${t.message}")
                        filePath = MediaStore.Images.Media.getBitmap(this.contentResolver, data.data)
                        uploadImage()

                    }
                }
            loading_pic_profile.visibility = View.VISIBLE
        }
    }

    private fun uploadImage() {
        var url = ""
        deleteImage()

        val baos = ByteArrayOutputStream()
        filePath.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val data = baos.toByteArray()
        val task = storageRef.getReference("user/${model.getUID()}")

        task.putBytes(data)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, task.downloadUrl.toString(), Toast.LENGTH_LONG).show()

                task.downloadUrl
                    .addOnSuccessListener {
                        url = it.toString()
                        customer.picture = url
                        uploadToRTBD()
                        loading_pic_profile.visibility = View.GONE

                    }
                    .addOnFailureListener { }
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, it.message.toString(), Toast.LENGTH_LONG).show()
            }


    }

    private fun deleteImage() {
        try {
            val imageRef = storageRef.getReferenceFromUrl(customer.picture!!)
            imageRef.delete()
        } catch (e:Exception) {
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        name_profile.text = "${customer.firstName} ${customer.lastName}"
        edt_firstName.setText(customer.firstName)
        edt_lastName.setText(customer.lastName)
        edt_email.setText(model.getEmail())
        edt_personID.setText(customer.personID)
        edt_phoneNum.setText(customer.phoneNumber)
        edt_birthday.setText(customer.birthday)
        try {
            edt_address.setText(customer.address?.address)
            edt_sub_district.setText(customer.address?.subDistrict)
            edt_district.setText(customer.address?.district)
            edt_province.setText(customer.address?.province)
            if (customer.address?.postalCode != null){
                edt_postCode.setText(customer.address!!.postalCode.toString())
            }

        } catch (e: Exception) {
        }
        if (customer.picture != null) {
            Glide.with(this).load(customer.picture)
                .into(picture_myprofile)
        }

    }

    private fun initProfile() {
        email_profile.text = model.getEmail()

        edit(btn_editPerson, edtPerson, btn_savePerson)
        save(btn_savePerson, edtPerson, btn_editPerson)

        edit(btn_editAddress, edtAddress, btn_saveAddress)
        save(btn_saveAddress, edtAddress, btn_editAddress)

        picture_myprofile.setOnClickListener {
            chooseImage()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun save(btnSave: ImageButton, edtPerson: ArrayList<EditText>, btnEdit: ImageButton) {
        btnSave.setOnClickListener {
            for (i in edtPerson)
                i.isEnabled = false
            btnSave.visibility = View.GONE
            btnEdit.visibility = View.VISIBLE

            if (btnEdit === btn_editPerson || btnEdit === btn_savePerson) {
                btn_pick_date.visibility = View.GONE

            } else {

            }


            uploadToRTBD()


        }
    }

    private fun uploadToRTBD() {
        customerTemp.firstName = edt_firstName.text.toString()
        customerTemp.lastName = edt_lastName.text.toString()
        customerTemp.personID = edt_personID.text.toString()
        customerTemp.phoneNumber = edt_phoneNum.text.toString()
        customerTemp.birthday = edt_birthday.text.toString()
        customerTemp.picture = customer.picture
        try {
            customerTemp.address?.address = edt_address.text.toString()
            customerTemp.address?.subDistrict = edt_sub_district.text.toString()
            customerTemp.address?.district = edt_district.text.toString()
            customerTemp.address?.province = edt_province.text.toString()
            customerTemp.address?.postalCode = edt_postCode.text.toString().toInt()
        } catch (e: Exception) {

        }
        model.setCustomer(customerTemp)
    }

    private fun edit(btnEdit: ImageButton, edtPerson: ArrayList<EditText>, btnSave: ImageButton) {
        btnEdit.setOnClickListener {
            for (i in edtPerson)
                i.isEnabled = true

            btnSave.visibility = View.VISIBLE
            btnEdit.visibility = View.GONE
            if (btnEdit === btn_editPerson || btnEdit === btn_savePerson)
                btn_pick_date.visibility = View.VISIBLE

        }
    }

    private fun binding() {
        edtPerson.add(edt_firstName as EditText)
        edtPerson.add(edt_lastName as EditText)
        edtPerson.add(edt_personID as EditText)
        edtPerson.add(edt_phoneNum as EditText)

        edtAddress.add(edt_address as EditText)
        edtAddress.add(edt_sub_district as EditText)
        edtAddress.add(edt_district as EditText)
        edtAddress.add(edt_province as EditText)
        edtAddress.add(edt_postCode as EditText)

        btn_pick_date.visibility = View.GONE
    }
}
