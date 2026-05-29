package com.example.stalkermapoffline;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.graphics.Matrix;
import android.graphics.RectF;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private float currentScale = 1.0f;
    private final float MIN_SCALE = 0.5f;
    private final float MAX_SCALE = 3.0f;
    private Button zoomOutButton;
    private Button resetButton;
    private Button zoomInButton;

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
                        break;
                    case android.view.MotionEvent.ACTION_MOVE:
                        if (mode == 1) {
                            matrix.set(savedMatrix);
                            float dx = event.getX() - startX;
                            float dy = event.getY() - startY;
                            matrix.postTranslate(dx, dy);
                            imageView.setImageMatrix(matrix);
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
        buttonPanel.setPadding(0, 10, 0, 30); // Добавляем отступ снизу 30px

        RelativeLayout.LayoutParams panelParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                250  // Увеличили высоту панели
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

        // Добавляем все на экран
        layout.addView(imageView);
        layout.addView(buttonPanel);

        setContentView(layout);

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
    }
}