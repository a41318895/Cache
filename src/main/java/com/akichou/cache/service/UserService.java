package com.akichou.cache.service;

import com.akichou.cache.entity.dto.QueryUserDto;
import com.akichou.cache.entity.dto.UserDto;
import com.akichou.cache.entity.vo.UserVo;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<UserVo> getUserById(QueryUserDto queryUserDto);

    ResponseEntity<UserVo> addUser(UserDto userDto);
}
