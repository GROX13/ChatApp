package org.giorgi.chatapp.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface Parser {
    List parse(InputStream stream) throws IOException;
}
