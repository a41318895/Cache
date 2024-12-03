package com.akichou.cache.controller;

import com.akichou.cache.entity.dto.QueryUserDto;
import com.akichou.cache.entity.dto.UserDto;
import com.akichou.cache.entity.vo.UserVo;
import com.akichou.cache.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService ;

    @PostMapping("/query")
    public ResponseEntity<UserVo> getUserById(@Validated @RequestBody QueryUserDto queryUserDto) {

       return userService.getUserById(queryUserDto) ;
    }

    @PostMapping("/add")
    public ResponseEntity<UserVo> addUser(@Validated @RequestBody UserDto userDto) {

        return userService.addUser(userDto) ;
    }
}
