package com.example.mid_work

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView

class UserListAdapter(
    private val context: Context,
    private val userList: MutableList<PersonItem>,
    private val onCheckingIDChange: (Int) -> Unit, // 添加一個回調函數
    private val newUserName: EditText,
    private val changePay: EditText
) : ArrayAdapter<PersonItem>(context, 0, userList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.user_list, parent, false)
        val person = userList[position]

        view.findViewById<TextView>(R.id.personName).text = "名稱: ${person.name}"
        view.findViewById<TextView>(R.id.pay).text = "需支付費用: ${person.pay}"

        view.findViewById<Button>(R.id.choice_user).setOnClickListener {
            onCheckingIDChange(person.id) // 通知 `User_setting_page` 更新 `checkingID`
            newUserName.setText(person.name)
            changePay.setText(person.pay.toString())
        }

        view.findViewById<Button>(R.id.delete).setOnClickListener {
            val dbrw = MydbUserHelper(context).writableDatabase
            dbrw.execSQL("DELETE FROM UserTable WHERE id = ?", arrayOf(person.id))
            userList.removeAt(position)
            notifyDataSetChanged()
        }

        return view
    }
}
