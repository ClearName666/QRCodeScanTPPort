package com.example.qrcodesandserver

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qrcodesandserver.databinding.ActivityMainBinding
import com.google.zxing.integration.android.IntentIntegrator
import android.content.Intent
import android.util.Log
import android.view.View
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val DEFAULT_HOST_PORT: String = "192.168.1.14:6067"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.inputHostPort.setText(DEFAULT_HOST_PORT)

        // Автоматически запускаем сканер при открытии активности

    }

    private fun startQRScanner() {
        try {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("Сканируйте QR-код")
            integrator.setCameraId(0)
            integrator.setBeepEnabled(true)
            integrator.setBarcodeImageEnabled(false)
            integrator.setTorchEnabled(false)
            integrator.setOrientationLocked(true)
            // integrator.captureActivity = CaptureActivityPortrait::class.java
            integrator.initiateScan()
        } catch (e: Exception) {
            Log.e("MainActivity", "Ошибка при запуске сканера: ${e.message}", e)
        }
    }

    fun onClickStartScan(view: View) {
        val validing = Validing()
        if (validing.isValidIpAddressPort(binding.inputHostPort.text.toString())) {
            startQRScanner()
        } else {
            showDialogText("Ошибка", "Вы ввели неправильно host:port")
        }
    }

    // Обрабатываем результат сканирования
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Сканирование отменено", Toast.LENGTH_LONG).show()
            } else {
                showDialog(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showDialogText(title: String, content: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(content)
        builder.setPositiveButton("OK", null)
        builder.show()
    }

    private fun showDialog(content: String) {
        // отправка данных по порту
        val validing = Validing()
        if (validing.isValidIpAddressPort(binding.inputHostPort.text.toString())) {
            try {
                sendUDPRequest(
                    binding.inputHostPort.text.toString().substringBefore(":"),
                    binding.inputHostPort.text.toString().substringAfter(":").toInt(),
                    content,
                    true
                )
            } catch (_: Exception) {}
        }
    }

    private fun sendUDPRequest(host: String, port: Int, message: String, responseFlag: Boolean = false) {
        Thread {
            val socket = DatagramSocket()
            try {
                val address = InetAddress.getByName(host)
                val buf = message.toByteArray()
                val packet = DatagramPacket(buf, buf.size, address, port)

                // Отправляем пакет
                socket.send(packet)
                Log.d("UDP", "Пакет отправлен на $host:$port")

                // если нужно получить ответ
                if (responseFlag) {
                    // Устанавливаем тайм-аут на получение ответа (например, 5 секунд)
                    socket.soTimeout = 1000

                    try {
                        // Получение ответа
                        val responseBuf = ByteArray(1024)
                        val responsePacket = DatagramPacket(responseBuf, responseBuf.size)
                        socket.receive(responsePacket)

                        val response = String(responsePacket.data, 0, responsePacket.length)
                        runOnUiThread { showDialogText("Успешно", "$message отправлен в порт") }


                    } catch (e: SocketTimeoutException) {
                        Log.d("UDP", "Ответ не получен: время ожидания истекло")
                        runOnUiThread { showDialogText("Ошибка", "Время ожидания ответа истекло") }
                    } catch (e: Exception) {
                        Log.e("UDP", "Ошибка при получении ответа: ${e.message}")
                        runOnUiThread { showDialogText("Ошибка", "Произошла ошибка при получении ответа") }
                    }
                } else {
                    runOnUiThread { showDialogText("Успешно", "$message отправлен в порт") }
                }
            } catch (e: Exception) {
                Log.e("UDP", "Ошибка при отправке пакета: ${e.message}")
                runOnUiThread { showDialogText("Ошибка", "Похоже что порт занят, или введен несуществующий ip адрес!") }
            } finally {
                socket.close()  // Закрываем сокет в блоке finally, чтобы гарантированно закрыть его
                Log.d("UDP", "Сокет закрыт")
            }
        }.start()
    }

}
