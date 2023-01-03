package com.example.mapcapturetest

import android.graphics.PointF
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import androidx.activity.viewModels
import androidx.annotation.UiThread
import com.example.mapcapturetest.databinding.ActivityDrawingBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay


class DrawingActivity : BindingActivity<ActivityDrawingBinding>(R.layout.activity_drawing),
    OnMapReadyCallback {
    //지도객체 얻어오기 {

    private val viewModel: DrawingViewModel by viewModels()
    private lateinit var naverMap: NaverMap //지도객체 변수
    private lateinit var fusedLocation: FusedLocationProviderClient//현재 위치 반환 객체 변수
    private var startLocation: LatLng = LatLng(37.5731176, 127.0325851) //출발 지점
    private val markerList = ArrayList<Marker>()//마커 배열
    private val positionList = ArrayList<LatLng>()//각 마커 위치 배열
    private val pathList = ArrayList<PathOverlay>()//경로 배열
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        binding.vm = viewModel
        addListeners()
    }

    private fun init() {
        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        initView()
    }

    private fun initView() {
        //클라이언트 ID설정
        val naverClientId = getString(R.string.NAVER_CLIENT_ID)
        NaverMapSdk.getInstance(this).client = NaverMapSdk.NaverCloudPlatformClient(naverClientId)

        //FragmentTransaction을 사용해 FrameLayout에 MapFragment를 추가(권장)
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.fl_drawing_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.fl_drawing_map, it).commit()
            }
        mapFragment.getMapAsync(this) //지도 객체 얻어오기
    }

    @UiThread
    override fun onMapReady(map: NaverMap) {
        naverMap = map //지도 객체 초기화
        naverMap.uiSettings.logoGravity = Gravity.TOP
        cameraUpdate(startLocation)
        val startMarker = Marker()
        if (!markerList.contains(startMarker)) { //startMarker가 onMapReady가 실행될 때마다 markerList에 중복으로 들어가는 것을 방지
            startMarker.position = startLocation
            startMarker.anchor = PointF(0.5f, 0.5f) //이렇게 해야 경로가 마커의 중앙에서부터 그려짐! 안그러면 맨 하단에서 시작
            startMarker.icon = OverlayImage.fromResource(R.drawable.map_start_point)
            startMarker.map = naverMap
            markerList.add(startMarker)
            positionList.add(startMarker.position)
        }
        naverMap.setOnMapClickListener { _, position -> //지도 클릭 시 마커 생성 및 배열에 추가
            val marker = Marker() //마커 찍기
            marker.position = position
            marker.anchor = PointF(0.5f, 0.5f) //이렇게 해야 경로가 마커 정중앙에서부터 시작함!! 기본은 마커 아래임.
            marker.map = naverMap
            marker.icon = OverlayImage.fromResource(R.drawable.map_point)
            markerList.add(marker)
            positionList.add(marker.position)

            val path = PathOverlay() //경로 그리기
            path.coords = listOf(
                markerList[markerList.lastIndex - 1].position,
                markerList[markerList.lastIndex].position
            )
            path.map = naverMap
            pathList.add(path)
        }
        binding.btnDrawingDeleteMarker.setOnClickListener { //마커,경로 삭제(시작 포인트만 빼고~)
            if (markerList.size > 1) {
                markerList.last().map = null //null값을 넣어줘야 사라짐
                markerList.removeLast()
                positionList.removeLast()
                pathList.last().map = null
                pathList.removeLast()
            }
        }
    }

    private fun addListeners() {
        binding.btnCapture.setOnClickListener {
            createMbr()
        }
    }

    //모든 마커를 포함할 수 있도록 하는 꼭지점 좌표 두개를 만들고
    //중간지점의 좌표값을 구해서 카메라 위치를 이동할 수 있게 함.
    private fun createMbr() {
        val bounds = LatLngBounds.Builder()
            .include(startLocation)
            .include(positionList)
            .build()
        naverMap.uiSettings.isZoomControlEnabled = false
        naverMap.setContentPadding(100, 100, 100, 100)
        cameraUpdate(bounds)
        //이슈 사항 -> 경로 다 그리고 캡쳐하면 종종 지도가 캡쳐할 화면으로 미처 이동하지 못한 상태에서 캡쳐 되어버림 -> 딜레이 부여
        Handler(Looper.getMainLooper()).postDelayed(
            {
                captureMap(bounds)
            }, 500
        )
    }

    private fun captureMap(bounds: LatLngBounds) {
        //캡쳐해서 이미지 뷰에 set하기~
        naverMap.takeSnapshot {
            binding.ivDrawingCaptured.setImageBitmap(it)
        }
    }

    //카메라 위치 변경 함수
    private fun cameraUpdate(location: Any) {
        //이건 맨 처음 지도 켤 때 startLocation으로 위치 옮길 때 사용
        if (location is LatLng) {
            val cameraUpdate = CameraUpdate.scrollTo(location)
            naverMap.moveCamera(cameraUpdate)
        }
        //이건 카메라 이동해서 캡쳐할 때 사용
        else if (location is LatLngBounds) {
            val cameraUpdate = CameraUpdate.fitBounds(location)
            naverMap.moveCamera(cameraUpdate)
        }
    }

}