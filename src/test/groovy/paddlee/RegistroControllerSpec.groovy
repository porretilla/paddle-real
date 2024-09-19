package paddlee

import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Specification

class RegistroControllerSpec extends Specification implements ControllerUnitTest<RegistroController> {

     void "test index action"() {
        when:
        controller.index()

        then:
        status == 200

     }
}
