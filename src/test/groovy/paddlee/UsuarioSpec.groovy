package paddlee

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class UsuarioSpec extends Specification implements DomainUnitTest<Usuario> {

     void "test domain constraints"() {
        when:
        Usuario domain = new Usuario()
        //TODO: Set domain props here

        then:
        domain.validate()
     }
}
