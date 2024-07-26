package com.erenyurtcu.foodrecipeapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.erenyurtcu.foodrecipeapp.databinding.FragmentListBinding
import com.erenyurtcu.foodrecipeapp.databinding.FragmentRecipeBinding
import com.erenyurtcu.foodrecipeapp.RecipeFragmentArgs


class RecipeFragment : Fragment() {
    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):  View? {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageView.setOnClickListener { selectImage(it) }
        binding.saveButton.setOnClickListener { saveRecipe(it) }
        binding.deleteButton.setOnClickListener { deleteRecipe(it) }

        arguments?.let {
            val info = RecipeFragmentArgs.fromBundle(it).info

            if(info == "new"){
                binding.deleteButton.isEnabled = false
                binding.saveButton.isEnabled = true
            } else{
                binding.deleteButton.isEnabled = true
                binding.saveButton.isEnabled = true
            }
        }
    }

    private fun deleteRecipe(it: View?) {

    }

    private fun saveRecipe(it: View?) {

    }

    private fun selectImage(it: View?) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}