package paddlee

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class UsuarioControllerSpec extends Specification implements ControllerUnitTest<UsuarioController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
