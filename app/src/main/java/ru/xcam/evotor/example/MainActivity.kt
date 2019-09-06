package ru.xcam.evotor.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.xcam.evotor.example.interactor.OpenReceiptInteractor

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnOpenReceipt.setOnClickListener {
            OpenReceiptInteractor().execute(this)
        }
    }
}
