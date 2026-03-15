package com.plcoding.cryptotracker.widget.presentation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.core.graphics.createBitmap
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import com.plcoding.cryptotracker.cryto.presentation.coin_detail.ChartStyle
import com.plcoding.cryptotracker.cryto.presentation.coin_detail.DataPoint
import com.plcoding.cryptotracker.cryto.presentation.coin_detail.ValueLabel

fun renderLineChartBitmap(
    dataPoints: List<DataPoint>,
    style: ChartStyle,
    visibleDataPointsIndices: IntRange,
    unit: String,
    selectedDataPoint: DataPoint? = null,
    showHelperLines: Boolean = true,
    widthPx: Int,
    heightPx: Int,
    density: Float = 3f
): Bitmap {
    fun Dp.toPx() = value * density
    fun TextUnit.toPx() = value * density

    val bitmap = createBitmap(widthPx, heightPx)
    val canvas = Canvas(bitmap)

    val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = style.labelFontSize.toPx()
        typeface = Typeface.MONOSPACE
    }

    val visibleDataPoints = dataPoints.slice(visibleDataPointsIndices)
    val maxYValue = visibleDataPoints.maxOfOrNull { it.y } ?: 0f
    val minYValue = visibleDataPoints.minOfOrNull { it.y } ?: 0f
    val selectedDataPointIndex = dataPoints.indexOf(selectedDataPoint)

    val verticalPaddingPx = style.verticalPadding.toPx()
    val horizontalPaddingPx = style.horizontalPadding.toPx()
    val xAxisLabelSpacingPx = style.xAxisLabelSpacing.toPx()
    val minLabelSpacingYPx = style.minYLabelSpacing.toPx()

    val bounds = Rect()
    var maxXLabelWidth = 0
    var maxXLabelHeight = 0
    var maxXLabelLines = 0

    visibleDataPoints.forEach { dp ->
        val lines = dp.xLabel.split("\n")
        if (lines.size > maxXLabelLines) maxXLabelLines = lines.size
        lines.forEach { line ->
            labelPaint.getTextBounds(line, 0, line.length, bounds)
            if (bounds.width() > maxXLabelWidth) maxXLabelWidth = bounds.width()
            if (bounds.height() > maxXLabelHeight) maxXLabelHeight = bounds.height()
        }
    }

    val xLabelLineHeight = if (maxXLabelLines > 0) maxXLabelHeight else 0
    val totalXLabelHeight = xLabelLineHeight * maxXLabelLines + (maxXLabelLines - 1) * 4
    val viewPortHeightPx = heightPx - (totalXLabelHeight + 2 * verticalPaddingPx + xLabelLineHeight + xAxisLabelSpacingPx)

    val labelViewPortHeightPx = viewPortHeightPx + xLabelLineHeight
    val labelCount = (labelViewPortHeightPx / (xLabelLineHeight + minLabelSpacingYPx)).toInt()
    val valueIncrement = if (labelCount > 0) (maxYValue - minYValue) / labelCount else 0f

    val yLabels = (0..labelCount).map { i ->
        ValueLabel(value = maxYValue - valueIncrement * i, unit = unit)
    }

    var maxYLabelWidth = 0
    yLabels.forEach { lbl ->
        val txt = lbl.formatted()
        labelPaint.getTextBounds(txt, 0, txt.length, bounds)
        if (bounds.width() > maxYLabelWidth) maxYLabelWidth = bounds.width()
    }

    val viewPortTopY = verticalPaddingPx + xLabelLineHeight + 10f
    val viewPortRightX = widthPx.toFloat()
    val viewPortBottomY = viewPortTopY + viewPortHeightPx
    val viewPortLeftX = 2f * horizontalPaddingPx + maxYLabelWidth
    val xLabelWidth = maxXLabelWidth + xAxisLabelSpacingPx

    visibleDataPoints.forEachIndexed { index, dp ->
        val centerX = viewPortLeftX + xAxisLabelSpacingPx / 2f + xLabelWidth * index + maxXLabelWidth / 2f
        val isSelected = index == selectedDataPointIndex

        labelPaint.color = if (isSelected) style.selectedColor.toArgb() else style.unselectedColor.toArgb()

        dp.xLabel.split("\n").forEachIndexed { lineIdx, line ->
            val lineY = viewPortBottomY + xAxisLabelSpacingPx + xLabelLineHeight * (lineIdx + 1)
            canvas.drawText(line, centerX - labelPaint.measureText(line) / 2f, lineY, labelPaint)
        }

        if (showHelperLines) {
            val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = if (isSelected) style.selectedColor.toArgb() else style.unselectedColor.toArgb()
                strokeWidth = if (isSelected) style.helperLinesThicknessPx * 1.8f else style.helperLinesThicknessPx
                this.style = Paint.Style.STROKE
            }
            canvas.drawLine(centerX, viewPortBottomY, centerX, viewPortTopY, linePaint)
        }

        if (isSelected) {
            val valueTxt = ValueLabel(value = dp.y, unit = unit).formatted()
            val valWidth = labelPaint.measureText(valueTxt)
            var textX = centerX - valWidth / 2f
            if (index == visibleDataPointsIndices.last) textX = centerX - valWidth
            if (textX >= 0 && textX + valWidth <= widthPx) {
                labelPaint.color = style.selectedColor.toArgb()
                canvas.drawText(valueTxt, textX, viewPortTopY - 10f, labelPaint)
            }
        }
    }

    val heightRequiredForLabels = xLabelLineHeight * (labelCount + 1)
    val remainingHeight = labelViewPortHeightPx - heightRequiredForLabels
    val spaceBetweenLabels = if (labelCount > 0) remainingHeight / labelCount else 0f

    yLabels.forEachIndexed { index, lbl ->
        val txt = lbl.formatted()
        labelPaint.getTextBounds(txt, 0, txt.length, bounds)
        val x = horizontalPaddingPx + maxYLabelWidth - bounds.width().toFloat()
        val y = viewPortTopY + index * (xLabelLineHeight + spaceBetweenLabels) + bounds.height() / 2f

        labelPaint.color = style.unselectedColor.toArgb()
        canvas.drawText(txt, x, y, labelPaint)

        if (showHelperLines) {
            val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = style.unselectedColor.toArgb()
                strokeWidth = style.helperLinesThicknessPx
                this.style = Paint.Style.STROKE
            }
            canvas.drawLine(viewPortLeftX, y - bounds.height() / 2f, viewPortRightX, y - bounds.height() / 2f, linePaint)
        }
    }

    val drawPoints = visibleDataPointsIndices.map { i ->
        val x = viewPortLeftX + (i - visibleDataPointsIndices.first) * xLabelWidth + xLabelWidth / 2f
        val ratio = if (maxYValue != minYValue) (dataPoints[i].y - minYValue) / (maxYValue - minYValue) else 0.5f
        val y = viewPortBottomY - ratio * viewPortHeightPx
        DataPoint(x = x, y = y, xLabel = dataPoints[i].xLabel)
    }

    val conPoints1 = mutableListOf<DataPoint>()
    val conPoints2 = mutableListOf<DataPoint>()
    for (i in 1 until drawPoints.size) {
        val p0 = drawPoints[i - 1]
        val p1 = drawPoints[i]
        val midX = (p0.x + p1.x) / 2f
        conPoints1.add(DataPoint(midX, p0.y, ""))
        conPoints2.add(DataPoint(midX, p1.y, ""))
    }

    if (drawPoints.isNotEmpty()) {
        val linePath = Path().apply {
            moveTo(drawPoints.first().x, drawPoints.first().y)
            for (i in 1 until drawPoints.size) {
                cubicTo(
                    conPoints1[i - 1].x, conPoints1[i - 1].y,
                    conPoints2[i - 1].x, conPoints2[i - 1].y,
                    drawPoints[i].x, drawPoints[i].y
                )
            }
        }
        val chartLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = style.chartLineColor.toArgb()
            strokeWidth = 5f
            this.style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
        canvas.drawPath(linePath, chartLinePaint)
    }

    if (selectedDataPoint != null) {
        drawPoints.forEachIndexed { index, point ->
            canvas.drawCircle(point.x, point.y, 10f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = style.selectedColor.toArgb()
                this.style = Paint.Style.FILL
            })
            if (index == selectedDataPointIndex) {
                canvas.drawCircle(point.x, point.y, 15f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.White.toArgb()
                    this.style = Paint.Style.FILL
                })
                canvas.drawCircle(point.x, point.y, 15f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = style.selectedColor.toArgb()
                    this.style = Paint.Style.STROKE
                    strokeWidth = 3f
                })
            }
        }
    }

    return bitmap
}

@Composable
fun GlanceLineChart(
    dataPoints: List<DataPoint>,
    style: ChartStyle,
    visibleDataPointsIndices: IntRange,
    unit: String,
    modifier: GlanceModifier = GlanceModifier,
    selectedDataPoint: DataPoint? = null,
    showHelperLines: Boolean = true,
    widthPx: Int = 700,
    heightPx: Int = 300,
    density: Float = 3f
) {
    val bitmap = remember(dataPoints, visibleDataPointsIndices, selectedDataPoint, widthPx, heightPx) {
        renderLineChartBitmap(
            dataPoints = dataPoints,
            style = style,
            visibleDataPointsIndices = visibleDataPointsIndices,
            unit = unit,
            selectedDataPoint = selectedDataPoint,
            showHelperLines = showHelperLines,
            widthPx = widthPx,
            heightPx = heightPx,
            density = density
        )
    }

    Image(
        provider = ImageProvider(bitmap),
        contentDescription = null,
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Fit
    )
}