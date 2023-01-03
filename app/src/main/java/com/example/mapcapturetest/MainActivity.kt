package com.example.mapcapturetest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.UiThread
import androidx.core.app.ActivityCompat
import com.example.mapcapturetest.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource

class MainActivity : BindingActivity<ActivityMainBinding>(R.layout.activity_main),
    OnMapReadyCallback//지도객체 얻어오기
{
    private val viewModel:MainViewModel by viewModels()
    private lateinit var naverMap: NaverMap //지도객체 변수
    private lateinit var fusedLocation: FusedLocationProviderClient//현재 위치 반환 객체 변수
    private lateinit var locationSource: FusedLocationSource //위치 추적모드 변수
    private var currentLocation: LatLng = LatLng(37.52901832956373, 126.9136196847032) //국회의사당 좌표
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        addListener()
        binding.vm = viewModel

    }

    private fun addListener() {
        binding.btnCurrent.setOnClickListener {
            cameraUpdate(currentLocation)
        }
    }

    private fun init() {
        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        requestPermission()
    }

    private fun requestPermission() {
        TedPermission.create()
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() { //요청 승인 시
                    initView() //지도 뷰 표시
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) { //요청 거부 시
                    onDestroy() //앱 종료
                }
            })
            .setRationaleTitle("위치권한 요청")
            .setRationaleMessage("현재 위치로 이동하기 위해 위치 권한이 필요합니다.")
            .setDeniedMessage("권한을 허용해주세요. [설정] > [앱 및 알림] > [고급] > [앱 권한]")
            .setPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .check()
    }

    private fun initView() {
        //클라이언트 ID설정
        val naverClientId = getString(R.string.NAVER_CLIENT_ID)
        NaverMapSdk.getInstance(this).client = NaverMapSdk.NaverCloudPlatformClient(naverClientId)

        //FragmentTransaction을 사용해 FrameLayout에 MapFragment를 추가(권장)
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this) //지도 객체 얻어오기
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    @UiThread
    override fun onMapReady(map: NaverMap) {
        Log.d("지도 객체 준비", "완료")
        naverMap = map //지도 객체 초기화
        //내장된 위치 추적 기능 사용 최신버전 play-services 의존성 사용 시 에러 발생 주의!!
        map.locationSource = locationSource
        map.locationTrackingMode = LocationTrackingMode.Follow //위치추적 모드 Follow

        //권한 체크
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        naverMap.addOnLocationChangeListener { location ->
            currentLocation = LatLng(location.latitude, location.longitude)
            map.locationOverlay.run { //현재 위치 마커
                isVisible = true //현재 위치 마커 가시성(default = false)
                position = LatLng(currentLocation.latitude, currentLocation.longitude)
                subIcon = OverlayImage.fromResource(R.drawable.ic_launcher_foreground)
            }
        }
    }

    private fun cameraUpdate(location: LatLng) {
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(location.latitude, location.longitude))
        naverMap.moveCamera(cameraUpdate)
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1000 //현재 위치 권한 코드
    }
}