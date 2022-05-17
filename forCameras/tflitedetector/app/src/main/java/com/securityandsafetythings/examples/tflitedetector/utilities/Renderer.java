/*
 * Copyright 2019-2020 by Security and Safety Things GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.securityandsafetythings.examples.tflitedetector.utilities;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Size;

import com.securityandsafetythings.examples.tflitedetector.detector.Recognition;
import com.securityandsafetythings.examples.tflitedetector.detector.model.RecognitionJohn;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Utility class to aid in rendering {@link Recognition} data
 */
@SuppressWarnings("MagicNumber")
public final class Renderer {
    private static final Paint NON_DETECTED_AREA_PAINT = getNonDetectedAreaPaint();
    private static final Paint BACKGROUND_PAINT = getBackgroundPaint();
    private static final HashMap<String, Paint> LABEL_PAINT_CACHE = new HashMap<>();
    private static final Paint TEXT_PAINT = getTextPaint();
    private static final Paint TEXT_BG_PAINT = getTextBackgroundPaint();
    private static final int TEXT_MARGIN = 1;
    private static final float TEXT_SIZE = 12.0f;
    private static final int COLOR_MASK = 0xffffff;
    private static final int ALPHA_MASK = 0xff000000;
    private static final int COLOR_BLACK = 0xff000000;
    private static final int COLOR_WHITE = 0xffffffff;
    private static final int COLOR_NON_DETECTION_AREA = 0x66000000;

    private final Size mInputSize;
    private final Size mMargin;
    private final Size mCropArea;

    /**
     * Constructs a Renderer object.
     * @param inputSize The size of the original image on which the bounding boxes must be drawn.
     * @param cropArea The area, the detector uses for running inference.
     * @param margin The area that was not used by the detector so that it is grayed.
     */
    public Renderer(final Size inputSize, final Size cropArea, final Size margin) {
        mInputSize = inputSize;
        mCropArea = cropArea;
        mMargin = margin;
    }

    /**
     * Gets a paint that shades areas that are not actively used by a detector
     *
     * @return Paint for use in non-detection areas
     */
    private static Paint getNonDetectedAreaPaint() {
        final Paint res = new Paint(Paint.ANTI_ALIAS_FLAG);
        res.setColor(COLOR_NON_DETECTION_AREA);
        return res;
    }

    /**
     * Gets a base paint that is clear
     *
     * @return Paint for use on the background
     */
    private static Paint getBackgroundPaint() {
        final Paint res = new Paint();
        res.setStyle(Paint.Style.FILL);
        res.setColor(0);
        return res;
    }

    /**
     * Gets a base paint that is white
     *
     * @return Paint for use in drawing text
     */
    private static Paint getTextPaint() {
        final Paint res = new Paint();
        res.setStyle(Paint.Style.FILL);
        res.setTextSize(TEXT_SIZE);
        res.setColor(COLOR_WHITE);
        return res;
    }

    /**
     * Gets a base paint that is black
     *
     * @return Black paint
     */
    private static Paint getTextBackgroundPaint() {
        final Paint res = new Paint();
        res.setStyle(Paint.Style.FILL);
        res.setColor(COLOR_BLACK);
        return res;
    }

    /**
     * Renders bounding boxes (for all the detected objects) on a canvas.
     *
     * @param canvas The canvas to use for drawing
     * @param objects The objects whose bounding boxes must be rendered.
     */
    public void render(final Canvas canvas, final List<RecognitionJohn> objects) {
        canvas.drawPaint(BACKGROUND_PAINT);
        // Shade the areas that were not used in detection
        canvas.drawRect(0, 0, mInputSize.getWidth(), mMargin.getHeight(), NON_DETECTED_AREA_PAINT);
        canvas.drawRect(0, mCropArea.getHeight() + mMargin.getHeight(),
            mInputSize.getWidth(), mInputSize.getHeight(),
            NON_DETECTED_AREA_PAINT);
        canvas.drawRect(0, mMargin.getHeight(), mMargin.getWidth(), mCropArea.getHeight() + mMargin.getHeight(), NON_DETECTED_AREA_PAINT);
        canvas.drawRect(mMargin.getWidth() + mCropArea.getWidth(),
            mMargin.getHeight(), mInputSize.getWidth(),
            mCropArea.getHeight() + mMargin.getHeight(),
            NON_DETECTED_AREA_PAINT);
        // Render each object on the canvas
        for (RecognitionJohn obj : objects) {
            final RectF box = translate(obj.getLocation());
            // Draw the translated bounding box
            canvas.drawRect(box, getPaint(obj.getLabel()));
            // Draw the label and confidence inside a black rectangle for readability
            final String label = String.format(Locale.US, "%s: %.1f%%", obj.getLabel(), obj.getConfidence() * 100);
            final float textW = TEXT_PAINT.measureText(label);
            canvas.drawRect(box.left,
                box.top + TEXT_MARGIN,
                box.left + textW + (TEXT_MARGIN << 1),
                box.top + TEXT_SIZE + (TEXT_MARGIN << 1),
                TEXT_BG_PAINT);
            canvas.drawText(label,
                box.left + TEXT_MARGIN,
                box.top + TEXT_SIZE,
                TEXT_PAINT);
        }
    }

    /**
     * Helper function maps from relative bounding box to rendering coordinates
     *
     * @param location The relative bounding box
     * @return Scaled bounding box ready to render on the Canvas with crop area (region sent to detection) defined by
     * sMarginX, sMarginY, sCropAreaH, and sCropAreaW
     */
    private RectF translate(final RectF location) {
        return new RectF((location.left * mCropArea.getWidth()) + mMargin.getWidth(),
            (location.top * mCropArea.getHeight()) + mMargin.getHeight(),
            (location.right * mCropArea.getWidth()) + mMargin.getWidth(),
            (location.bottom * mCropArea.getHeight()) + mMargin.getHeight());
    }

    /**
     * Gets the paint for a specific class of object. The first time an object is encountered the paint is built and
     * configured then stored in a cache. All subsequent requests for that object are returned from the cache directly.
     * @param label The object class
     * @return A paint unique to that class
     */
    private static Paint getPaint(final String label) {
        if (!LABEL_PAINT_CACHE.containsKey(label)) {
            final int strokeWidth = 2;
            final Paint p = new Paint(Paint.LINEAR_TEXT_FLAG);
            p.setColor((label.hashCode() & COLOR_MASK) | ALPHA_MASK);
            p.setAntiAlias(true);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeCap(Paint.Cap.ROUND);
            p.setStrokeWidth(strokeWidth);
            LABEL_PAINT_CACHE.put(label, p);
        }
        return LABEL_PAINT_CACHE.get(label);
    }
}
