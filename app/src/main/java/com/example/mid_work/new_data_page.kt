package com.example.mid_work

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class new_data_page : AppCompatActivity() {
    private lateinit var ed_date: EditText
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_data_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val cls = resources.getStringArray(R.array.cls_array)
        val ed_item = findViewById<EditText>(R.id.ed_item)
        ed_date = findViewById(R.id.ed_date)
        val ed_cost = findViewById<EditText>(R.id.ed_cost)
        val cls_spinner = findViewById<Spinner>(R.id.cls_spinner)
        val ad_dt_in_db = findViewById<Button>(R.id.ch_dt_in_db)
        val ed_size = findViewById<EditText>(R.id.ed_size)


        cls_spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cls)

        // 設置日期選擇器
        ed_date.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            ed_date.setText(String.format("%04d/%02d/%02d", selectedYear, selectedMonth + 1, selectedDay))
        }, year, month, day).show()
    }

        ad_dt_in_db.setOnClickListener {
            if (ed_item.text.isEmpty()) {
                Toast.makeText(this, "請輸入品項名稱", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (ed_date.text.isEmpty()) {
                Toast.makeText(this, "請選擇購買日期", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (ed_cost.text.isEmpty()) {
                Toast.makeText(this, "請輸入購買金額", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val intent = Intent()
                intent.putExtra("item", ed_item.text.toString())
                intent.putExtra("time", ed_date.text.toString())
                intent.putExtra("month", ed_date.text.toString().substring(5, 7))
                intent.putExtra("year", ed_date.text.toString().substring(0, 4))
                intent.putExtra("cls", cls_spinner.selectedItem.toString())
                intent.putExtra("cost", ed_cost.text.toString().toInt())
                intent.putExtra("size", ed_size.text.toString().toInt())

                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }
}
