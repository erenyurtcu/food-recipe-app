package com.erenyurtcu.foodrecipeapp.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.room.Room
import com.erenyurtcu.foodrecipeapp.databinding.FragmentRecipeBinding
import com.erenyurtcu.foodrecipeapp.model.Recipe
import com.erenyurtcu.foodrecipeapp.roomdb.RecipeDAO
import com.erenyurtcu.foodrecipeapp.roomdb.RecipeDatabase
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream


class RecipeFragment : Fragment() {
    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var selectedImage : Uri? = null
    private var selectedBitmap : Bitmap? = null
    private val mDisposable = CompositeDisposable()

    private lateinit var db : RecipeDatabase
    private lateinit var recipeDao: RecipeDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()

        db = Room.databaseBuilder(requireContext(),RecipeDatabase::class.java,"Recipes").build()
        recipeDao = db.recipeDao()
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

    private fun deleteRecipe(view : View) {

    }

    private fun saveRecipe(view : View) {
        val name = binding.nameText.toString()
        val ingredient = binding.ingredientText.toString()

        if(selectedBitmap != null) {
            val smallBitmap = createSmallBitmap(selectedBitmap!!,300)
            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val theByteArray = outputStream.toByteArray()
            val recipe = Recipe(name, ingredient,theByteArray)

            mDisposable.add(
                recipeDao.insert(recipe)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseForInsert)
            )
        }
    }

    private fun handleResponseForInsert(){

        val action = RecipeFragmentDirections.actionRecipeFragmentToListFragment()
        Navigation.findNavController(requireView()).navigate(action)
    }

    private fun selectImage(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(view, "You need to allow it to reach the gallery and select an image!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Allow") {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }.show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, "You need to allow it to reach the gallery and select an image!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Allow") {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }.show()
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }


    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == AppCompatActivity.RESULT_OK){
                val intentFromResult = result.data
                if(intentFromResult != null) {
                    selectedImage = intentFromResult.data
                    try {
                        if(Build.VERSION.SDK_INT>=28){
                            val source = ImageDecoder.createSource(requireActivity().contentResolver,selectedImage!!)
                            selectedBitmap = ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(selectedBitmap)
                        } else{
                            selectedBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,selectedImage)
                            binding.imageView.setImageBitmap(selectedBitmap)
                        }
                    } catch (e : Exception){
                        println(e.localizedMessage)
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                result->
            if(result){
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else{
                Toast.makeText(requireContext(),"Permission is not granted.",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createSmallBitmap(bitmapSelectedByUser : Bitmap,maximumSize : Int) : Bitmap{
        var width = bitmapSelectedByUser.width
        var height = bitmapSelectedByUser.height
        val bitmapRatio : Double = width.toDouble() / height.toDouble()

        if(bitmapRatio > 1) { // image is horizontal
            width = maximumSize
            height = (width / bitmapRatio).toInt()
        } else{ // image is vertical
            height = maximumSize
            width = (height * bitmapRatio).toInt()

        }
        return Bitmap.createScaledBitmap(bitmapSelectedByUser,width,height,true)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}