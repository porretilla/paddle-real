package paddlee
import grails.rest.RestfulController
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.validation.ValidationException

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
        def requestBody = request.JSON // Captura el cuerpo de la solicitud en JSON
        def usuario = new Usuario(request.JSON) // Captura los datos del request en formato JSON

        if (usuario.save(flush: true)) {
            // Si el usuario se guarda correctamente
             render requestBody as JSON
        } else {
            // Si hay errores al crear el usuario
            render(status: 400, [message: "Error al crear el usuario", errors: usuario.errors] as JSON)
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
            [horario_id: 1, fecha: "2024-09-23", hora: "10:00"],
            [horario_id: 2, fecha: "2024-09-28", hora: "12:00"],
            [horario_id: 3, fecha: "2024-09-30", hora: "14:00"],
            [horario_id: 4, fecha: "2024-10-01", hora: "16:00"],
            [horario_id: 5, fecha: "2024-10-04", hora: "18:00"],
            [horario_id: 6, fecha: "2024-10-06", hora: "20:00"],
            [horario_id: 7, fecha: "2024-10-09", hora: "22:00"],
            [horario_id: 8, fecha: "2024-10-12", hora: "10:00"]
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
    def validateRoleAndToken() {
        def token = request.getHeader("Authorization")?.replace("Bearer ", "")
        def idTipoUsuario = request.JSON.idTipoUsuario

        if (!token) {
            render(status: 401, message: 'Token es requerido')
            return
        }
        
        if (!idTipoUsuario) {
            render(status: 400, message: 'idTipoUsuario es requerido')
            return
        }

        try {
            def alias = jwtService.verifyToken(token)
            def usuario = Usuario.findByAlias(alias)

            if (!usuario) {
                render(status: 404, message: 'Usuario no encontrado')
                return
            }

            if (usuario.idTipoUsuario != idTipoUsuario) {
                render(status: 403, message: 'Rol invalido')
                return
            }

            render([message: 'Token y rol valido', usuario: usuario.alias] as JSON, status: 200)

        } catch (Exception e) {
            render(status: 401, text: 'Token expirado o invalido: ' + e.message)
        }

    }
}