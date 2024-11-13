package com.example.mid_work

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodAdapter(
    private val context: Context,
    private val foodList: List<FoodItem>,
    private val onSeekBarChange: (Int, Int) -> Unit,
    private val allUserList: List<Person>,
    private val much_per_person: TextView
) : ArrayAdapter<FoodItem>(context, 0, foodList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.can_use_food_list_view, parent, false)
        val foodItem = foodList[position]

        view.findViewById<TextView>(R.id.foodName).text = foodItem.name
        view.findViewById<TextView>(R.id.foodCost).text = "花費: ${0} 使用份量: ${foodItem.quantity}份 總份數: ${foodItem.startsize}"

        val seekBar = view.findViewById<SeekBar>(R.id.foodQuantity)
        seekBar.max = foodItem.startsize
        seekBar.progress = 0

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                onSeekBarChange(position, progress)
                if(foodItem.quantity == foodItem.startsize){
                    view.findViewById<TextView>(R.id.foodCost).text = "花費: ${foodItem.cost} 使用份量: ${foodItem.quantity}份 總份數: ${foodItem.startsize}"
                }else{
                    if (foodItem.startsize>0){
                        view.findViewById<TextView>(R.id.foodCost).text = "花費: ${Math.rint((foodItem.cost / foodItem.startsize).toDouble()).toInt() * foodItem.quantity} 使用份量: ${foodItem.quantity}份 總份數: ${foodItem.startsize}"
                    }
                }
                val selectedPeople = allUserList.filter { it.isEating }
                val totalCost = foodList.sumOf { if (it.startsize > 0) Math.rint((it.cost / it.startsize).toDouble()).toInt()*it.quantity else 0 }
                if (selectedPeople.isNotEmpty() && totalCost > 0) {
                    val costPerPerson= totalCost / selectedPeople.size
                    much_per_person.text = "每人花費: $costPerPerson"
                }else{
                    much_per_person.text = "可憐沒人吃飯(っ °Д °;)っ"
                }
            }


            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        return view
    }
}