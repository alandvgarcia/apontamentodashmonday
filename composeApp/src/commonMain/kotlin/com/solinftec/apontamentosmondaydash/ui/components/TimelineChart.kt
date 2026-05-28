package com.solinftec.apontamentosmondaydash.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solinftec.apontamentosmondaydash.model.Apontamento

@Composable
fun TimelineChart(
    apontamentos: List<Apontamento>,
    modifier: Modifier = Modifier
) {
    val startHour = 5
    val endHour = 24 // Representing 00:00 as the end of the day
    val hourWidth = 60.dp
    val rowHeight = 40.dp
    val labelWidth = 150.dp

    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
            .horizontalScroll(rememberScrollState())
        ) {
            val totalWidth = labelWidth + (hourWidth * (endHour - startHour))
            val totalHeight = (rowHeight * (apontamentos.size + 1))

            Box(modifier = Modifier.size(width = totalWidth, height = totalHeight)) {
                // Draw grid and labels
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val labelWidthPx = labelWidth.toPx()
                    val hourWidthPx = hourWidth.toPx()
                    val rowHeightPx = rowHeight.toPx()

                    // Vertical lines (hours)
                    for (i in startHour..endHour) {
                        val x = labelWidthPx + (i - startHour) * hourWidthPx
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            start = Offset(x, 0f),
                            end = Offset(x, size.height),
                            strokeWidth = 1f
                        )
                    }

                    // Horizontal lines
                    for (i in 0..apontamentos.size) {
                        val y = i * rowHeightPx
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1f
                        )
                    }
                }

                // Labels and Bars
                apontamentos.forEachIndexed { index, apontamento ->
                    val y = rowHeight * index
                    
                    Row(modifier = Modifier.padding(top = y).height(rowHeight)) {
                        Text(
                            text = apontamento.nameTask,
                            modifier = Modifier.width(labelWidth).padding(horizontal = 8.dp),
                            fontSize = 12.sp,
                            maxLines = 1,
                            lineHeight = 14.sp
                        )
                        
                        Box(modifier = Modifier.fillMaxHeight()) {
                            val start = apontamento.startTime
                            val end = apontamento.endTime
                            
                            if (start != null && end != null) {
                                val startInMinutes = start.hour * 60 + start.minute
                                // If endTime is 00:00, treat it as 24:00 for the purpose of the chart
                                val endInMinutes = if (end.hour == 0 && end.minute == 0) {
                                    24 * 60
                                } else {
                                    end.hour * 60 + end.minute
                                }
                                
                                val durationInMinutes = endInMinutes - startInMinutes
                                
                                if (durationInMinutes > 0) {
                                    // Calculate offset relative to startHour (05:00)
                                    val startOffsetMinutes = startInMinutes - (startHour * 60)
                                    val startX = hourWidth * (startOffsetMinutes / 60f)
                                    val width = hourWidth * (durationInMinutes / 60f)
                                    
                                    val hasOverlap = apontamentos.filterIndexed { i, _ -> i != index }.any { other ->
                                        val otherStart = other.startTime
                                        val otherEnd = other.endTime
                                        if (otherStart != null && otherEnd != null) {
                                            val os = otherStart.hour * 60 + otherStart.minute
                                            val oe = if (otherEnd.hour == 0 && otherEnd.minute == 0) {
                                                24 * 60
                                            } else {
                                                otherEnd.hour * 60 + otherEnd.minute
                                            }
                                            // Overlap check
                                            os < endInMinutes && oe > startInMinutes
                                        } else false
                                    }

                                    // Only draw if it starts or ends within the visible range (approx)
                                    if (startX + width > 0.dp) {
                                        Box(
                                            modifier = Modifier
                                                .padding(start = startX)
                                                .width(width)
                                                .height(rowHeight * 0.7f)
                                                .background(if (hasOverlap) Color.Red else Color(0xFF4A90E2))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Hour labels at the bottom
                Row(modifier = Modifier.padding(top = (rowHeight * apontamentos.size)).height(rowHeight)) {
                    Spacer(modifier = Modifier.width(labelWidth))
                    for (i in startHour..endHour) {
                        val displayHour = if (i == 24) 0 else i
                        Text(
                            text = "${displayHour}h",
                            modifier = Modifier.width(hourWidth),
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}
