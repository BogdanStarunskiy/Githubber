package com.example.testtask.ui.repos.database

import com.example.testtask.model.repository.RepositoryModel
import com.example.testtask.model.repository.RepositoryModelItem
import com.example.testtask.model.repository.RepositoryRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults

open class RepositoryDatabaseOperations {
    private val config = RealmConfiguration.Builder(schema = setOf(RepositoryRealm::class)).build()
    private val realm = Realm.open(config)

    suspend fun updateOrCreateRepository(
        repositoryName: String,
        programmingLanguage: String?,
        starCount: Int,
        url: String,
        username: String
    ) {
        realm.write {
            val user: RepositoryRealm? = query<RepositoryRealm>("url == $0", url).first().find()
            if (user != null) {
                user.repositoryName = repositoryName
                user.programmingLanguage = programmingLanguage
                user.starCount = starCount
                user.url = url
            } else {
                copyToRealm(RepositoryRealm().apply {
                    this.repositoryName = repositoryName
                    this.programmingLanguage = programmingLanguage
                    this.starCount = starCount
                    this.url = url
                    this.username = username
                })
            }
        }
    }

    fun retrieveRepositoriesByUsername(
        username: String
    ): RepositoryModel {
        val repositoryModel = RepositoryModel()
        val tasks: RealmResults<RepositoryRealm> = realm.query<RepositoryRealm>("username == $0", username).find()
        val list = ArrayList<RepositoryModelItem>()
        tasks.forEach { repository ->
            list.add(
                RepositoryModelItem(
                    name = repository.repositoryName,
                    language = repository.programmingLanguage,
                    stargazers_count = repository.starCount,
                    html_url = repository.url,
                    username = repository.username
                )
            )
        }
        for (i in 0 until list.size) {
            repositoryModel.add(list[i])
        }
        return repositoryModel
    }

}