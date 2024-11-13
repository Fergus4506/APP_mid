package com.example.mid_work

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class User_setting_page : AppCompatActivity() {
    private var checkingID=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_setting_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setContentView(R.layout.activity_user_setting_page)

        // 抓取參數
        val newUserName=findViewById<EditText>(R.id.new_user_name)
        val changePay=findViewById<EditText>(R.id.change_pay)
        val dbrw=MydbUserHelper(this).writableDatabase
        val personList=ArrayList<PersonItem>()


        // 初始化 `checkingID` 更新回調
        val adapter = UserListAdapter(this, personList, { newCheckingID ->
            checkingID = newCheckingID // 更新 `checkingID` 值
        }, newUserName, changePay)

        // 設定 ListView 的 adapter
        findViewById<ListView>(R.id.User_List).adapter = adapter

        // 載入用戶列表
        loadUserList(personList, dbrw, adapter)
        findViewById<Button>(R.id.new_user_btn).setOnClickListener {
            if(newUserName.text.isNotEmpty()){
                dbrw.execSQL("INSERT INTO UserTable(name,needpay) VALUES(?,?)",
                    arrayOf(newUserName.text.toString(),0))
                loadUserList(personList, dbrw, adapter)
            }
        }

        // 修改用戶金額
        findViewById<Button>(R.id.change_pay_btn).setOnClickListener {
            Toast.makeText(this,"$checkingID",Toast.LENGTH_SHORT).show()
            if(changePay.text.isNotEmpty()){
                dbrw.execSQL("UPDATE UserTable SET name = '${newUserName.text}', needpay = ${changePay.text.toString().toInt()} WHERE id = $checkingID")
                loadUserList(personList, dbrw, adapter)
            }else{
                Toast.makeText(this,"請輸入金額",Toast.LENGTH_SHORT).show()
            }
            newUserName.setText("")
            changePay.setText("")
        }

        // 回到首頁
        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.change_to_user).setOnClickListener {
            finish()
        }
    }

    // 載入用戶列表的函數
    private fun loadUserList(personList: ArrayList<PersonItem>, dbrw: SQLiteDatabase, adapter: UserListAdapter) {
        personList.clear()
        val cs = dbrw.rawQuery("SELECT * FROM UserTable", null)
        cs.moveToFirst()
        for (i in 0 until cs.count) {
            personList.add(PersonItem(cs.getInt(0), cs.getString(1), cs.getInt(2)))
            cs.moveToNext()
        }
        cs.close()
        adapter.notifyDataSetChanged()
    }
}