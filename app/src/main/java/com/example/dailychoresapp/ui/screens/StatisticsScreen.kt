package com.example.dailychoresapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dailychoresapp.viewmodel.TaskViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

@Composable
fun StatsScreen(taskViewModel: TaskViewModel) {
    val total = taskViewModel.totalTaskCount.observeAsState(0).value
    val completed = taskViewModel.completedCount.observeAsState(0).value
    val pending = total - completed
    val tasks = taskViewModel.allTasks.observeAsState(emptyList()).value

    val pieData = listOf("Completed" to completed.toFloat(), "Pending" to pending.toFloat())
    val barData = tasks.groupingBy { it.category }.eachCount().toList()

    val animatedTotal = animateCount(total)
    val animatedCompleted = animateCount(completed)
    val animatedPending = animateCount(pending)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AnimatedVisibility(visible = true, enter = fadeIn(tween(500)) + expandVertically()) {
            Text(
                "Task Statistics",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(6.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatRow(label = "Total Tasks", value = animatedTotal)
                StatRow(label = "Completed Tasks", value = animatedCompleted)
                StatRow(label = "Pending Tasks", value = animatedPending)
            }
        }

        AnimatedVisibility(visible = true, enter = fadeIn(tween(600))) {
            Text("Completion Status", style = MaterialTheme.typography.titleMedium)
        }

        AnimatedPieChart(
            data = pieData,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )

        PieChartLegend(data = pieData)

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(visible = true, enter = fadeIn(tween(600))) {
            Text("Tasks by Category", style = MaterialTheme.typography.titleMedium)
        }

        AnimatedBarChart(
            data = barData,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun StatRow(label: String, value: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = "$value", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun animateCount(targetValue: Int): Int {
    var displayedValue by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(targetValue) {
        scope.launch {
            val duration = 300
            val step = if (targetValue > 0) (duration / targetValue).coerceAtLeast(15) else duration
            for (i in 0..targetValue) {
                displayedValue = i
                delay(step.toLong())
            }
        }
    }

    return displayedValue
}

@Composable
fun AnimatedPieChart(
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier
) {
    val total = data.sumOf { it.second.toDouble() }.toFloat().coerceAtLeast(1f)
    val proportions = data.map { it.second / total }
    val angles = proportions.map { 360 * it }

    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.error
    )

    val animatedAngles = remember {
        angles.map { Animatable(0f) }
    }

    LaunchedEffect(Unit) {
        animatedAngles.forEachIndexed { index, animatable ->
            launch {
                animatable.animateTo(
                    targetValue = angles[index],
                    animationSpec = tween(durationMillis = 1000)
                )
            }
        }
    }

    Canvas(modifier = modifier) {
        val diameter = min(size.width, size.height)
        val topLeftX = (size.width - diameter) / 2
        val topLeftY = (size.height - diameter) / 2

        var startAngle = 0f
        for ((index, angleAnim) in animatedAngles.withIndex()) {
            val sweepAngle = angleAnim.value
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(topLeftX, topLeftY),
                size = Size(diameter, diameter)
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
fun PieChartLegend(data: List<Pair<String, Float>>) {
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.error
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        data.forEachIndexed { index, (label, value) ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(colors[index % colors.size])
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "$label (${value.toInt()})", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun AnimatedBarChart(
    data: List<Pair<String, Int>>,
    modifier: Modifier = Modifier
) {
    val max = data.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1
    val barColor = MaterialTheme.colorScheme.primary

    Column(modifier) {
        data.forEach { (label, value) ->
            val animatedProgress by animateFloatAsState(
                targetValue = value / max.toFloat(),
                animationSpec = tween(durationMillis = 1000),
                label = ""
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    modifier = Modifier.width(100.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .weight(1f)
                        .background(
                            color = barColor.copy(alpha = 0.2f),
                            shape = MaterialTheme.shapes.medium
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .background(color = barColor, shape = MaterialTheme.shapes.medium)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$value",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}