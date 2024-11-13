package com.example.mid_work

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.util.Calendar

class MainActivity : AppCompatActivity(),ChartUpdateListener {
    private lateinit var dbrw:SQLiteDatabase
    private lateinit var changeDataLauncher: ActivityResultLauncher<Intent>

    //更新圓餅圖
    fun creatpieChart(month:String,year:String,pieChart: PieChart,listView: ListView){
        val cussor = dbrw.rawQuery("SELECT * FROM costTable WHERE month = '$month' AND year = '$year'", null)
        val list_db= mutableListOf<CostItem>()
        //建立二微陣列來計算不同類別的資料
        val map = mutableMapOf<String, Int>()
        cussor.moveToFirst()
        if(cussor.count==0){
            // 创建数据条目
            val entries = listOf(
                PieEntry(100f),
            )
            // 创建数据集
            val dataSet = PieDataSet(entries, "Colors")
            dataSet.colors = listOf(
                Color.rgb(192,192,192)
            )

            dataSet.valueTextSize=0f
            // 创建数据对象
            val data = PieData(dataSet)
            pieChart.data = data

            // 配置饼图的一些属性
            pieChart.setUsePercentValues(true)
            pieChart.description.isEnabled = false
            pieChart.setEntryLabelColor(Color.WHITE)
            //在圓餅圖中間加上文字
            pieChart.centerText="本月還沒開始記帳喔"
            pieChart.setCenterTextSize(25f)
            //關閉左下角提示
            pieChart.legend.isEnabled = false
            // 刷新图表
            pieChart.invalidate()
            val adapter = CustomAdapter(this, list_db, this,changeDataLauncher)
            listView.adapter=adapter
        }else{
            for (i in 0 until cussor.count) {
                val nowDT=CostItem(cussor.getInt(0),cussor.getString(1),cussor.getString(2),cussor.getString(3),cussor.getString(4),cussor.getString(5),cussor.getInt(6),cussor.getInt(7))
                if (map.containsKey(nowDT.cls)) {
                    map[nowDT.cls] = map[nowDT.cls]!! + nowDT.cost
                } else {
                    map[nowDT.cls] = nowDT.cost
                }
                list_db.add(nowDT)
                cussor.moveToNext()
            }
            val entries = mutableListOf<PieEntry>()
            for ((key, value) in map) {
                entries.add(PieEntry(value.toFloat(), key))
            }

            val dataSet = PieDataSet(entries, "Colors")
            val colors = mutableListOf<Int>()

            for (i in 0 until entries.size) {
                if(entries.get(i).label=="豬肉"){
                    colors.add(Color.rgb(255,150,150))
                }else if(entries.get(i).label=="雞肉"){
                    colors.add(Color.rgb(11,255,11))
                }else if(entries.get(i).label=="牛肉"){
                    colors.add(Color.rgb(255,0,0))
                }else if(entries.get(i).label=="蔬菜"){
                    colors.add(Color.rgb(0,255,255))
                }else if(entries.get(i).label=="水果"){
                    colors.add(Color.rgb(0,0,255))
                }
            }
            dataSet.colors = colors
            dataSet.valueTextSize = 0f
            val data = PieData(dataSet)
            pieChart.data = data

            pieChart.setEntryLabelTextSize(20f)
            // 配置饼图的一些属性
            pieChart.setUsePercentValues(true)
            pieChart.description.isEnabled = false
            pieChart.setEntryLabelColor(Color.WHITE)
            //在圓餅圖中間加上文字
            pieChart.centerText=month.toString()+"月"
            pieChart.setCenterTextSize(25f)
            //關閉左下角提示
            pieChart.legend.isEnabled = false
            // 刷新图表
            pieChart.invalidate()
            //將list_db資料按照日期排序
            list_db.sortBy { it.time }
            val adapter = CustomAdapter(this, list_db, this,changeDataLauncher)
            listView.adapter=adapter
        }
    }

    //回掉參數用的更新圓餅圖的函數
    override fun updateChart(month: String, year: String) {
        val pieChart: PieChart = findViewById(R.id.pieChart)
        val listView: ListView = findViewById(R.id.mainListView)
        creatpieChart(month, year, pieChart, listView)
        Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show()
    }

