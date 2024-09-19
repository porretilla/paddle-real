package paddlee

class Usuario {
    String nombre
    String email
    String contrasena

     static constraints = {
        nombre blank: false  // Asegúrate de que nombre no puede ser nulo
        contrasena blank: false  // Asegúrate de que contrasena no puede ser nulo
        email blank: false, email: true, unique: true  // Asegúrate de que email no puede ser nulo
    }
    
}