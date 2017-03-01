package org.talend.dataprep.schema;

import java.io.InputStream;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Component;

@Component
public class NoOpFormatGuesser implements FormatGuesser {

    @Autowired
    private NoOpFormatGuess noOpFormatGuess;

    @Override
    public FormatGuesser.Result guess(InputStream stream) {
        return new FormatGuesser.Result(noOpFormatGuess, Collections.emptyMap());
    }

}