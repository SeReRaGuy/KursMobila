package com.example.mapYandex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mapYandex.databinding.FragmentListTagBinding

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

        binding.addbuttonid.setOnClickListener {
            val navAction = ListTagFragmentDirections.actionListTagFragmentToEditTagFragment(-1)
            findNavController().navigate(navAction)
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private val action = object : ActionInterface {
        override fun onItemClick(tagId: Int) {
            val action = ListTagFragmentDirections
                .actionListTagFragmentToEditTagFragment(tagId)
            findNavController().navigate(action)
        }

        override fun onDeleteTag(tagId: Int) {
            viewModel.deleteTag(tagId)
        }
    }
}