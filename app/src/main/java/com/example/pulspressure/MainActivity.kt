package com.example.pulspressure

import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

const val COLLECTION = "pulsePressure"

class MainActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var rv: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var adapter: RvAdapter
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button
    private lateinit var layoutAdd: LinearLayout
    private lateinit var etHigh: EditText
    private lateinit var etLow: EditText
    private lateinit var etPulse: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = FirebaseFirestore.getInstance()

        rv =  findViewById(R.id.rv)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = RvAdapter()
        rv.adapter = adapter
        loadData()

        fabAdd = findViewById(R.id.fab_add)
        layoutAdd = findViewById(R.id.layout_add)
        fabAdd.setOnClickListener {
            clearAddLayout()
            showLayoutAdd()
        }

        btnCancel = findViewById(R.id.btn_cancel)
        btnCancel.setOnClickListener {
            showRv()
        }

        etHigh = findViewById(R.id.et_high)
        etLow = findViewById(R.id.et_low)
        etPulse = findViewById(R.id.et_pulse)

        btnSave = findViewById(R.id.btn_save)
        btnSave.setOnClickListener {
            val date = Date()
            val saveData = hashMapOf(
                "addDate" to DateFormat.getMediumDateFormat(this).format(date) + " "
                        + DateFormat.getTimeFormat(this).format(date),
                "high" to etHigh.text.toString(),
                "low" to etLow.text.toString(),
                "pulse" to etPulse.text.toString()
            )

            db.collection(COLLECTION)
                .add(saveData)
                .addOnSuccessListener { documentReference ->
                    loadData()
                    showRv()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Ошибка добавления: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadData() {
        db.collection(COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                val data = mutableListOf<Model>()
                for (document in result) {
                    val model = document.toObject(Model::class.java)
                    data.add(model)
                }
                data.sortBy { model -> model.addDate }
                adapter.setData(data)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLayoutAdd() {
        rv.visibility = View.GONE
        layoutAdd.visibility = View.VISIBLE
    }

    private fun showRv() {
        rv.visibility = View.VISIBLE
        layoutAdd.visibility = View.GONE
    }

    private fun clearAddLayout() {
        etHigh.setText("")
        etLow.setText("")
        etPulse.setText("")
    }
}