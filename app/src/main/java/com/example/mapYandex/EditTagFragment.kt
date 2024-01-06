package com.example.mapYandex

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mapYandex.databinding.FragmentEditTagBinding

class EditTagFragment : Fragment() {
    private var _binding: FragmentEditTagBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<EditTagFragmentArgs>()
    private val tagId by lazy { args.tagId }
    private val viewModel: EditTagViewModel by viewModels { EditTagViewModel.Factory(tagId) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditTagBinding.inflate(layoutInflater, container, false)
        with(viewModel) {
            with(binding) {
                tag.observe(viewLifecycleOwner) { currentTag ->
                    nameField.setText(currentTag.name)
                    descriptionField.setText(currentTag.description)
                    commentField.setText(currentTag.comment)
                    if (currentTag.image != null) {
                        tagImage.setImageBitmap(currentTag.image)
                        setImage(currentTag.image)
                    } else {
                        tagImage.setImageResource(R.drawable.null_image)
                    }
                }
                image.observe(viewLifecycleOwner) {
                    tagImage.setImageBitmap(it)
                }
                tagImage.setOnClickListener {
                    getSystemContent.launch("image/*")
                }
                nameError.observe(viewLifecycleOwner) {
                    nameField.error = it
                }
                descriptionError.observe(viewLifecycleOwner) {
                    descriptionField.error = it
                }
                commentError.observe(viewLifecycleOwner) {
                    commentField.error = it
                }
                status.observe(viewLifecycleOwner) {
                    if (it.isProcessed) {
                        return@observe
                    }
                    when (it) {
                        is Failed -> Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG)
                            .show()

                        is Success -> if (!viewModel.checkIfNewTag()) {
                            val navAction =
                                EditTagFragmentDirections.actionEditTagFragmentToListTagFragment()
                            findNavController().navigate(navAction)
                        } else {
                            val navAction = EditTagFragmentDirections
                                .actionEditTagFragmentToListTagFragment()
                            findNavController().navigate(navAction)
                        }
                    }
                    it.isProcessed = true
                }
                nameField.addTextChangedListener(object : CustomEmptyTextWatcher() {
                    override fun afterTextChanged(s: Editable?) {
                        validateName(s.toString())
                    }
                })
                descriptionField.addTextChangedListener(object : CustomEmptyTextWatcher() {
                    override fun afterTextChanged(s: Editable?) {
                        validateDescription(s.toString())
                    }
                })
                commentField.addTextChangedListener(object : CustomEmptyTextWatcher() {
                    override fun afterTextChanged(s: Editable?) {
                        validateComment(s.toString())
                    }
                })
                saveButton.setOnClickListener {
                    viewModel.saveTag(
                        nameField.text.toString(),
                        descriptionField.text.toString(),
                        commentField.text.toString(),
                    )
                }
                return root
            }
        }
    }

    private val getSystemContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        viewModel.setImage(it.bitmap(requireContext()))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

