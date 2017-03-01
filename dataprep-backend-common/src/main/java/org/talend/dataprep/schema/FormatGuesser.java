package org.talend.dataprep.schema;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

/**
 * Represents a class able to create {@link org.talend.dataprep.schema.FormatGuess} from a data set content.
 */
public interface FormatGuesser {

    /**
     * Guess the content type of the provided stream.
     *
     * @param stream The raw data set content.
     * @return A {@link org.talend.dataprep.schema.FormatGuess guess} that can never be null (see
     * {@link FormatGuess#getConfidence()}.
     */
    default Result guess(InputStream stream) {
        return new Result(new NoOpFormatGuess(), Collections.emptyMap());
    }

    class Result {

        private FormatGuess formatGuess;

        private Map<String, String> parameters;

        public Result(FormatGuess formatGuess, Map<String, String> parameters) {
            this.formatGuess = formatGuess;
            this.parameters = parameters;
        }

        public FormatGuess getFormatGuess() {
            return formatGuess;
        }

        public void setFormatGuess(FormatGuess formatGuess) {
            this.formatGuess = formatGuess;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public void setParameters(Map<String, String> parameters) {
            this.parameters = parameters;
        }
    }
}