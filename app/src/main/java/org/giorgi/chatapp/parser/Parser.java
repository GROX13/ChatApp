package org.giorgi.chatapp.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public interface Parser {

    ArrayList parse(InputStream stream);

}
