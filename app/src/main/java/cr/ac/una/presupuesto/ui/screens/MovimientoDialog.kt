package cr.ac.una.presupuesto.ui.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import cr.ac.una.presupuesto.util.crearUriImage
import cr.ac.una.presupuesto.viewmodel.MovimientoViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientoDialog(
    viewModel: MovimientoViewModel
) {
    var context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val opcionesTipo = listOf("Ingreso", "Egreso")
    val hoy = Calendar.getInstance().timeInMillis
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = hoy,
        selectableDates = object : androidx.compose.material3.SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= hoy
            }

            override fun isSelectableYear(year: Int): Boolean {
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                return year in currentYear..(currentYear + 3)
            }
        }
    )

    var localUri by remember {
        mutableStateOf<Uri?>(null)
    }
    var cameraLaucher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if(success){
            viewModel.imagenUri = localUri
        }
    }
    var permissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestPermission()
        ) { granted ->
            if(granted){
                var uri = crearUriImage(context)
                localUri = uri
                cameraLaucher.launch(uri)
            }
        }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                        viewModel.fecha = sdf.format(Date(it))
                        viewModel.fechaError = false
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = {
            viewModel.cerrarDialog()
        },
        title = {
            Text(if (viewModel.movimientoSeleccionado == null) "Nuevo Movimiento" else "Modificar Movimiento")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = viewModel.monto,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() || char == '.' }) {
                            viewModel.monto = it
                            viewModel.montoError = false
                        }
                    },
                    label = { Text("Monto") },
                    isError = viewModel.montoError,
                    supportingText = {
                        if (viewModel.montoError) {
                            Text("Ingrese un monto válido")
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = viewModel.tipo,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        isError = viewModel.tipoError,
                        supportingText = {
                            if (viewModel.tipoError) {
                                Text("Seleccione un tipo")
                            }
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        opcionesTipo.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    viewModel.tipo = opcion
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = viewModel.fecha,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Seleccionar fecha",
                            modifier = Modifier.clickable { showDatePicker = true }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    isError = viewModel.fechaError,
                    supportingText = {
                        if (viewModel.fechaError) {
                            Text("Seleccione una fecha")
                        }
                    }
                )
                Button(
                    onClick = {
                        permissionLauncher.launch(
                            Manifest.permission.CAMERA
                        )
                    }
                ) {
                    Text("Tomar foto")
                }
                viewModel.imagenUri?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = "Foto",
                        modifier = Modifier.size(120.dp)
                    )
                }
            }
        },
        dismissButton = {
            Button(
                onClick = { viewModel.cerrarDialog() }
            ) {
                Text("Cancelar")
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.guardarMovimiento()
                }
            ) {
                Text(if (viewModel.movimientoSeleccionado == null) "Guardar" else "Actualizar")
            }
        }
    )
}
