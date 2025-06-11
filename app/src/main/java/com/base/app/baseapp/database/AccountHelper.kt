package com.base.app.baseapp.database

import com.base.app.baseapp.model.UserAccount
import com.google.firebase.firestore.FirebaseFirestore

object AccountHelper {
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")

    fun createAccount(user: UserAccount, onResult: (Boolean, String) -> Unit) {
        userCollection
            .whereIn("email", listOf(user.email))
            .get()
            .addOnSuccessListener { emailDocs ->
                userCollection
                    .whereIn("phoneNumber", listOf(user.phoneNumber))
                    .get()
                    .addOnSuccessListener { phoneDocs ->
                        if (emailDocs.isEmpty && phoneDocs.isEmpty) {
                            userCollection.add(user)
                                .addOnSuccessListener {
                                    onResult(true, "Tạo tài khoản thành công")
                                }
                                .addOnFailureListener {
                                    onResult(false, "Lỗi: ${it.message}")
                                }
                        } else {
                            onResult(false, "Email hoặc số điện thoại đã được sử dụng")
                        }
                    }
                    .addOnFailureListener {
                        onResult(false, "Lỗi: ${it.message}")
                    }
            }
            .addOnFailureListener {
                onResult(false, "Lỗi: ${it.message}")
            }
    }

    fun login(phoneNumber: String, password: String, onResult: (Boolean, String, UserAccount?) -> Unit) {
        userCollection
            .whereEqualTo("phoneNumber", phoneNumber)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val user = documents.first().toObject(UserAccount::class.java)
                    onResult(true, "Đăng nhập thành công", user)
                } else {
                    onResult(false, "Sai số điện thoại hoặc mật khẩu", null)
                }
            }
            .addOnFailureListener {
                onResult(false, "Lỗi: ${it.message}", null)
            }
    }

    fun updateUser(phoneNumber: String, updatedUser: UserAccount, onResult: (Boolean, String) -> Unit) {
        userCollection
            .whereEqualTo("phoneNumber", phoneNumber)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val docId = documents.first().id
                    userCollection.document(docId)
                        .set(updatedUser)
                        .addOnSuccessListener {
                            onResult(true, "Cập nhật thành công")
                        }
                        .addOnFailureListener {
                            onResult(false, "Lỗi: ${it.message}")
                        }
                } else {
                    onResult(false, "Không tìm thấy người dùng")
                }
            }
            .addOnFailureListener {
                onResult(false, "Lỗi: ${it.message}")
            }
    }
    fun isEmailUsed(email: String, callback: (Boolean) -> Unit) {
        userCollection
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                callback(!documents.isEmpty)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun isPhoneUsed(phone: String, callback: (Boolean) -> Unit) {
        userCollection
            .whereEqualTo("phoneNumber", phone)
            .get()
            .addOnSuccessListener { documents ->
                callback(!documents.isEmpty)
            }
            .addOnFailureListener {
                callback(false)
            }
    }
}
