package com.example.testtask.ui.main.database


import com.example.testtask.model.user.UserModel
import com.example.testtask.model.user.UserModelItem
import com.example.testtask.model.user.UserRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults


class UserDatabaseOperations {
    private val config = RealmConfiguration.Builder(schema = setOf(UserRealm::class)).build()
    private val realm = Realm.open(config)

    suspend fun insertUser(
        username: String,
        imageUrl: String,
        id: String
    ) {
        realm.write {
            copyToRealm(UserRealm().apply {
                this.id = id
                this.username = username
                this.imageUrl = imageUrl
                this.changesCount = 0
            })
        }
    }

    suspend fun updateUser(
        username: String,
        imageUrl: String,
        id: String
    ) {
        realm.write {
            val user: UserRealm? = query<UserRealm>("id == $0", id).first().find()
            user?.apply {
                this.username = username
                this.imageUrl = imageUrl
            }
        }
    }

    fun updateUserChanges(
        id: String,
        changesCount: Int
    ) {
        realm.writeBlocking {
            val user: UserRealm? = query<UserRealm>("id == $0", id).first().find()
            user?.apply {
                this.changesCount = changesCount
            }
        }
    }


    fun retrieveUsers(): UserModel {
        val userModel = UserModel()
        val tasks: RealmResults<UserRealm> = realm.query<UserRealm>().find()
        val list = ArrayList<UserModelItem>()
        tasks.forEach { user ->
            list.add(
                UserModelItem(
                    login = user.username,
                    avatar_url = user.imageUrl,
                    id = user.id.toInt(),
                    changesCount = user.changesCount
                )
            )
        }
        for (i in 0 until list.size) {
            userModel.add(list[i])
        }
        return userModel
    }

}