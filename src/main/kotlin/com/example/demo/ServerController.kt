package com.example.demo

import org.springframework.web.bind.annotation.*
import java.security.MessageDigest
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.spec.SecretKeySpec

@RestController
class ServerController(
    private val adminrepositorio: UserAdminRepository,
    private val userrepositorio: UserRepository,
    private val mensajeRepository: MensajeRepository
) {
    //token
    fun getToken(): String {
        val tamano = 20
        var cadena = ""
        val numeros = (0..9) + ('a'..'z') + ('A'..'Z')
        for (i in 0 until tamano) {
            cadena += numeros.random()
        }
        return cadena
    }

    @PostMapping("crearUsuario")
    fun crearUsuario(@RequestBody usuario: Usuario): Any? {
        var retorno: Any? = null
        var userExist=false
        if (userrepositorio.findAll().size < 1) {
            val usuario = User(usuario.nombre, usuario.pass, getToken())
            userrepositorio.save(usuario)
            retorno = UserRespuesta(usuario.id,usuario.token)
        }
        else {
            userrepositorio.findAll().forEach {
                if (it.nombre == usuario.nombre) {
                    if (it.pass == usuario.pass) {
                        retorno = it.token
                    } else {

                        retorno = ErrorCode(1, "Pass invalida")
                    }
                    userExist = true
                }

            }
            if (!userExist) {
                val usuario = User(usuario.nombre, usuario.pass, getToken())
                userrepositorio.save(usuario)
                retorno = usuario.token
            }
        }
        return retorno
    }



    @PostMapping("crearMensaje")

    fun crearMensaje(@RequestBody mensaje: Mensaje): Any? {
        var retorno: Any? = null

        userrepositorio.findAll().forEach {

            if (it.id == mensaje.id) {
                if (it.token == mensaje.token){
                mensajeRepository.save(Mensaje(mensaje.texto,mensaje.id,mensaje.token,))
                retorno = "Success"
                }
                else{retorno = ErrorCode(3, "Token inválido")}
            }
            else{retorno = ErrorCode(2, "Destinatario inexistente")}
        }
        return retorno
    }


    @GetMapping("descargarMensajes")
    fun descargarMensajes(): Retorno {
        //mete en una lista todos los mensajes
        val lista = mensajeRepository.findAll()
        val retorno = Retorno(lista)
        return retorno
    }


    @GetMapping("descargarMensajesPorToken")
    fun descargarMensajesPorToken(@RequestBody token: String): Retorno {
        val lista = mutableListOf<Mensaje>()
        mensajeRepository.findAll().forEach {
            if (it.token==token)
                lista.add(it)
        }
        var retorno = Retorno(lista)
        return retorno
    }



    @DeleteMapping("borrarMensajesPorToken")
    fun borrarMensajesPorToken(@RequestBody token: String): Any?  {
        var retorno: Any? = null
        var numMensajes=0
        val lista = mutableListOf<Mensaje>()
        userrepositorio.findAll().forEach {
            if (it.token == token) {
                mensajeRepository.findAll().forEach {
                    if (it.token.contains(token)){
                        mensajeRepository.delete(it)
                        numMensajes++}
                    retorno = "Se han borrado $numMensajes mensajes"
                }
            }
            else retorno = ErrorCode(3, "Token inexistente")
        }

        return retorno
    }

    @DeleteMapping("borrarCuenta")
    fun borrarCuenta(@RequestBody usuario: User): Any?  {
        var retorno: Any? = null
        if (adminrepositorio.findAll()[0].user == usuario.nombre && adminrepositorio.findAll()[0].pass == usuario.pass) {
        userrepositorio.findAll().forEach {
            if (it.token == usuario.token) {
                mensajeRepository.findAll().forEach {
                    if (it.token.contains(usuario.token)){
                        mensajeRepository.delete(it) }
                }
                userrepositorio.delete(it)
            }
        }
            retorno="Succes"
        }
        else retorno=ErrorCode(4, "Pass de administrador falsa")


        return retorno
    }























}