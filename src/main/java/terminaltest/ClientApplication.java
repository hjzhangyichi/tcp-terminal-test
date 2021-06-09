package terminaltest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import terminaltest.service.ClientService;

import java.io.IOException;


/**
 * @author zhangyichi
 */
@SpringBootApplication
@EnableScheduling
@RestController
public class ClientApplication implements ApplicationListener<ApplicationEvent> {

    @Value("${remote.host}")
    private String host;

    @Value("${remote.port}")
    private int port;

    @Autowired
    private ClientService service;

    @RequestMapping("start")
    public void start(){
        service.start();
    }

    @RequestMapping("stop")
    public void stop() throws IOException {
        service.stop();
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event){
        try{
            // 容器启动完毕时
            if(event instanceof ApplicationReadyEvent){
                service.init(host,port);
            }
            // 容器关闭时
            if(event instanceof ContextClosedEvent){
                if(service != null){
                    service.close();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String... args) {
        SpringApplication.run(ClientApplication.class, args);
    }
}
