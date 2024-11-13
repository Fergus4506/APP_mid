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

class activity_change_data_page : AppCompatActivity() {

    //初始化參數
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_change_data_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //抓取Intent傳過來的id並且利用id連接資料庫並抓取特定資料
        val db_id=intent.getIntExtra("id",0)
        val dbrw=MydbCostHelper(this).writableDatabase
        val cussor=dbrw.rawQuery("SELECT * FROM costTable WHERE id = $db_id",null)
        cussor.moveToFirst()
        val cls=resources.getStringArray(R.array.cls_array)

        // 抓取元件
        val ed_item=findViewById<EditText>(R.id.ed_item)
        val ed_date=findViewById<EditText>(R.id.ed_date)
        val ed_cost=findViewById<EditText>(R.id.ed_cost)
        val cls_spinner=findViewById<Spinner>(R.id.cls_spinner)
        val ad_dt_in_db=findViewById<Button>(R.id.ch_dt_in_db)
        val new_size=findViewById<EditText>(R.id.ed_size)

        //設定adapter
        cls_spinner.adapter= ArrayAdapter(this,android.R.layout.simple_spinner_item,cls)

        //抓取資料並顯示在元件上
        ed_item.setText(cussor.getString(1))
        ed_date.setText(cussor.getString(2))
        ed_cost.setText(cussor.getInt(6).toString())
        val spinnerPosition = cls.indexOf(cussor.getString(5))
        cls_spinner.setSelection(spinnerPosition)
        new_size.setText(cussor.getInt(7).toString())

        //日期選擇(雙擊會顯示日曆)
        ed_date.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                ed_date.setText(String.format("%04d/%02d/%02d", selectedYear, selectedMonth + 1, selectedDay))
            }, year, month, day).show()
        }

        //點擊按鈕回到主頁面並更新資料
        ad_dt_in_db.setOnClickListener {
            if(ed_item.text.isEmpty()){
                Toast.makeText(this, "請輸入品項名稱", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if(ed_date.text.isEmpty()){
                Toast.makeText(this, "請輸入購買日期", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if(ed_cost.text.isEmpty()){
                Toast.makeText(this, "請輸入購買金額", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if(cls_spinner.selectedItem.toString().isEmpty()){
                Toast.makeText(this, "請選擇分類", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if(new_size.text.isEmpty()){
                Toast.makeText(this, "請輸入份量", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{
                dbrw.execSQL("UPDATE costTable SET item = '${ed_item.text}',time = '${ed_date.text}',month = '${ed_date.text.substring(5,7)}',year = '${ed_date.text.substring(0,4)}',cost = '${ed_cost.text}',cls = '${cls_spinner.selectedItem}',size = '${new_size.text}' WHERE id = $db_id")
                val intent= Intent()
                setResult(Activity.RESULT_OK,intent)
                finish()
            }
        }
    }
}