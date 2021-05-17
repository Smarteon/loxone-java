package cz.smarteon.loxone;

import cz.smarteon.loxone.message.Token;

/**
 * Evaluates the {@link TokenState} from {@link Token}.
 * Currently package private, as it's used in test only.
 */
interface TokenStateEvaluator {

    default TokenState evaluate(final Token token) {
        return new TokenState(token);
    }
}
