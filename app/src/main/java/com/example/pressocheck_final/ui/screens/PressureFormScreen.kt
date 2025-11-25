package com.example.pressocheck_final.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pressocheck_final.model.PressureEntity
import com.example.pressocheck_final.utils.PressureUtils
import com.example.pressocheck_final.viewmodel.PressureViewModel
import kotlinx.coroutines.delay

/**
 * Tela de formulário para adicionar ou editar uma medição de pressão arterial.
 * 
 * @param viewModel ViewModel que gerencia o estado
 * @param pressureId ID da medição a ser editada (null para nova medição)
 * @param onBackClick Callback para voltar à lista
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PressureFormScreen(
    viewModel: PressureViewModel,
    pressureId: Long? = null,
    onBackClick: () -> Unit
) {
    val pressures by viewModel.pressures.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    
    // Estado do formulário
    var sistolica by remember { mutableStateOf("") }
    var diastolica by remember { mutableStateOf("") }
    var observacao by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Carregar dados se estiver editando
    LaunchedEffect(pressureId) {
        pressureId?.let { id ->
            val pressure = pressures.find { it.id == id }
            pressure?.let {
                sistolica = it.sistolica.toString()
                diastolica = it.diastolica.toString()
                observacao = it.observacao
            }
        }
    }
    
    // Voltar automaticamente após salvar com sucesso
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            delay(800) // Aguardar para mostrar feedback
            onBackClick()
        }
    }
    
    val isEditing = pressureId != null
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Editar Medição" else "Nova Medição",
                        fontSize = 22.sp,
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Informações sobre limites normais
                InfoCard()
                
                // Preview visual dos valores
                if (sistolica.isNotEmpty() && diastolica.isNotEmpty()) {
                    val sistolicaInt = sistolica.toIntOrNull()
                    val diastolicaInt = diastolica.toIntOrNull()
                    if (sistolicaInt != null && diastolicaInt != null && 
                        PressureUtils.isValidPressure(sistolicaInt, diastolicaInt)) {
                        PressurePreviewCard(sistolicaInt, diastolicaInt)
                    }
                }
                
                // Seção de entrada de dados
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Text(
                            text = "Dados da Medição",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        // Campo Sistólica com visual melhorado
                        PressureInputField(
                            label = "Pressão Sistólica (máxima)",
                            value = sistolica,
                            onValueChange = {
                                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                    sistolica = it
                                    errorMessage = null
                                }
                            },
                            placeholder = "Ex: 120",
                            helperText = "Valor normal: até 120 mmHg",
                            isError = errorMessage != null && sistolica.isEmpty()
                        )
                        
                        // Campo Diastólica com visual melhorado
                        PressureInputField(
                            label = "Pressão Diastólica (mínima)",
                            value = diastolica,
                            onValueChange = {
                                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                    diastolica = it
                                    errorMessage = null
                                }
                            },
                            placeholder = "Ex: 80",
                            helperText = "Valor normal: até 80 mmHg",
                            isError = errorMessage != null && diastolica.isEmpty()
                        )
                        
                        // Campo Observação
                        OutlinedTextField(
                            value = observacao,
                            onValueChange = { observacao = it },
                            label = { 
                                Text(
                                    "Observações (opcional)",
                                    fontSize = 16.sp
                                ) 
                            },
                            placeholder = { 
                                Text(
                                    "Sintomas, medicamentos, horário, etc.",
                                    fontSize = 14.sp
                                ) 
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            maxLines = 4,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }
                
                // Mensagem de erro
                errorMessage?.let {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = it,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 15.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Botão de Confirmação Grande e Destacado
                ConfirmButton(
                    text = if (isEditing) "Confirmar Alteração" else "Confirmar Medição",
                    isLoading = isLoading,
                    enabled = sistolica.isNotEmpty() && diastolica.isNotEmpty(),
                    onClick = {
                        val sistolicaInt = sistolica.toIntOrNull()
                        val diastolicaInt = diastolica.toIntOrNull()
                        
                        when {
                            sistolicaInt == null -> {
                                errorMessage = "Por favor, informe a pressão sistólica"
                            }
                            diastolicaInt == null -> {
                                errorMessage = "Por favor, informe a pressão diastólica"
                            }
                            !PressureUtils.isValidPressure(sistolicaInt, diastolicaInt) -> {
                                errorMessage = "Valores inválidos. A sistólica deve ser maior que a diastólica."
                            }
                            else -> {
                                errorMessage = null
                                if (isEditing && pressureId != null) {
                                    val pressure = pressures.find { it.id == pressureId }
                                    pressure?.let {
                                        val updated = it.copy(
                                            sistolica = sistolicaInt,
                                            diastolica = diastolicaInt,
                                            observacao = observacao
                                        )
                                        viewModel.updatePressure(updated)
                                    }
                                } else {
                                    viewModel.insertPressure(sistolicaInt, diastolicaInt, observacao)
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

/**
 * Card informativo sobre limites de pressão com visual melhorado.
 */
@Composable
fun InfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Valores de Referência",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ReferenceItem("Normal", "até 120/80 mmHg", Color(0xFF4CAF50))
                ReferenceItem("Pré-hipertensão", "120-139/80-89 mmHg", Color(0xFFFF9800))
                ReferenceItem("Hipertensão", "≥ 140/90 mmHg", Color(0xFFF44336))
            }
        }
    }
}

@Composable
fun ReferenceItem(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color)
            )
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Text(
            text = value,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
    }
}

/**
 * Campo de entrada de pressão com visual melhorado.
 */
@Composable
fun PressureInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    helperText: String,
    isError: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { 
                Text(
                    label,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                ) 
            },
            placeholder = { 
                Text(
                    placeholder,
                    fontSize = 16.sp
                ) 
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            isError = isError,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
        Text(
            text = helperText,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

/**
 * Preview visual da pressão inserida.
 */
@Composable
fun PressurePreviewCard(sistolica: Int, diastolica: Int) {
    val category = PressureUtils.classifyPressure(sistolica, diastolica)
    val categoryColor = Color(android.graphics.Color.parseColor(PressureUtils.getCategoryColor(category)))
    val categoryMessage = PressureUtils.getCategoryMessage(category)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = categoryColor.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Preview da Medição",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PressureValuePreview("Sistólica", sistolica, categoryColor)
                Text(
                    text = "/",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                PressureValuePreview("Diastólica", diastolica, categoryColor)
            }
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                color = categoryColor.copy(alpha = 0.2f)
            ) {
                Text(
                    text = categoryMessage,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = categoryColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun PressureValuePreview(label: String, value: Int, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$value",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = "mmHg",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Botão de confirmação grande e destacado.
 */
@Composable
fun ConfirmButton(
    text: String,
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp,
            disabledElevation = 0.dp
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 3.dp
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = text,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

