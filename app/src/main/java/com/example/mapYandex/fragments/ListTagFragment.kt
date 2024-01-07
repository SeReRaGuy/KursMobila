package com.example.mapYandex.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapYandex.databinding.FragmentListTagBinding
import com.example.mapYandex.ActionInterface
import com.example.mapYandex.CustomRecyclerAdapter
import com.example.mapYandex.viewmodels.ListTagViewModel

class ListTagFragment : Fragment() {
    private var _binding: FragmentListTagBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CustomRecyclerAdapter
    private val viewModel: ListTagViewModel by viewModels { ListTagViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListTagBinding.inflate(layoutInflater, container, false)

        val recyclerView: RecyclerView = binding.recyclerid
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = CustomRecyclerAdapter(action).apply {
            viewModel.tags.observe(viewLifecycleOwner) {
                tags = it
            }
        }
        recyclerView.adapter = adapter

        binding.buttonToMap.setOnClickListener {
            val navAction = ListTagFragmentDirections.actionListTagFragmentToMapFragment()
            findNavController().navigate(navAction)
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private val action = object : ActionInterface {
        override fun onItemClick(tagId: Long) {
            val action =
                ListTagFragmentDirections.actionListTagFragmentToEditTagFragment(
                    tagId
                )
            findNavController().navigate(action)
        }

        override fun onDeleteTag(tagId: Long) {
            viewModel.deleteTag(tagId)
        }
    }
}