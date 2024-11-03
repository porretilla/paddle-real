package paddlee
import grails.rest.RestfulController
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.validation.ValidationException
import io.jsonwebtoken.JwtException

@Transactional
class GestionController extends RestfulController<Usuario> {
    static responseFormats = ['json', 'xml']
    JwtService jwtService  // Inyectar el servicio JWT
    
GestionController() {
        super(Usuario)
    }

// Acción para iniciar sesión y generar el token JWT
   def login() {
    def alias = request.JSON.alias
    def contrasena = request.JSON.contrasena

        // Buscar al usuario por alias y verificar la contraseña
        def usuario = Usuario.findByAlias(alias)
        if (usuario && usuario.contrasena == contrasena) {
            String token = jwtService.generateToken(alias)
            render([token: token, id: usuario.id, idTipoUsuario: usuario.idTipoUsuario,
            nombre: usuario.nombre,apellido: usuario.apellido,
            alias: usuario.alias,
            contrasena: usuario.contrasena,
            email: usuario.email,
            genero: usuario.genero,
            direccion: usuario.direccion,
            tipoDeJuego: usuario.tipoDeJuego,
            fotoPerfil: usuario.fotoPerfil,
            telefono: usuario.telefono,
            edad: usuario.edad,
            nivel: usuario.nivel,
            recontrasena: usuario.recontrasena,
            remail: usuario.remail] as JSON)
        } else {
            render(status: 401, message: 'Credenciales inválidas')
        }
    }


    // Acción para crear (guardar) un nuevo usuario
def save() {
    def requestBody = request.JSON
    def aliasExistente = Usuario.findByAlias(requestBody.alias)

    if (aliasExistente) {
        // Si el alias ya está en uso, responder con status 409
        render(status: 409, [message: "El alias ya está en uso"] as JSON)
    } else {
        def usuario = new Usuario(requestBody)

        if (usuario.save(flush: true)) {
            // Si el usuario se guarda correctamente
            render usuario as JSON
        } else {
            // Si hay errores al crear el usuario
            render(status: 400, [message: "Error al crear el usuario", errors: usuario.errors] as JSON)
        }
    }
}

    def usuario(Long id) {
        if (!id) {
            render(status: 400, message: 'Falta ID usuario')
            return
        }

        // busca al usuario por el ID
        def usuario = Usuario.get(id)
        if (!usuario) {
            render(status: 404, message: 'Usuario no encontrado')
            return
        }

        //responde con informacion del usuario en JSON
        respond usuario
    }

    //Metodo para actualizar y ver si existe el usuario
    def update (Long id) {
        if (!id) {
            render(status: 400, message: 'No se encuentra el ID')
            return
        }

        // busca el usuario por ID
        def usuario = Usuario.get(id)
        if (!usuario) {
            render(status: 404, message: 'Usuario no encontrado')
            return
        }

        // parametros para usar este objeto
        usuario.properties = request.JSON

        try {
            // valida y guarda el update del usuario
            if (usuario.save(flush: true)) {
                respond usuario, [status: 200]
            } else {
                // si la validacion falla, devuelve un 422 
                respond usuario.errors, status: 422
            }
        } catch (ValidationException e) {
            render(status: 422, message: 'validacion fallida: ' + e.message)
        }
    }

// Metodo para borrar a un usuario por ID
    def delete(Long id) {
        if (!id) {
            render(status: 400, message: 'Falta ID usuario')
            return
        }

        def usuario = Usuario.get(id)
        if (!usuario) {
            render(status: 404, message: 'Usuario no encontrado')
            return
        }

        try {
            usuario.delete(flush: true)
            render(status: 204, message: 'Borrado con exito!') //borrado exitoso
} catch (Exception e) {
            render(status: 500, message: 'Fallo al borrar al usuario: ' + e.message)
        }
    }

// Acciòn que devuelve mock de horarios
    def getHorarios() {
        def mockHorarios = [
            [horario_id: 1, fecha: "2024-11-23", hora: "10:00"],
            [horario_id: 2, fecha: "2024-11-28", hora: "12:00"],
            [horario_id: 3, fecha: "2024-11-30", hora: "14:00"],
            [horario_id: 4, fecha: "2024-12-01", hora: "16:00"],
            [horario_id: 5, fecha: "2024-12-04", hora: "18:00"],
            [horario_id: 6, fecha: "2024-12-06", hora: "20:00"],
            [horario_id: 7, fecha: "2024-12-09", hora: "22:00"],
            [horario_id: 8, fecha: "2024-12-12", hora: "10:00"]
        ]
        render mockHorarios as JSON
    }

    // Acciòn que devuelve un mock de canchas
    def getCanchas() {
        def mockCanchas = [
            [cancha_id: 1, nombre: "Cancha A", ubicacion: "Location A"],
            [cancha_id: 2, nombre: "Cancha B", ubicacion: "Location B"],
            [cancha_id: 3, nombre: "Cancha C", ubicacion: "Location C"]
        ]
        render mockCanchas as JSON
    }



    // Valida el token y el role de usuario (idTipoUsuario)
      // Validate token and user role (idTipoUsuario)
 def validateRoleAndToken() {
        def token = request.getHeader("Authorization")?.replace("Bearer ", "")
        //def idTipoUsuario = request.JSON.idTipoUsuario
        Integer idTipoUsuario = (request.JSON.idTipoUsuario?.trim()) ? request.JSON.idTipoUsuario.toInteger() : null
        println "tipo de usuario recibido: " + idTipoUsuario
        
        if (!token) {
            render([message: 'Token es requerido', isValid: false] as JSON, status: 401)
            return
        }
        
        if (!idTipoUsuario) {
            render([message: 'idTipoUsuario es requerido', isValid: false] as JSON, status: 400)
            return
        }

        try {

            // def jwtService = grailsApplication.mainContext.getBean('jwtService')
            //     jwtService.getClass().methods.each { method ->
            //         println method.name + " " + method.parameterTypes.collect { it.simpleName }.join(', ')
            //     }
            def isValid = jwtService.validateToken(token) // Call without the secret
            println "es valido?:" + isValid
            def alias = jwtService.getUsernameFromToken(token)
            println "alias recuperado:" + alias
            def usuario = Usuario.findByAlias(alias)
            println "contrasenia encontrada: " + usuario.contrasena
            //println "idTipoUsuario de usuario:" + usuario.idTipoUsuario
            println "idTipoUsuario de usuario: ${usuario?.idTipoUsuario}"
            
            //print "tipo valido :"  (usuario.idTipoUsuario != idTipoUsuario)
            if (!usuario) {
                render([message: 'Usuario no encontrado', isValid: false] as JSON, status: 404)
                return
            }

            //print "idTipoUsuario de usuario:" + usuario.idTipoUsuario
            if (usuario.idTipoUsuario != idTipoUsuario) {
                render([message: 'Role no valido', isValid: false] as JSON, status: 403)
               return
            }

            render([message: 'Token y role Valido', isValid: true] as JSON, status: 200)

        } catch (JwtException e) {
            log.error("JWT validation failed: ${e.message}") // Log specific error message
            render([message: 'Token no valido o el mismo ha expirado: ' + e.message, isValid: false] as JSON, status: 401)
        } catch (Exception e) {
            log.error("Unexpected error: ${e.message}") // Log unexpected errors
            render([message: 'Error inesperado: ' + e.message, isValid: false] as JSON, status: 500)
        }
    }
}