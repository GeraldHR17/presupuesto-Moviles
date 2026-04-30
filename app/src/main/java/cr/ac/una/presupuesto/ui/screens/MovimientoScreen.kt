package cr.ac.una.presupuesto.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cr.ac.una.presupuesto.viewmodel.MovimientoViewModel

@Composable
fun MovimientoScreen(
    viewModel: MovimientoViewModel
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.abrirDialog() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar"
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn {
                items(viewModel.listaMovimientos) { mov ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { viewModel.abrirDialog(mov) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("Monto: ${mov.monto}")
                                Text("Tipo: ${mov.tipo}")
                                Text("Fecha: ${mov.fecha}")
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = { viewModel.confirmarEliminar(mov) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar"
                                )
                            }
                        }
                    }
                }
            }

            if (viewModel.showDialog) {
                MovimientoDialog(viewModel)
            }

            if (viewModel.showDeleteConfirmDialog) {
                ConfirmDeleteDialog(viewModel)
            }

            if (viewModel.showUpdateConfirmDialog) {
                ConfirmUpdateDialog(viewModel)
            }
        }
    }
}

@Composable
fun ConfirmDeleteDialog(viewModel: MovimientoViewModel) {
    AlertDialog(
        onDismissRequest = { viewModel.showDeleteConfirmDialog = false },
        title = { Text("Confirmar eliminación") },
        text = { Text("¿Está seguro de que desea eliminar este registro?") },
        confirmButton = {
            TextButton(onClick = { viewModel.eliminarConfirmado() }) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.showDeleteConfirmDialog = false }) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ConfirmUpdateDialog(viewModel: MovimientoViewModel) {
    AlertDialog(
        onDismissRequest = { viewModel.showUpdateConfirmDialog = false },
        title = { Text("Confirmar actualización") },
        text = { Text("¿Está seguro de que desea actualizar este registro?") },
        confirmButton = {
            TextButton(onClick = { viewModel.actualizarConfirmado() }) {
                Text("Actualizar")
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.showUpdateConfirmDialog = false }) {
                Text("Cancelar")
            }
        }
    )
}