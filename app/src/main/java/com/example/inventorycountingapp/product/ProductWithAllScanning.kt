package com.example.inventorycountingapp.product

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.ScanDevice
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
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
import com.example.inventorycountingapp.common.dialog.NoProductDialog
import com.example.inventorycountingapp.common.dialog.ProductDeleteDialog
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

    private var mScanDevice: ScanDevice? = null
    private var mmediaplayer: MediaPlayer? = null
    private var mvibrator: Vibrator? = null

    private var productResponse : ProductResponse ?= null

    private val mScanDataReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // TODO Auto-generated method stub
            val action = intent.action
            if (action == "ACTION_BAR_SCAN") {
                val str = intent.getStringExtra("EXTRA_SCAN_DATA")
                if (str != null) {
                    mmediaplayer!!.start();
                    mvibrator!!.vibrate(150)
                    binding.etBarcode.setText(str)
                    fetchProductInformation(str)
                }
                else{
                    resetData()
                }

            }
        }
    }

    private fun resetData() {
        binding.etBarcode.setText("")
        productResponse = null
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductWithAllScanningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        submit = findViewById(R.id.btnSubmit)

        setupRv()
        setUpScanner()

        submit.setOnClickListener {
            val intent = Intent(this, SubmittedSuccessfully::class.java)
            startActivity(intent)
        }

        binding.ivSearchByQr.setOnClickListener {
            val barCode = binding.etBarcode.text.toString()
            fetchProductInformation(barCode)
        }
    }

    private fun fetchProductInformation(barCode: String) {
        viewModel.getProduct(
            barCode,
            onSuccess = {
                productResponse = it
                if (productResponse != null) {
                    binding.apply {
                        ivProductImage.load(it.data.imagePath)
                        tvName.text = it.data.name
                        tvQuantity.text = it.data.defaultQty + " pcs, "
                        tvPricee.text = it.data.salePriceTax
                    }


                    if (productResponse!!.data.defaultQty.toDouble() == 0.0) {
                        productResponse!!.data.defaultQty = "1"
                    }

                    val existingItem = viewModel.selectedList.find { it.barcode == barCode }

                    if (existingItem != null) {
                        existingItem.defaultQty =
                            (existingItem.defaultQty.toDouble() + 1).toString()
                    } else {
                        viewModel.selectedList.add(productResponse!!.data)
                    }

//                    viewModel.selectedList.reverse()
                    adapter.setData(viewModel.selectedList)
                    adapter.notifyDataSetChanged()
                } else {
                    "No product found".toast()
                }
            },
            onFailed = {
                val customDialog = NoProductDialog(this, "Oops!  No Product!", barCode, it)
                customDialog.show()
            })
    }

    private fun setUpScanner() {
        mScanDevice = ScanDevice(this) //初始化接口  Initialization interface
        mvibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator // motor
        mmediaplayer = MediaPlayer() //   Initialize sound
        mmediaplayer = MediaPlayer.create(this, R.raw.scanok)
        mmediaplayer!!.isLooping = false
    }

    private fun setupRv() {
        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter = adapter
        adapter.onItemClick = {
            showDeleteDialog(it)
        }
    }

    private fun showDeleteDialog(it: ProductResponse.Data) {
        val customDialog = ProductDeleteDialog(this, it,
            onDelete = {
                viewModel.selectedList.remove(it)
                adapter.setData(viewModel.selectedList)
                adapter.notifyDataSetChanged()
            })
        customDialog.show()
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
            val intent = Intent()
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

    override fun onPause() {
        // TODO Auto-generated method stub
        super.onPause()
        if (mScanDevice != null) {
            mScanDevice!!.stopScan()
        }
        unregisterReceiver(mScanDataReceiver)
    }


    // Registered Data Broadcasting
    override fun onResume() {
        // TODO Auto-generated method stub
        val scanDataIntentFilter = IntentFilter()
        scanDataIntentFilter.addAction("ACTION_BAR_SCAN")
        registerReceiver(mScanDataReceiver, scanDataIntentFilter)
        super.onResume()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (mScanDevice != null) {
            mScanDevice!!.closeScan()
            mScanDevice = null
            mmediaplayer!!.release()
        }
    }
}