package bank.demo.was.controller;

import bank.demo.common.util.DemoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BankWasDemoController {
    private final DemoProperties demoProperties;

    @PostMapping(name = "/", headers = "Content-Type=application/json")
    public @ResponseBody DemoProperties index(@RequestBody String body) {
        log.info("body: {}", body);
        log.info("demoProperties: {}", demoProperties);
        log.info("FrameworkProperties: {}", demoProperties.getCoreProperties());
        return demoProperties;
    }

    @GetMapping(name = "/")
    public @ResponseBody String index() {
        return "Hello World!";
    }
}
