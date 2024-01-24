package com.example.inventorycountingapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraActivity : AppCompatActivity() {
    private var mCamera: Camera? = null
    private var mPreview: CameraPreview? = null
    private var mPicture: Camera.PictureCallback? = null
    private var myContext: Context? = null
    private var cameraPreview: LinearLayout? = null
    private var cameraFront = false
    private var bitmap: Bitmap? = null
    private var capture: MaterialButton? = null
    private var btnSwitch: MaterialButton? = null
    private var fromcamera: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(

            R.layout.activity_camera)
        myContext = this
        capture = findViewById(R.id.capture_beauty_mirror)
        btnSwitch = findViewById(R.id.btnSwitchCameraMain)

        val cameraId = findBackFacingCamera()
        mPicture = getPictureCallback()
       /* mCamera = Camera.open(cameraId)

        mCamera?.setDisplayOrientation(90)*/

        cameraPreview = findViewById<View>(R.id.cPreview) as LinearLayout
        mPreview = com.example.inventorycountingapp.CameraPreview(myContext, mCamera)
        cameraPreview?.addView(mPreview)

        capture?.setOnClickListener(View.OnClickListener {
            try {
                mCamera?.let { camera ->
                    val parameters = camera.parameters
                    val supportedPictureFormats = parameters.supportedPictureFormats

                    if (supportedPictureFormats.contains(ImageFormat.JPEG)) {
                        camera.takePicture(null, null, mPicture)
                    } else {
                        // Handle the case where JPEG format is not supported
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle any exceptions that occur during picture capture
            }
        })


        btnSwitch?.setOnClickListener(View.OnClickListener {
            val camerasNumber = Camera.getNumberOfCameras()
            if (camerasNumber > 1) {
                releaseCamera()
                chooseCamera()
            } else {
                // Handle the case when there is only one camera available
            }
        })
    }

    private fun findFrontFacingCamera(): Int {
        fromcamera = true
        var cameraId = -1
        val numberOfCameras = Camera.getNumberOfCameras()
        for (i in 0 until numberOfCameras) {
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(i, info)
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i
                cameraFront = true
                break
            }
        }
        return cameraId
    }

    private fun findBackFacingCamera(): Int {
        fromcamera = false
        var cameraId = -1
        val numberOfCameras = Camera.getNumberOfCameras()
        for (i in 0 until numberOfCameras) {
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(i, info)
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i
                cameraFront = false
                break
            }
        }
        return cameraId
    }

    override fun onResume() {
        super.onResume()
        if (mCamera == null) {
            val cameraId = findBackFacingCamera()
            mCamera = Camera.open(cameraId)
            mCamera?.setDisplayOrientation(90)
            mPicture = getPictureCallback()
            mPreview!!.refreshCamera(mCamera)
        }
    }

    private fun chooseCamera() {
        if (cameraFront) {
            fromcamera = true
            val cameraId = findBackFacingCamera()
            if (cameraId >= 0) {
                mCamera = Camera.open(
                    cameraId
                )
                mCamera?.setDisplayOrientation(90)
                mPicture = getPictureCallback()
                mPreview!!.refreshCamera(mCamera)
            }
        } else {
            fromcamera = false
            val cameraId = findFrontFacingCamera()
            if (cameraId >= 0) {
                mCamera = Camera.open(cameraId)
                mCamera?.setDisplayOrientation(90)
                mPicture = getPictureCallback()
                mPreview!!.refreshCamera(mCamera)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        releaseCamera()
    }

    private fun releaseCamera() {
        if (mCamera != null) {
            mCamera!!.stopPreview()
            mCamera!!.setPreviewCallback(null)
            mCamera!!.release()
            mCamera = null
        }
    }

    private fun getPictureCallback(): Camera.PictureCallback {
        return Camera.PictureCallback { data, camera ->
            val rotatedBitmap = if (fromcamera == true) {
                rotateBitmapFront(data)
            } else {
                rotateBitmapBack(data)
            }
           /* val intent = Intent(this, ImageEditActivity::class.java)
            val uriImage = getImageUri(this, rotatedBitmap)
            intent.putExtra("image_uri", uriImage.toString())
            startActivity(intent)*/
        }
    }

    private fun rotateBitmapFront(data: ByteArray): Bitmap {
        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
        val matrix = Matrix()
        matrix.postRotate(270f)
        matrix.postScale(-1f, 1f) // Apply a horizontal flip
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun rotateBitmapBack(data: ByteArray): Bitmap {
        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
        val matrix = Matrix()
        matrix.postRotate(90f)
        matrix.preScale(1.0f, -1.0f)
        matrix.postScale(-1f, 1f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }


    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

        // Generate unique file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timeStamp.jpg"

        // Get the external storage directory
        val storageDir: File? = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val imageFile = File(storageDir, fileName)

        return try {
            FileOutputStream(imageFile).apply {
                write(bytes.toByteArray())
                flush()
                close()
            }
            Uri.fromFile(imageFile)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}