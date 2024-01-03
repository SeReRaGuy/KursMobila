package com.example.mapYandex

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.map.Map


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.mapYandex.databinding.ActivityBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider



class Main : AppCompatActivity(), CameraListener {
    private lateinit var binding: ActivityBinding

    private val startLocation = Point(50.593679, 36.576692)
    private var zoomValue: Float = 17.0f

    private lateinit var mapObjectCollection: MapObjectCollection
    private lateinit var placemarkMapObject: PlacemarkMapObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setApiKey(savedInstanceState) // Проверяем: был ли уже ранее установлен API-ключ в приложении. Если нет - устанавливаем его.
        MapKitFactory.initialize(this) // Инициализация библиотеки для загрузки необходимых нативных библиотек.
        binding = ActivityBinding.inflate(layoutInflater) // Раздуваем макет только после того, как установили API-ключ
        setContentView(binding.root) // Размещаем пользовательский интерфейс в экране активности

        moveToStartLocation()
        setMarkerInStartLocation()

        binding.mapview.map.addCameraListener(this)
    }

    private fun setApiKey(savedInstanceState: Bundle?) {
        val haveApiKey = savedInstanceState?.getBoolean("haveApiKey") ?: false // При первом запуске приложения всегда false
        if (!haveApiKey) {
            MapKitFactory.setApiKey(MAPKIT_API_KEY) // API-ключ должен быть задан единожды перед инициализацией MapKitFactory
        }
    }

    // Если Activity уничтожается (например, при нехватке памяти или при повороте экрана) - сохраняем информацию, что API-ключ уже был получен ранее
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("haveApiKey", true)
    }

    // Отображаем карты перед моментом, когда активити с картой станет видимой пользователю:
    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapview.onStart()
    }

    // Останавливаем обработку карты, когда активити с картой становится невидимым для пользователя:
    override fun onStop() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    private fun moveToStartLocation() {
        binding.mapview.map.move(
            CameraPosition(startLocation, zoomValue, 0.0f, 0.0f)
        )
    }

    companion object {
        const val MAPKIT_API_KEY = "acfc921a-3c8b-490a-8c83-b82f9fd50e44"
        const val ZOOM_BOUNDARY = 16.4f
        val marker = R.drawable.test // Добавляем ссылку на картинку
        val icstyle1 = IconStyle(null,null,null,null,null,0.05f,null)
        val icstyle2 = IconStyle(null,null,null,null,null,0.025f,null)
    }

    private fun setMarkerInStartLocation() {
        mapObjectCollection = binding.mapview.map.mapObjects // Инициализируем коллекцию различных объектов на карте
        placemarkMapObject = mapObjectCollection.addPlacemark(startLocation, ImageProvider.fromResource(this, marker)) // Добавляем метку со значком (тут что-то щёлкать?)
        placemarkMapObject.opacity = 0.5f // Устанавливаем прозрачность метке
        placemarkMapObject.setIcon(ImageProvider.fromResource(this, marker),icstyle1)
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if (finished) { // Если камера закончила движение
            when {
                cameraPosition.zoom >= ZOOM_BOUNDARY -> {
                    placemarkMapObject.setIcon(ImageProvider.fromResource(this, marker),icstyle1)
                }
                cameraPosition.zoom < ZOOM_BOUNDARY -> {
                    placemarkMapObject.setIcon(ImageProvider.fromResource(this, marker),icstyle2)
                }
            }
        }
    }
}


//class Main : AppCompatActivity(){
//    private lateinit var binding: ActivityBinding
//
//    private lateinit var mapObjectCollection: MapObjectCollection
//    private lateinit var placemarkMapObject: PlacemarkMapObject
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        MapKitFactory.setApiKey("acfc921a-3c8b-490a-8c83-b82f9fd50e44")
//        MapKitFactory.initialize(this)
//
//        binding = ActivityBinding.inflate(layoutInflater)
//        val view = binding.root
//        setContentView(view)
//
//        binding.mapview.map.move(
//            CameraPosition(Point(50.593679, 36.576692), 17.0f, 0.0f, 45.0f), // Позиция камеры
//            Animation(Animation.Type.SMOOTH, 1.5f), null // Анимация при переходе
//        )
//
//    }
//
//    override fun onStop() {
//        binding.mapview.onStop()
//        MapKitFactory.getInstance().onStop()
//        super.onStop()
//    }
//
//    override fun onStart() {
//        binding.mapview.onStart()
//        MapKitFactory.getInstance().onStart()
//        super.onStart()
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putBoolean("acfc921a-3c8b-490a-8c83-b82f9fd50e44",true)
//    }
//}



















