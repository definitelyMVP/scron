package scron;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author defMVP
 * @date 2013-02-04
 */
public class SimpleTask {

    private static final Logger log = LoggerFactory.getLogger(SimpleTask.class);

    public void print() {
        log.info("===========Hello, world!=========");
    }

}
