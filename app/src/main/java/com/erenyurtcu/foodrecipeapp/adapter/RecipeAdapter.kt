package com.erenyurtcu.foodrecipeapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.erenyurtcu.foodrecipeapp.databinding.RecyclerRowBinding
import com.erenyurtcu.foodrecipeapp.model.Recipe
import com.erenyurtcu.foodrecipeapp.view.ListFragmentDirections

class RecipeAdapter(val RecipeList: List<Recipe>) : RecyclerView.Adapter<RecipeAdapter.RecipeHolder>() {

    class RecipeHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeHolder {
        val recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeHolder(recyclerRowBinding)
    }

    override fun getItemCount(): Int {
        return RecipeList.size
    }

    override fun onBindViewHolder(holder: RecipeHolder, position: Int) {
        holder.binding.recyclerViewTextView.text = RecipeList[position].name
        holder.itemView.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToRecipeFragment(info = "old", id = RecipeList[position].id)
            Navigation.findNavController(it).navigate(action)
        }
    }
}
