package com.example.pressocheck_final.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pressocheck_final.model.PressureEntity
import com.example.pressocheck_final.utils.PressureUtils
import com.example.pressocheck_final.viewmodel.PressureViewModel
import kotlin.math.max
import kotlin.math.min

/**
 * Tela que exibe gráficos de evolução da pressão arterial ao longo do tempo.
 * 
 * Mostra gráficos simples de linha para sistólica e diastólica usando Canvas do Compose.
 * 
 * @param viewModel ViewModel que gerencia o estado
 * @param onBackClick Callback para voltar à lista
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(
    viewModel: PressureViewModel,
    onBackClick: () -> Unit
) {
    val pressures by viewModel.pressures.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Gráficos de Evolução",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        if (pressures.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhuma medição para exibir no gráfico",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Gráfico Sistólica
                PressureChart(
                    title = "Pressão Sistólica (máxima)",
                    pressures = pressures,
                    getValue = { it.sistolica },
                    color = Color(0xFFE53935),
                    maxValue = 200,
                    minValue = 70
                )
                
                // Gráfico Diastólica
                PressureChart(
                    title = "Pressão Diastólica (mínima)",
                    pressures = pressures,
                    getValue = { it.diastolica },
                    color = Color(0xFF1976D2),
                    maxValue = 120,
                    minValue = 40
                )
                
                // Estatísticas
                StatisticsCard(pressures = pressures)
            }
        }
    }
}

/**
 * Componente de gráfico de linha simples para pressão arterial.
 */
@Composable
fun PressureChart(
    title: String,
    pressures: List<PressureEntity>,
    getValue: (PressureEntity) -> Int,
    color: Color,
    maxValue: Int,
    minValue: Int
) {
    if (pressures.isEmpty()) return
    
    // Ordenar por data (mais antiga primeiro)
    val sortedPressures = pressures.sortedBy { it.data }
    val values = sortedPressures.map { getValue(it) }
    
    val maxChartValue = max(maxValue, values.maxOrNull() ?: maxValue) + 20
    val minChartValue = min(minValue, values.minOrNull() ?: minValue) - 20
    val range = maxChartValue - minChartValue
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Gráfico
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val padding = 40.dp.toPx()
                    
                    if (values.size > 1) {
                        // Desenhar linha
                        val path = Path()
                        val stepX = (width - 2 * padding) / (values.size - 1)
                        
                        values.forEachIndexed { index, value ->
                            val x = padding + index * stepX
                            val normalizedValue = (value - minChartValue).toFloat() / range
                            val y = height - padding - (normalizedValue * (height - 2 * padding))
                            
                            if (index == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }
                        }
                        
                        // Desenhar linha
                        drawPath(
                            path = path,
                            color = color,
                            style = Stroke(width = 3.dp.toPx())
                        )
                        
                        // Desenhar pontos
                        values.forEachIndexed { index, value ->
                            val x = padding + index * stepX
                            val normalizedValue = (value - minChartValue).toFloat() / range
                            val y = height - padding - (normalizedValue * (height - 2 * padding))
                            
                            drawCircle(
                                color = color,
                                radius = 5.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Legenda
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Mín: $minChartValue",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Máx: $maxChartValue",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Card com estatísticas das medições.
 */
@Composable
fun StatisticsCard(pressures: List<PressureEntity>) {
    if (pressures.isEmpty()) return
    
    val avgSistolica = pressures.map { it.sistolica }.average().toInt()
    val avgDiastolica = pressures.map { it.diastolica }.average().toInt()
    val maxSistolica = pressures.maxOfOrNull { it.sistolica } ?: 0
    val minSistolica = pressures.minOfOrNull { it.sistolica } ?: 0
    val maxDiastolica = pressures.maxOfOrNull { it.diastolica } ?: 0
    val minDiastolica = pressures.minOfOrNull { it.diastolica } ?: 0
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Estatísticas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Média Sistólica",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$avgSistolica mmHg",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Column {
                    Text(
                        text = "Média Diastólica",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$avgDiastolica mmHg",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Divider()
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Sistólica",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Máx: $maxSistolica | Mín: $minSistolica",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column {
                    Text(
                        text = "Diastólica",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Máx: $maxDiastolica | Mín: $minDiastolica",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

