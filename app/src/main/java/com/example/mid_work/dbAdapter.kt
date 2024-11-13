package com.example.mid_work

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.example.mid_work.*
import java.util.Calendar


class CustomAdapter(
    private val context: Context,
    private val costItems: List<CostItem>,
    private val chartUpdateListener: ChartUpdateListener,
    private val changeDataLauncher: ActivityResultLauncher<Intent>
) : BaseAdapter() {

    // ViewHolder 模式用于优化性能
    private class ViewHolder(row: View?) {
        val itemTextView: TextView? = row?.findViewById(R.id.Item_tv)
        val costTextView: TextView? = row?.findViewById(R.id.Cost_tv)
        val changeButton: Button? = row?.findViewById(R.id.change)
        val deleteButton: Button? = row?.findViewById(R.id.delete)
        val time: TextView? = row?.findViewById(R.id.time)
        val List_main: View? = row?.findViewById(R.id.List_main)
    }

    override fun getCount(): Int = costItems.size

    override fun getItem(position: Int): Any = costItems[position]

    override fun getItemId(position: Int): Long = costItems[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View?
        val viewHolder: ViewHolder



        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.main_page_list_item, parent, false)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val costItem = getItem(position) as CostItem
        viewHolder.itemTextView?.text = costItem.item
        viewHolder.costTextView?.text = costItem.cost.toString()+"$"
        viewHolder.time?.text = costItem.time.substring(5,7)+"/"+costItem.time.substring(8,10)
        viewHolder.itemTextView?.setTextColor(Color.WHITE)
        viewHolder.costTextView?.setTextColor(Color.WHITE)
        viewHolder.time?.setTextColor(Color.WHITE)
        if(costItem.cls=="豬肉"){
            //改變背景顏色
            viewHolder.List_main?.setBackgroundColor(Color.rgb(255,150,150))
        }
        else if(costItem.cls=="雞肉"){
            viewHolder.List_main?.setBackgroundColor(Color.rgb(11,255,11))
        }
        else if(costItem.cls=="牛肉"){
            viewHolder.List_main?.setBackgroundColor(Color.rgb(255,0,0))
        }
        else if(costItem.cls=="蔬菜"){
            viewHolder.List_main?.setBackgroundColor(Color.rgb(0,255,255))
        }
        else if(costItem.cls=="水果"){
            viewHolder.List_main?.setBackgroundColor(Color.rgb(0,0,255))
        }

        viewHolder.changeButton?.setOnClickListener {
            //跳轉頁面進入修改頁面
            val intent = Intent(context, activity_change_data_page::class.java)
            intent.putExtra("id", costItem.id)
            changeDataLauncher.launch(intent)
        }

        viewHolder.deleteButton?.setOnClickListener {
            val dbrw = MydbCostHelper(context).writableDatabase
            dbrw.execSQL("DELETE FROM costTable WHERE id = ${costItem.id}")
            val month = Calendar.getInstance().get(Calendar.MONTH) + 1
            val year = Calendar.getInstance().get(Calendar.YEAR)
            chartUpdateListener.updateChart(month.toString(), year.toString())
        }

        return view!!
    }
}