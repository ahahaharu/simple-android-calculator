package com.example.simplecalculator // Убедись, что пакет соответствует твоему проекту

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Включаем режим "от края до края"
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        // Находим наш главный контейнер по ID, который мы только что добавили
        val mainContainer = findViewById<View>(R.id.mainContainer)

        // Устанавливаем слушатель, который срабатывает при изменении системных отступов
        ViewCompat.setOnApplyWindowInsetsListener(mainContainer) { view, insets ->
            // Получаем размеры системных панелей (статус бар сверху, навигация снизу, вырезы под камеру сбоку)
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())

            // Добавляем эти размеры как padding к нашему контейнеру
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            // Возвращаем insets, чтобы система знала, что мы их обработали
            insets
        }
    }
}