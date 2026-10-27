package com.example.alertatemprana.data.repository

import com.example.alertatemprana.data.local.GalponDao
import com.example.alertatemprana.data.local.MedicionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GalponRepository(private val galponDao: GalponDao) {

    val todasLasMediciones: Flow<List<MedicionEntity>> = galponDao.getAllMediciones()

    suspend fun insertarMedicion(medicion: MedicionEntity): Long {
        return withContext(Dispatchers.IO) {
            galponDao.insertMedicion(medicion)
        }
    }

    suspend fun eliminarMedicion(medicion: MedicionEntity) {
        withContext(Dispatchers.IO) {
            galponDao.eliminarMedicion(medicion)
        }
    }
}