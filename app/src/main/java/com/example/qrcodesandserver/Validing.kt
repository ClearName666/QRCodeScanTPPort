package com.example.qrcodesandserver

class Validing {
    fun isValidIpAddressPort(input: String): Boolean {
        // Регулярное выражение для IP-адреса и порта
        val regex = """^(\d{1,3}\.){3}\d{1,3}:\d{1,5}$""".toRegex()

        // Проверка формата с регулярным выражением
        if (!input.matches(regex)) {
            return false
        }

        // Разделяем строку на IP и порт
        val (ip, port) = input.split(":")

        // Проверяем корректность каждого октета IP-адреса
        val ipParts = ip.split(".")
        for (part in ipParts) {
            val number = part.toIntOrNull() ?: return false
            if (number !in 0..255) {
                return false
            }
        }

        // Проверяем корректность порта
        val portNumber = port.toIntOrNull() ?: return false
        if (portNumber !in 1..65535) {
            return false
        }

        return true
    }

}