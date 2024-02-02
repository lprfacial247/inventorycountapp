package com.example.inventorycountingapp.product

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventorycountingapp.CameraActivity
import com.example.inventorycountingapp.ProfileScreen
import com.example.inventorycountingapp.R
import com.example.inventorycountingapp.SubmittedSuccessfully
import com.example.inventorycountingapp.common.dialog.NoProductDialog
import com.example.inventorycountingapp.common.load
import com.example.inventorycountingapp.common.toast
import com.example.inventorycountingapp.databinding.ActivityProductBinding
import com.example.inventorycountingapp.login.LoginViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.permissionx.guolindev.PermissionX

class ProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductBinding
    private val REQUEST_PERMISSION_SETTINGS = 1001
    private val viewModel by lazy { ViewModelProvider(this)[ProductViewModel::class.java] }
    private var productResponse : ProductResponse ?= null
    private val adapter by lazy { SelectedProductAdapter() }


    private val readImagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE

    private val storagePermissions33 = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_MEDIA_IMAGES
    )
    private val storagePermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private lateinit var cameraActivityLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var addImage: ImageView ?= null


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRv()
        initClicks()
        imageLauncher()
    }

    private fun imageLauncher() {
        cameraActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data: Intent? = result.data
                    val imageUri = data?.getStringExtra("image_uri")
                    if (addImage != null && imageUri != null) {
                        addImage?.load(imageUri)
                    }

                }
            }

        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data: Intent? = result.data
                    val selectedImageUri: Uri? = data?.data
                    if (addImage != null && selectedImageUri != null) {
                        addImage?.load(selectedImageUri)
                    }
                }
            }
    }

    private fun setupRv() {
        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter = adapter
        adapter.onItemClick = {
            showSaveBottomSheet(it)
        }
    }

    private fun initClicks() {
        binding.btnOk.setOnClickListener {
            if (productResponse == null) {
                "No product found".toast()
                return@setOnClickListener
            }

            val inputBarCode = binding.tvBarcode.text.toString()
            val num = binding.etQuantity.text.toString()
            productResponse?.data?.defaultQty = num

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
            resetData()
        }
        binding.btnSubmit.setOnClickListener {
            val intent = Intent(this, SubmittedSuccessfully::class.java)
            startActivity(intent)
        }


        binding.ivBack.setOnClickListener {
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
                        tvQuantity.text = it.data.defaultQty +" pcs, "
                        tvPricee.text = it.data.salePriceTax
                        if (it.data.defaultQty.toDouble() == 0.0) {
                            etQuantity.setText("1")
                        }
                        else {
                            etQuantity.setText(it.data.defaultQty)
                        }

                    }

                },
                onFailed = {
                    val customDialog = NoProductDialog(this, "Oops!  No Product!", barCode, it)
                    customDialog.show()
                })
        }
    }

    private fun resetData() {
        binding.ivProductImage.load("")
        binding.tvName.text = ""
        binding.tvPricee.text = ""
        binding.tvQuantity.text = ""
        binding.etQuantity.setText("0")
        binding.tvBarcode.setText("")
        productResponse = null
    }

    private fun showSaveBottomSheet(item: ProductResponse.Data) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = layoutInflater.inflate(R.layout.receipt_layout, null)

        addImage = bottomSheetView.findViewById(R.id.iv_product)
        val tvName: TextView = bottomSheetView.findViewById(R.id.tvName)
        val tvBarcode: TextView = bottomSheetView.findViewById(R.id.tvBarcode)
        val tvPrice: TextView = bottomSheetView.findViewById(R.id.tvPrice)
        val tvP: TextView = bottomSheetView.findViewById(R.id.tvP)
        val btnSave: MaterialButton = bottomSheetView.findViewById(R.id.btnSave)

        addImage?.load(item.imagePath, R.drawable.ic_default_item)
        tvName.text = item.name
        tvBarcode.text = item.barcode
        tvPrice.text = item.salePriceTax
        tvP.text = item.saleUnitIdx.toString()

        btnSave.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        addImage?.setOnClickListener {
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
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            pickImageLauncher.launch(intent)
            dialog.dismiss()
        }

        fromCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, readImagePermission) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(this, CameraActivity::class.java)
                cameraActivityLauncher.launch(intent)
                dialog.dismiss()
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
        val backToHome: MaterialButton = bottomSheetView.findViewById(R.id.btnBackToHome)
        backToHome.setOnClickListener {
            val intent = Intent(this, ProfileScreen::class.java)
            startActivity(intent)
        }
        dialog.setContentView(bottomSheetView)
        dialog.show()
    }
}