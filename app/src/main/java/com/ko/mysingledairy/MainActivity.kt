package com.ko.mysingledairy

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.ko.mysingledairy.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val locationGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

            val albumGranted =
                permissions[Manifest.permission.READ_MEDIA_IMAGES] == true ||
                        permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true

            if (locationGranted && albumGranted) {
                Toast.makeText(this, "모든 권한 허용됨", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "일부 권한이 거부됨", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate +")
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAndRequestPermissions()

//        enableEdgeToEdge()

//        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)

        startService(Intent(this, MainService::class.java))

        Timber.d("onCreate +")
    }

    override fun onDestroy() {
        Timber.d("onDestroy +")
        super.onDestroy()
        Timber.d("onDestroy -")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.tab1 -> {
                Toast.makeText(this@MainActivity, "목록", Toast.LENGTH_SHORT).show()
            }

            R.id.tab2 -> {
                Toast.makeText(this@MainActivity, "작성", Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }

    private fun checkAndRequestPermissions() {
        val permissionList = mutableListOf<String>()

        // 위치 권한
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // 앨범 권한 (버전 분기)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionList.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionList.isNotEmpty()) {
            showPermissionDialog(permissionList.toTypedArray())
        }
    }

    // 사용자에게 설명 팝업
    private fun showPermissionDialog(permissions: Array<String>) {
        AlertDialog.Builder(this)
            .setTitle("권한 요청")
            .setMessage(
                "사진, 위치 정보는 앱 사용 중에만 필요합니다.\n" +
                        "원활한 앱 사용을 위해 권한을 허용해주세요."
            )
            .setPositiveButton("허용") { _, _ ->
                permissionLauncher.launch(permissions)
            }
            .setNegativeButton("거부") { _, _ -> }
            .show()
    }

}