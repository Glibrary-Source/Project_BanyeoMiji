package com.twproject.banyeomiji.view.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_fragment_host, FragmentLogin())
        transaction.commit()

    }
}