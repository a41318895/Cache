package com.akichou.cache.mapper;

import com.akichou.cache.entity.User;
import com.akichou.cache.entity.dto.UserDto;
import com.akichou.cache.entity.vo.UserVo;

public class UserMapper {

    public static User mapUserDtoToUser(UserDto userDto) {

        return User.builder()
                .username(userDto.username())
                .age(userDto.age())
                .isVip(userDto.isVip())
                .build() ;
    }

    public static UserVo mapUserToUserVo(User user) {

        if (user == null) return null ;

        return new UserVo(
                user.getId().toString(),
                user.getUsername(),
                user.getAge(),
                user.getIsVip()) ;
    }
}
