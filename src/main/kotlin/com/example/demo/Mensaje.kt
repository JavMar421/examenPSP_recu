package com.example.demo

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Mensaje(var texto:String,val id:Int, val token:String) {
    @Id
    @GeneratedValue
    var id2 = 0

}