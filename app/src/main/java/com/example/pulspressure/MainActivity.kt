package com.example.pulspressure

import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pulspressure.data.Model
import com.example.pulspressure.mvi.MainContract
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collect
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var rv: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var adapter: RvAdapter
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button
    private lateinit var layoutAdd: LinearLayout
    private lateinit var etHigh: EditText
    private lateinit var etLow: EditText
    private lateinit var etPulse: EditText
    private lateinit var pb: ProgressBar
    private lateinit var tv: TextView

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        loadData()
        initButtons()
        setOnIntentListeners()
    }

    private fun initViews() {
        rv =  findViewById(R.id.rv)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = RvAdapter()
        rv.adapter = adapter
        etHigh = findViewById(R.id.et_high)
        etLow = findViewById(R.id.et_low)
        etPulse = findViewById(R.id.et_pulse)
        pb = findViewById(R.id.pb)
        tv = findViewById(R.id.tv)
    }

    private fun initButtons() {
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
            viewModel.setEvent(MainContract.Event.OnAdd(saveData))
        }
    }

    private fun setOnIntentListeners() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {
                when (it.actionState) {
                    is MainContract.ActionState.Idle -> { pb.isVisible = false }
                    is MainContract.ActionState.Loading -> { pb.isVisible = true }
                    is MainContract.ActionState.Success -> {
                        pb.isVisible = false
                        adapter.setData(it.actionState.data)
                    }
                    is MainContract.ActionState.ItemAdded -> {
                        showRv()
                        loadData()
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.effect.collect {
                when (it) {
                    is MainContract.Effect.ShowError -> {
                        pb.isVisible = false
                        Toast.makeText(this@MainActivity, it.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun loadData() {
        viewModel.setEvent(MainContract.Event.OnGetList)
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