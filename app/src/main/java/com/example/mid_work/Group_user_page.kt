package com.example.mid_work

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Group_user_page : AppCompatActivity() {

    //初始化參數
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var personAdapter: PersonAdapter
    private var foodList = mutableListOf<FoodItem>()
    private var personList = mutableListOf<Person>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_group_user_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setContentView(R.layout.activity_group_user_page)

        // 初始化清單
        loadFoodData()
        loadPersonData()

        // 點擊計算每個人的這餐的花費
        findViewById<Button>(R.id.count).setOnClickListener {
            calculateAndUpdate()
            findViewById<TextView>(R.id.much_per_person).text = "每人花費:"
        }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.change_to_user).setOnClickListener {
            finish()
        }
    }

    //從資料庫讀取食材資料並更新食材清單
    private fun loadFoodData() {
        foodList= mutableListOf<FoodItem>()
        val dbrw=MydbCostHelper(this).writableDatabase
        val c=dbrw.rawQuery("SELECT * FROM costTable WHERE size >= 0",null)
        c.moveToFirst()
        for(i in 0 until c.count){
            foodList.add(FoodItem(c.getInt(0),c.getString(1),c.getInt(8),0,c.getInt(7)))
            c.moveToNext()
        }
        foodAdapter = FoodAdapter(this, foodList, { position, quantity ->
            foodList[position].quantity = quantity
        }, personList, findViewById(R.id.much_per_person))

        findViewById<ListView>(R.id.can_user_food_list).adapter = foodAdapter
    }

    //從資料庫讀取用餐人員資料並更新用餐人士的清單
    private fun loadPersonData() {
        val dbrw=MydbUserHelper(this).writableDatabase
        val c=dbrw.rawQuery("SELECT * FROM UserTable",null)
        c.moveToFirst()
        for(i in 0 until c.count){
            personList.add(Person(c.getInt(0),c.getString(1),false,c.getInt(2)))
            c.moveToNext()
        }
        personAdapter = PersonAdapter(this, personList,foodList,findViewById(R.id.much_per_person))
        findViewById<ListView>(R.id.people_who_eat).adapter = personAdapter
    }

    //計算每個人花費並儲存的函式
    private fun calculateAndUpdate() {
        val selectedPeople = personList.filter { it.isEating }
        val totalCost = foodList.sumOf {if(it.startsize>0) Math.rint((it.cost / it.startsize).toDouble()).toInt() * it.quantity else 0 }
        val costPerPerson = if (selectedPeople.isNotEmpty()) totalCost / selectedPeople.size else 0
        val dbrw_user=MydbUserHelper(this).writableDatabase
        val dbrw_cost=MydbCostHelper(this).writableDatabase
        // Update each person's data in database
        if (selectedPeople.isNotEmpty() && totalCost > 0) {
            for (person in selectedPeople) {
                dbrw_user.execSQL("UPDATE UserTable SET needpay = ? WHERE id = ?", arrayOf(person.needpay + costPerPerson, person.id))
            }
            for (foodItem in foodList) {
                dbrw_cost.execSQL("UPDATE costTable SET using_cost = ?, size = ? WHERE id = ?", arrayOf(if(foodItem.startsize>0) foodItem.cost-Math.rint(( foodItem.cost/ foodItem.startsize).toDouble()).toInt() * foodItem.quantity else foodItem.cost,foodItem.startsize-foodItem.quantity, foodItem.id))
            }
            personList= mutableListOf<Person>()
            loadFoodData()
            loadPersonData()

        }

    }


}