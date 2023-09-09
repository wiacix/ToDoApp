package com.example.todo


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_create_card.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class CreateCard : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private val calendar = Calendar.getInstance()
    private val formatter_date = SimpleDateFormat("dd MMMM yyyy", Locale.ROOT)
    private val formatter_time = SimpleDateFormat("HH:mm", Locale.ROOT)

    private lateinit var database: myDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_card)

        create_time.setOnClickListener{
            TimePickerDialog(
                this,
                this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        create_data.setOnClickListener{
            DatePickerDialog(
                this,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val priorit = arrayOf("High", "Medium", "Low")
        val aa = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, priorit)
        create_priority.adapter = aa

        database = Room.databaseBuilder(
            applicationContext, myDatabase::class.java, "To_Do"
        ).build()
        save_button.setOnClickListener {
            if (create_title.text.toString().trim { it <= ' ' }.isNotEmpty()
                && create_priority.adapter.toString().trim { it <= ' ' }.isNotEmpty()
            ) {
                var title = create_title.getText().toString()
                var priority = create_priority.selectedItem.toString()
                var time = create_time.getText().toString()
                var dt = create_data.getText().toString()
                DataObject.setData(title, priority, time, dt)
                GlobalScope.launch {
                    database.dao().insertTask(Entity(0, title, priority, time, dt))

                }

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
        displayFormattedDate(calendar.timeInMillis)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
        }
        displayFormattedTime(calendar.timeInMillis)
    }
    private fun displayFormattedDate(timestamp: Long){
        create_data.text = formatter_date.format(timestamp)
    }
    private fun displayFormattedTime(timestamp: Long){
        create_time.text = formatter_time.format(timestamp)
    }
}

