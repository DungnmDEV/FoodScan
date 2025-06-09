package com.base.app.baseapp.data

import android.content.Context
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

object RegisterHelper {

    private val firestore = FirebaseFirestore.getInstance()
    private const val TAG = "RegisterHelper"

    fun registerUser(
        context: Context,
        name: String,
        email: String,
        phone: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val usersRef = firestore.collection("users")

        // Bước 1: Kiểm tra email đã tồn tại chưa
        usersRef.whereEqualTo("email", email).get()
            .addOnSuccessListener { emailDocs ->
                if (!emailDocs.isEmpty) {
                    val msg = "Email đã được sử dụng."
                    Log.e(TAG, msg)
                    onFailure(msg)
                    return@addOnSuccessListener
                }

                // Bước 2: Kiểm tra số điện thoại đã tồn tại chưa
                usersRef.whereEqualTo("phone", phone).get()
                    .addOnSuccessListener { phoneDocs ->
                        if (!phoneDocs.isEmpty) {
                            val msg = "Số điện thoại đã được sử dụng."
                            Log.e(TAG, msg)
                            onFailure(msg)
                            return@addOnSuccessListener
                        }

                        // Bước 3: Tạo userId
                        val userId = usersRef.document().id
                        val encodedPassword = Base64.encodeToString(password.toByteArray(), Base64.NO_WRAP)

                        val userMap = hashMapOf(
                            "userId" to userId,
                            "name" to name,
                            "email" to email,
                            "phone" to phone,
                            "password" to encodedPassword,
                            "createdAt" to System.currentTimeMillis()
                        )

                        // Bước 4: Lưu vào Firestore
                        usersRef.document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                Log.e(TAG, "Đăng ký thành công với userId: $userId")
                                Toast.makeText(context, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                val msg = "Lỗi lưu dữ liệu: ${e.message}"
                                Log.e(TAG, msg, e)
                                onFailure(msg)
                            }

                    }
                    .addOnFailureListener { e ->
                        val msg = "Lỗi kiểm tra số điện thoại: ${e.message}"
                        Log.e(TAG, msg, e)
                        onFailure(msg)
                    }

            }
            .addOnFailureListener { e ->
                val msg = "Lỗi kiểm tra email: ${e.message}"
                Log.e(TAG, msg, e)
                onFailure(msg)
            }
    }
}
