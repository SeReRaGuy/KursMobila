package com.example.mapYandex.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.example.mapYandex.R
import com.example.mapYandex.databinding.FragmentMapBinding
import com.example.mapYandex.data.Tag
import com.example.mapYandex.data.TagDao
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
import java.math.BigDecimal
import kotlin.math.abs

class MapFragment : Fragment(), CameraListener {

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
        MapKitFactory.initialize(requireContext())
        _binding = FragmentMapBinding.inflate(
            layoutInflater, container, false
        )
        moveToStartLocation()
        mapObjectCollectionSmaller = binding.mapview.map.mapObjects
        mapObjectCollectionBigger = binding.mapview.map.mapObjects
        binding.mapview.map.addCameraListener(this)
        binding.mapview.map.addInputListener(inputListener)
        binding.buttonOpenList.setOnClickListener {
            val action = MapFragmentDirections.actionMapFragmentToListTagFragment()
            findNavController().navigate(action)
        }

//        var idInTag : Long = 0
        var cord1InTag : Double = 0.0
        var cord2InTag : Double = 0.0
//
//        lateinit var tags: LiveData<List<Tag>>
//        var tagDao = TagDatabase.getInstance(requireContext()).tagDao()
//        tags = tagDao.findAll()

        lateinit var tags: LiveData<List<Tag>>
        var tagDao = TagDatabase.getInstance(requireContext()).tagDao()
        tags = tagDao.findAll()

        tags.observe(viewLifecycleOwner) { tagsList ->
            for (tag in tagsList) {
                var idInTag = tag.id!!
                cord1InTag = tag.cord1!!
                cord2InTag = tag.cord2!!


                val pointForMapList = Point(cord1InTag, cord2InTag)
                val placemarkMapObject: PlacemarkMapObject = mapObjectCollectionBigger.addPlacemark(
                    pointForMapList, ImageProvider.fromResource(requireContext(), marker)
                )
//                placemarkMapObject.opacity = 0.5f
                placemarkMapObject.setIcon(
                    ImageProvider.fromResource(requireContext(), marker),
                    icstyle1
                )
                markerDataList[idInTag] = placemarkMapObject
                placemarkMapObject.addTapListener(mapObjectTapListener)
                num = idInTag
            }
        }

//        tags.value?.let { tagsList ->
//            for (tag in tagsList) {
//                tag.id?.let { idValue ->
//                    idInTag = idValue
//                }
//                tag.cord1?.let { cord1Value ->
//                    cord1InTag = cord1Value
//                }
//                tag.cord2?.let { cord2Value ->
//                    cord2InTag = cord2Value
//                }
//                val pointForMapList = Point(cord1InTag,cord2InTag)
//                val placemarkMapObject: PlacemarkMapObject = mapObjectCollectionBigger.addPlacemark(
//                    pointForMapList, ImageProvider.fromResource(requireContext(), marker))
//                placemarkMapObject.opacity = 0.5f
//                placemarkMapObject.setIcon(ImageProvider.fromResource(requireContext(), marker), icstyle1)
//                markerDataList[idInTag] = placemarkMapObject
//                placemarkMapObject.addTapListener(mapObjectTapListener)
//            }
//        }


        return binding.root
    }

    private fun moveToStartLocation() {
        binding.mapview.map.move(
            CameraPosition(startLocation, zoomValue, 0.0f, 45.0f)
        )
    }

    companion object {
        const val ZOOM_BOUNDARY = 16.4f
        val marker = R.drawable.test2
        val icstyle1 = IconStyle(null, null, null, null, null, 0.055f, null)
        val icstyle2 = IconStyle(null, null, null, null, null, 0.02f, null)
        val markerDataList = HashMap<Long, PlacemarkMapObject>()
        var num: Long = 0
    }

    private fun setMarker(pointIn: Point) {
        val placemarkMapObject: PlacemarkMapObject = mapObjectCollectionBigger.addPlacemark(
            pointIn, ImageProvider.fromResource(requireContext(), marker)
        )
//        placemarkMapObject.opacity = 0.5f
        placemarkMapObject.setIcon(ImageProvider.fromResource(requireContext(), marker), icstyle1)
        var tagDao = TagDatabase.getInstance(requireContext()).tagDao()
        GlobalScope.launch(Dispatchers.IO) {
            num = tagDao.insert(
                Tag(
                    null, null, null, null, null, pointIn.latitude, pointIn.longitude
                )
            )
        }
        markerDataList[num] = placemarkMapObject
        placemarkMapObject.addTapListener(mapObjectTapListener)
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if (finished) {
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



//            for ((key, value) in markerDataList) {
//                val placemarkPointLocation = value.getGeometry() // Получаем Point из PlacemarkMapObject
//
//                var placemarkLatitude : Double = placemarkPointLocation.latitude
//                var placemarkLongitude : Double = placemarkPointLocation.longitude
//
//                var pointLatitude : Double = point.latitude
//                var pointLongitude : Double = point.longitude
//                // Проверяем, совпадают ли Point
//                if (placemarkLatitude == pointLatitude && placemarkLongitude == pointLongitude) {
//                    // Если Point совпадают, получаем идентификатор
//                    val action =
//                        MapFragmentDirections.actionMapFragmentToEditTagFragment(
//                            key
//                        )
//                    findNavController().navigate(action)
//                    // Здесь вы можете использовать markerId для дальнейших действий
//                    break
//                }
//            }

            var idForBD : Long = 0L
            var help = 0

            var subtractionLatitudeMIN : Double = 0.0
            var subtractionLongitudeMIN : Double = 0.0
            for ((key, value) in markerDataList) {

                val placemarkPointLocation = value.getGeometry() // Получаем Point из PlacemarkMapObject

                var placemarkLatitude : Double = placemarkPointLocation.latitude
                var placemarkLongitude : Double = placemarkPointLocation.longitude

                var pointLatitude : Double = point.latitude
                var pointLongitude : Double = point.longitude

                var subtractionLatitude : Double = abs(placemarkLatitude - pointLatitude)
                var subtractionLongitude : Double = abs(placemarkLongitude - pointLongitude)

                if (help == 0)
                {
                    help = 1
                    subtractionLatitudeMIN = subtractionLatitude
                    subtractionLongitudeMIN = subtractionLongitude
                    idForBD = key
                }
                if (subtractionLatitudeMIN > subtractionLatitude && subtractionLongitudeMIN > subtractionLongitude)
                {
                    subtractionLatitudeMIN = subtractionLatitude
                    subtractionLongitudeMIN = subtractionLongitude
                    idForBD = key
                }
            }
            val action =
                MapFragmentDirections.actionMapFragmentToEditTagFragment(
                    idForBD
                )
            findNavController().navigate(action)




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