package com.example.stalkermapoffline;

import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private float currentScale = 1.0f;
    private final float MIN_SCALE = 0.5f;
    private final float MAX_SCALE = 4.0f;
    private Button zoomOutButton;
    private Button resetButton;
    private Button zoomInButton;
    private TextView locationText;

    private List<Zone> zones = new ArrayList<>();

    // Для скролла
    private float startX, startY;
    private int mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Создаем контейнер
        RelativeLayout layout = new RelativeLayout(this);

        // Создаем ImageView с картой
        imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.shoc_global_map);
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        imageView.setImageMatrix(matrix);

        // Обработка скролла
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, android.view.MotionEvent event) {
                switch (event.getAction() & android.view.MotionEvent.ACTION_MASK) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        savedMatrix.set(matrix);
                        startX = event.getX();
                        startY = event.getY();
                        mode = 1;
                        break;
                    case android.view.MotionEvent.ACTION_UP:
                        mode = 0;
                        updateLocationText();
                        break;
                    case android.view.MotionEvent.ACTION_MOVE:
                        if (mode == 1) {
                            matrix.set(savedMatrix);
                            float dx = event.getX() - startX;
                            float dy = event.getY() - startY;
                            matrix.postTranslate(dx, dy);
                            imageView.setImageMatrix(matrix);
                            updateLocationText();
                        }
                        break;
                }
                return true;
            }
        });

        // Создаем панель для кнопок внизу
        LinearLayout buttonPanel = new LinearLayout(this);
        buttonPanel.setOrientation(LinearLayout.HORIZONTAL);
        buttonPanel.setBackgroundColor(0x80000000);
        buttonPanel.setPadding(0, 0, 0, 0);

        RelativeLayout.LayoutParams panelParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                200
        );
        panelParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonPanel.setLayoutParams(panelParams);

        // Кнопка - (отдалить)
        zoomOutButton = new Button(this);
        zoomOutButton.setText("-");
        zoomOutButton.setTextSize(28);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
        );
        buttonParams.setMargins(5, 5, 5, 5); // Добавляем отступы между кнопками
        zoomOutButton.setLayoutParams(buttonParams);

        // Кнопка Reset (полный обзор)
        resetButton = new Button(this);
        resetButton.setText("◉");
        resetButton.setTextSize(28);
        resetButton.setLayoutParams(buttonParams);

        // Кнопка + (приблизить)
        zoomInButton = new Button(this);
        zoomInButton.setText("+");
        zoomInButton.setTextSize(28);
        zoomInButton.setLayoutParams(buttonParams);

        // Обработчики кнопок
        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomOut();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetView();
            }
        });

        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomIn();
            }
        });

        // Добавляем кнопки на панель
        buttonPanel.addView(zoomOutButton);
        buttonPanel.addView(resetButton);
        buttonPanel.addView(zoomInButton);

        //Добавляем название локация
        locationText = new TextView(this);
        locationText.setText("Зона: Глобальная карта");
        locationText.setTextSize(18);
        locationText.setTextColor(Color.WHITE);
        locationText.setPadding(20, 10, 20, 10);
        locationText.setBackgroundColor(0x80000000);

        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        textParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        textParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textParams.setMargins(0, 30, 0, 0);
        locationText.setLayoutParams(textParams);

        // Добавляем все на экран
        layout.addView(imageView);
        layout.addView(buttonPanel);
        layout.addView(locationText);

        setContentView(layout);

        zones.add(new Zone("ЧАЭС", 0.2910f, 0.0634f, 0.6445f, 0.1689f));
        zones.add(new Zone("Припять", 0.4688f, 0.2246f, 0.7178f, 0.3506f));
        zones.add(new Zone("Радар", 0.5088f, 0.3716f, 0.7666f, 0.4712f));
        zones.add(new Zone("Армейские склады", 0.3847f, 0.4599f, 0.5839f, 0.5356f));
        zones.add(new Zone("Мертвый город", 0.0410f, 0.4877f, 0.2138f, 0.5556f));
        zones.add(new Zone("Янтарь", 0.1210f, 0.5932f, 0.2265f, 0.6459f));
        zones.add(new Zone("Бар 100 рентген", 0.4091f, 0.5639f, 0.5205f, 0.6391f));
        zones.add(new Zone("Завод Росток", 0.2734f, 0.5561f, 0.4082f, 0.6005f));
        zones.add(new Zone("Агропром", 0.1582f, 0.6977f, 0.3408f, 0.7529f));
        zones.add(new Zone("Свалка", 0.3847f, 0.6518f, 0.5400f, 0.7436f));
        zones.add(new Zone("Темная долина", 0.6513f, 0.6347f, 0.8808f, 0.7768f));
        zones.add(new Zone("Кордон", 0.3574f, 0.7548f, 0.5703f, 0.9243f));
        zones.add(new Zone("Темная лощина", 0.6542f, 0.7827f, 0.9541f, 0.9101f));

        // После того как View отрисовался, применяем Reset
        imageView.post(new Runnable() {
            @Override
            public void run() {
                resetView();
            }
        });
    }

    private void zoomIn() {
        if (currentScale < MAX_SCALE) {
            currentScale *= 1.2f;
            if (currentScale > MAX_SCALE) currentScale = MAX_SCALE;

            float centerX = imageView.getWidth() / 2f;
            float centerY = imageView.getHeight() / 2f;

            matrix.postScale(1.2f, 1.2f, centerX, centerY);
            imageView.setImageMatrix(matrix);

            updateLocationText();
        }
    }

    private void zoomOut() {
        if (currentScale > MIN_SCALE) {
            currentScale /= 1.2f;
            if (currentScale < MIN_SCALE) currentScale = MIN_SCALE;

            float centerX = imageView.getWidth() / 2f;
            float centerY = imageView.getHeight() / 2f;

            matrix.postScale(0.83f, 0.83f, centerX, centerY);
            imageView.setImageMatrix(matrix);

            updateLocationText();
        }
    }

    private void resetView() {
        if (imageView.getWidth() == 0 || imageView.getHeight() == 0) return;

        // Получаем размеры картинки
        int imageWidth = imageView.getDrawable().getIntrinsicWidth();
        int imageHeight = imageView.getDrawable().getIntrinsicHeight();

        // Получаем размеры экрана
        int screenWidth = imageView.getWidth();
        int screenHeight = imageView.getHeight();

        // Вычисляем масштаб, чтобы картинка полностью помещалась
        float scaleX = (float) screenWidth / imageWidth;
        float scaleY = (float) screenHeight / imageHeight;

        // Выбираем меньший масштаб (чтобы картинка не выходила за края)
        currentScale = Math.min(scaleX, scaleY);

        // Ограничиваем максимальный масштаб при ресете
        if (currentScale > MAX_SCALE) currentScale = MAX_SCALE;
        if (currentScale < MIN_SCALE) currentScale = MIN_SCALE;

        // Сбрасываем матрицу
        matrix.reset();

        // Применяем масштаб
        matrix.postScale(currentScale, currentScale);

        // Центрируем картинку
        float dx = (screenWidth - imageWidth * currentScale) / 2f;
        float dy = (screenHeight - imageHeight * currentScale) / 2f;
        matrix.postTranslate(dx, dy);

        imageView.setImageMatrix(matrix);

        updateLocationText();
    }

    private void updateLocationText() {
        if (imageView.getWidth() == 0 || imageView.getHeight() == 0) return;

        // 1. Получаем центр экрана в пикселях
        float screenCenterX = imageView.getWidth() / 2f;
        float screenCenterY = imageView.getHeight() / 2f;

        // 2. Преобразуем экранные координаты в координаты карты (с учётом матрицы)
        Matrix inverse = new Matrix();
        matrix.invert(inverse);  // Получаем обратную матрицу

        float[] mapCoords = {screenCenterX, screenCenterY};
        inverse.mapPoints(mapCoords);  // Теперь mapCoords[0], mapCoords[1] — координаты на карте

        // 3. Получаем размеры картинки
        int imageWidth = imageView.getDrawable().getIntrinsicWidth();
        int imageHeight = imageView.getDrawable().getIntrinsicHeight();

        // 4. Нормируем координаты в диапазон 0..1
        float normX = mapCoords[0] / imageWidth;
        float normY = mapCoords[1] / imageHeight;

        // 5. Проверяем все зоны
        String currentZone = "Глобальная карта";
        for (Zone zone : zones) {
            if (zone.contains(normX, normY)) {
                currentZone = zone.name;
                break;
            }
        }

        // 6. Обновляем текст
        locationText.setText("📍 " + currentZone);
    }
}