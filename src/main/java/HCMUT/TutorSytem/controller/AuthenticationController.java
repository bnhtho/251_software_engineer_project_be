package HCMUT.TutorSytem.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

//    @GetMapping("/key")
//    public String getKey(){
//        SecretKey key = Jwts.SIG.HS256.key().build();
//        return Encoders.BASE64.encode(key.getEncoded());
//    }
}
