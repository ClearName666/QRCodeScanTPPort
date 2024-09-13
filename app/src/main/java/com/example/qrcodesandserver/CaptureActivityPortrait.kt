package com.example.qrcodesandserver

import android.os.Bundle
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import android.widget.FrameLayout
import android.view.View
import android.view.ViewGroup
import android.graphics.Color
import android.widget.TextView
import android.widget.Button
import android.widget.LinearLayout

class CaptureActivityPortrait : CaptureActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Настройка UI-элементов
        customizeScannerUI()
    }

    /**
     * Этот метод позволяет настроить интерфейс сканера, добавляя кастомные элементы или изменяя стиль.
     */
    private fun customizeScannerUI() {
        // Получаем стандартное представление сканера
        //val barcodeScannerView: DecoratedBarcodeView = findViewById(com.journeyapps.barcodescanner.R.id.zxing_barcode_scanner)

        // Убираем стандартное сообщение "Point your camera at a barcode"
        //barcodeScannerView.statusView.visibility = View.GONE

        // Создаем новый TextView, который будет отображать сообщение пользователю
        val instructionTextView = TextView(this).apply {
            text = "Наведите камеру на QR-код"
            textSize = 18f
            setTextColor(Color.WHITE)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }

        // Создаем кнопку для закрытия сканера (при необходимости)
        val closeButton = Button(this).apply {
            text = "Закрыть"
            setOnClickListener {
                finish() // Закрыть активность
            }
        }

        // Создаем вертикальный LinearLayout для размещения TextView и кнопки
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setPadding(0, 200, 0, 0) // Отступ сверху
            addView(instructionTextView) // Добавляем текстовое сообщение
            addView(closeButton) // Добавляем кнопку закрытия
        }

        // Добавляем наш кастомный layout поверх сканера
        addContentView(layout, FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }

}
