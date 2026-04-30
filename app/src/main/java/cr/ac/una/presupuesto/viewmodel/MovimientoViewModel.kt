package cr.ac.una.presupuesto.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cr.ac.una.presupuesto.data.model.Movimiento
import cr.ac.una.presupuesto.data.repository.MovimientoRepository

class MovimientoViewModel: ViewModel() {
    private val repo = MovimientoRepository()
    var listaMovimientos = mutableStateListOf<Movimiento>()

    var showDialog by mutableStateOf(false)
    var showDeleteConfirmDialog by mutableStateOf(false)
    var showUpdateConfirmDialog by mutableStateOf(false)
    var movimientoSeleccionado: Movimiento? by mutableStateOf(null)
    private var movimientoTemporal: Movimiento? = null

    var monto by mutableStateOf("")
    var tipo by mutableStateOf("")
    var fecha by mutableStateOf("")

    var montoError by mutableStateOf(false)
    var tipoError by mutableStateOf(false)
    var fechaError by mutableStateOf(false)

    var imagenUri by mutableStateOf<Uri?>(null)

    fun abrirDialog(movimiento: Movimiento? = null) {
        limpiarFormulario()
        movimientoSeleccionado = movimiento
        if (movimiento != null) {
            monto = movimiento.monto.toString()
            tipo = movimiento.tipo
            fecha = movimiento.fecha
        }
        showDialog = true
    }

    fun cerrarDialog() {
        showDialog = false
        limpiarFormulario()
    }

    private fun cargarMovimientos() {
        repo.obtenerMovimientos { lista ->
            listaMovimientos.apply {
                clear()
                addAll(lista)
            }
        }
    }

    init {
        cargarMovimientos()
    }

    fun confirmarEliminar(movimiento: Movimiento) {
        movimientoSeleccionado = movimiento
        showDeleteConfirmDialog = true
    }

    fun eliminarConfirmado() {
        movimientoSeleccionado?.let {
            repo.eliminarMovimiento(it.id)
        }
        showDeleteConfirmDialog = false
        movimientoSeleccionado = null
    }

    private fun validarCampos(): Boolean {
        montoError = monto.isBlank() || monto.toDoubleOrNull() == null
        tipoError = tipo.isBlank()
        fechaError = fecha.isBlank()

        return !montoError && !tipoError && !fechaError
    }

    fun guardarMovimiento() {
        if (validarCampos()) {
            val movimiento = movimientoSeleccionado?.copy(
                monto = monto.toDouble(),
                tipo = tipo,
                fecha = fecha
            ) ?: Movimiento(
                monto = monto.toDouble(),
                tipo = tipo,
                fecha = fecha
            )

            if (movimientoSeleccionado == null) {
                repo.guardarMovimientoConImagen(movimiento, imagenUri)
                cerrarDialog()
            } else {
                movimientoTemporal = movimiento
                showUpdateConfirmDialog = true
            }
        }
    }

    fun actualizarConfirmado() {
        movimientoTemporal?.let {
            repo.actualizarMovimiento(it)
        }
        showUpdateConfirmDialog = false
        movimientoTemporal = null
        cerrarDialog()
    }

    fun limpiarFormulario() {
        monto = ""
        tipo = ""
        fecha = ""
        montoError = false
        tipoError = false
        fechaError = false
        movimientoSeleccionado = null
    }
}