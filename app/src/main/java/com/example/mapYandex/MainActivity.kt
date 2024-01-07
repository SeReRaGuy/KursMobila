package com.example.mapYandex

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mapYandex.databinding.ActivityMainBinding
import com.yandex.mapkit.MapKitFactory

class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    val MAPKIT_API_KEY = "acfc921a-3c8b-490a-8c83-b82f9fd50e44"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}