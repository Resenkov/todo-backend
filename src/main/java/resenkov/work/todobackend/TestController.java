package resenkov.work.todobackend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// controller - это спец. бин, который обрабатывает веб запросы
@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "test work";
    }

    @GetMapping("/test2")
    public String test2() {
        return "test work2";
    }

    @GetMapping("/test3")
    public String test3() {
        return "test work3";
    }


}
