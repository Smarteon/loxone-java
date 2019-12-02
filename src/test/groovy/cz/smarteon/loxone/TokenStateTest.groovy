package cz.smarteon.loxone

import cz.smarteon.loxone.message.Token
import spock.lang.Specification
import spock.lang.Unroll

class TokenStateTest extends Specification {

    @Unroll
    def "test #tokenName"() {
        when:
        def token = expiration ? Stub(Token) {
            getSecondsToExpire() >> expiration
        } : null

        def tokenState = new TokenState(token)

        then:
        tokenState.usable == usable
        tokenState.expired == expired
        tokenState.needsRefresh() == refresh
        tokenState.secondsToRefresh() == secsToRefresh

        where:
        tokenName   | expiration || usable | expired | refresh | secsToRefresh
        'valid'     | 3600       || true   | false   | false   | 3300
        'old'       | -5         || false  | true    | false   | 0
        'expired'   | 5          || false  | true    | false   | 0
        'toRefresh' | 65         || false  | false   | true    | 0
        'null'      | null       || false  | true    | false   | 0
    }
}
