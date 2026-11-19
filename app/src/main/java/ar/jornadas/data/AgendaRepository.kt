package ar.jornadas.data

import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AgendaRepository(
    private val api: ProgramaApi,
    private val dao: CarteleraDao,
    private val prefs: SharedPreferences
) {
    val programa = dao.observar()

    private val _hayEdicion = MutableStateFlow(prefs.getBoolean("edicion_descargada", false))
    val hayEdicion = _hayEdicion.asStateFlow()

    suspend fun sincronizar() {
        val fichas = api.consultar().map {
            Charla(it.id, it.title, it.track, it.day, it.time, it.room, it.speaker, it.summary)
        }
        dao.reemplazarPrograma(fichas)
        prefs.edit().putBoolean("edicion_descargada", true).apply()   // también vale para una edición vacía
        _hayEdicion.value = true
    }

    suspend fun cambiarFavorita(id: String) = dao.alternar(id)
}