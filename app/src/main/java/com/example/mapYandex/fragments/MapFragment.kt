package com.example.mapYandex.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mapYandex.R
import com.example.mapYandex.databinding.FragmentMapBinding
import com.example.mapYandex.data.Tag
import com.example.mapYandex.data.TagDatabase
import com.example.mapYandex.viewmodels.EditTagViewModel
import com.example.mapYandex.viewmodels.MapViewModel
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MapFragment : Fragment(), CameraListener {

    private val viewModel: MapViewModel by viewModels { MapViewModel.Factory() }

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val startLocation = Point(50.593679, 36.576692)
    private var zoomValue: Float = 17.0f

    private lateinit var mapObjectCollectionSmaller: MapObjectCollection
    private lateinit var mapObjectCollectionBigger: MapObjectCollection

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        setApiKey(savedInstanceState) // Проверяем: был ли уже ранее установлен API-ключ в приложении. Если нет - устанавливаем его.
        MapKitFactory.initialize(requireContext()) // Инициализация библиотеки для загрузки необходимых нативных библиотек.
        _binding = FragmentMapBinding.inflate(
            layoutInflater, container, false
        ) // Раздуваем макет только после того, как установили API-ключ

        moveToStartLocation()

        mapObjectCollectionSmaller =
            binding.mapview.map.mapObjects // Инициализируем коллекцию различных объектов на карте
        mapObjectCollectionBigger =
            binding.mapview.map.mapObjects // Инициализируем коллекцию различных объектов на карте
        //setMarker(startLocation)

        binding.mapview.map.addCameraListener(this)

        binding.mapview.map.addInputListener(inputListener)

//        var tagDao = TagDatabase.getInstance(this).tagDao()
//        tagDao.findAll()

        binding.buttonOpenList.setOnClickListener {
            val action = MapFragmentDirections.actionMapFragmentToListTagFragment()
            findNavController().navigate(action)
        }
        return binding.root
    }

    private fun setApiKey(savedInstanceState: Bundle?) {
        val haveApiKey = savedInstanceState?.getBoolean("haveApiKey")
            ?: false // При первом запуске приложения всегда false
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
        val icstyle1 = IconStyle(null, null, null, null, null, 0.055f, null)
        val icstyle2 = IconStyle(null, null, null, null, null, 0.02f, null)
        val markerDataList = HashMap<Long, PlacemarkMapObject>()
        var num: Long = 0 //Прочитать высший id перед стартом
    }

    private fun setMarker(pointIn: Point) {
//        var database: TagDatabase
//        val _tag = MediatorLiveData<Tag>()
//        val tag: LiveData<Tag> = _tag


        //-------Создаём саму иконку и ставим её на карту-----------
        val placemarkMapObject: PlacemarkMapObject = mapObjectCollectionBigger.addPlacemark(
            pointIn, ImageProvider.fromResource(requireContext(), marker)
        )
        placemarkMapObject.opacity = 0.5f // Устанавливаем прозрачность метке
        placemarkMapObject.setIcon(ImageProvider.fromResource(requireContext(), marker), icstyle1)
        //----------------------------------------------------------

        var tagDao = TagDatabase.getInstance(requireContext()).tagDao()
//        val _tag = MediatorLiveData<Tag>()
//        val tag: LiveData<Tag> = _tag
//        val newTag = tag.value?.copy(
//            name = null,
//            description = null,
//            comment = null,
//            image = null,
//            cord1 = pointIn.latitude,
//            cord2 = pointIn.longitude
//        )

        GlobalScope.launch(Dispatchers.IO) {
            num = tagDao.insert(
                Tag(
                    null, null, null, null, null, pointIn.latitude, pointIn.longitude
                )
            )
        }
//        num = tagDao.insert(Tag(null,null,null,null,null,pointIn.latitude,pointIn.longitude))

        markerDataList[num] = placemarkMapObject //Хранение меток
//        num += 1

        placemarkMapObject.addTapListener(mapObjectTapListener)
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
                    for ((Num) in markerDataList) {
                        markerDataList[Num]?.setIcon(
                            ImageProvider.fromResource(
                                requireContext(), marker
                            ), icstyle1
                        )
                    }
                }

                cameraPosition.zoom < ZOOM_BOUNDARY -> {
                    for ((Num) in markerDataList) {
                        markerDataList[Num]?.setIcon(
                            ImageProvider.fromResource(
                                requireContext(), marker
                            ), icstyle2
                        )
                    }
                }
            }
        }
    }

    private val mapObjectTapListener = object : MapObjectTapListener {
        override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
            Toast.makeText(
                requireContext(), "Эрмитаж — музей изобразительных искусств", Toast.LENGTH_SHORT
            ).show()
            return true
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