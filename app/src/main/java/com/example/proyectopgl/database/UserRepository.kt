package com.example.proyectopgl.database

import com.example.proyectopgl.database.model.User
import com.example.proyectopgl.database.dao.UserDao

class UserRepository(private val userDao: UserDao) {

    suspend fun insertAndRetrieveUser(user: User): User? {
        userDao.insert(user)
        return userDao.getUserByUsername(user.username)
    }
    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }
    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }
    suspend fun userNameExists(username: String): Boolean {
        return userDao.getUserByUsername(username) != null
    }
    suspend fun emailExists(email: String): Boolean {
        return userDao.getUserByEmail(email) != null
    }
    suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    }
    suspend fun updateUserPassword(userId: Int, newPassword: String) {
        userDao.updateUserPassword(userId, newPassword)
    }
    suspend fun deleteUser(user: User) {
        userDao.delete(user)
    }
}
