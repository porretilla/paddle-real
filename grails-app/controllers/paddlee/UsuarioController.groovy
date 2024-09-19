package paddlee
import grails.rest.RestfulController
import grails.converters.JSON

class UsuarioController extends RestfulController<Usuario> {
    static responseFormats = ['json', 'xml']
    
UsuarioController() {
        super(Usuario)
    }

    // Acción de login
    def login() {
        def email = request.JSON.email
        def password = request.JSON.password
        
        // Busca al usuario por email
        def usuario = Usuario.findByEmail(email)
        
        if (usuario && usuario.contrasena == password) {
            // Si el usuario existe y el password coincide, retornar un éxito
            render([message: "Login exitoso", usuarioId: usuario.id] as JSON)
        } else {
            // Si no coincide, retornar un error
            render(status: 401, [message: "Credenciales incorrectas"] as JSON)
        }
    }
}