package app.prgghale.roomdb.di

import app.prgghale.roomdb.data.repository.UserRepoImpl
import app.prgghale.roomdb.data.repository.UserRepository
import app.prgghale.roomdb.ui.search.SearchUser
import org.koin.dsl.module

val repositoryModule = module {
    single<UserRepository> { UserRepoImpl(get(), get()) }

    single { SearchUser(get(), get(), get()) }
}