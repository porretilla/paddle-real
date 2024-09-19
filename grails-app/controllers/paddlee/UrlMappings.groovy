package paddlee

class UrlMappings {
    static mappings = {
        "/api/usuarios"(resources: 'usuario')
        "/api/login"(controller: 'usuario', action: 'login', method: 'POST')
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
