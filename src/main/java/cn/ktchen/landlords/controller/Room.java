package cn.ktchen.landlords.controller;

import cn.ktchen.landlords.util.Decoder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/room")
public class Room {

    @GetMapping
    public String getRoom() throws IOException {
        Resource resource = new ClassPathResource("templates/Room.html");
        return Decoder.inputStream2Str(resource.getInputStream());
    }
}
