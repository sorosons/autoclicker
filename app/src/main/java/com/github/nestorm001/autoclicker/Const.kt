package com.github.nestorm001.autoclicker

class Const {
}

class Server(val serverno: String) {
    companion object Factory {
        val servers = mutableListOf<Server>()
        fun makeCar(horsepowers: String): Server {
            val ser = Server(horsepowers)
            servers.add(ser)
            return ser
        }
    }
}

class Test {
    companion object {
        var serverno: String = "1"
        fun hello() = println("hello world !")
        var clickingtime:Long=500;
        var refreshtime :Long =1500;
        var serverUrl:String="http://142.132.172.244:5030/checkClick?device_id=";
    }
}
