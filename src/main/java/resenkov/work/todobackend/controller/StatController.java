package resenkov.work.todobackend.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import resenkov.work.todobackend.entity.Stat;
import  resenkov.work.todobackend.service.StatService;

@RestController

public class StatController {

    private final StatService statService;

    public StatController(StatService statService) {
        this.statService = statService;
    }


    @PostMapping("/stat")
    public ResponseEntity<Stat> findByEmail(@RequestBody String email) {
        return ResponseEntity.ok(statService.findStat(email));
    }


}

