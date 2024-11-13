package com.example.mid_work

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView

class PersonAdapter(context: Context, private var personList: List<Person>,private val allFoodList: List<FoodItem>,private val much_per_person: TextView) :
    ArrayAdapter<Person>(context, 0, personList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.person_using, parent, false)
        val person = personList[position]

        view.findViewById<TextView>(R.id.personName).text = person.name
        val checkBox = view.findViewById<CheckBox>(R.id.personEatingCheckBox)
        checkBox.isChecked = person.isEating

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            person.isEating = isChecked
            val selectedPeople = personList.filter { it.isEating }
            val totalCost = allFoodList.sumOf { if (it.startsize > 0) Math.rint((it.cost / it.startsize).toDouble()).toInt()*it.quantity else 0 }

            if (selectedPeople.isNotEmpty() && totalCost > 0) {
                val costPerPerson= totalCost / selectedPeople.size
                much_per_person.text = "每人花費: $costPerPerson"
            }else if (selectedPeople.isEmpty()){
                much_per_person.text = "可憐沒人吃(っ °Д °;)っ"
            }else
                much_per_person.text = "沒有食材了(っ °Д °;)っ"
        }


        return view
    }
}