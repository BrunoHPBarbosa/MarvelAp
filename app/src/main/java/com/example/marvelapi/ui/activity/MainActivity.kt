package com.example.marvelapi.ui.activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.marvelapi.R
import com.example.marvelapi.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

initViews(binding)
    }

    private fun initViews(binding: ActivityMainBinding) {
navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navControler = navHostFragment.navController

        binding.bottomNavigation.apply {
            setupWithNavController(navControler)
            setOnItemReselectedListener {  }
        }
    }
}