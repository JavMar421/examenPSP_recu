package com.example.demo

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class UsuarioAdmin(val user:String,val pass:String){
    @Id
    @GeneratedValue
    var id = 0
}
data class Usuario(val nombre:String,val pass:String)
