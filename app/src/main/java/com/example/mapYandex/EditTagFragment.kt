//package com.example.mapYandex
//
//import android.os.Bundle
//import android.text.Editable
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.navArgs
//import com.example.mapYandex.databinding.FragmentEditTagBinding
//
//class EditTagFragment : Fragment() {
//    private var _binding: FragmentEditTagBinding? = null
//    private val binding get() = _binding!!
//    private val args by navArgs<EditTagFragmentArgs>()
//    private val tagId by lazy { args.tagId }
//    private val viewModel: EditTagViewModel by viewModels { EditTagViewModel.Factory(tagId) }
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentEditCardBinding.inflate(layoutInflater, container, false)
//        with(viewModel) {
//            with(binding) {
//                card.observe(viewLifecycleOwner) { currentCard ->
//                    questionField.setText(currentCard.question)
//                    exampleField.setText(currentCard.example)
//                    answerField.setText(currentCard.answer)
//                    translationField.setText(currentCard.translation)
//                    if (currentCard.image != null) {
//                        cardImage.setImageBitmap(currentCard.image)
//                        setImage(currentCard.image)
//                    } else {
//                        cardImage.setImageResource(R.drawable.wallpapericon)
//                    }
//                }
////                observeFieldsErrors(viewModel, this)
////                addFieldsTextChangedListeners(viewModel, this)
//                image.observe(viewLifecycleOwner) {
//                    cardImage.setImageBitmap(it)
//                }
//                cardImage.setOnClickListener {
//                    getSystemContent.launch("image/*")
//                }
//                questionError.observe(viewLifecycleOwner) { //-
//                    questionField.error = it //-
//                } //-
//                exampleError.observe(viewLifecycleOwner) { //-
//                    exampleField.error = it //-
//                } //-
//                answerError.observe(viewLifecycleOwner) { //-
//                    answerField.error = it //-
//                } //-
//                translationError.observe(viewLifecycleOwner) { //-
//                    translationField.error = it //-
//                } //-
//                status.observe(viewLifecycleOwner) {
//                    if (it.isProcessed) {
//                        return@observe
//                    }
//                    when (it) {
//                        is Failed -> Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG)
//                            .show()
//
//                        is Success -> if (!viewModel.checkIfNewCard()) { //-
////                            is Success -> if (!checkIfNewCard()) {
//                            val navAction =
//                                EditCardFragmentDirections.actionEditCardFragmentToSeeCardFragment(
//                                    cardId
//                                )
//                            findNavController().navigate(navAction)
//                        } else {
//                            val navAction = EditCardFragmentDirections
//                                .actionEditCardFragmentToListCardFragment()
//                            findNavController().navigate(navAction)
//                        }
//                    }
//                    it.isProcessed = true
//                }
//                questionField.addTextChangedListener(object : CustomEmptyTextWatcher() { //-
//                    override fun afterTextChanged(s: Editable?) { //-
//                        validateQuestion(s.toString()) //-
//                    } //-
//                }) //-
//                exampleField.addTextChangedListener(object : CustomEmptyTextWatcher() { //-
//                    override fun afterTextChanged(s: Editable?) { //-
//                        validateExample(s.toString()) //-
//                    } //-
//                }) //-
//                answerField.addTextChangedListener(object : CustomEmptyTextWatcher() { //-
//                    override fun afterTextChanged(s: Editable?) { //-
//                        validateAnswer(s.toString()) //-
//                    } //-
//                }) //-
//                translationField.addTextChangedListener(object : CustomEmptyTextWatcher() { //-
//                    override fun afterTextChanged(s: Editable?) { //-
//                        validateTranslation(s.toString()) //-
//                    } //-
//                }) //-
//                saveButton.setOnClickListener {
//                    viewModel.saveCard( //-
////                        saveCard(
//                        questionField.text.toString(),
//                        exampleField.text.toString(),
//                        answerField.text.toString(),
//                        translationField.text.toString(),
//                    )
//                }
//                return root
//            }
//        }
//    }
//
//    private val getSystemContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
//        viewModel.setImage(it.bitmap(requireContext()))
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        _binding = null
//    }
//}