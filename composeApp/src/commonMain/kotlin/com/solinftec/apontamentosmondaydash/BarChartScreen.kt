//package com.solinftec.apontamentosmondaydash
//
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.derivedStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import io.github.koalaplot.core.ChartLayout
//import io.github.koalaplot.core.bar.VerticalBarPlot
//import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
//import io.github.koalaplot.core.xygraph.CategoryAxisModel
//import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
//import io.github.koalaplot.core.xygraph.XYGraph
//import kotlin.time.Duration
//
//@OptIn(ExperimentalKoalaPlotApi::class, kotlin.time.ExperimentalTime::class)
//@Composable
//fun SimpleBarChart(apontamentos: List<Apontamento>) {
//
//    val data = remember(apontamentos) {
//        derivedStateOf {
//            apontamentos.groupBy { it.nameTask }
//                .mapValues { (_, apontamentos) ->
//                    var totalDuration = Duration.ZERO
//                    apontamentos.forEach {
//                        totalDuration += it.duration ?: Duration.ZERO
//                    }
//                    totalDuration.inWholeMinutes.toFloat() / 60
//                }
//        }
//    }
//
//
//
//    if (data.value.isNotEmpty()) {
//        ChartLayout(
//            modifier = Modifier.height(300.dp).fillMaxWidth()
//        ) {
//            XYGraph(
//                xAxisModel = CategoryAxisModel(data.value.map { it.key }),
//                yAxisModel = FloatLinearAxisModel(
//                    0f..(data.value.maxOfOrNull { it.value }?.plus(1f) ?: 1f)
//                ),
//            ) {
//                VerticalBarPlot(
//                    data,
//                    bar = { index ->
//                        DefaultVerticalBar(
//                            brush = SolidColor(colors[0]),
//                            modifier = Modifier.fillMaxWidth(),
//                        ) {
//                            if (!thumbnail) {
//                                HoverSurface { Text(barChartEntries[index].y.yMax.toString()) }
//                            }
//                        }
//                    },
//                    barWidth = BarWidth
//                )
//            }
//            }
//        }
//    }
//}
