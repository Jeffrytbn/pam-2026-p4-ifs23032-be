package org.delcom.module

import org.delcom.repositories.IPlantRepository
import org.delcom.repositories.PlantRepository
import org.delcom.repositories.IRumahAdatRepository
import org.delcom.repositories.RumahAdatRepository
import org.delcom.services.PlantService
import org.delcom.services.ProfileService
import org.delcom.services.RumahAdatService
import org.koin.dsl.module

val appModule = module {

    // Plant
    single<IPlantRepository> { PlantRepository() }
    single { PlantService(get()) }

    // Rumah Adat
    single<IRumahAdatRepository> { RumahAdatRepository() }
    single { RumahAdatService(get()) }

    // Profile
    single { ProfileService() }
}