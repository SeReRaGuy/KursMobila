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
import android.provider.ContactsContract
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
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
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.launch


class Main : AppCompatActivity(), CameraListener {
    private lateinit var binding: ActivityBinding

    private val startLocation = Point(50.593679, 36.576692)
    private var zoomValue: Float = 17.0f

    private lateinit var mapObjectCollectionSmaller: MapObjectCollection
    private lateinit var mapObjectCollectionBigger: MapObjectCollection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setApiKey(savedInstanceState) // Проверяем: был ли уже ранее установлен API-ключ в приложении. Если нет - устанавливаем его.
        MapKitFactory.initialize(this) // Инициализация библиотеки для загрузки необходимых нативных библиотек.
        binding = ActivityBinding.inflate(layoutInflater) // Раздуваем макет только после того, как установили API-ключ
        setContentView(binding.root) // Размещаем пользовательский интерфейс в экране активности

        moveToStartLocation()

        mapObjectCollectionSmaller = binding.mapview.map.mapObjects // Инициализируем коллекцию различных объектов на карте
        mapObjectCollectionBigger = binding.mapview.map.mapObjects // Инициализируем коллекцию различных объектов на карте
        //setMarker(startLocation)

        binding.mapview.map.addCameraListener(this)

        binding.mapview.map.addInputListener(inputListener)
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
            CameraPosition(startLocation, zoomValue, 0.0f, 45.0f)
        )
    }

    companion object {
        const val MAPKIT_API_KEY = "acfc921a-3c8b-490a-8c83-b82f9fd50e44"
        const val ZOOM_BOUNDARY = 16.4f
        val marker = R.drawable.test // Добавляем ссылку на картинку
        val icstyle1 = IconStyle(null,null,null,null,null,0.055f,null)
        val icstyle2 = IconStyle(null,null,null,null,null,0.02f,null)
        val markerDataList = HashMap<Int, PlacemarkMapObject>()
        var num : Int = 0 //Прочитать высший id перед стартом
    }

    private fun setMarker(pointIn: Point) {
        var database: TagDatabase
        val _tag = MediatorLiveData<Tag>()
        val tag: LiveData<Tag> = _tag


        lateinit var placemarkMapObject : PlacemarkMapObject
        placemarkMapObject = mapObjectCollectionBigger.addPlacemark(pointIn, ImageProvider.fromResource(this, marker))
        placemarkMapObject.opacity = 0.5f // Устанавливаем прозрачность метке
        placemarkMapObject.setIcon(ImageProvider.fromResource(this, marker),icstyle1)
        markerDataList[num] = placemarkMapObject //Хранение меток
        num += 1


        val newTag = tag.value?.copy(
            id = num,
            name = null,
            description = null,
            comment = null,
            image = null
        )
        newTag?.let {
            viewModelScope.launch {
                if (checkIfNewCard()) {
                    database.tagDao().insert(it) //-
//                        cardRepository.insert(it)
                } else {
                    database.cardDao().update(it) //-
//                        cardRepository.update(it)
                }
                _status.value = Success()
            }
        }
    }

    fun checkIfNewTag() = tagId == -1
    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if (finished) { // Если камера закончила движение
            when {
                cameraPosition.zoom >= ZOOM_BOUNDARY -> {
                    for ((Num) in markerDataList)
                    {
                        markerDataList[Num]?.setIcon(ImageProvider.fromResource(this, marker),icstyle1)
                    }
                }
                cameraPosition.zoom < ZOOM_BOUNDARY -> {
                    for ((Num) in markerDataList)
                    {
                        markerDataList[Num]?.setIcon(ImageProvider.fromResource(this, marker),icstyle2)
                    }
                }
            }
        }
    }

    val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) {
        }

        override fun onMapLongTap(map: Map, point: Point) {
            setMarker(point)
        }
    }
}
