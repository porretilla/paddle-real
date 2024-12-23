package paddlee

class UrlMappings {
    static mappings = {
        "/api/usuarios"(controller:'gestion', action: 'getAllUsuarios', method: 'GET')
        "/api/usuario"(controller:'gestion', action: 'save', method: 'POST')
        "/api/login"(controller: 'gestion', action: 'login', method: 'POST')
        "/api/usuario/$id"(controller: 'gestion', action: 'usuario', method: 'GET')
        "/api/usuario/$id"(controller: 'gestion', action: 'update', method: 'PUT')
        "/api/usuario/$id"(controller: 'gestion', action: 'delete', method: 'DELETE')
        "/api/horarios"(controller: "gestion", action: "getHorarios", method: "GET")
        "/api/canchas"(controller: "gestion", action: "getCanchas", method: "GET")
        "/api/validarToken"(controller: "gestion", action: "validateRoleAndToken", method: "POST")

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')

    }
}