//class Main : AppCompatActivity(), UserLocationObjectListener, CameraListener {
//
//    //
//
////    private lateinit var mapView: MapView
//
//    //
//
//
//    private lateinit var binding: ActivityBinding
//    private lateinit var checkLocationPermission: ActivityResultLauncher<Array<String>>
//    private lateinit var userLocationLayer: UserLocationLayer
//
//    private var belgorodLocation = Point(50.595289, 36.587130) // Координаты Белгорода
//    private var startLocation = Point(0.0, 0.0)
//    private val zoomValue: Float = 14.5f // Величина зума
//
//    private var permissionLocation = false //Есть ли разрешение на определение местоположения.
//    private var followUserLocation = false //Включен ли режим следования за пользователем на карте.
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setApiKey(savedInstanceState)
//        binding = ActivityBinding.inflate(layoutInflater)
//
//        val view = binding.root
//        setContentView(view)
//
//        checkLocationPermission = registerForActivityResult( //Получение нескольких разрешений
//            ActivityResultContracts.RequestMultiplePermissions()
//        ) { permissions ->
//            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || //Разрешение на получение точной локации
//                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) { //Разрешение на получение приблизительной локации
//                onMapReady()
//            }
//        }
//        checkPermission()
//        userInterface()
//
//
//        //
//
////        MapKitFactory.initialize(this)
////        setContentView(R.layout.activity)
////        mapView = findViewById(R.id.mapview)
////
////        val imageProvider = ImageProvider.fromResource(this, R.drawable.test)
////        val placemark = mapView.map.mapObjects.addPlacemark().apply {
////            geometry = Point(50.594891, 36.587088)
////            setIcon(imageProvider)
////        }
//
//        //
//
//
//
//
//    }
//
//    //Проверка разрешений на определение местоположения
//    private fun checkPermission() {
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED ||
//            ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            onMapReady()
//        } else {
//            checkLocationPermission.launch(arrayOf(
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ))
//        }
//    }
//
//    /*Устанавливает положение логотипа Якарты.
//    Устанавливает обработчик кнопки отображения местоположения*/
//    private fun userInterface() {
//        val mapLogoAlignment = Alignment(HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM)
//        binding.mapview.map.logo.setAlignment(mapLogoAlignment)
//        binding.userLocationFab.setOnClickListener {
//            if (permissionLocation) {
//                cameraUserPosition()
//                followUserLocation = true
//            } else {
//                moveToBelgorodLocation()
//            }
//        }
//    }
//
//    private fun onMapReady() {
//        val mapKit = MapKitFactory.getInstance()
//        userLocationLayer = mapKit.createUserLocationLayer(binding.mapview.mapWindow) //Создание местоположение пользователя
//        userLocationLayer.isVisible = true //Метка
//        userLocationLayer.isHeadingEnabled = true //Направление метки
//        userLocationLayer.setObjectListener(this) //
//        binding.mapview.map.addCameraListener(this) //Установка слушателя - камеры
//
//        cameraUserPosition()
//        permissionLocation = true
//    }
//
//    //Перемещение камеры к текущему местоположению пользователя на карте
//    private fun cameraUserPosition() {
//        if (userLocationLayer.cameraPosition() != null) {
//            startLocation = userLocationLayer.cameraPosition()!!.target
//            binding.mapview.map.move(
//                CameraPosition(startLocation, zoomValue, 0.0f, 0.0f), // Позиция камеры
//                Animation(Animation.Type.SMOOTH, 0.5f), null // Анимация при переходе на точку
//            )
//        } else {
//            binding.mapview.map.move(CameraPosition(startLocation, zoomValue, 0f, 0f),
//                Animation(Animation.Type.SMOOTH, 3.5f), null // Анимация при переходе на точку
//            )
//        }
//    }
//
//    //Обработчик изменения позиции камеры на карте.
//    override fun onCameraPositionChanged(
//        map: Map, cPos: CameraPosition, cUpd: CameraUpdateReason, finish: Boolean
//    ) {
//        if (finish) {
//            if (followUserLocation) {
//                setAnchor()
//            }
//        } else {
//            if (!followUserLocation) {
//                noAnchor()
//            }
//        }
//    }
//
//    /* Устанавка якоря для отображения местоположения пользователя.
//    Вызывается, когда followUserLocation = true.*/
//    private fun setAnchor() {
//        // Установка якоря в центр экрана с учетом высоты и ширины карты
//        userLocationLayer.setAnchor(
//            PointF(
//                (binding.mapview.width * 0.5).toFloat(), (binding.mapview.height * 0.5).toFloat()
//            ),
//            PointF(
//                (binding.mapview.width * 0.5).toFloat(), (binding.mapview.height * 0.83).toFloat()
//            )
//        )
//        binding.userLocationFab.setImageResource(R.drawable.location)
//
//        followUserLocation = false
//    }
//
//    /* Сбрасывает якорь.
//    Вызывается, когда followUserLocation = false.*/
//    private fun noAnchor() {
//        userLocationLayer.resetAnchor()
//        binding.userLocationFab.setImageResource(R.drawable.no_location)
//    }
//
//    //Переход камеры к координатам Белгорода
//    private fun moveToBelgorodLocation() {
//        binding.mapview.map.move(
//            CameraPosition(belgorodLocation, zoomValue, 0.0f, 0.0f), // Позиция камеры
//            Animation(Animation.Type.SMOOTH, 1.5f), null // Анимация при переходе
//        )
//    }
//
//    //Сохранение API-ключа, если активность потребуется воссоздать
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putBoolean(MAPKIT_API_KEY,true)
//    }
//
//    //Проверяет наличие API-ключа в активности. Для проверки его единоразовой установки
//    private fun setApiKey(savedInstanceState: Bundle?) {
//        val haveApiKey = savedInstanceState?.getBoolean(MAPKIT_API_KEY) ?: false
//        if (!haveApiKey) {
//            MapKitFactory.setApiKey(MAPKIT_API_KEY)
//        }
//    }
//
//    override fun onObjectAdded(userLocationView: UserLocationView) {
//        setAnchor()
//    }
//
//    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {}
//    override fun onObjectRemoved(p0: UserLocationView) {}
//
//    override fun onStop() {
//        binding.mapview.onStop()
//        MapKitFactory.getInstance().onStop()
//        super.onStop()
//    }
//
//    override fun onStart() {
//        binding.mapview.onStart()
//        MapKitFactory.getInstance().onStart()
//        super.onStart()
//    }
//
//    companion object {
//        const val MAPKIT_API_KEY = "acfc921a-3c8b-490a-8c83-b82f9fd50e44"
//    }
//}
