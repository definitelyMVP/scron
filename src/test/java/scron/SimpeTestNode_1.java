package scron;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author defMVP
 * @date 2013-02-04
 */
public class SimpeTestNode_1 {

    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        Thread.sleep(Long.MAX_VALUE);
    }

}
