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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.provider.Settings
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
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
import com.example.inventorycountingapp.common.DeviceUtils
import com.example.inventorycountingapp.common.convertToTransformedList
import com.example.inventorycountingapp.common.dialog.Loader
import com.example.inventorycountingapp.common.dialog.NoProductDialog
import com.example.inventorycountingapp.common.dialog.ProductDeleteDialog
import com.example.inventorycountingapp.common.load
import com.example.inventorycountingapp.common.pref.SpManager
import com.example.inventorycountingapp.common.toast
import com.example.inventorycountingapp.databinding.ActivityProductBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.permissionx.guolindev.PermissionX
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class ProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductBinding
    private val REQUEST_PERMISSION_SETTINGS = 1001
    private val viewModel by lazy { ViewModelProvider(this)[ProductViewModel::class.java] }
    private var productResponse: ProductResponse? = null
    private val adapter by lazy { SelectedProductAdapter() }
    private val loader by lazy { Loader(this) }


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
    private var addImage: ImageView? = null

    private var mScanDevice: ScanDevice? = null
    private var mmediaplayer: MediaPlayer? = null
    private var mvibrator: Vibrator? = null

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

            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setUpScanner()
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
                        addImage?.tag = imageUri
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
                        addImage?.tag = selectedImageUri
                    }
                }
            }
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
            //showSaveBottomSheet(it)
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
                if (item.barcode != inputBarCode) {
                    tempList.add(item)
                }
            }
            tempList.add(productResponse!!.data)
            viewModel.selectedList.clear()
            viewModel.selectedList.addAll(tempList)
//            viewModel.selectedList.reverse()
            adapter.setData(viewModel.selectedList)
            resetData()
        }
        binding.btnSubmit.setOnClickListener {
            if (viewModel.selectedList.isEmpty()) {
                "Nothing selected".toast()
                return@setOnClickListener
            }

            loader.showLoader()

            val user = SpManager.getUser(this)
            val wireHouseId = SpManager.getInt(this, SpManager.KEY_WIRE_HOUSE_INDEX, 0)
            val floorId = SpManager.getInt(this, SpManager.KEY_FLOOR_INDEX, 0)
            val roomId = SpManager.getInt(this, SpManager.KEY_ROOM_INDEX, 0)
            val sectionId = SpManager.getInt(this, SpManager.KEY_SECTION_INDEX, 0)
            val deviceToken = DeviceUtils.getDeviceIMEI(this)
            val listToSubmit = convertToTransformedList(viewModel.selectedList)

            viewModel.submitScanningResult(user.userIdx, wireHouseId, floorId, roomId, sectionId, deviceToken,
                Gson().toJson(listToSubmit),
                onSuccess = {
                    loader.hideLoader()
                    val intent = Intent(this, SubmittedSuccessfully::class.java)
                    startActivity(intent)
                    finish()
                },
                onFailed = {
                    it.toast()
                    loader.hideLoader()
                })

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
                binding.apply {
                    ivProductImage.load(it.data.imagePath, R.drawable.ic_default_item)
                    tvName.text = it.data.name
                    tvQuantity.text = it.data.defaultQty + " pcs, "
                    tvPricee.text = it.data.salePriceTax
                    if (it.data.defaultQty.toDouble() == 0.0) {
                        etQuantity.setText("1")
                    } else {
                        etQuantity.setText(it.data.defaultQty)
                    }
                }

            },
            onFailed = {
                val customDialog = NoProductDialog(this, "Oops!  No Product!", barCode, it)
                customDialog.show()
                resetData()
            })
    }

    private fun resetData() {
        binding.ivProductImage.load("")
        binding.tvName.text = ""
        binding.tvPricee.text = ""
        binding.tvQuantity.text = ""
        binding.etQuantity.setText("0")
        binding.etBarcode.setText("")
        productResponse = null
    }

    private fun showSaveBottomSheet(item: ProductResponse.Data) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = layoutInflater.inflate(R.layout.receipt_layout, null)

        addImage = bottomSheetView.findViewById(R.id.iv_product)
        val tvName: TextView = bottomSheetView.findViewById(R.id.tvName)
        val tvBarcode: TextView = bottomSheetView.findViewById(R.id.etBarcode)
        val tvPrice: TextView = bottomSheetView.findViewById(R.id.tvPrice)
        val tvP: TextView = bottomSheetView.findViewById(R.id.tvP)
        val btnSave: MaterialButton = bottomSheetView.findViewById(R.id.btnSave)

        addImage?.load(item.imagePath, R.drawable.ic_default_item)
        tvName.text = item.name
        tvBarcode.text = item.barcode
        tvPrice.text = item.salePriceTax
        tvP.text = item.saleUnitIdx.toString()

        btnSave.setOnClickListener {
            loader.showLoader()
            val productIdx = item.index.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val fileName = item.name.toRequestBody("text/plain".toMediaTypeOrNull())

            val file = File(addImage?.tag.toString().replace("file:", ""))
            val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

            viewModel.updateProductImage(productIdx, fileName, imagePart,
                onSuccess = {
                    loader.hideLoader()
                    bottomSheetDialog.dismiss()
                    it.toast()
                },
                onFailed = {
                    loader.hideLoader()
                    it.toast()
                }
            )


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