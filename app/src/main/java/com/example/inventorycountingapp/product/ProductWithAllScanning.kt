package com.example.inventorycountingapp.product

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventorycountingapp.CameraActivity
import com.example.inventorycountingapp.R
import com.example.inventorycountingapp.SubmittedSuccessfully
import com.example.inventorycountingapp.common.load
import com.example.inventorycountingapp.common.toast
import com.example.inventorycountingapp.databinding.ActivityProductWithAllScanningBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.permissionx.guolindev.PermissionX


class ProductWithAllScanning : AppCompatActivity() {
    private lateinit var binding: ActivityProductWithAllScanningBinding
    private val viewModel by lazy { ViewModelProvider(this)[ProductViewModel::class.java] }
    private val adapter by lazy { SelectedProductAdapter() }
    lateinit var submit: MaterialButton
    lateinit var btnBack: ImageView
    private val REQUEST_PICK_IMAGE = 1
    private val REQUEST_PERMISSION_SETTINGS = 1001
    var PICK_IMAGE: Int = 111
    private val readImagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE

    val storagePermissions33 = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_MEDIA_IMAGES
    )
    val storagePermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private var productResponse : ProductResponse ?= null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductWithAllScanningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        submit = findViewById(R.id.btnSubmit)
        btnBack = findViewById(R.id.iv_back)

        setupRv()


        submit.setOnClickListener {
            val intent = Intent(this, SubmittedSuccessfully::class.java)
            startActivity(intent)
        }

        btnBack.setOnClickListener {
            super.onBackPressed()
        }

        binding.ivSearchByQr.setOnClickListener {
            val barCode = binding.tvBarcode.text.toString()
            viewModel.getProduct(barCode,
                onSuccess = {
                    productResponse = it
                    binding.apply {
                        ivProductImage.load(it.data.imagePath)
                        tvName.text = it.data.name
                        tvQuantity.text = it.data.defaultQty + " pcs, "
                        tvPricee.text = it.data.salePriceTax
                    }

                    if (productResponse == null) {
                        "No product found".toast()
                    }

                    val inputBarCode = binding.tvBarcode.text.toString()

                    val tempList: MutableList<ProductResponse.Data> = ArrayList()
                    for (item in viewModel.selectedList) {
                        if (item.barcode !=  inputBarCode) {
                            tempList.add(item)
                        }
                    }
                    tempList.add(productResponse!!.data)
                    viewModel.selectedList.clear()
                    viewModel.selectedList.addAll(tempList)
                    adapter.setData(viewModel.selectedList)

                },
                onFailed = {
                    it.toast()
                })
        }
    }

    private fun setupRv() {
        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter = adapter
        adapter.onItemClick = {
            showSaveBottomSheet(it)
        }
    }

    private fun showSaveBottomSheet(item: ProductResponse.Data) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = layoutInflater.inflate(R.layout.receipt_layout, null)

        val addImage: ImageView = bottomSheetView.findViewById(R.id.iv_product)
        val tvName: TextView = bottomSheetView.findViewById(R.id.tvName)
        val tvBarcode: TextView = bottomSheetView.findViewById(R.id.etBarcode)
        val tvPrice: TextView = bottomSheetView.findViewById(R.id.tvPrice)
        val tvP: TextView = bottomSheetView.findViewById(R.id.tvP)
        val btnSave: MaterialButton = bottomSheetView.findViewById(R.id.btnSave)

        addImage.load(item.imagePath, R.drawable.ic_default_item)
        tvName.text = item.name
        tvBarcode.text = item.barcode
        tvPrice.text = item.salePriceTax
        tvP.text = item.saleUnitIdx.toString()

        btnSave.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        addImage.setOnClickListener {
            cameraDialog()
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }


    private fun cameraDialog() {
        val dialog = Dialog(this)
        val bottomSheetView = layoutInflater.inflate(R.layout.pick_image_dialog, null)

        val fromLaibrary: MaterialButton = bottomSheetView.findViewById(R.id.fromLabrary)
        val fromCamera: MaterialButton = bottomSheetView.findViewById(R.id.fromCamera)

        fromLaibrary.setOnClickListener {
            var intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
        }


        fromCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, readImagePermission) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(this, CameraActivity::class.java)
                startActivity(intent)
            } else {
                val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    storagePermissions33
                } else {
                    storagePermissions
                }
                PermissionX.init(this)
                    .permissions(*permissions)
                    .onExplainRequestReason { scope, deniedList ->
                        scope.showRequestReasonDialog(
                            deniedList,
                            "Core functionality is based on these permissions",
                            "OK",
                            "Cancel"
                        )
                    }
                    .onForwardToSettings { scope, deniedList ->
                        scope.showForwardToSettingsDialog(
                            deniedList,
                            "You need to allow necessary permissions in Settings manually",
                            "OK",
                            "Cancel"
                        )
                    }
                    .request { allGranted, grantedList, deniedList ->
                        if (allGranted) {
                            // Permissions granted, proceed with the camera functionality
                            // Start your activity or perform your desired action here
                        } else if (deniedList.isNotEmpty()) {
                            // Permissions denied, handle accordingly
                            Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()

                            // Open app settings screen for the user to manually grant permissions
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTINGS)
                        }
                    }
            }
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(bottomSheetView)


        dialog.window!!.setLayout(
            (WindowManager.LayoutParams.MATCH_PARENT).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun saveSuccessfully() {
        val dialog = Dialog(this)
        val bottomSheetView = layoutInflater.inflate(R.layout.temp_dailog, null)
        dialog.setContentView(bottomSheetView)
        dialog.show()
    }
}