    //轉至添加買菜頁面的intent回傳值設定
    private val recall_for_new_page=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result->
            if(result.resultCode== RESULT_OK){
                val intent=result.data
                val item=intent?.getStringExtra("item")
                val time=intent?.getStringExtra("time")
                val month=intent?.getStringExtra("month")
                val year=intent?.getStringExtra("year")
                val cls=intent?.getStringExtra("cls")
                val cost=intent?.getIntExtra("cost",0)
                val size=intent?.getIntExtra("size",0)
                try{
                    dbrw.execSQL("INSERT INTO costTable(item,time,month,year,cls,cost,size,using_cost) VALUES(?,?,?,?,?,?,?,?)",
                        arrayOf(item,time,month,year,cls,cost,size,cost))
                    Toast.makeText(this, month.toString()+" "+year.toString(), Toast.LENGTH_SHORT).show()

                    val month_spinner=findViewById<Spinner>(R.id.month_spinner)
                    val cussor = dbrw.rawQuery("SELECT DISTINCT month,year FROM costTable", null)
                    cussor.moveToFirst()
                    val list_month= mutableListOf<String>()
                    for (i in 0 until cussor.count) {
                        val now_dt=cussor.getString(0)+"/"+cussor.getString(1)
                        if(now_dt !in list_month){
                            list_month.add(cussor.getString(0)+"/"+cussor.getString(1))
                        }
                        cussor.moveToNext()
                    }

                    month_spinner.adapter=ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,list_month)
                    creatpieChart(month.toString(),year.toString(),findViewById(R.id.pieChart),findViewById(R.id.mainListView))
                }catch (e:Exception){
                    Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show()
                }

            }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 初始化 ActivityResultLauncher
        changeDataLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // 在修改頁面返回後執行更新
                val month = Calendar.getInstance().get(Calendar.MONTH) + 1
                val year = Calendar.getInstance().get(Calendar.YEAR)
                updateChart(month.toString(), year.toString())
                Toast.makeText(this, "資料更新成功", Toast.LENGTH_SHORT).show()
            }
        }


        //初始化圓餅圖資訊
        dbrw = MydbCostHelper(this).writableDatabase
        //抓取今天月份並且與抓取該月份的資料做比對
        val month = Calendar.getInstance().get(Calendar.MONTH) + 1
        val year = Calendar.getInstance().get(Calendar.YEAR)
        Toast.makeText(this, month.toString()+" "+year.toString(), Toast.LENGTH_SHORT).show()
        val pieChart: PieChart = findViewById(R.id.pieChart)
        creatpieChart(month.toString(),year.toString(),pieChart,findViewById(R.id.mainListView))

        //抓取所有月份做spinner
        val month_spinner=findViewById<Spinner>(R.id.month_spinner)
        val cussor = dbrw.rawQuery("SELECT DISTINCT month,year FROM costTable", null)
        cussor.moveToFirst()
        val list_month= mutableListOf<String>()
        for (i in 0 until cussor.count) {
            val now_dt=cussor.getString(0)+"/"+cussor.getString(1)
            if(now_dt !in list_month){
                list_month.add(cussor.getString(0)+"/"+cussor.getString(1))
            }
            cussor.moveToNext()
        }
        month_spinner.adapter=ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,list_month)

        val add_data_btn=findViewById<Button>(R.id.new_add_data_btn)
        add_data_btn.setOnClickListener {
            val intent=android.content.Intent(this,new_data_page::class.java)
            recall_for_new_page.launch(intent)
        }
        month_spinner.onItemSelectedListener= object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedItem = p0?.getItemAtPosition(p2).toString()
                val selectedMonth = selectedItem.substring(0,2)
                val selectedYear = selectedItem.substring(3,7)
                creatpieChart(selectedMonth,selectedYear,pieChart,findViewById(R.id.mainListView))
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Toast.makeText(this@MainActivity, "沒有選擇", Toast.LENGTH_SHORT).show()
            }

        }
        val group_btn=findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.change_to_group)
        group_btn.setOnClickListener {
            val intent=android.content.Intent(this,Group_user_page::class.java)
            startActivity(intent)
        }
        val user_btn=findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.change_to_user)
        user_btn.setOnClickListener {
            val intent=android.content.Intent(this,User_setting_page::class.java)
            startActivity(intent)
        }



    }
    override fun onDestroy() {
        super.onDestroy()
        dbrw.close()
    }

}