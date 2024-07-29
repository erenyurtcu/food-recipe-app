package com.erenyurtcu.foodrecipeapp.adapter

import android.graphics.BitmapFactory
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
        val recipe = RecipeList[position]
        holder.binding.recyclerViewTextView.text = recipe.name

        // Decode the image byte array and set it to the ImageView
        val bitmap = BitmapFactory.decodeByteArray(recipe.image, 0, recipe.image.size)
        holder.binding.recyclerViewImageView.setImageBitmap(bitmap)

        holder.itemView.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToRecipeFragment(info = "old", id = recipe.id)
            Navigation.findNavController(it).navigate(action)
        }
    }
}
