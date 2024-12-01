package com.example.proyectopgl.ui

import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.proyectopgl.App
import com.example.proyectopgl.database.utils.PasswordUtils
import com.example.proyectopgl.R
import com.example.proyectopgl.validator.Validator
import com.example.proyectopgl.database.UserRepository
import com.example.proyectopgl.database.model.User
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPwd = findViewById<EditText>(R.id.etPwd)
        val etPwd_conf = findViewById<EditText>(R.id.etPwd_conf)
        val cbTerms = findViewById<CheckBox>(R.id.cbTerms)

        signUpButton.setOnClickListener {


            val database = (application as App).database
            val userDao = database.userDao()
            val userRepository = UserRepository(userDao)

            val validator = Validator(userRepository)

            if(validateFields(etUsername, etEmail, etPwd, etPwd_conf, cbTerms)){
                return@setOnClickListener
            }

            lifecycleScope.launch {
                if (!validator.validateExistingAccount(etUsername, etEmail)) {
                    return@launch
                }
                val user = User(
                    username = etUsername.text.toString(),
                    email = etEmail.text.toString(),
                    password = PasswordUtils.hashPassword(etPwd.text.toString())
                )
                val insertedUser = userRepository.insertAndRetrieveUser(user)
                if (insertedUser != null) {
                    finish()
                }else{
                    Toast.makeText(this@SignUpActivity, "Error al crear el usuario", Toast.LENGTH_SHORT).show()
                }
            }
        }




    }

    fun validateFields(etUsername: EditText, etEmail: EditText, etPwd: EditText, etPwdConf: EditText, cbTerms: CheckBox): Boolean {
        var error = false

        if (etUsername.text.toString().isEmpty()) {
            etUsername.error = "El nombre de usuario no puede estar vacío"
            error = true
        } else if (!usernameIsValid(etUsername.text.toString())) {
            etUsername.error = "El nombre de usuario solo puede contener letras y números"
            error = true
        }

        if (etEmail.text.toString().isEmpty()) {
            etEmail.error = "El email no puede estar vacío"
            error = true
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) {
            etEmail.error = "El email no es válido"
            error = true
        }

        if (etPwd.text.toString().isEmpty()) {
            etPwd.error = "La contraseña no puede estar vacía"
            error = true
        } else if (!passwordIsSafe(etPwd.text.toString())) {
            etPwd.error = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial"
            error = true
        }

        if (etPwdConf.text.toString().isEmpty()) {
            etPwdConf.error = "La contraseña no puede estar vacía"
            error = true
        } else if (etPwd.text.toString() != etPwdConf.text.toString()) {
            etPwdConf.error = "Las contraseñas no coinciden"
            error = true
        }

        if (!cbTerms.isChecked) {
            cbTerms.error = "Debes aceptar los términos y condiciones"
            error = true
        }

        return error
    }
    private fun passwordIsSafe(password: String): Boolean {
        val hasUpperCase = password.matches(".*[A-Z].*".toRegex()) // verifica mayusculas
        val hasLowerCase = password.matches(".*[a-z].*".toRegex()) // verifica minusculas
        val hasDigit = password.matches(".*[0-9].*".toRegex()) // verifica numeros
        val hasSpecialChar = password.matches(".*[^A-Za-z0-9].*".toRegex()) // verifica caracteres especiales

        // adicionalmente, se verifica que sea de 8 caracteres o más
        return password.length >= 8 && hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar
    }
    private fun usernameIsValid(username: String): Boolean {
        return !username.matches(".*[^A-Za-z0-9].*".toRegex())
    }
}