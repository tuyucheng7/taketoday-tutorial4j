package cn.tuyucheng.taketoday.stringtypes

import spock.lang.Specification

class SingleQuotedString extends Specification {

    def 'single quoted string'() {
        given:
        def example = 'Hello world!'

        expect:
        example == 'Hello' + ' world!'
    }
}
