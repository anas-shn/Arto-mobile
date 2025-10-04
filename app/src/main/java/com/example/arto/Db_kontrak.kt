package com.example.arto

class Db_kontrak {
    companion object {
        const val ip: String = "10.120.196.86"

        val urlRegister = "http://$ip/arto_db/api-register.php"
        val urlLogin = "http://$ip/arto_db/api-login.php"
    }
}