package cr.ac.una.presupuesto.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cr.ac.una.presupuesto.data.model.Movimiento
import cr.ac.una.presupuesto.data.repository.MovimientoRepository
import cr.ac.una.presupuesto.viewmodel.states.MovimientoUIState

class MovimientoViewModel : ViewModel() {
    private val repo = MovimientoRepository()
    var listaMovimientos = mutableStateListOf<Movimiento>()

    var uiState by mutableStateOf(MovimientoUIState())
        private set

    private var movimientoTemporal: Movimiento? = null

    init {
        cargarMovimientos()
    }

    private fun cargarMovimientos() {
        repo.obtenerMovimientos { lista ->
            listaMovimientos.apply {
                clear()
                addAll(lista)
            }
        }
    }

    fun onMontoChange(monto: String) {
        if (monto.all { char -> char.isDigit() || char == '.' }) {
            uiState = uiState.copy(monto = monto, montoError = false)
        }
    }

    fun onTipoChange(tipo: String) {
        uiState = uiState.copy(tipo = tipo, tipoError = false)
    }

    fun onFechaChange(fecha: String) {
        uiState = uiState.copy(fecha = fecha, fechaError = false)
    }

    fun onImagenUriChange(uri: Uri?) {
        uiState = uiState.copy(imagenUri = uri)
    }

    fun abrirDialog(movimiento: Movimiento? = null) {
        uiState = if (movimiento != null) {
            MovimientoUIState(
                showDialog = true,
                movimientoSeleccionado = movimiento,
                monto = movimiento.monto.toString(),
                tipo = movimiento.tipo,
                fecha = movimiento.fecha
            )
        } else {
            MovimientoUIState(showDialog = true)
        }
    }

    fun cerrarDialog() {
        uiState = uiState.copy(showDialog = false)
        limpiarFormulario()
    }

    fun confirmarEliminar(movimiento: Movimiento) {
        uiState = uiState.copy(
            movimientoSeleccionado = movimiento,
            showDeleteConfirmDialog = true
        )
    }

    fun cancelarEliminar() {
        uiState = uiState.copy(showDeleteConfirmDialog = false, movimientoSeleccionado = null)
    }

    fun eliminarConfirmado() {
        uiState.movimientoSeleccionado?.let {
            repo.eliminarMovimiento(it.id)
        }
        uiState = uiState.copy(showDeleteConfirmDialog = false, movimientoSeleccionado = null)
    }

    private fun validarCampos(): Boolean {
        val montoErr = uiState.monto.isBlank() || uiState.monto.toDoubleOrNull() == null
        val tipoErr = uiState.tipo.isBlank()
        val fechaErr = uiState.fecha.isBlank()

        uiState = uiState.copy(
            montoError = montoErr,
            tipoError = tipoErr,
            fechaError = fechaErr
        )

        return !montoErr && !tipoErr && !fechaErr
    }

    fun guardarMovimiento() {
        if (validarCampos()) {
            val movimiento = uiState.movimientoSeleccionado?.copy(
                monto = uiState.monto.toDouble(),
                tipo = uiState.tipo,
                fecha = uiState.fecha
            ) ?: Movimiento(
                monto = uiState.monto.toDouble(),
                tipo = uiState.tipo,
                fecha = uiState.fecha
            )

            // Usamos guardarMovimientoConImagen para ambos casos (nuevo y edición)
            // Si imagenUri es null, simplemente guarda los datos de texto.
            repo.guardarMovimientoConImagen(movimiento, uiState.imagenUri)
            cerrarDialog()
        }
    }

    fun cancelarActualizar() {
        uiState = uiState.copy(showUpdateConfirmDialog = false)
        movimientoTemporal = null
    }

    fun actualizarConfirmado() {
        movimientoTemporal?.let {
            repo.guardarMovimientoConImagen(it, uiState.imagenUri)
        }
        uiState = uiState.copy(showUpdateConfirmDialog = false)
        movimientoTemporal = null
        cerrarDialog()
    }

    private fun limpiarFormulario() {
        uiState = MovimientoUIState()
    }
}
